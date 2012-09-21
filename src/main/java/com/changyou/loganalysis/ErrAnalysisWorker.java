package com.changyou.loganalysis;

import java.io.File;

import com.changyou.loganalysis.config.AnalysisConfigurator;
import com.changyou.loganalysis.config.LogEntity;

public class ErrAnalysisWorker extends AnalysisWorker {

    private static String ERR_SCRIPT = AnalysisConfigurator.getInstance().getConfig().getErrScript();

    private static String SCRIPT_FILE_DIR = ERR_SCRIPT.substring(0, ERR_SCRIPT.lastIndexOf("/"));

    public ErrAnalysisWorker(LogEntity logentity, String file) {
        super(logentity, file);
    }

    @Override
    protected Process createAnalysisProcess() throws Exception {

        Process process = null;
        String[] cmdArr = new String[] { SCRIPT_EXEC, ERR_SCRIPT, file};
        try {
            process = Runtime.getRuntime().exec(cmdArr, null, new File(SCRIPT_FILE_DIR));
        } catch (Exception e) {
            throw e;
        }


        return process;

    }

}
