package com.changyou.loganalysis.config;

import java.io.File;

import org.apache.commons.digester3.Digester;
import org.apache.log4j.Logger;

public class AnalysisConfigurator {
    private static Logger logger = Logger.getLogger(AnalysisConfigurator.class);

    private static AnalysisConfigurator configInstance = null;

    private static String XML_CONFIG_FILE = "config.xml";

    private LogAnalysisConfig config = null;

    private AnalysisConfigurator() {
        this(XML_CONFIG_FILE);
    }

    private AnalysisConfigurator(String configFile) {
        try {
            init(configFile);
        } catch (Exception e) {
            logger.error("error when loading config", e);
        }
    }

    private void init(String configFile) throws Exception {
        config = digestConfig(configFile);
    }

    public static AnalysisConfigurator getInstance(String configFile) {
        if (configInstance == null) {
            synchronized (AnalysisConfigurator.class) {
                if (configInstance == null) {
                    configInstance = new AnalysisConfigurator(configFile);
                }
            }
        }

        return configInstance;

    }

    public static AnalysisConfigurator getInstance() {
        if (configInstance == null) {
            synchronized (AnalysisConfigurator.class) {
                if (configInstance == null) {
                    configInstance = new AnalysisConfigurator();
                }
            }
        }

        return configInstance;
    }

    public LogAnalysisConfig getConfig() {
        return config;
    }

    protected LogAnalysisConfig digestConfig(String configFile) throws Exception {
        Digester d = new Digester();
        d.addObjectCreate("log-analysis", "com.changyou.loganalysis.config.LogAnalysisConfig");
        d.addSetNestedProperties("log-analysis/server", new String[] { "port" }, new String[] { "serverPort" });
        d.addSetNestedProperties("log-analysis/mongodb", new String[] { "host", "port" }, new String[] { "mongodbHost",
                "mongodbPort" });
        d.addSetNestedProperties("log-analysis/analysis-worker", new String[] { "script-exec", "log-script",
                "err-script", "report-path", "thread-pool-size", "mongodb-host", "mongodb-port" }, new String[] {
                "scriptExec", "logScript", "errScript", "reportPath", "threadPoolSize", "mongodbHost", "mongodbPort" });

        String pattern = "log-analysis/profile";
        d.addObjectCreate(pattern, "com.changyou.loganalysis.config.ProfileConfig");
        d.addSetProperties(pattern);
        d.addSetNestedProperties(pattern, new String[] { "log-format", "log-separator", "log-costunit" }, new String[] {
                "logFormat", "logSeparator", "logCostunit" });
        d.addSetNext(pattern, "addProfileMap");

        pattern = "log-analysis/log-config";
        d.addObjectCreate(pattern, "com.changyou.loganalysis.config.LogConfig");
        d.addSetProperties(pattern, new String[] { "name", "memo", "profile", "parent-path" }, new String[] { "name",
                "memo", "profile", "parentPath" });
        d.addSetNext(pattern, "addLogConfig");

        pattern = "log-analysis/log-config/log";
        d.addObjectCreate(pattern, "com.changyou.loganalysis.config.LogFile");
        d.addSetProperties(pattern, new String[] { "file", "err-file", "err-file-pattern", "memo" }, new String[] {
                "file", "errFile", "errFilePattern", "memo" });
        d.addSetNestedProperties(pattern, new String[] { "log-format", "log-separator", "log-costunit" }, new String[] {
                "logFormat", "logSeparator", "logCostunit" });
        d.addSetNext(pattern, "addLogEntity");

        pattern = "log-analysis/log-config/log-group";
        d.addObjectCreate(pattern, "com.changyou.loganalysis.config.LogGroup");
        d.addSetProperties(
                           pattern,
                           new String[] { "dir", "file-pattern", "err-file", "err-file-pattern", "memo" },
                           new String[] { "dir", "filePattern", "errFile", "errFilePattern", "memo" });
        d.addSetNestedProperties(pattern, new String[] { "log-format", "log-separator", "log-costunit" }, new String[] {
                "logFormat", "logSeparator", "logCostunit" });
        d.addSetNext(pattern, "addLogEntity");

        LogAnalysisConfig config = d.parse(new File(configFile));

        return config;
    }
}
