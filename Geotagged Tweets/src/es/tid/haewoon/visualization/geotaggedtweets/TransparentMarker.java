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
	protected boolean visible = true;
	protected boolean hovered = false;
	private PGraphics pg;
	private int opacity = 0;
	float screenX = 0.0f;
	float screenY = 0.0f;


	public TransparentMarker(Status s, GeoLocation gl, Map map) {
		this.status = s;
		float longitude = (float) gl.getLongitude();
		float latitude = (float) gl.getLatitude();
		this.location = new Location((float)latitude, (float)longitude);

		float xy[] = map.getScreenPositionFromLocation(this.location);
		screenX = xy[0];
		screenY = xy[1];
		
		pg = map.mapDisplay.getPG();
	}

	public void setOpacity(int opacity) {
		this.opacity = opacity;
	}

	public int getOpacity() {
		return this.opacity;
	}

	public void draw(int opacity) {
        pg.fill(222, 81, 36, opacity);
        pg.ellipse(this.screenX, this.screenY, 20, 20);
		pg.noStroke();
		
		if (this.hovered) {
			pg.fill(0, 200);
            pg.noStroke();
            pg.rect(this.screenX + 1, this.screenY - 15, pg.textWidth(this.getText()) + 2, 12);
            pg.fill(-256, 200);
            pg.text(this.getText(), this.screenX + 2, this.screenY - 5);
		}
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
