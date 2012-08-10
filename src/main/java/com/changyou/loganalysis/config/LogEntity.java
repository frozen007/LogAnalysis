package com.changyou.loganalysis.config;

public class LogEntity {

    private String file;
    private String memo;
    private String logFormat;
    private String logSeparator;
    private String logCostunit;
    private String errFile;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
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

    public String getErrFile() {
        return errFile;
    }

    public void setErrFile(String errFile) {
        this.errFile = errFile;
    }

}
