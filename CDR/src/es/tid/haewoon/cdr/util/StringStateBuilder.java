package es.tid.haewoon.cdr.util;

import java.util.Map;

public class StringStateBuilder implements StateBuilder<String> {
    @Override
    public MarkovChainState<String> build(String line, Map<String, MarkovChainState<String>> chain) {
        String[] tokens = line.split("\\t");
        String cur = tokens[0];
        String next = tokens[1];
        double wt = Double.valueOf(tokens[2]);

        MarkovChainState<String> s = chain.get(cur);
        if (s == null) {
            s = new MarkovChainState<String>();
        }
        s.setID(cur);
        s.addNext(next, wt);
        return s;
    }
}
