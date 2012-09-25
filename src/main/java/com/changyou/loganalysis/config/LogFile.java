package com.changyou.loganalysis.config;

import java.io.File;

import com.changyou.loganalysis.LogAnalysisUtil;
import com.changyou.loganalysis.tool.VarParser;

public class LogFile extends LogEntity {

    private String file;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public File[] getLogFiles(String parentPath, VarParser parser) {
        return new File[] { new File(parentPath + "/" + dir + "/" + LogAnalysisUtil.parseLogFilename(file, parser)) };
    }

}
