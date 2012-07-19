package es.tid.haewoon.cdr;


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
    
    public static void main(String[] args) {
        double[][] barcelonaBox = new double[2][2];
        
        barcelonaBox[0][0] = 2.05513;        // left-top longitude
        barcelonaBox[0][1] = 41.452505;      // left-top latitude 
        
        barcelonaBox[1][0] = 2.261124;       // right-bottom longitude
        barcelonaBox[1][1] = 41.336607;      // right-bottom latitude
        
    }
}
