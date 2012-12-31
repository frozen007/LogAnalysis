package com.myz.loganalysis.mongo;

import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.myz.loganalysis.MongoDBManager;

public class TestMongo extends TestCase {

    public void test001() throws Exception {
        try{
            Mongo m = new Mongo("10.127.1.172");
            for(String dbn : m.getDatabaseNames()) {
                System.out.println(dbn);
            }
        }catch(Exception e) {
            
        }
    }

    public void test002() throws Exception {
        try{
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
            
            DBCollection tmp = db.getCollection("shit");
            System.out.println(tmp.count());
        }catch(Exception e) {
            
        }
        
    }

    public void test003() {
        String upDate = "20121021";
        DB logdb = MongoDBManager.getInstance().getLogDB();
        Set<String> colNames = logdb.getCollectionNames();
        for(String colName : colNames) {
            if(colName.startsWith("log2012")) {
                String strLogDate = colName.substring(3, colName.indexOf('.'));
                if(strLogDate.compareTo(upDate) <=0) {
                    System.out.println(colName + " dropped");
                    //logdb.getCollection(colName).dropIndexes();
                    //logdb.getCollection(colName).drop();
                }
            }
            
        }
    }
}
