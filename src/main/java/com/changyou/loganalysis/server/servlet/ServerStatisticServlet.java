package com.changyou.loganalysis.server.servlet;

import java.util.ArrayList;
import java.util.HashMap;
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
        String sortoption = request.getParameter("sortoption");

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

        BasicDBObject sortObj = getSortObject(sortoption);

        AggregationOutput aggOut = null;
        if (matchObj != null) {
            aggOut = dbc.aggregate(matchObj, groupObj, new BasicDBObject("$sort", sortObj));
        } else {
            aggOut = dbc.aggregate(groupObj, new BasicDBObject("$sort", sortObj));
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
        request.setAttribute("sortoptionMap", getSortOptionMap(sortObj));

        this.dispatch("/WEB-INF/serverstatistic.jsp", request, response);
    }

    public BasicDBObject getSortObject(String sortoption) {
        BasicDBObject sortObj = new BasicDBObject();

        // sortoption:avg^2
        if (LogAnalysisUtil.isNull(sortoption) || sortoption.indexOf('^') == -1) {
            sortObj.put("avg", -1);
            return sortObj;
        }

        String[] arr = sortoption.split("\\^");
        if (arr.length == 1) {
            sortObj.put(arr[0], -1);
        } else {
            // 1 - asc; 2 - desc
            int flag = 1;
            try {
                flag = Integer.parseInt(arr[1]);
                switch (flag) {
                case 1:
                    flag = 1;
                    break;
                case 2:
                    flag = -1;
                    break;
                default:
                    flag = 1;
                }
            } catch (Exception e) {
                flag = 1;
            }

            sortObj.put(arr[0], flag);
        }
        return sortObj;
    }

    public HashMap<String, String> getSortOptionMap(BasicDBObject sortObj) {
        HashMap<String, String> sortOptMap = new HashMap<String, String>();
        for (String key : sortObj.keySet()) {
            int flag = sortObj.getInt(key);
            switch (flag) {
            case 1:
                flag = 1;
                break;
            case -1:
                flag = 2;
                break;
            default:
                flag = 2;
            }

            sortOptMap.put(key, String.valueOf(flag));
        }
        return sortOptMap;
    }
}
