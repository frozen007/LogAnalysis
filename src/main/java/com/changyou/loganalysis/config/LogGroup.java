package com.changyou.loganalysis.config;

import java.io.File;
import java.io.FilenameFilter;

import com.changyou.loganalysis.LogAnalysisUtil;
import com.changyou.loganalysis.tool.VarParser;

public class LogGroup extends LogEntity {
    protected String filePattern;

    public String getFilePattern() {
        return filePattern;
    }

    public void setFilePattern(String filePattern) {
        this.filePattern = filePattern;
    }

    @Override
    public File[] getLogFiles(String parentPath, VarParser parser) {
        File dirFile = new File(parentPath + "/" + dir);
        final String resolvedFilePattern = LogAnalysisUtil.parseLogFilename(filePattern, parser);
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
