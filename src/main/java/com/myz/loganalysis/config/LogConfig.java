package com.myz.loganalysis.config;

import java.util.ArrayList;
import java.util.List;

public class LogConfig {

    private String name = "";
    private String memo = "";
    private String profile = "";
    private String parentPath = "";
    private List<LogEntity> logList = new ArrayList<LogEntity>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public List<LogEntity> getLogEntities() {
        return logList;
    }
    
    public void addLogEntity(LogEntity log) {
        this.logList.add(log);
    }
}
