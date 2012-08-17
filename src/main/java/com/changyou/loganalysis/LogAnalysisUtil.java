package com.changyou.loganalysis;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.changyou.loganalysis.config.VarParser;

public class LogAnalysisUtil {
    private static Logger logger = Logger.getLogger(LogAnalysisUtil.class);

    public static final String PARAM_KEY_ANALYSISDATE = "date";
    public static final String PARAM_KEY_ANALYSISCONFIG = "config";

    private static final String VAR_DATE_PREFIX = "date:";
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    public static VarParser defaultParser = new VarParser() {

        private HashMap<String, String> cache = new HashMap<String, String>();

        public String resolveVar(String varName, String defaultValue) {
            if (cache.containsKey(varName)) {
                return cache.get(varName);
            }

            if (varName.startsWith(VAR_DATE_PREFIX)) {
                int index = VAR_DATE_PREFIX.length();
                if (index < varName.length()) {
                    String analysisDateStr = System.getProperty(PARAM_KEY_ANALYSISDATE);
                    try {
                        String value = mergeDateString(sdf.parse(analysisDateStr), varName.substring(index));
                        return value;
                    } catch (Exception e) {
                        logger.error("error when resolving var:" + varName, e);
                    }
                }
            } else {
                // get from system property
                String value = System.getProperty(varName);
                if (value != null) {
                    return value;
                }

            }
            return defaultValue;
        }

        public String resolveVar(String varName) {
            return resolveVar(varName, "");
        }
    };

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

    public static void parseParam(String[] args) {
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
                    System.setProperty(PARAM_KEY_ANALYSISDATE, analysisDateStr);
                } else if (("-" + PARAM_KEY_ANALYSISCONFIG).equals(args[index])) {
                    index++;
                    if (index >= args.length) {
                        throw new IllegalArgumentException("'" + PARAM_KEY_ANALYSISCONFIG + "' has no value");
                    }
                    System.setProperty(PARAM_KEY_ANALYSISCONFIG, args[index]);
                }
                index++;

            }
        }

        if (analysisDateStr == null) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, -1);
            analysisDateStr = sdf.format(c.getTime());
            System.setProperty(PARAM_KEY_ANALYSISDATE, analysisDateStr);
        }

    }

    public static String parseLogFilename(String filename) {
        String result = filename;
        try {
            result = substVars(result);
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
        return substVars(val, defaultParser);
    }

    public static String substVars(String val, VarParser parser) throws IllegalArgumentException {

        StringBuilder sbuf = new StringBuilder();

        int i = 0;
        int j, k;

        while (true) {
            j = val.indexOf(DELIM_START, i);
            if (j == -1) {
                // no more variables
                if (i == 0) { // this is a simple string
                    return val;
                } else { // add the tail string which contails no variables and
                         // return the result.
                    sbuf.append(val.substring(i, val.length()));
                    return sbuf.toString();
                }
            } else {
                sbuf.append(val.substring(i, j));
                k = val.indexOf(DELIM_STOP, j);
                if (k == -1) {
                    throw new IllegalArgumentException('"' + val
                            + "\" has no closing brace. Opening brace at position " + j + '.');
                } else {
                    j += DELIM_START_LEN;
                    String key = val.substring(j, k);
                    String replacement = parser.resolveVar(key, "");

                    if (replacement != null) {
                        // Do variable substitution on the replacement string
                        // such that we can solve "Hello ${x2}" as "Hello p1"
                        // the where the properties are
                        // x1=p1
                        // x2=${x1}
                        String recursiveReplacement = substVars(replacement, parser);
                        sbuf.append(recursiveReplacement);
                    }
                    i = k + DELIM_STOP_LEN;
                }
            }
        }
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
