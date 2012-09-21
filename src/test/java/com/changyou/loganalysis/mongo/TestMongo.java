package com.changyou.loganalysis.mongo;

import junit.framework.TestCase;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;

public class TestMongo extends TestCase {

    public void test001() throws Exception {
        Mongo m = new Mongo();
        DB db = m.getDB("logdb");
        DBCollection dbc = db.getCollection("log20120906.logstat0");
        DBCursor cur = dbc.find();
        while(cur.hasNext()) {
            System.out.println(cur.next());
        }
    }
}
