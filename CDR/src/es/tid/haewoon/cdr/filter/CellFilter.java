package es.tid.haewoon.cdr.filter;

import java.util.HashSet;
import java.util.Set;

import es.tid.haewoon.cdr.util.CDR;
import es.tid.haewoon.cdr.util.Cell;

public class CellFilter implements CDRFilter {
    Set<String> s;
    public CellFilter(Set<Cell> cs) {
        s = new HashSet<String>();
        
        for (Cell c : cs) {
            s.add(c.getID());
        }
    }
    @Override
    public boolean filter(CDR cdr) {
        return s.contains(cdr.getInitCellID()) && s.contains(cdr.getFinCellID());
    }

}
