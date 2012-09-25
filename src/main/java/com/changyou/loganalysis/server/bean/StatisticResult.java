package com.changyou.loganalysis.server.bean;

public class StatisticResult {

    private String requestUrl;
    private String count;
    private String avgCost;

    public String getRequestUrl() {
        return requestUrl;
    }
    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }
    public String getCount() {
        return count;
    }
    public void setCount(String count) {
        this.count = count;
    }
    public String getAvgCost() {
        return avgCost;
    }
    public void setAvgCost(String avgCost) {
        this.avgCost = avgCost;
    }
}
