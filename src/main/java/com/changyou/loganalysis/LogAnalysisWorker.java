package com.changyou.loganalysis;

import java.io.File;

import org.apache.log4j.Logger;

import com.changyou.loganalysis.config.AnalysisConfigurator;

public class LogAnalysisWorker extends AnalysisWorker {
    private static Logger logger = Logger.getLogger(LogAnalysisWorker.class);

    private static String LOG_SCRIPT = AnalysisConfigurator.getInstance().getConfig().getLogScript();

    private static String SCRIPT_FILE_DIR = LOG_SCRIPT.substring(0, LOG_SCRIPT.lastIndexOf("/"));

    private static String analysisDateStr = System.getProperty(LogAnalysisUtil.PARAM_KEY_ANALYSISDATE);

    private String logformat;
    private String logseperator;
    private String logcostunit;

    public LogAnalysisWorker(String servername, String file, String logformat, String logseperator,
            String logcostunit) {
        super(servername, file);
        this.logformat = logformat;
        this.logseperator = logseperator;
        this.logcostunit = logcostunit;

    }

    @Override
    protected Process createAnalysisProcess() throws Exception {
        logger.debug("file=" + file + ", logformat=\"" + logformat + "\", logseperator=\"" + logseperator
                     + "\", logcostunit=" + logcostunit);
        String logCollectionName = "log" + analysisDateStr + "." + servername;
        String[] cmdArr = new String[] { SCRIPT_EXEC, LOG_SCRIPT, logCollectionName, file, logformat, logseperator, logcostunit};
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmdArr, null, new File(SCRIPT_FILE_DIR));
        } catch (Exception e) {
            throw e;
        }


        return process;
    }

}
