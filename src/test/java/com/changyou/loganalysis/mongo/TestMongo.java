package com.changyou.loganalysis.mongo;

import java.util.Iterator;

import junit.framework.TestCase;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class TestMongo extends TestCase {

    public void test001() throws Exception {
        /*
        Mongo m = new Mongo();
        DB db = m.getDB("logdb");
        DBCollection dbc = db.getCollection("log20120906.logstat0");
        DBCursor cur = dbc.find();
        while(cur.hasNext()) {
            System.out.println(cur.next());
        }*/
    }

    public void test002() throws Exception {
        Mongo m = new Mongo("192.168.56.101");
        DB db = m.getDB("logdb");
        DBCollection dbc = db.getCollection("log20120921.logstat2090294265");
        BasicDBObject obj = new BasicDBObject("$group", 
                                              new BasicDBObject("_id", "$action_url")
                                                        .append("cnt", new BasicDBObject("$sum", 1))
                                                        .append("avg", new BasicDBObject("$avg", "$cost")));
        AggregationOutput aggOut = dbc.aggregate(obj, new BasicDBObject().append("$sort", new BasicDBObject("_id", 1)));
        Iterator<DBObject> itO = aggOut.results().iterator();
        while(itO.hasNext()) {
            System.out.println(itO.next());
        }
        
        
        
    }
}
