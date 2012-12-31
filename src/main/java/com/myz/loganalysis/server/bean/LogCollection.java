package com.myz.loganalysis.server.bean;

import com.myz.loganalysis.config.LogEntity;

public class LogCollection {
    private String collectionName;
    private long collectionCnt;
    private LogEntity logEntity;

    public String getCollectionName() {
        return collectionName;
    }
    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }
    public long getCollectionCnt() {
        return collectionCnt;
    }
    public void setCollectionCnt(long collectionCnt) {
        this.collectionCnt = collectionCnt;
    }
    public LogEntity getLogEntity() {
        return logEntity;
    }
    public void setLogEntity(LogEntity logEntity) {
        this.logEntity = logEntity;
    }
}
