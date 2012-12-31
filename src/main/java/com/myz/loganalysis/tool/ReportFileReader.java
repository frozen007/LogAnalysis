package com.myz.loganalysis.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.HashMap;

import com.myz.loganalysis.LogStatistic;
import com.myz.loganalysis.ReportUtil;

public class ReportFileReader {

    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        ReportFileReader rfReader = new ReportFileReader();
        rfReader.parseReportFiles(args[0]);
    }

    public void parseReportFiles(String path) throws Exception {
        File folder = new File(path);
        File[] reportFiles = folder.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                if (name.endsWith("csv")) {
                    return true;
                }
                return false;
            }
        });
        HashMap<String, LogStatistic> statMap = new HashMap<String, LogStatistic>();
        for (File reportFile : reportFiles) {
            BufferedReader reader = new BufferedReader(new FileReader(reportFile));
            reader.readLine(); // skip first line
            String line = reader.readLine();
            while (line != null) {
                String[] fields = line.split(",");
                LogStatistic stat = getStat(fields);
                if(statMap.containsKey(stat.servername)) {
                    statMap.get(stat.servername).mergeResult(stat);
                } else {
                    statMap.put(stat.servername, stat);
                }
                line = reader.readLine();
            }
        }

        ReportUtil.generateAnalysisXLSReport("report/analysis_template.xls", "report/all.xls", statMap.values());
    }

    private LogStatistic getStat(String[] fields) {
        LogStatistic logstat = new LogStatistic(fields[0], "");
        logstat.cost0_1s=Long.parseLong(fields[1]);
        logstat.cost1_3s = Long.parseLong(fields[3]);
        logstat.cost3_10s = Long.parseLong(fields[5]);
        logstat.cost10s = Long.parseLong(fields[7]);
        logstat.totalrecord = logstat.cost0_1s + logstat.cost1_3s + logstat.cost3_10s + logstat.cost10s;
        logstat.status500 = Long.parseLong(fields[9]);
        logstat.exceptioncnt = Long.parseLong(fields[11]);
        return logstat;
        
    }
}
