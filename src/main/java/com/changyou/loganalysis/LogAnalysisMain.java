package com.changyou.loganalysis;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
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

        long totalFileLength = 0;
        long beginTime = System.currentTimeMillis();
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

                        totalFileLength +=logfile.length();
                        AnalysisWorker worker = new LogAnalysisWorker(
                                                                      log.getMemo(),
                                                                      logfileStr,
                                                                      logformat,
                                                                      logseparator,
                                                                      logcostunit);
                        workerList.add(worker);

                    }

                    File[] errFiles = log.getErrFiles(logConfig.getParentPath());
                    if (errFiles == null || errFiles.length == 0) {
                        logger.info("no error file found for :" + log.getMemo());
                    } else {
                        for (File errFile : errFiles) {
                            String errfileStr = errFile.getAbsolutePath().replace('\\', '/');
                            if (!errFile.exists() || errFile.isDirectory()) {
                                logger.info("err file not exists:" + errfileStr + " for " + log.getMemo());
                                continue;
                            }

                            totalFileLength +=errFile.length();
                            AnalysisWorker worker = new ErrAnalysisWorker(log.getMemo(), errfileStr);
                            workerList.add(worker);
                        }
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

        logger.info("LogAnalysis Ends");
        logger.info("Total length of files that have been analyzed: " + totalFileLength / 1024 + "KB");
        logger.info("Total time taken to complete the analysis:" + (System.currentTimeMillis() - beginTime) + "ms");

        Map<String, LogStatistic> statisticMap = LogAnalysisMonitor.getInstance().getStatisticMap();
        Collection<LogStatistic> statList = statisticMap.values();

        Properties props = new Properties();
        props.put("analysisdate", analysisDateStr);

        String reportFileName = "analysis_" + sdf.format(analysisDate);
        File csvReportFile = null;
        try {
            csvReportFile = ReportUtil
                                      .generateAnalysisCSVReport(
                                                                 config.getReportPath() + "/" + reportFileName + ".csv",
                                                                 statList);

            props.put("resultfile.path", csvReportFile.getAbsolutePath());
            props.put("resultfile", csvReportFile.getName());

            ReportUtil.generateAnalysisXLSReport(
                                                 config.getReportPath() + "/analysis_template.xls",
                                                 config.getReportPath() + "/" + reportFileName + ".xls",
                                                 statList);
        } catch (Exception e) {
            logger.error("Error when generating analysis report:", e);
        }

        String antScript = System.getProperty("send.mail.script", "sendmail.xml");
        AntRunner.runAntScript(antScript, "sendmail", props);
    }
}
