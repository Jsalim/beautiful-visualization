package es.tid.haewoon.cdr;

import java.util.Set;

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
