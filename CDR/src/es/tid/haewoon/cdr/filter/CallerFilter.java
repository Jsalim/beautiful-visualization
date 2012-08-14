package es.tid.haewoon.cdr.filter;

import java.util.Set;

import es.tid.haewoon.cdr.util.CDR;
import es.tid.haewoon.cdr.util.Operator;

public class CallerFilter implements CDRFilter {
    Set s;
    public CallerFilter(Set s) {
        this.s = s;
    }
    
    @Override
    public boolean filter(CDR cdr) {
        if (cdr.getOrigOpr() == Operator.MOVISTAR) {
            return s.contains(cdr.getOrigNum());
        } else {
            // if caller is not served by Movistar, we don't care.
            return false;
        }
    }
}
