package com.changyou.loganalysis.server.servlet;

import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.changyou.loganalysis.LogAnalysisUtil;
import com.changyou.loganalysis.MongoDBManager;
import com.changyou.loganalysis.config.AnalysisConfigurator;
import com.changyou.loganalysis.config.LogAnalysisConfig;
import com.changyou.loganalysis.config.LogEntity;
import com.changyou.loganalysis.server.bean.LogCollection;
import com.changyou.loganalysis.server.bean.StatisticResult;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class ServerStatisticServlet extends BaseServlet {
    private static int[] QUERY_COST_RANGE = new int[] { 0, 50 };

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        String logCollectionName = request.getParameter("logcollection");
        String logUniqueID  = request.getParameter("loguniqueid");

        if (logCollectionName == null) {
            this.dispathError("logcollection parameter is null", request, response);
            return;
        }

        LogAnalysisConfig config = AnalysisConfigurator.getInstance().getConfig();
        LogEntity logentity = config.getLogEntityByUniqueID(logUniqueID);
        if (logentity == null) {
            this.dispathError("cannot find logentity by uniqueID:" + logUniqueID, request, response);
            return;
        }

        int queryMinCost = LogAnalysisUtil.parseInt(request.getParameter("mincost"), Integer.MIN_VALUE);
        int queryMaxCost = LogAnalysisUtil.parseInt(request.getParameter("maxcost"), Integer.MIN_VALUE);
        request.setAttribute("mincost", queryMinCost == Integer.MIN_VALUE ? QUERY_COST_RANGE[0] : queryMinCost);
        request.setAttribute("maxcost", queryMaxCost == Integer.MIN_VALUE ? QUERY_COST_RANGE[1] + 1 : queryMaxCost);
        
        DB logdb = MongoDBManager.getInstance().getLogDB();
        DBCollection dbc = logdb.getCollection(logCollectionName);

        BasicDBObject condObj = new BasicDBObject();
        if (queryMinCost > 0) {
            condObj.put("$gte", queryMinCost);
        }
        if (queryMaxCost > 0 && queryMaxCost <= QUERY_COST_RANGE[1]) {
            condObj.put("$lt", queryMaxCost);
        }

        BasicDBObject matchObj = null;
        if(!condObj.isEmpty()) {
            matchObj = new BasicDBObject("$match", new BasicDBObject("cost", condObj));
        }
        BasicDBObject groupObj = new BasicDBObject("$group", 
                                              new BasicDBObject("_id", "$action_url")
                                                        .append("cnt", new BasicDBObject("$sum", 1))
                                                        .append("avg", new BasicDBObject("$avg", "$cost")));
        AggregationOutput aggOut = null;
        if (matchObj != null) {
            aggOut = dbc.aggregate(matchObj, groupObj, new BasicDBObject("$sort", new BasicDBObject("avg", -1)));
        } else {
            aggOut = dbc.aggregate(groupObj, new BasicDBObject("$sort", new BasicDBObject("avg", -1)));
        }        

        ArrayList<StatisticResult> resultList = new ArrayList<StatisticResult>();
        Iterator<DBObject> itO = aggOut.results().iterator();
        while (itO.hasNext()) {
            DBObject dbo = itO.next();
            StatisticResult result = new StatisticResult();
            result.setRequestUrl((String) dbo.get("_id"));
            result.setCount(Long.parseLong(dbo.get("cnt").toString()));
            result.setAvgCost(LogAnalysisUtil.round(Double.parseDouble(dbo.get("avg").toString()), 2));
            resultList.add(result);
        }

        LogCollection lc = new LogCollection();
        lc.setCollectionName(logCollectionName);
        lc.setLogEntity(config.getLogEntityByUniqueID(""));
        request.setAttribute("statisticresultList", resultList);
        request.setAttribute("logentity", logentity);
        request.setAttribute("logcollection", logCollectionName);
        request.setAttribute("loguniqueid", logUniqueID);
        request.setAttribute("QUERY_COST_RANGE", QUERY_COST_RANGE);

        this.dispatch("/WEB-INF/serverstatistic.jsp", request, response);
    }

}
