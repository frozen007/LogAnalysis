package com.changyou.loganalysis;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

public class LogAnalysisMonitor {
    private static Logger logger = Logger.getLogger(LogAnalysisMonitor.class);

    private static LogAnalysisMonitor instance = null;

    private CountDownLatch countDown = null;
    private Map<String, LogStatistic> statisticMap = null;

    private LogAnalysisMonitor(int count) {
        countDown = new CountDownLatch(count);
        statisticMap = new TreeMap<String, LogStatistic>();
    }

    public static LogAnalysisMonitor initialize(int countDown) {
        if (instance == null) {
            synchronized (LogAnalysisMonitor.class) {
                if (instance == null) {
                    instance = new LogAnalysisMonitor(countDown);
                }
            }
        }

        return instance;
    }

    public static LogAnalysisMonitor getInstance() throws Exception {
        if (instance == null) {
            throw new Exception("LogAnalysisCountDown not initiated.");
        }
        return instance;
    }

    public void countDown() {
        countDown.countDown();
    }

    public void waitForFinish() {
        try {
            countDown.await();
        } catch (Exception e) {
            logger.error("error when waitForFinish", e);
        }
    }

    public void addLogStatistic(String servername, HashMap<String, String> resultMap) {
        LogStatistic statistic = statisticMap.get(servername);
        if (statistic == null) {
            synchronized (statisticMap) {
                statistic = statisticMap.get(servername);
                if (statistic == null) {
                    statistic = new LogStatistic(servername, resultMap);
                    statisticMap.put(servername, statistic);
                    return;
                }
            }
        }
        statistic.mergeResult(resultMap);
    }

    public Map<String, LogStatistic> getStatisticMap() {
        return statisticMap;
    }
}
