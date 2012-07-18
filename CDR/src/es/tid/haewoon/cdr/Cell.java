package es.tid.haewoon.cdr;

public class Cell {
    double longitude;
    double latitude;
    String cellID;
    
    float x;
    float y;
    
    public Cell(double longitude, double latitude) {
        this("", longitude, latitude);
    }
    
    public Cell(String cellID, double longitude, double latitude) {
        this.cellID = cellID;
        this.longitude = longitude;
        this.latitude = latitude;
    }
    
    public boolean isIn(double[][] boundingBox) {
        double leftTopLong = boundingBox[0][0];
        double leftTopLat = boundingBox[0][1];
        
        double rightBottomLong = boundingBox[1][0];
        double rightBottomLat = boundingBox[1][1];
        
        // FIXME: we are not considering 180 to -180, but (at least) no problem with Spain data 
        return (longitude > leftTopLong) && (longitude < rightBottomLong) && (latitude < leftTopLat) && (latitude > rightBottomLat);
    }
    
    public String getID() {
        return cellID;
    }
    
    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setScreenPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    public float getScreenX() {
        return this.x;
    }
    
    public float getScreenY() {
        return this.y;
    }
    
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Cell)) {
            return false;
        }
        if (this.getID().equals(((Cell) obj).getID())) {
            return true;
        } else {
            return false;
        }
    }
}
