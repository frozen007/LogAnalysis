package com.changyou.loganalysis.config;

import java.io.File;
import java.io.FilenameFilter;

import com.changyou.loganalysis.LogAnalysisUtil;

public abstract class LogEntity {

    protected String dir = "";
    protected String memo;
    protected String logFormat;
    protected String logSeparator;
    protected String logCostunit;
    protected String errFile;
    protected String errFilePattern;

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

    public String getErrFilePattern() {
        return errFilePattern;
    }

    public void setErrFilePattern(String errFilePattern) {
        this.errFilePattern = errFilePattern;
    }

    public abstract File[] getLogFiles(String parentPath);

    public File[] getErrFiles(String parentPath) {
        if(!LogAnalysisUtil.isNull(errFile)) {
            return new File[] { new File(parentPath + "/" + dir + "/" + LogAnalysisUtil.parseLogFilename(errFile)) };
        }

        File dirFile = new File(parentPath + "/" + dir);
        final String resolvedFilePattern = LogAnalysisUtil.parseLogFilename(errFilePattern);
        File[] errfiles = dirFile.listFiles(new FilenameFilter() {

            public boolean accept(File dir, String name) {
                if (name.matches(resolvedFilePattern)) {
                    return true;
                }
                return false;
            }
        });
        return errfiles;
    }
}
