package es.tid.haewoon.cdr.filter;

import es.tid.haewoon.cdr.util.CDR;
import es.tid.haewoon.cdr.util.CDRUtil;
import es.tid.haewoon.cdr.util.Cell;

@Deprecated
public class LocationFilter implements CDRFilter {
    double[][] boundingBox;
    public LocationFilter(double[][] boundingBox) {
        this.boundingBox = boundingBox;
    }
    
    @Override
    // return true when cdr is in the box
    public boolean filter(CDR cdr) {
        Cell initCell = CDRUtil.getCell(cdr.getInitCellID());
        Cell finCell = CDRUtil.getCell(cdr.getFinCellID());
        
        return (initCell.isIn(boundingBox) && finCell.isIn(boundingBox));
    }
}
