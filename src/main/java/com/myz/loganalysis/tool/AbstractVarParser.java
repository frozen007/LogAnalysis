package com.myz.loganalysis.tool;

import java.util.Map;

public abstract class AbstractVarParser implements VarParser {

    protected Map<String, String> context = null;

    public AbstractVarParser(Map<String, String> context) {
        this.context = context;
    }

    public String getVarValue(String var) {
        if(context.containsKey(var)) {
            return context.get(var);
        } 
        
        return System.getProperty(var);
    }

    public String substVars(String val) throws IllegalArgumentException {

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
                    String replacement = resolveVar(key, "");

                    if (replacement != null) {
                        // Do variable substitution on the replacement string
                        // such that we can solve "Hello ${x2}" as "Hello p1"
                        // the where the properties are
                        // x1=p1
                        // x2=${x1}
                        String recursiveReplacement = substVars(replacement);
                        sbuf.append(recursiveReplacement);
                    }
                    i = k + DELIM_STOP_LEN;
                }
            }
        }
    }

}
