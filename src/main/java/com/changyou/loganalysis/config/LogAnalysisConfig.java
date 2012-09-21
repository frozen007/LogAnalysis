package com.changyou.loganalysis.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.changyou.loganalysis.LogAnalysisUtil;

public class LogAnalysisConfig {

    private String scriptExec = "";
    private String logScript = "";
    private String errScript = "";
    private String reportPath = "report";
    private int threadPoolSize = 20;
    private HashMap<String, ProfileConfig> profileMap = new HashMap<String, ProfileConfig>();
    private ArrayList<LogConfig> logConfigList = new ArrayList<LogConfig>();
    private LinkedHashMap<String, LogEntity> logEntityMap = new LinkedHashMap<String, LogEntity>();

    public String getScriptExec() {
        return scriptExec;
    }
    public void setScriptExec(String scriptExec) {
        this.scriptExec = LogAnalysisUtil.substVars(scriptExec);
    }
    public String getLogScript() {
        return logScript;
    }
    public void setLogScript(String scriptFile) {
        this.logScript = LogAnalysisUtil.substVars(scriptFile);
    }
    public String getErrScript() {
        return errScript;
    }
    public void setErrScript(String errScript) {
        this.errScript = LogAnalysisUtil.substVars(errScript);
    }
    public String getReportPath() {
        return reportPath;
    }
    public void setReportPath(String reportPath) {
        this.reportPath = LogAnalysisUtil.substVars(reportPath);
    }
    public int getThreadPoolSize() {
        return threadPoolSize;
    }
    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    public HashMap<String, ProfileConfig> getProfiles() {
        return profileMap;
    }
    
    public void addProfileMap(ProfileConfig profile) {
        profileMap.put(profile.getName(), profile);
    }

    public List<LogConfig> getLogConfigList() {
        return this.logConfigList;
    }
    
    public void addLogConfig(LogConfig lc) {
        this.logConfigList.add(lc);
        int counter = logEntityMap.size();
        for (LogEntity entity : lc.getLogEntities()) {
            String uniqueID = String.valueOf(counter);
            entity.setUniqueID(uniqueID);
            this.logEntityMap.put(uniqueID, entity);
            counter++;
        }
    }

    public LogEntity getLogEntityByUniqueID(String uniqueID) {
        return this.logEntityMap.get(uniqueID);
    }
}
