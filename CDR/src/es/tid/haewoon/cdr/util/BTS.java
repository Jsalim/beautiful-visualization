package es.tid.haewoon.cdr.util;

import java.text.ParseException;

public class BTS {
    double longitude;
    double latitude;
    String btsID;
    
    public BTS(String line) throws ParseException {
        String[] tokens = line.split("\\|");
        this.btsID = tokens[1];
        this.longitude = Double.valueOf(tokens[6]);
        this.latitude = Double.valueOf(tokens[7]);
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getID() {
        return btsID;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BTS)) {
            return false;
        }
        if (this.getID().equals(((BTS) obj).getID())) {
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return (this.longitude + this.latitude + this.btsID).hashCode();
    }
    
    public boolean closeEnough (BTS b2, double threshold_meter) {
        return CDRUtil.getDistance(this, b2) < threshold_meter;
    }
    
    public boolean closeEnough (double lat, double lon, double threshold_meter) {
        return CDRUtil.getDistance(this.latitude, this.longitude, lat, lon) < threshold_meter;
    }
    
    public boolean isIn(double[][] boundingBox) {
        
        double leftTopLat = boundingBox[0][0];
        double leftTopLong = boundingBox[0][1];
        
        double rightBottomLat = boundingBox[1][0];
        double rightBottomLong = boundingBox[1][1];
        
        // FIXME: we are not considering 180 to -180, but (at least) no problem with Spain data 
        return (longitude > leftTopLong) && (longitude < rightBottomLong) && (latitude < leftTopLat) && (latitude > rightBottomLat);
    }
    
    public String toString() {
        return btsID;
    }
}
