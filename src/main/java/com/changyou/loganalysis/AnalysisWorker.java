package com.changyou.loganalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.changyou.loganalysis.config.AnalysisConfigurator;

public class AnalysisWorker implements Runnable {
    private static Logger logger = Logger.getLogger(AnalysisWorker.class);

    private static String SCRIPT_EXEC = AnalysisConfigurator.getInstance().getConfig().getScriptExec();

    private static String SCRIPT_FILE = AnalysisConfigurator.getInstance().getConfig().getScriptFile();

    private static String SCRIPT_FILE_DIR = SCRIPT_FILE.substring(0, SCRIPT_FILE.lastIndexOf("/"));

    private String servername;
    private String logfile;
    private String logformat;
    private String logseperator;
    private String logcostunit;
    private String errlogfile;

    private LogStatistic statistic;

    public AnalysisWorker(String servername, String logfile, String logformat, String logseperator, String logcostunit, String errlogfile) {
        this.servername = servername;
        this.logfile = logfile;
        this.logformat = logformat;
        this.logseperator = logseperator;
        this.logcostunit = logcostunit;
        this.errlogfile = errlogfile;
    }

    public void run() {
        String[] cmdArr = new String[] { SCRIPT_EXEC, SCRIPT_FILE, logfile, logformat, logseperator, logcostunit, errlogfile };
        logger.info("Analyzing log file:" + logfile);
        logger.debug("logfile=" + logfile + ", logformat=\"" + logformat + "\", logseperator=\"" + logseperator
                + "\", logcostunit=" + logcostunit);
        try {
            Process process = Runtime.getRuntime().exec(cmdArr, null, new File(SCRIPT_FILE_DIR));
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            HashMap<String, String> resultMap = new HashMap<String, String>();
            String line = reader.readLine();
            while (line != null) {
                if (!"".equals(line)) {
                    String[] arr = line.split("=");
                    if (arr != null && arr.length == 2) {
                        resultMap.put(arr[0], arr[1]);
                    }
                }

                logger.debug(line);

                line = reader.readLine();
                
            }

            statistic = new LogStatistic(servername, resultMap);

            logger.info("Analysis completed for log file:" + logfile);
        } catch (Exception e) {
            logger.error("error when analyzing log file:" + logfile, e);
        }

    }

    public LogStatistic getStatistic() {
        return statistic;
    }
}
