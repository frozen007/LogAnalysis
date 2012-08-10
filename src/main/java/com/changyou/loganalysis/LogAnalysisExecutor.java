package com.changyou.loganalysis;

import com.changyou.concurrent.GroupThreadPoolService;

public class LogAnalysisExecutor extends GroupThreadPoolService<AnalysisWorker> {

    public LogAnalysisExecutor(int threadpoolSize) {
        super(threadpoolSize);
    }

    @Override
    protected void processSingle(AnalysisWorker e) {
        e.run();
    }

}
