package com.myz.loganalysis;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.myz.loganalysis.tool.LogVarParser;
import com.myz.loganalysis.tool.VarParser;

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
        String currentParaName = null;
        if (args != null && args.length > 0) {
            int index = 0;
            while (index < args.length) {

                String param = args[index];
                if (param.startsWith("-") && param.length() > 1) {
                    if (currentParaName != null) {
                        paraMap.put(currentParaName, "true");
                    }

                    currentParaName = param.substring(1);
                } else {
                    if (currentParaName != null) {
                        paraMap.put(currentParaName, param);
                        currentParaName = null;
                    }
                }
                index++;
            }
        }

        if (currentParaName != null) {
            paraMap.put(currentParaName, "true");
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

    public static int parseInt(String str, int def) {
        try{
            return Integer.parseInt(str);
        } catch(Exception e) {
            
        }
        return def;
    }

}
