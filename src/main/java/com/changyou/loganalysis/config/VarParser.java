package com.changyou.loganalysis.config;

public interface VarParser {

    public String resolveVar(String varName, String defaultValue);

    public String resolveVar(String varName);

}
