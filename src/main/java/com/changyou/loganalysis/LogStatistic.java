package com.changyou.loganalysis;

import java.util.HashMap;

public class LogStatistic {
    public String servername;
    public String logfile;

    // 统计数据
    public long totalrecord; // 总记录数

    public long cost0_1s; // 响应时间<1s
    public long cost1_3s; // 响应时间>=1s,<3s
    public long cost3_10s; // 响应时间>=3s,<10s
    public long cost10s; // 响应时间>=10s

    public long status500; // 500错误页面量
    public long exceptioncnt; // exception数量

    public LogStatistic(String servername, HashMap<String, String> resultMap) {
        this.servername = servername;
        logfile = resultMap.get("logfile");
        totalrecord = parseLong(resultMap.get("totalrecord"), 0);
        cost0_1s = parseLong(resultMap.get("cost0_1s"), 0);
        cost1_3s = parseLong(resultMap.get("cost1_3s"), 0);
        cost3_10s = parseLong(resultMap.get("cost3_10s"), 0);
        cost10s = parseLong(resultMap.get("cost10s"), 0);
        status500 = parseLong(resultMap.get("status500"), 0);
        exceptioncnt = parseLong(resultMap.get("exceptioncnt"), 0);
    }

    private long parseLong(String str, long defaultL) {
        long result = defaultL;
        try {
            result = Long.parseLong(str);
        } catch (Exception e) {

        }
        return result;
    }
}
