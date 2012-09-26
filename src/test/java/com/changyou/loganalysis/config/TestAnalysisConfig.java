package com.changyou.loganalysis.config;

import junit.framework.TestCase;

public class TestAnalysisConfig extends TestCase {

    public void test001() {
        
    }

    public void test003() throws Exception {
        LogAnalysisConfig config = AnalysisConfigurator.getInstance("test-config.xml").getConfig();

        System.out.println(config);
    }

    public void test004() {
        String str1 = new String("HR招聘系统前台_64_128");
        String str2 = new String("HR招聘系统前台_64_128");
        System.out.println(str1.hashCode());
        System.out.println(str2.hashCode());
    }

    public void test005() {
        LogAnalysisConfig config = AnalysisConfigurator.getInstance().getConfig();
        for(LogEntity log : config.logEntityMap.values()) {
            System.out.println(log.uniqueID + ":" + log.memo);
        }
    }
}
