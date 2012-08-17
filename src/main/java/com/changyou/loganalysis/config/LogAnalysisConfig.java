package com.changyou.loganalysis.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LogAnalysisConfig {

    private String scriptExec = "";
    private String scriptFile = "";
    private String reportPath = "report";
    private int threadPoolSize = 20;
    private HashMap<String, ProfileConfig> profileMap = new HashMap<String, ProfileConfig>();
    private ArrayList<LogConfig> logConfigList = new ArrayList<LogConfig>();

    public String getScriptExec() {
        return scriptExec;
    }
    public void setScriptExec(String scriptExec) {
        this.scriptExec = scriptExec;
    }
    public String getScriptFile() {
        return scriptFile;
    }
    public void setScriptFile(String scriptFile) {
        this.scriptFile = scriptFile;
    }
    public String getReportPath() {
        return reportPath;
    }
    public void setReportPath(String reportPath) {
        this.reportPath = reportPath;
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
    }
}
