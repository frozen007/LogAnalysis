package com.changyou.loganalysis;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogAnalysisUtil {

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

    public static String mergeDateString(Date date, String template) {
        SimpleDateFormat sdf = new SimpleDateFormat(template);
        return sdf.format(date);
    }
}
