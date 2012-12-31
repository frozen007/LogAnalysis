package com.myz.loganalysis.server.bean;

public class StatisticResult {

    private String requestUrl;
    private long count;
    private double avgCost;

    public String getRequestUrl() {
        return requestUrl;
    }
    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }
    public long getCount() {
        return count;
    }
    public void setCount(long count) {
        this.count = count;
    }
    public double getAvgCost() {
        return avgCost;
    }
    public void setAvgCost(double avgCost) {
        this.avgCost = avgCost;
    }
}
