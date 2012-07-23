package es.tid.haewoon.cdr.util;

import java.util.Comparator;

public class TransitionComparator implements Comparator<Transition> {
    NumericComparator nc = new NumericComparator();
    @Override
    public int compare(Transition arg0, Transition arg1) {
        // TODO Auto-generated method stub
        arg0 = (Transition) arg0;
        arg1 = (Transition) arg1;

        int result = (nc.compare(arg0.cur, arg1.cur) == 0) ? nc.compare(arg0.next, arg1.next) : nc.compare(arg0.cur, arg1.cur);
        return result;
    }
}
