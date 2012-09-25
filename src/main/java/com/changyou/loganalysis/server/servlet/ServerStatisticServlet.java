package com.changyou.loganalysis.server.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.changyou.loganalysis.MongoDBManager;
import com.changyou.loganalysis.server.bean.StatisticResult;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class ServerStatisticServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        String logCollectionName = request.getParameter("logcollection");
        if (logCollectionName == null) {
            request.setAttribute("error_msg", "logcollection parameter is null");
        }
        DB logdb = MongoDBManager.getInstance().getLogDB();
        DBCollection dbc = logdb.getCollection(logCollectionName);
        BasicDBObject obj = new BasicDBObject("$group", 
                                              new BasicDBObject("_id", "$action_url")
                                                        .append("cnt", new BasicDBObject("$sum", 1))
                                                        .append("avg", new BasicDBObject("$avg", "$cost")));
        AggregationOutput aggOut = dbc.aggregate(obj, new BasicDBObject().append("$sort", new BasicDBObject("_id", 1)));

        ArrayList<StatisticResult> resultList = new ArrayList<StatisticResult>();
        Iterator<DBObject> itO = aggOut.results().iterator();
        while(itO.hasNext()) {
            DBObject dbo = itO.next();
            StatisticResult result = new StatisticResult();
            result.setRequestUrl((String) dbo.get("_id"));
            result.setCount(dbo.get("cnt").toString());
            result.setAvgCost(dbo.get("avg").toString());
            resultList.add(result);
        }
        request.setAttribute("statistic_results", resultList);
        try {
            request.getRequestDispatcher("/WEB-INF/serverstatistic.jsp").forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
