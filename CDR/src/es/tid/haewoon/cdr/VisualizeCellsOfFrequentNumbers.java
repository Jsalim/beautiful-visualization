package es.tid.haewoon.cdr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import codeanticode.glgraphics.GLConstants;
import de.fhpotsdam.unfolding.Map;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;
import toxi.geom.Polygon2D;
import toxi.geom.Vec2D;
import toxi.geom.mesh2d.Voronoi;
import toxi.processing.ToxiclibsSupport;

public class VisualizeCellsOfFrequentNumbers extends PApplet {
    private int WIDTH = 1000;
    private int HEIGHT = 1000;
    Map map;
    Voronoi voronoi;
    ToxiclibsSupport gfx;
    HashMap<Cell,Integer> cell2Count = new HashMap<Cell, Integer>();
    int max_count = -1;
    private static Logger logger = Logger.getLogger(CountBasicStatistics.class); 

    public void setup() {
        size(WIDTH, HEIGHT, GLConstants.GLGRAPHICS);
        smooth();
        createFont("Arial", 10);

        gfx = new ToxiclibsSupport( this );
        voronoi  = new Voronoi();

        map = new Map(this, new Google.GoogleMapProvider());
        //         map = new Map(this, new Microsoft.RoadProvider());
        //         map = new Map(this, new Yahoo.HybridProvider());
        map.zoomAndPanTo(new Location(41.387628f, 2.1698f), 13);

        MapUtils.createDefaultEventDispatcher(this, map);

        String line="";

        try {
            BufferedReader br = new BufferedReader(new FileReader(Constants.RESULT_PATH + "/most_frequent_100_numbers/5-689384391"));
            while((line = br.readLine()) != null) {
                CDR cdr = new CDR(line);

                Cell cell = CDRUtil.getInstance().getCell(cdr.getInitCellID());

                Integer i = (Integer) cell2Count.get(cell);
                if (i != null) {
                    cell2Count.put(cell, i+1);
                } else {
                    cell2Count.put(cell, 1);   // initialization
                }

                if (cdr.getInitCellID() != cdr.getFinCellID()) {
                    cell = CDRUtil.getInstance().getCell(cdr.getFinCellID());

                    i = (Integer) cell2Count.get(cell);
                    if (i != null) {
                        cell2Count.put(cell, i+1);
                    } else {
                        cell2Count.put(cell, 1);   // initialization
                    }
                }
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug(line);
        }
     }

    public void draw() {
        map.draw();

        fill(242,118,16);
        noStroke();
        
        for (Cell cell:cell2Count.keySet()) {
            float xy[] = this.map.getScreenPositionFromLocation(new Location((float) cell.getLatitude(), (float) cell.getLongitude()));
            ellipse(xy[0], xy[1], 20, 20);
//            voronoi.addPoint(new Vec2D(xy[0], xy[1]));
        }

//        for ( Polygon2D polygon : voronoi.getRegions() ) {
//            int count = 0;
//            for (Cell cell:cell2Count.keySet()) {
//                if (polygon.containsPoint(new Vec2D(cell.getScreenX(), cell.getScreenY()))) {
//                    count = cell2Count.get(cell);
//                    red = (int) map(count, 0, max_count, 200, 255);
//                    fill(red, 127, 80, 255);
//                }       
//            }
//            gfx.polygon2D( polygon );
//        }


        //        float xy[] = this.map.getScreenPositionFromLocation(tm.location);
        //        float screenX = xy[0];
        //        float screenY = xy[1];
        //     
        //        if (tm.isVisible()) {
        //            voronoi.addPoint(new Vec2D(screenX, screenY));
        //        }
    }
}
