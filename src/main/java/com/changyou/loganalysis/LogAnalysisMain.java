package com.changyou.loganalysis;

import java.io.File;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.changyou.loganalysis.config.AnalysisConfigurator;
import com.changyou.loganalysis.config.LogAnalysisConfig;
import com.changyou.loganalysis.config.LogConfig;
import com.changyou.loganalysis.config.LogEntity;
import com.changyou.loganalysis.config.ProfileConfig;

public class LogAnalysisMain {
    private static Logger logger = Logger.getLogger(LogAnalysisMain.class);

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    public static void main(String[] args) throws Exception {

        Date analysisDate = null;

        if (args != null && args.length > 0) {
            try {
                analysisDate = sdf.parse(args[0]);
            } catch (ParseException e) {
                logger.error("Error when parse analysis date from input:" + args[0], e);
            }
        }

        if (analysisDate == null) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, -1);
            analysisDate = c.getTime();
        }

        LogAnalysisConfig config = AnalysisConfigurator.getInstance().getConfig();

        int threadpoolSize = config.getThreadPoolSize();
        ExecutorService executor = Executors.newFixedThreadPool(threadpoolSize);

        ArrayList<AnalysisWorker> workerList = new ArrayList<AnalysisWorker>();

        HashMap<String, ProfileConfig> profileMap = config.getProfiles();
        for (LogConfig logConfig : config.getLogConfigList()) {
            ProfileConfig pc = profileMap.get(logConfig.getProfile());
            for (LogEntity log : logConfig.getLogEntities()) {

                String logformat = LogAnalysisUtil.isNull(log.getLogFormat()) ? pc.getLogFormat() : log.getLogFormat();
                String logseparator = LogAnalysisUtil.isNull(log.getLogSeparator()) ? pc.getLogSeparator()
                        : log.getLogSeparator();
                String logcostunit = LogAnalysisUtil.isNull(log.getLogCostunit()) ? pc.getLogCostunit()
                        : log.getLogCostunit();

                String logfileStr = logConfig.getParentPath() + "/"
                        + LogAnalysisUtil.mergeDateString(analysisDate, log.getFile());
                logfileStr = logfileStr.replace('\\', '/');
                File logFile = new File(logfileStr);
                if (!logFile.exists() || logFile.isDirectory()) {
                    continue;
                }

                String errfileStr = "";
                if (!LogAnalysisUtil.isNull(log.getErrFile())) {
                    errfileStr = logConfig.getParentPath() + "/"
                            + LogAnalysisUtil.mergeDateString(analysisDate, log.getErrFile());
                    errfileStr = errfileStr.replace('\\', '/');
                    File errFile = new File(errfileStr);
                    if (!errFile.exists() || errFile.isDirectory()) {
                        errfileStr = "";
                    }
                }

                AnalysisWorker worker = new AnalysisWorker(
                                                           log.getMemo(),
                                                           logfileStr,
                                                           logformat,
                                                           logseparator,
                                                           logcostunit,
                                                           errfileStr);

                workerList.add(worker);
            }
        }

        LogAnalysisCountDown countDownMonitor = LogAnalysisCountDown.initCountDown(workerList.size());

        logger.info("LogAnalysis Begins");
        for (AnalysisWorker worker : workerList) {
            executor.execute(worker);
        }
        countDownMonitor.waitForFinish();

        File resultFile = new File("analysis_" + sdf.format(analysisDate) + ".csv");

        logger.info("Generating analysis result:" + resultFile.getAbsolutePath());

        PrintWriter writer = new PrintWriter(resultFile, "GBK");
        StringBuilder buf = new StringBuilder();
        // header
        writer.println("服务器,响应时间<1S,比例,响应时间1~3s,比例,响应时间3~10s,比例,响应时间>=10s,比例,500错误页面量,比例,exception数量,日志");
        for (AnalysisWorker worker : workerList) {
            LogStatistic statistic = worker.getStatistic();
            buf.setLength(0);
            buf.append(statistic.servername).append(",");
            buf
               .append(statistic.cost0_1s)
               .append(",")
               .append(LogAnalysisUtil.round((double) statistic.cost0_1s / statistic.totalrecord, 2))
               .append(",");
            buf
               .append(statistic.cost1_3s)
               .append(",")
               .append(LogAnalysisUtil.round((double) statistic.cost1_3s / statistic.totalrecord, 2))
               .append(",");
            buf
               .append(statistic.cost3_10s)
               .append(",")
               .append(LogAnalysisUtil.round((double) statistic.cost3_10s / statistic.totalrecord, 2))
               .append(",");
            buf
               .append(statistic.cost10s)
               .append(",")
               .append(LogAnalysisUtil.round((double) statistic.cost10s / statistic.totalrecord, 2))
               .append(",");
            buf
               .append(statistic.status500)
               .append(",")
               .append(LogAnalysisUtil.round((double) statistic.status500 / statistic.totalrecord, 2))
               .append(",");
            buf.append(statistic.exceptioncnt).append(",");
            buf.append(statistic.logfile);
            writer.println(buf.toString());
        }
        writer.flush();

        logger.info("LogAnalysis Ends");
        System.exit(0);
    }
}
