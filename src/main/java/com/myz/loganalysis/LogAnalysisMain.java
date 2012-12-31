package com.myz.loganalysis;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.mongodb.DB;
import com.myz.loganalysis.config.AnalysisConfigurator;
import com.myz.loganalysis.config.LogAnalysisConfig;
import com.myz.loganalysis.config.LogConfig;
import com.myz.loganalysis.config.LogEntity;
import com.myz.loganalysis.config.ProfileConfig;
import com.myz.loganalysis.server.LogAnalysisServer;
import com.myz.loganalysis.tool.LogVarParser;

public class LogAnalysisMain {
    private static Logger logger = Logger.getLogger(LogAnalysisMain.class);

    public static void main(String[] args) throws Exception {

        HashMap<String, String> paraMap = LogAnalysisUtil.parseParam(args);

        String analysisDateStr = null;
        if (!paraMap.containsKey(LogAnalysisUtil.PARAM_KEY_ANALYSISDATE)) {
            analysisDateStr = LogAnalysisUtil.getPreAnalysisDate();
            paraMap.put(LogAnalysisUtil.PARAM_KEY_ANALYSISDATE, analysisDateStr);
        }

        init(paraMap);

        if (paraMap.containsKey(LogAnalysisUtil.PARAM_KEY_DAEMON)) {
            LogAnalysisServer server = new LogAnalysisServer(AnalysisConfigurator
                                                                                 .getInstance()
                                                                                 .getConfig()
                                                                                 .getServerPort());
            server.startServer();
        } else {
            try {
                LogAnalysisMain main = new LogAnalysisMain();
                main.execAnalysiMain(paraMap);
            } catch (Exception e) {
                logger.error("error when execute analysis.", e);
            }
            System.exit(0);
        }

    }

    public static void init(HashMap<String, String> paraMap) {
        String configFile = (String) paraMap.get(LogAnalysisUtil.PARAM_KEY_ANALYSISCONFIG);
        if (configFile != null) {
            AnalysisConfigurator.getInstance(configFile);
        } else {
            AnalysisConfigurator.getInstance();
        }
    }

    public void execAnalysiMain(HashMap<String, String> paraMap) throws Exception {

        String analysisDateStr = (String) paraMap.get(LogAnalysisUtil.PARAM_KEY_ANALYSISDATE);
        LogVarParser varParser = new LogVarParser(paraMap);
        LogAnalysisConfig config = AnalysisConfigurator.getInstance().getConfig();

        int threadpoolSize = config.getThreadPoolSize();
        ExecutorService executor = Executors.newFixedThreadPool(threadpoolSize);

        ArrayList<AnalysisWorker> workerList = new ArrayList<AnalysisWorker>();

        DB logdb = null;
        try {
            logdb = MongoDBManager.getInstance().getLogDB();
        } catch (Exception e) {
            logger.error("error when get logdb from MongoDB", e);
        }
        long totalFileLength = 0;
        long beginTime = System.currentTimeMillis();
        HashMap<String, ProfileConfig> profileMap = config.getProfiles();
        for (LogConfig logConfig : config.getLogConfigList()) {
            ProfileConfig pc = profileMap.get(logConfig.getProfile());
            for (LogEntity logentity : logConfig.getLogEntities()) {

                String logformat = LogAnalysisUtil.isNull(logentity.getLogFormat()) ? pc.getLogFormat()
                        : logentity.getLogFormat();
                String logseparator = LogAnalysisUtil.isNull(logentity.getLogSeparator()) ? pc.getLogSeparator()
                        : logentity.getLogSeparator();
                String logcostunit = LogAnalysisUtil.isNull(logentity.getLogCostunit()) ? pc.getLogCostunit()
                        : logentity.getLogCostunit();

                String logCollectionName = logentity.getLogCollectionName(analysisDateStr);

                // clear collection
                if (logdb != null) {
                    logdb.getCollection(logCollectionName).drop();
                }

                File[] logfiles = logentity.getLogFiles(logConfig.getParentPath(), varParser);
                if (logfiles == null || logfiles.length == 0) {
                    logger.info("log file not exists for :" + logentity.getMemo());
                } else {
                    for (File logfile : logfiles) {

                        String logfileStr = logfile.getAbsolutePath();
                        logfileStr = logfileStr.replace('\\', '/');
                        if (!logfile.exists() || logfile.isDirectory()) {
                            logger.info("log file not exists:" + logfileStr + " for " + logentity.getMemo());
                            continue;
                        }

                        totalFileLength += logfile.length();
                        AnalysisWorker worker = new LogAnalysisWorker(
                                                                      logentity,
                                                                      logfileStr,
                                                                      logCollectionName,
                                                                      logformat,
                                                                      logseparator,
                                                                      logcostunit);
                        workerList.add(worker);

                    }

                    File[] errFiles = logentity.getErrFiles(logConfig.getParentPath(), varParser);
                    if (errFiles == null || errFiles.length == 0) {
                        logger.info("no error file found for :" + logentity.getMemo());
                    } else {
                        for (File errFile : errFiles) {
                            String errfileStr = errFile.getAbsolutePath().replace('\\', '/');
                            if (!errFile.exists() || errFile.isDirectory()) {
                                logger.info("err file not exists:" + errfileStr + " for " + logentity.getMemo());
                                continue;
                            }

                            totalFileLength += errFile.length();
                            AnalysisWorker worker = new ErrAnalysisWorker(logentity, errfileStr);
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

        String reportFileName = "analysis_" + analysisDateStr;
        try {
            File csvReportFile = ReportUtil.generateAnalysisCSVReport(config.getReportPath() + "/" + reportFileName
                    + ".csv", statList);

            File xlsReportFile = ReportUtil
                                           .generateAnalysisXLSReport(
                                                                      config.getReportPath() + "/analysis_template.xls",
                                                                      config.getReportPath() + "/" + reportFileName
                                                                              + ".xls",
                                                                      statList);

            props.put("resultfile.path", csvReportFile.getAbsolutePath() + "," + xlsReportFile.getAbsolutePath());
            props.put("resultfile", csvReportFile.getName() + "," + xlsReportFile.getName());

        } catch (Exception e) {
            logger.error("Error when generating analysis report:", e);
        }

        String antScript = System.getProperty("send.mail.script", "sendmail.xml");
        AntRunner.runAntScript(antScript, "sendmail", props);
    }
}
