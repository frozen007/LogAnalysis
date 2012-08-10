package com.changyou.loganalysis;

import java.io.File;
import java.util.Date;

import junit.framework.TestCase;

import org.apache.commons.digester3.Digester;

import com.changyou.loganalysis.config.LogAnalysisConfig;

public class TestAnalysisConfig extends TestCase {

    public void test001() {
        System.out.println(LogAnalysisUtil.mergeDateString(new Date(), "'154_113/protect_acc_log_'yyyy-MM-dd"));
    }

    public void test003() throws Exception {
        Digester d = new Digester();
        d.addObjectCreate("log-analysis", "com.changyou.loganalysis.config.LogAnalysisConfig");
        d.addSetNestedProperties("log-analysis/analysis-worker", new String[] { "script-exec", "script-file",
                "thread-pool-size" }, new String[] { "scriptExec", "scriptFile", "threadPoolSize" });

        String pattern = "log-analysis/profile";
        d.addObjectCreate(pattern, "com.changyou.loganalysis.config.ProfileConfig");
        d.addSetProperties(pattern);
        d.addSetNestedProperties(pattern, new String[] { "log-format", "log-separator", "log-costunit" }, new String[] {
                "logFormat", "logSeparator", "logCostunit" });
        d.addSetNext(pattern, "addProfileMap");

        pattern = "log-analysis/log-config";
        d.addObjectCreate(pattern, "com.changyou.loganalysis.config.LogConfig");
        d.addSetProperties(pattern, new String[] { "name", "profile", "parent-path" }, new String[] { "name",
                "profile", "parentPath" });
        d.addSetNext(pattern, "addLogConfig");

        pattern = "log-analysis/log-config/log";
        d.addObjectCreate(pattern, "com.changyou.loganalysis.config.LogEntity");
        d.addSetProperties(pattern, new String[] { "file", "err-file" }, new String[] { "file", "errFile" });
        d.addSetNestedProperties(pattern, new String[] { "log-format", "log-separator", "log-costunit" }, new String[] {
                "logFormat", "logSeparator", "logCostunit" });
        d.addSetNext(pattern, "addLogEntity");

        LogAnalysisConfig config = d.parse(new File("D:/repo/trang-20081028/config.xml"));

        System.out.println(config);
    }

}
