package com.changyou.loganalysis;

import java.io.File;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
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

        LogAnalysisUtil.parseParam(args);

        try {
            LogAnalysisMain main = new LogAnalysisMain();
            main.execAnalysiMain();
        } catch (Exception e) {
            logger.error("error when execute analysis.", e);
        }

        System.exit(0);
    }

    public void execAnalysiMain() throws Exception {
        Date analysisDate = null;

        String analysisDateStr = System.getProperty(LogAnalysisUtil.PARAM_KEY_ANALYSISDATE);
        try {
            analysisDate = sdf.parse(analysisDateStr);
        } catch (ParseException e) {
            logger.error("Error when parse analysis date:" + analysisDateStr, e);
        }

        LogAnalysisConfig config = null;
        String configFile = System.getProperty(LogAnalysisUtil.PARAM_KEY_ANALYSISCONFIG);
        if (configFile != null) {
            config = AnalysisConfigurator.getInstance(configFile).getConfig();
        } else {
            config = AnalysisConfigurator.getInstance().getConfig();
        }

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

                File[] logfiles = log.getLogFiles(logConfig.getParentPath());
                if (logfiles == null || logfiles.length == 0) {
                    logger.info("log file not exists for :" + log.getMemo());
                } else {
                    for (File logfile : logfiles) {

                        String logfileStr = logfile.getAbsolutePath();
                        logfileStr = logfileStr.replace('\\', '/');
                        if (!logfile.exists() || logfile.isDirectory()) {
                            logger.info("log file not exists:" + logfileStr + " for " + log.getMemo());
                            continue;
                        }

                        AnalysisWorker worker = new LogAnalysisWorker(
                                                                   log.getMemo(),
                                                                   logfileStr,
                                                                   logformat,
                                                                   logseparator,
                                                                   logcostunit);
                        workerList.add(worker);

                    }

                    File[] errFiles = log.getErrFiles(logConfig.getParentPath());
                    for(File errFile : errFiles) {
                        String errfileStr = errFile.getAbsolutePath().replace('\\', '/');
                        if(!errFile.exists() || errFile.isDirectory()) {
                            logger.info("err file not exists:" + errfileStr + " for " + log.getMemo());
                            continue;
                        }

                        AnalysisWorker worker = new ErrAnalysisWorker(log.getMemo(), errfileStr);
                        workerList.add(worker);
                    }
                }

            }
        }

        LogAnalysisMonitor monitor = LogAnalysisMonitor.initialize(workerList.size());

        logger.info("LogAnalysis Begins");
        for (AnalysisWorker worker : workerList) {
            executor.execute(worker);
        }
        monitor.waitForFinish();

        File resultFile = new File(config.getReportPath(), "analysis_" + sdf.format(analysisDate) + ".csv");
        resultFile.getParentFile().mkdir();
        logger.info("Generating analysis result:" + resultFile.getAbsolutePath());

        PrintWriter writer = new PrintWriter(resultFile, "GBK");
        StringBuilder buf = new StringBuilder();
        // header
        writer.println("服务器,响应时间<1S,比例,响应时间1~3s,比例,响应时间3~10s,比例,响应时间>=10s,比例,500错误页面量,比例,exception数量,日志");
        Map<String, LogStatistic> statisticMap = LogAnalysisMonitor.getInstance().getStatisticMap();
        for (String servername : statisticMap.keySet()) {
            LogStatistic statistic = statisticMap.get(servername);
            buf.setLength(0);
            buf.append(statistic.servername).append(",");
            buf
               .append(statistic.cost0_1s)
               .append(",")
               .append(LogAnalysisUtil.round((double) statistic.cost0_1s / statistic.totalrecord, 4))
               .append(",");
            buf
               .append(statistic.cost1_3s)
               .append(",")
               .append(LogAnalysisUtil.round((double) statistic.cost1_3s / statistic.totalrecord, 4))
               .append(",");
            buf
               .append(statistic.cost3_10s)
               .append(",")
               .append(LogAnalysisUtil.round((double) statistic.cost3_10s / statistic.totalrecord, 4))
               .append(",");
            buf
               .append(statistic.cost10s)
               .append(",")
               .append(LogAnalysisUtil.round((double) statistic.cost10s / statistic.totalrecord, 4))
               .append(",");
            buf
               .append(statistic.status500)
               .append(",")
               .append(LogAnalysisUtil.round((double) statistic.status500 / statistic.totalrecord, 4))
               .append(",");
            buf.append(statistic.exceptioncnt).append(",");
            buf.append(statistic.logfile);
            writer.println(buf.toString());
        }
        writer.flush();

        logger.info("LogAnalysis Ends");

        String antScript = System.getProperty("send.mail.script", "sendmail.xml");
        Properties props = new Properties();
        props.put("analysisdate", analysisDateStr);
        props.put("resultfile.path", resultFile.getAbsolutePath());
        props.put("resultfile", resultFile.getName());
        AntRunner.runAntScript(antScript, "sendmail", props);
    }
}
