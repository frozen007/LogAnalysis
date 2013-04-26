package com.myz.loganalysis.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * OptionsParser
 * 
 * @author zhaomingyu
 *
 * @param <V>
 */
public abstract class OptionsParser<V> implements OptionParseListener<V> {

    protected Map<String, V> optionsMap = new HashMap<String, V>();
    protected ArrayList<OptionParseListener<V>> listeners = null;
    protected String[] optionSeq = null;

    public OptionsParser(String[] optionSeq) {
        this.optionSeq = optionSeq;
    }

    public Map<String, V> getOptionsMap() {
        return optionsMap;
    }
    
    private void resolve() {
        int index = 0;
        String currentOptName = null;
        while (index < optionSeq.length) {
            String element = optionSeq[index];
            int len = element.length();
            if(element.startsWith("--") && len>2) {
                
            } else if(element.startsWith("-") && len>1) {
                
            } else {
                
            }
            index++;
        }
    }

    /**
     * Invoked when "-name optionLiteral" or "--name=optionLiteral" is encountered
     * @param name
     * @param optionLiteral
     */
    protected void optionPair(String name, String optionLiteral) {
        //trigger listeners
        V option = refinePairValue(name, optionLiteral);
        pairEvent(name, option);

        putOptionPair(name, option);
    }

    /**
     * Put an option pair to the map
     * @param name
     * @param option
     */
    protected void putOptionPair(String name, V option) {
        this.optionsMap.put(name, option);
    }

    /**
     * Invoked when "-name" or "--name" is encountered
     * @param name
     */
    protected void optionSwitch(String name) {
        //trigger listeners
        switchEvent(name);

        this.optionsMap.put(name, refineSwitchValue(name));
    }

    /**
     * Convert a optionLiteral to an appropriate Object value
     * @param name
     * @param option
     * @return <V>
     */
    protected abstract V refinePairValue(String name, String optionLiteral);

    /**
     * Create a value for a switch
     * @param name
     * @return <V>
     */
    protected abstract V refineSwitchValue(String name);
    
    public void addListener(OptionParseListener<V> listener) {
        if (listeners == null) {
            listeners = new ArrayList<OptionParseListener<V>>();
        }
        listeners.add(listener);
    }
    
    @Override
    public void pairEvent(String name, V option) {
        if (listeners != null) {
            for (OptionParseListener<V> listener : listeners) {
                listener.pairEvent(name, option);
            }
        }
    }

    @Override
    public void switchEvent(String name) {
        if (listeners != null) {
            for (OptionParseListener<V> listener : listeners) {
                listener.switchEvent(name);
            }
        }
    }

}
