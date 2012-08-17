package com.changyou.loganalysis.config;

import java.io.File;
import java.util.Date;

import junit.framework.TestCase;

import com.changyou.loganalysis.LogAnalysisUtil;

public class TestAnalysisConfig extends TestCase {

    public void test001() {
        System.out.println(LogAnalysisUtil.mergeDateString(new Date(), "'154_113/protect_acc_log_'yyyy-MM-dd"));
    }

    public void test003() throws Exception {
        LogAnalysisConfig config = AnalysisConfigurator.getInstance().getConfig();

        System.out.println(config);
    }

    public void testLogGroup() throws Exception {
        System.setProperty(LogAnalysisUtil.PARAM_KEY_ANALYSISDATE, "20120716");
        LogGroup lg = new LogGroup();
        lg.dir = "64_175";
        lg.filePattern = "host.access.log.${date:yyyyMMdd}*";

        File[] logs = lg.getLogFiles("D:/mydoc/work/loganalisys/log/nginxlog");
        for (File log : logs) {
            System.out.println(log.getAbsolutePath());
        }
    }

    public void testParseFileName() {
        System.setProperty(LogAnalysisUtil.PARAM_KEY_ANALYSISDATE, "20120817");
        String filePattern = "host.access.log.${date:yyyyMMdd}*";
        LogAnalysisUtil.substVars(filePattern, new VarParser() {

            public String resolveVar(String varName, String defaultValue) {
                System.out.println(varName);
                return "";
            }

            public String resolveVar(String varName) {
                return resolveVar(varName, "");
            }
        });

        String sample = "host.access.log.20120817";
        String samplePattern = "\\Qhost.access.log.20120817\\E.*";
        System.out.println(sample.matches(samplePattern));

        String sample2 = sample + "*hehe*.2012*";
        System.out.println("sample2=" + sample2);
        System.out.println(LogAnalysisUtil.substWildcards(sample2));

        String sample3 = "host.access.log.${date:yyyyMMdd}*";
        sample3 = LogAnalysisUtil.substVars(sample3);
        System.out.println(sample3);
        sample3 = LogAnalysisUtil.substWildcards(sample3);
        System.out.println(sample3);
        System.out.println(sample.matches(sample3));
    }
}
