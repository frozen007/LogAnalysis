package com.changyou.loganalysis.config;

public class ProfileConfig {
    private String name = "";
    private String logFormat = "";
    private String logSeparator = "";
    private String logCostunit = "";

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getLogFormat() {
        return logFormat;
    }
    public void setLogFormat(String logFormat) {
        this.logFormat = logFormat;
    }
    public String getLogSeparator() {
        return logSeparator;
    }
    public void setLogSeparator(String logSeparator) {
        this.logSeparator = logSeparator;
    }
    public String getLogCostunit() {
        return logCostunit;
    }
    public void setLogCostunit(String logCostunit) {
        this.logCostunit = logCostunit;
    }
}
