package com.myz.loganalysis.tool;

import java.util.Set;

import com.mongodb.DB;
import com.myz.loganalysis.MongoDBManager;

public class LogCleaner {

    /**
     * @param args
     */
    public static void main(String[] args) {
        String upDate = "20130331";
        DB logdb = MongoDBManager.getInstance().getLogDB();
        Set<String> colNames = logdb.getCollectionNames();
        for(String colName : colNames) {
            if(colName.startsWith("log2013")) {
                String strLogDate = colName.substring(3, colName.indexOf('.'));
                if(strLogDate.compareTo(upDate) <=0) {
                    System.out.println(colName + " dropped");
                    /*
                    logdb.getCollection(colName).dropIndexes();
                    logdb.getCollection(colName).drop();
                    */
                }
            }
            
        }

    }

}
