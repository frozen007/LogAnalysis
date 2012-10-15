package com.changyou.loganalysis.server.servlet;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.changyou.loganalysis.LogAnalysisUtil;
import com.changyou.loganalysis.MongoDBManager;
import com.changyou.loganalysis.config.AnalysisConfigurator;
import com.changyou.loganalysis.config.LogAnalysisConfig;
import com.changyou.loganalysis.config.LogConfig;
import com.changyou.loganalysis.config.LogEntity;
import com.changyou.loganalysis.server.bean.LogCollection;
import com.mongodb.DB;
import com.mongodb.DBCollection;

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
