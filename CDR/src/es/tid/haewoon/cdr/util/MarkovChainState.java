package es.tid.haewoon.cdr.util;

import java.util.HashMap;
import java.util.Map;

public class MarkovChainState {
    private int threshold = 1;
    Map<String, Double> transitions; 

    public MarkovChainState() {
        transitions = new HashMap<String, Double>(); 
    }
    
    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
    
    public int getThreshold() {
        return this.threshold;
    }
    
    public void clear() {
        transitions.clear();
    }

    public void normalize() {
        double sum = 0.0;
        for (String next: transitions.keySet()) {
            sum += transitions.get(next);
        }
        Map<String, Double> nzTrans = new HashMap<String, Double>();
        for (String next: transitions.keySet()) {
            nzTrans.put(next, transitions.get(next)/sum);
        }

        this.transitions = nzTrans;
    }

    public void addNext(String next) {
        addNext(next, 1.0);
    }
    
    public void addNext(String next, double value) {
        Double curWt = transitions.get(next);
        if (curWt == null) {
            curWt = 0.0;
        }
        curWt += value;
        transitions.put(next, curWt);
    }

    public Map<String, Double> getTransitions() {
        return transitions;
    }

    // remove transitions of weight equal or less than threshold
    public void pruning() {
        Map<String, Double> pruned = new HashMap<String, Double>();
        for (String next: transitions.keySet()) {
            double weight = transitions.get(next);
            if (weight > threshold) {
                pruned.put(next, weight);
            }
        }
        transitions.clear();    // (care memory?)
        transitions = pruned;
    }

}
