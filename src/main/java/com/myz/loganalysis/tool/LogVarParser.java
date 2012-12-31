package com.myz.loganalysis.tool;

import java.text.SimpleDateFormat;
import java.util.Map;

import org.apache.log4j.Logger;

import com.myz.loganalysis.LogAnalysisUtil;

public class LogVarParser extends AbstractVarParser {
    private static Logger logger = Logger.getLogger(LogVarParser.class);

    private static final String VAR_DATE_PREFIX = "date:";
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    
    public LogVarParser(Map<String, String> context) {
        super(context);
    }

    @Override
    public String resolveVar(String varName, String defaultValue) {
        if (varName.startsWith(VAR_DATE_PREFIX)) {
            int index = VAR_DATE_PREFIX.length();
            if (index < varName.length()) {
                String analysisDateStr = (String) getVarValue(LogAnalysisUtil.PARAM_KEY_ANALYSISDATE);
                try {
                    String value = LogAnalysisUtil
                                                  .mergeDateString(sdf.parse(analysisDateStr), varName.substring(index));
                    return value;
                } catch (Exception e) {
                    logger.error("error when resolving var:" + varName, e);
                }
            }
        } else {
            String value = getVarValue(varName);
            if (value != null) {
                return value;
            }

        }
        return defaultValue;
    }

    @Override
    public String resolveVar(String varName) {
        return resolveVar(varName, "");
    }

}
