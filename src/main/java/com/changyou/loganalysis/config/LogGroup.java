package com.changyou.loganalysis.config;

import java.io.File;
import java.io.FilenameFilter;

import com.changyou.loganalysis.LogAnalysisUtil;

public class LogGroup extends LogEntity {
    protected String filePattern;

    public String getFilePattern() {
        return filePattern;
    }

    public void setFilePattern(String filePattern) {
        this.filePattern = filePattern;
    }

    @Override
    public File[] getLogFiles(String parentPath) {
        File dirFile = new File(parentPath + "/" + dir);
        final String resolvedFilePattern = LogAnalysisUtil.parseLogFilename(filePattern);
        File[] files = dirFile.listFiles(new FilenameFilter() {

            public boolean accept(File dir, String name) {
                if (name.matches(resolvedFilePattern)) {
                    return true;
                }
                return false;
            }
        });

        return files;
    }

}
