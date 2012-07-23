package es.tid.haewoon.cdr.util;

import java.util.HashMap;
import java.util.Map;

public class MarkovChainState<T> {
    private int threshold = 1;
    Map<T, Double> transitions;
    private T ID;

    public MarkovChainState() {
        transitions = new HashMap<T, Double>(); 
    }
    
    public void setID(T ID) {
        this.ID = ID;
    }
    
    public T getID() {
        return this.ID;
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
        for (T next: transitions.keySet()) {
            sum += transitions.get(next);
        }
        Map<T, Double> nzTrans = new HashMap<T, Double>();
        for (T next: transitions.keySet()) {
            nzTrans.put(next, transitions.get(next)/sum);
        }

        this.transitions = nzTrans;
    }

    public void addNext(T next) {
        addNext(next, 1.0);
    }
    
    public void addNext(T next, double value) {
        Double curWt = transitions.get(next);
        if (curWt == null) {
            curWt = 0.0;
        }
        curWt += value;
        transitions.put(next, curWt);
    }

    public Map<T, Double> getTransitions() {
        return transitions;
    }

    // remove transitions of weight equal or less than threshold
    public void pruning() {
        Map<T, Double> pruned = new HashMap<T, Double>();
        for (T next: transitions.keySet()) {
            double weight = transitions.get(next);
            if (weight > threshold) {
                pruned.put(next, weight);
            }
        }
        transitions.clear();    // while lazy GC
        transitions = pruned;
    }

}
