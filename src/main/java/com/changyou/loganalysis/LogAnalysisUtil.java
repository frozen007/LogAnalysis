package com.changyou.loganalysis;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.changyou.loganalysis.tool.LogVarParser;
import com.changyou.loganalysis.tool.VarParser;

public class LogAnalysisUtil {
    private static Logger logger = Logger.getLogger(LogAnalysisUtil.class);

    public static final String PARAM_KEY_ANALYSISDATE = "date";
    public static final String PARAM_KEY_ANALYSISCONFIG = "config";
    public static final String PARAM_KEY_DAEMON = "daemon";

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    private static VarParser defaultParser = new LogVarParser(new HashMap<String,String>());

    public static boolean isNull(String str) {
        if (str == null) {
            return true;
        }
        if ("".equals(str)) {
            return true;
        }
        return false;
    }

    public static double round(double d, int scale) {
        double pow = Math.pow(10, scale);
        long tmp = (long) (d * pow);
        double result = (double) tmp / pow;
        return result;
    }

    public static HashMap<String, String> parseParam(String[] args) {
        HashMap<String, String> paraMap = new HashMap<String, String>();
        String analysisDateStr = null;
        if (args != null && args.length > 0) {
            int index = 0;
            while (index < args.length) {
                if (("-" + PARAM_KEY_ANALYSISDATE).equals(args[index])) {
                    index++;
                    if (index >= args.length) {
                        throw new IllegalArgumentException("'" + PARAM_KEY_ANALYSISDATE + "' has no value");
                    }
                    analysisDateStr = args[index];
                    paraMap.put(PARAM_KEY_ANALYSISDATE, analysisDateStr);
                } else if (("-" + PARAM_KEY_ANALYSISCONFIG).equals(args[index])) {
                    index++;
                    if (index >= args.length) {
                        throw new IllegalArgumentException("'" + PARAM_KEY_ANALYSISCONFIG + "' has no value");
                    }
                    paraMap.put(PARAM_KEY_ANALYSISCONFIG, args[index]);
                } else if (("-" + PARAM_KEY_DAEMON).equals(args[index])) {
                    paraMap.put(PARAM_KEY_DAEMON, "true");
                }
                index++;

            }
        }

        if (analysisDateStr == null) {
            analysisDateStr = getPreAnalysisDate();
            paraMap.put(PARAM_KEY_ANALYSISDATE, analysisDateStr);
        }

        return paraMap;
    }

    public static String getPreAnalysisDate() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -1);
        return sdf.format(c.getTime());
    }
    
    public static String parseLogFilename(String filename, VarParser parser) {
        String result = filename;
        try {
            result = parser.substVars(result);
            result = substWildcards(result);
        } catch (Exception e) {
            logger.error("error when parsing log filename:" + result, e);
        }
        return result;
    }

    public static String mergeDateString(Date date, String template) {
        SimpleDateFormat sdf = new SimpleDateFormat(template);
        return sdf.format(date);
    }

    static String DELIM_START = "${";
    static char DELIM_STOP = '}';
    static int DELIM_START_LEN = 2;
    static int DELIM_STOP_LEN = 1;

    public static String substVars(String val) {
        return defaultParser.substVars(val);
    }

    public static String substWildcards(String val) {
        StringBuilder sbuf = new StringBuilder();

        int ib = 0, ie = 0;

        int length = val.length();
        while (true) {
            if (ie >= length) {
                break;
            }
            ie = val.indexOf("*", ie);
            if (ie == -1) {
                sbuf.append(val.substring(ib));
                break;
            }
            sbuf.append("\\Q").append(val.substring(ib, ie)).append("\\E").append(".*");
            ie++;
            ib = ie;
        }
        return sbuf.toString();
    }
}
