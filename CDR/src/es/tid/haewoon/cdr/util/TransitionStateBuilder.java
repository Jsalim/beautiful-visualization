package es.tid.haewoon.cdr.util;

import java.util.Map;

public class TransitionStateBuilder implements StateBuilder<Transition> {

    @Override
    public MarkovChainState<Transition> build(String line, Map<Transition, MarkovChainState<Transition>> chain) {
        String[] tokens = line.split("\\t");
        String aBTS = tokens[0];
        String bBTS = tokens[1];
        Transition btsTR = new Transition(aBTS, bBTS);

        String aCell = tokens[2];
        String bCell = tokens[3];
        Transition cellTR = new Transition(aCell, bCell);

        double wt = Double.valueOf(tokens[4]);

        MarkovChainState<Transition> s = (MarkovChainState<Transition>) chain.get(btsTR);
        if (s == null) {
            s = new MarkovChainState<Transition>();
        }
        s.setID(btsTR);
        s.addNext(cellTR, wt);
        return s;
    }

}
