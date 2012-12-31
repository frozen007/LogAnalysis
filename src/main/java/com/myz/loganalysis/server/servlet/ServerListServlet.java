package com.myz.loganalysis.server.servlet;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.myz.loganalysis.LogAnalysisUtil;
import com.myz.loganalysis.MongoDBManager;
import com.myz.loganalysis.config.AnalysisConfigurator;
import com.myz.loganalysis.config.LogAnalysisConfig;
import com.myz.loganalysis.config.LogConfig;
import com.myz.loganalysis.config.LogEntity;
import com.myz.loganalysis.server.bean.LogCollection;

public class ServerListServlet extends BaseServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        String analysisdate = request.getParameter("analysisdate");
        if (analysisdate == null) {
            analysisdate = LogAnalysisUtil.getPreAnalysisDate();
        }
        DB db = MongoDBManager.getInstance().getLogDB();
        LogAnalysisConfig config = AnalysisConfigurator.getInstance().getConfig();
        ArrayList<LogCollection> colList = new ArrayList<LogCollection>();
        for (LogConfig logConfig : config.getLogConfigList()) {
            for (LogEntity logentity : logConfig.getLogEntities()) {
                LogCollection lc = new LogCollection();
                String collectionName = logentity.getLogCollectionName(analysisdate);
                DBCollection dbc = db.getCollection(collectionName);
                lc.setCollectionName(collectionName);
                lc.setCollectionCnt(dbc.count());
                lc.setLogEntity(logentity);
                colList.add(lc);
            }
        }
        request.setAttribute("analysisdate", analysisdate);
        request.setAttribute("logcollectionList", colList);
        this.dispatch("/WEB-INF/serverlist.jsp", request, response);

    }
}
