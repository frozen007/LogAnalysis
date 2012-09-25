package com.changyou.loganalysis.tool;

public interface VarParser {

    public String DELIM_START = "${";
    public char DELIM_STOP = '}';
    public int DELIM_START_LEN = 2;
    public int DELIM_STOP_LEN = 1;

    public String resolveVar(String varName, String defaultValue);

    public String resolveVar(String varName);

    public String substVars(String val) throws IllegalArgumentException;
}
