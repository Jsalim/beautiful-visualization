package es.tid.haewoon.cdr.filter;

import java.util.Set;

import es.tid.haewoon.cdr.util.CDR;
import es.tid.haewoon.cdr.util.Operator;

public class TelephoneNumberFilter implements CDRFilter {
    Set s;
    public TelephoneNumberFilter(Set s) {
        this.s = s;
    }
    
    @Override
    public boolean filter(CDR cdr) {
        // Either origin or destination must be MOVISTAR (not both)
        if (cdr.getOrigOpr() == Operator.MOVISTAR) {
            return s.contains(cdr.getOrigNum());
        } else {
            return s.contains(cdr.getDestNum());
        }
    }
}
