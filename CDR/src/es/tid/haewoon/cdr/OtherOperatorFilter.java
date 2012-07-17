package es.tid.haewoon.cdr;

public class OtherOperatorFilter implements CDRFilter {

    @Override
    public boolean filter(CDR cdr) {
        // we only fiter from 
        return !(cdr.getOrigOpr() == Operator.MOVISTAR && cdr.getDestOpr() == Operator.MOVISTAR);
    }

}
