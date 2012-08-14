package com.changyou.loganalysis;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

public class LogAnalysisCountDown {
    private static Logger logger = Logger.getLogger(LogAnalysisCountDown.class);
    private CountDownLatch countDown = null;
    private static LogAnalysisCountDown instance = null;

    private LogAnalysisCountDown(int count) {
        countDown = new CountDownLatch(count);
    }

    public static LogAnalysisCountDown initCountDown(int count) {
        if (instance == null) {
            synchronized (LogAnalysisCountDown.class) {
                if (instance == null) {
                    instance = new LogAnalysisCountDown(count);
                }
            }
        }

        return instance;
    }

    public static LogAnalysisCountDown getInstance() throws Exception {
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
}
