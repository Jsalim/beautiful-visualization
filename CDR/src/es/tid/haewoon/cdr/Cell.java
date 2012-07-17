package es.tid.haewoon.cdr;

public class Cell {
    double longitude;
    double latitude;
    
    public Cell(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }
    
    public boolean isIn(double[][] boundingBox) {
        double leftTopLong = boundingBox[0][0];
        double leftTopLat = boundingBox[0][1];
        
        double rightBottomLong = boundingBox[1][0];
        double rightBottomLat = boundingBox[1][1];
        
        // FIXME: Not considering 180 to -180, but no problem in Spain 
        return (longitude > leftTopLong) && (longitude < rightBottomLong) && (latitude < leftTopLat) && (latitude > rightBottomLat);
    }
}
