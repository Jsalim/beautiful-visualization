package es.tid.haewoon.cdr.util;

import java.text.ParseException;

public class Cell {
    double longitude;
    double latitude;
    String cellID;
    String btsID;
    
    float x;
    float y;
    
    public Cell(String line) throws ParseException {
        String[] tokens = line.split("\\|");
        this.cellID = tokens[0];
        this.btsID = tokens[1];
        this.longitude = Double.valueOf(tokens[6]);
        this.latitude = Double.valueOf(tokens[7]);
    }
    
    @Deprecated
    public Cell(String cellID, String btsID, double longitude, double latitude) {
        this.cellID = cellID;
        this.btsID = btsID;
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
    
    public String getBTSID() {
        return btsID;
    }
    
    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    @Deprecated
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
    
    /*
     * two cells are served by the same BTS?
     */
    public boolean IsSiblingOf(Cell other) {
        return this.btsID.equals(other.getBTSID());
    }
}
