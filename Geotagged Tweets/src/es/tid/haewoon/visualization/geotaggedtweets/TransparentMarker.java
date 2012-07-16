package es.tid.haewoon.visualization.geotaggedtweets;

import processing.core.PGraphics;
import twitter4j.GeoLocation;
import twitter4j.Status;
import de.fhpotsdam.unfolding.Map;
import de.fhpotsdam.unfolding.geo.Location;

/*
 * With Unfolding 0.8.0
 */
public class TransparentMarker {
    protected Location location;
    private Status status;

    protected boolean selected = false;
    protected boolean visible = false;
    protected boolean hovered = false;
    private PGraphics pg;
    private int opacity = 0;
    private Map map;
    float screenX = 0.0f;
    float screenY = 0.0f;
    


    public TransparentMarker(Status s, GeoLocation gl) {
        this.status = s;
        float longitude = (float) gl.getLongitude();
        float latitude = (float) gl.getLatitude();
        this.location = new Location((float)latitude, (float)longitude);
    }

    public void setOpacity(int opacity) {
        this.opacity = opacity;
    }

    public int getOpacity() {
        return this.opacity;
    }
    

    public String getText() {
        return status.getText();
    }

    public boolean isHovered(float mouseX, float mouseY, int size) {
        return visible && (Math.sqrt(Math.pow(screenX-mouseX, 2)+Math.pow(screenY-mouseY,2)) < size/2);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }	

    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }
}
