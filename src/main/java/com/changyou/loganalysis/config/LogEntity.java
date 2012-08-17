package com.changyou.loganalysis.config;

import java.io.File;
import java.util.Date;

public abstract class LogEntity {

    protected String dir = "";
    protected String memo;
    protected String logFormat;
    protected String logSeparator;
    protected String logCostunit;
    protected String errFile;

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
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

    public abstract File[] getLogFiles(String parentPath);
}
