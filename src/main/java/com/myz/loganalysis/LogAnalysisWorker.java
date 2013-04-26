package com.myz.loganalysis;

import java.io.File;

import org.apache.log4j.Logger;

import com.myz.loganalysis.config.AnalysisConfigurator;
import com.myz.loganalysis.config.LogEntity;

public class LogAnalysisWorker extends AnalysisWorker {
    private static Logger logger = Logger.getLogger(LogAnalysisWorker.class);

    private static String LOG_SCRIPT = AnalysisConfigurator.getInstance().getConfig().getLogScript();
    private static String REC_LEVEL = AnalysisConfigurator.getInstance().getConfig().getRecLevel();

    private static String MONGODB_HOST = AnalysisConfigurator.getInstance().getConfig().getMongodbHost();
    private static int MONGODB_PORT = AnalysisConfigurator.getInstance().getConfig().getMongodbPort();

    private static String SCRIPT_FILE_DIR = LOG_SCRIPT.substring(0, LOG_SCRIPT.lastIndexOf("/"));

    private String logCollection;
    private String logformat;
    private String logseperator;
    private String logcostunit;
    private String logreclevel = REC_LEVEL;

    public LogAnalysisWorker(LogEntity logentity, String file, String logCollection, String logformat,
            String logseperator, String logcostunit, String logreclevel) {
        super(logentity, file);
        this.logCollection = logCollection;
        this.logformat = logformat;
        this.logseperator = logseperator;
        this.logcostunit = logcostunit;
        if (logreclevel != null) {
            this.logreclevel = logreclevel;
        }
    }

    @Override
    public Process createAnalysisProcess() throws Exception {
        logger.debug("file=" + file + ", logformat=\"" + logformat + "\", logseperator=\"" + logseperator
                + "\", logcostunit=" + logcostunit);
        String[] cmdArr = new String[] { SCRIPT_EXEC, LOG_SCRIPT, "-format", logformat, "-sep", logseperator, "-cu",
                logcostunit, "-reclevel", logreclevel, "-mongohost", MONGODB_HOST, "-mongoport", String.valueOf(MONGODB_PORT), "-mongocol",
                logCollection, file };
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmdArr, null, new File(SCRIPT_FILE_DIR));
        } catch (Exception e) {
            throw e;
        }

        return process;
    }
}
