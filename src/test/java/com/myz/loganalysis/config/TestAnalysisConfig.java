package com.myz.loganalysis.config;

import java.util.HashMap;

import com.myz.loganalysis.LogAnalysisUtil;
import com.myz.loganalysis.config.AnalysisConfigurator;
import com.myz.loganalysis.config.LogAnalysisConfig;
import com.myz.loganalysis.config.LogEntity;

import junit.framework.TestCase;

public class TestAnalysisConfig extends TestCase {

    public void test001() {
        String out = LogAnalysisUtil.substWildcards("host.access.log.${date:yyyyMMdd}*dd");
        System.out.println(out);
    }

    public void test003() throws Exception {
        /*
        LogAnalysisConfig config = AnalysisConfigurator.getInstance("test-config.xml").getConfig();

        System.out.println(config);
        */
    }

    public void test004() {
        String str1 = new String("HR招聘系统前台_64_128");
        String str2 = new String("HR招聘系统前台_64_128");
        System.out.println(str1.hashCode());
        System.out.println(str2.hashCode());
    }

    public void test005() {
        /*
        LogAnalysisConfig config = AnalysisConfigurator.getInstance().getConfig();
        for(LogEntity log : config.logEntityMap.values()) {
            System.out.println(log.uniqueID + ":" + log.memo);
        }*/
    }

    public void test006() {
        String[] args = new String[]{"-daemon", "-date", "20121201", "-aflag", "-log", "log1"};
        HashMap<String, String> paraMap = LogAnalysisUtil.parseParam(args);
        for(String key : paraMap.keySet()) {
            System.out.println(key+":"+paraMap.get(key));
        }
    }
}
