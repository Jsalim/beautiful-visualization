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

public class VisualizeVoronoiPrototyping extends PApplet {
    private int WIDTH = 1000;
    private int HEIGHT = 1000;
    Map map;
    Voronoi voronoi;
    ToxiclibsSupport gfx;
    HashMap<Cell,Integer> cell2Count = new HashMap<Cell, Integer>();
    int max_count = -1;
    private static Logger logger = Logger.getLogger(VisualizeVoronoiPrototyping.class); 
    
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
        
        List<File> files = loadFiles(Constants.RESULT_PATH + "/count_basic_statistics/");
        for (File file: files) {
            System.out.println("processing " + file);
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                while((line = br.readLine()) != null) {
                    String[] cellAndCount = line.split("\\t");
                    
                    Cell cell = CDRUtil.getInstance().getCell(cellAndCount[0]);
                    if (max_count < Integer.valueOf(cellAndCount[1])) {
                        max_count = Integer.valueOf(cellAndCount[1]);
                    }
                    cell2Count.put(cell, Integer.valueOf(cellAndCount[1]));
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
            logger.debug(max_count);
            break;
        }
    }
    
    public void draw() {
        map.draw();
        
        for (Cell cell:cell2Count.keySet()) {
            float xy[] = this.map.getScreenPositionFromLocation(new Location((float) cell.getLatitude(), (float) cell.getLongitude()));
            cell.setScreenPosition(xy[0], xy[1]);
            voronoi.addPoint(new Vec2D(xy[0], xy[1]));
        }
        
        noFill();
        stroke(0);
        int red = 0;
        
        for ( Polygon2D polygon : voronoi.getRegions() ) {
            int count = 0;
            for (Cell cell:cell2Count.keySet()) {
                if (polygon.containsPoint(new Vec2D(cell.getScreenX(), cell.getScreenY()))) {
                    count = cell2Count.get(cell);
                    red = (int) map(count, 0, max_count, 200, 255);
                    fill(red, 127, 80, 255);
                }       
            }
            gfx.polygon2D( polygon );
        }
        
        
//        float xy[] = this.map.getScreenPositionFromLocation(tm.location);
//        float screenX = xy[0];
//        float screenY = xy[1];
//     
//        if (tm.isVisible()) {
//            voronoi.addPoint(new Vec2D(screenX, screenY));
//        }
    }
    
    private List<File> loadFiles(String string) {
        // TODO Auto-generated method stub
        List<File> filtered = new ArrayList<File>();
        File targetPath = new File(string);
        if (targetPath.isDirectory()) {
            File[] files = targetPath.listFiles();
            for (File file : files) {
                String filename = file.getName();
                if (filename.matches("^.*\\.cl2c$")) {
                    filtered.add(file);
                }
            }
        }
        return filtered;
    }
}
