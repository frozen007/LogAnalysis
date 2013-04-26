package com.myz.loganalysis.config;

/**
 * OptionParseListener
 * 
 * @author zhaomingyu
 * @param <V>
 */
public interface OptionParseListener<V> {

    /**
     * Invoked when parsing an option pair
     * @param name
     * @param option <V>
     */
    public void pairEvent(String name, V option);

    /**
     * Invoked when parsing a switch option
     * @param name
     */
    public void switchEvent(String name);
}
