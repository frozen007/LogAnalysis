package com.changyou.loganalysis;

import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import com.changyou.loganalysis.config.AnalysisConfigurator;
import com.changyou.loganalysis.config.LogAnalysisConfig;
import com.mongodb.DB;
import com.mongodb.Mongo;

public class MongoDBManager {
    private static Logger logger = Logger.getLogger(MongoDBManager.class);

    private static MongoDBManager manager = null;

    private Mongo mongo = null;

    private MongoDBManager() {
        LogAnalysisConfig config = AnalysisConfigurator.getInstance().getConfig();
        String mongoHost = config.getMongodbHost();
        int mongoPort = config.getMongodbPort();
        try {
            mongo = new Mongo(mongoHost, mongoPort);
        } catch (UnknownHostException e) {
            logger.error("error when initializing mongoDB", e);
        }
    }

    public static MongoDBManager getInstance() {
        if (manager == null) {
            synchronized (MongoDBManager.class) {
                if (manager == null) {
                    manager = new MongoDBManager();
                }
            }
        }
        return manager;
    }

    public Mongo getMongo() {
        return mongo;
    }

    public DB getLogDB() {
        return mongo.getDB("logdb");
    }
}
