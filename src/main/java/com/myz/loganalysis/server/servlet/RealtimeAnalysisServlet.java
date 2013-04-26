package com.myz.loganalysis.server.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.mongodb.DB;
import com.myz.loganalysis.AnalysisWorker;
import com.myz.loganalysis.LogAnalysisUtil;
import com.myz.loganalysis.LogAnalysisWorker;
import com.myz.loganalysis.MongoDBManager;
import com.myz.loganalysis.config.AnalysisConfigurator;
import com.myz.loganalysis.config.LogAnalysisConfig;
import com.myz.loganalysis.config.LogConfig;
import com.myz.loganalysis.config.LogEntity;
import com.myz.loganalysis.config.ProfileConfig;
import com.myz.loganalysis.tool.LogVarParser;

public class RealtimeAnalysisServlet extends BaseServlet {
    private static Logger logger = Logger.getLogger(RealtimeAnalysisServlet.class);
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        String logUniqueId = request.getParameter("loguniqueid");
        String logRecLevel = request.getParameter("logreclevel");
        String logCollection = request.getParameter("logcollection");
        String analysisDate = request.getParameter("analysisdate");

        HashMap<String, String> paraMap = new HashMap<String, String>();
        paraMap.put(LogAnalysisUtil.PARAM_KEY_ANALYSISDATE, analysisDate);
        LogVarParser varParser = new LogVarParser(paraMap);
        LogAnalysisConfig config = AnalysisConfigurator.getInstance().getConfig();
        LogEntity logEntity = config.getLogEntityByUniqueID(logUniqueId);
        if (logEntity == null) {
            this.dispathError("Can not find logentity by logUniqueId:" + logUniqueId, request, response);
            return;
        }

        HashMap<String, ProfileConfig> profileMap = config.getProfiles();
        LogConfig logConfig = logEntity.getLogConfig();
        ProfileConfig pc = profileMap.get(logConfig.getProfile());
        String logformat = LogAnalysisUtil.isNull(logEntity.getLogFormat()) ? pc.getLogFormat()
                : logEntity.getLogFormat();
        String logseparator = LogAnalysisUtil.isNull(logEntity.getLogSeparator()) ? pc.getLogSeparator()
                : logEntity.getLogSeparator();
        String logcostunit = LogAnalysisUtil.isNull(logEntity.getLogCostunit()) ? pc.getLogCostunit()
                : logEntity.getLogCostunit();

        String logCollectionName = logCollection;
        DB logdb = null;
        try {
            logdb = MongoDBManager.getInstance().getLogDB();
        } catch (Exception e) {
            logger.error("error when get logdb from MongoDB", e);
        }
        // clear collection
        if (logdb != null) {
            logdb.getCollection(logCollectionName).drop();
        }

        File[] logfiles = logEntity.getLogFiles(logConfig.getParentPath(), varParser);
        if (logfiles == null || logfiles.length == 0) {
            logger.info("log file not exists for :" + logEntity.getMemo());
        } else {
            for (File logfile : logfiles) {

                String logfileStr = logfile.getAbsolutePath();
                logfileStr = logfileStr.replace('\\', '/');
                if (!logfile.exists() || logfile.isDirectory()) {
                    logger.info("log file not exists:" + logfileStr + " for " + logEntity.getMemo());
                    continue;
                }

                AnalysisWorker worker = new LogAnalysisWorker(
                                                              logEntity,
                                                              logfileStr,
                                                              logCollectionName,
                                                              logformat,
                                                              logseparator,
                                                              logcostunit,
                                                              logRecLevel);
                try {
                    Process process = worker.createAnalysisProcess();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                    String line = reader.readLine();
                    while (line != null) {

                        response.getOutputStream().println(line);
                        line = reader.readLine();

                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
        try {
            response.getOutputStream().println("logCollection:" + logCollection);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
