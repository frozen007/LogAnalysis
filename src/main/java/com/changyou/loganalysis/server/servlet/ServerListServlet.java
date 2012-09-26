package com.changyou.loganalysis.server.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.changyou.loganalysis.LogAnalysisUtil;
import com.changyou.loganalysis.MongoDBManager;
import com.changyou.loganalysis.config.AnalysisConfigurator;
import com.changyou.loganalysis.config.LogAnalysisConfig;
import com.changyou.loganalysis.config.LogConfig;
import com.changyou.loganalysis.config.LogEntity;
import com.changyou.loganalysis.server.bean.LogCollection;
import com.mongodb.DB;
import com.mongodb.DBCollection;

public class ServerListServlet extends HttpServlet {
    private static Logger logger = Logger.getLogger(ServerListServlet.class);

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        String analysisdate = request.getParameter("analysisdate");
        if (analysisdate == null) {
            analysisdate = LogAnalysisUtil.getPreAnalysisDate();
        }
        logger.info("[" + new Date() + "] incoming request ip=" + request.getRemoteAddr() + " analysisdate=" + analysisdate);
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
        request.setAttribute("logcollections", colList);
        try {
            request.getRequestDispatcher("/WEB-INF/serverlist.jsp").forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
