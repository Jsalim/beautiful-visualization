package es.tid.haewoon.cdr;

public class OtherOperatorFilter implements CDRFilter {

    @Override
    public boolean filter(CDR cdr) {
        // we are interested in the exact one of peole is movistar 
        return !(cdr.getOrigOpr() == Operator.MOVISTAR && cdr.getDestOpr() == Operator.MOVISTAR);
    }

}
