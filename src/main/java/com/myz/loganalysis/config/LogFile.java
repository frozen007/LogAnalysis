package com.myz.loganalysis.config;

import java.io.File;

import com.myz.loganalysis.LogAnalysisUtil;
import com.myz.loganalysis.tool.VarParser;

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
