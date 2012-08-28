package es.tid.haewoon.cdr.visualization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import processing.core.PApplet;
import processing.core.PFont;
import toxi.geom.Polygon2D;
import toxi.geom.Vec2D;
import toxi.geom.mesh2d.Voronoi;
import toxi.processing.ToxiclibsSupport;
import codeanticode.glgraphics.GLConstants;
import de.fhpotsdam.unfolding.Map;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.utils.MapUtils;
import es.tid.haewoon.cdr.util.Cell;
import es.tid.haewoon.cdr.util.Constants;
import es.tid.haewoon.cdr.util.Transition;

public class VisualizeMarkovChains extends PApplet {
    private static Logger logger = Logger.getLogger(VisualizeMarkovChains.class);

    private static final long serialVersionUID = -4085039003253887831L;
    java.util.Map<String, Location> BTS2Location = new HashMap<String, Location>();
    int chainIndex = 0;
    List<String> numbers;
    
    PFont font;
    int from = 0xFFd91901;
    int to = 0xFF2e3dbf;
    ToxiclibsSupport gfx;
    Map map;

    double max_weight = -1;

    boolean normalize = false;
    Random random = new Random(0);

    boolean showAgg = false;
    boolean showHome = false;
    boolean showLabel = false;
    boolean showMarkov = false;
    boolean showVoronoi = false;
    boolean showGoogle = false;
    boolean showBox = false;
    
    String targetPath;

    java.util.Map<String, String> num2home = new HashMap<String, String>();
    java.util.Map<String, String> num2work = new HashMap<String, String>();
    java.util.Map<Transition, Double> cTran2wt = new HashMap<Transition, Double>(); // for commuting

    Voronoi voronoi;

    public void draw() {
        map.draw();
        interactiveMode();
    }

    public void interactiveMode() {
        fill(100);
        textSize(14);
        int yLoc = 30;
        text("Press V to toggle the Voronoi diagram", 15, yLoc);
        // text("Press S to save the current screen", 15, 50);
        yLoc += 20;
        //        text("Press Q to toggle the individual sequence", 15, yLoc);
        text("Press M to toggle the individual Markov chain", 15, yLoc);
        yLoc += 20;
        text("Press N to proceed to the next people", 15, yLoc);
        yLoc += 20;
        text("Press P to return to the last people", 15, yLoc);
        yLoc += 20;
        text("Press H to toggle the home/work location", 15, yLoc);
        yLoc += 20;
        text("Press G to toggle the driving route between home/work by Google");
        yLoc += 20;
        text("Press B to toggle BTSs in the home-work bounding box");
        yLoc += 20;
        text("Press A to show the aggregated Markov chain", 15, yLoc);
        yLoc += 20;
        text("Press Z to toggle the normalization of the chain", 15, yLoc);
        yLoc += 20;
        text("Press L to toggle the label of transitions", 15, yLoc);

        if (showVoronoi) {
            showVoronoi();
        }

        if (showMarkov) {
            showMarkov();
            pushStyle();
            textSize(30);
            fill(0xFFf52a76);
            textAlign(RIGHT);
            if (showAgg) {
                text("The aggregated Markov chain", width, 30);
            } else {
                text("[" + numbers.get(chainIndex) + "]", width, 30);
            }
            popStyle();
        }

        if (showHome) {
            showHomeAndWork();
        }
        
        if (showGoogle) {
            showGoogleRoute();
        }
        
        if (showBox) {
            showBoundingBox();
        }
        
    }

    public void keyPressed() {
        switch (key) {
        case 'v':
            showVoronoi = !showVoronoi;
            break;

        case 'm':
            showMarkov = !showMarkov;
            break;

        case 'g':
            showGoogle = !showGoogle;
            break;
            
        case 'b':
            showBox = !showBox;
            break;
            
        case 'h':
            showHome = !showHome;
            break;

        case 'n':
            chainIndex = (chainIndex+1) % numbers.size();
            loadIndividualMarkov();
            showAgg = false;
            break;

        case 'p':
            chainIndex = (chainIndex == 0) ?numbers.size()-1 :chainIndex-1;
            loadIndividualMarkov();
            showAgg = false;
            break;

        case 'a':
            loadEntireBCN();
            showAgg = true;
            break;

        case 'z':
            normalize = !normalize;
            if (showAgg) {
                loadEntireBCN();
            } else {
                loadIndividualMarkov();
            }
            break;

        case 'l':
            showLabel = !showLabel;
        }

    }
    public void loadEntireBCN() {
        load(Constants.RESULT_PATH + File.separator + "10_3_one_big_markov_chain_of_BTS_in_commuting_hours" + File.separator + "3_normalized_big_chain", 
             Constants.RESULT_PATH + File.separator + "10_3_one_big_markov_chain_of_BTS_in_commuting_hours" + File.separator + "2_pruned_big_chain");
    }


    public void loadIndividualMarkov() {
        String number = numbers.get(chainIndex);
        load(Constants.RESULT_PATH + File.separator + "9_3_normalized_markov_chain_of_BTS_in_commuting_hours" + File.separator + number,
             Constants.RESULT_PATH + File.separator + "7_3_markov_chain_of_BTS_in_commuting_hours" + File.separator + number);
    }

    private void load(String normalizedFilePath, String filePath) {
        max_weight = -1;

        BufferedReader br;
        try {
            if (normalize) {
                br = new BufferedReader(new FileReader(normalizedFilePath));
            } else {
                br = new BufferedReader(new FileReader(filePath));
            }
            cTran2wt.clear();

            String line = "";
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split("\t");
                //                    if (tokens[0].equals(tokens[1])) {
                //                        continue;
                //                    }
                if (max_weight < Double.valueOf(tokens[2])) {
                    max_weight = Double.valueOf(tokens[2]);
                }
                Transition t = new Transition(tokens[0], tokens[1]);
                cTran2wt.put(t, Double.valueOf(tokens[2]));
            }
            br.close();
            
            logger.debug("size(): " + cTran2wt.size());
            logger.debug("max_weight: " + max_weight);
        } catch (IOException e) {
            logger.error("not happened", e);
        }
    }

    public void setup() {
        size(1900, 1000, GLConstants.GLGRAPHICS);
        smooth();
        gfx = new ToxiclibsSupport(this);
        font = createFont("Arial", 30);
        textFont(font);
        String line;
        numbers = new ArrayList<String>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(
                    Constants.RESULT_PATH + File.separator + "11_home_work_lat_long" + File.separator + "home_2_work"));
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split("\t");
                String number = tokens[0];
                numbers.add(number);
                
                String home = tokens[1];
                num2home.put(number, home);
                String work = tokens[2];
                num2work.put(number, work);
            }
        } catch (Exception e) {
            logger.error("not happend", e);
        }


        map = new Map(this, new Google.GoogleMapProvider());
        // map = new Map(this, new Microsoft.RoadProvider());
        // map = new Map(this, new Yahoo.HybridProvider());

        map.zoomAndPanTo(new Location(41.387628f, 2.1698f), 13); // lat-long
        MapUtils.createDefaultEventDispatcher(this, map);

        try {
            BufferedReader br = new BufferedReader(new FileReader(
                    Constants.BARCELONA_CELL_INFO_PATH));
            while ((line = br.readLine()) != null) {
                if (line.startsWith("cell"))
                    continue; // skip the first line of column description
                Cell cell = new Cell(line);
                String btsID = cell.getBTSID();
                BTS2Location.put(btsID,
                        new Location((float) cell.getLatitude(), (float) cell.getLongitude()));
            }
        } catch (IOException e) {
            logger.error(e);
        } catch (ParseException e) {
            logger.error(e);
        }
        loadIndividualMarkov();
    }

    public void showMarkov() {
//        drawMarkov(hTran2wt, 0xFFe12a33, 0xFF50f766);   // home     // blue
//        drawMarkov(wTran2wt, 0xFF03b73b, 0xFFe17302);   // work     // green
        int btsColor = 0xFF331de8;
        int transitionColor = 0xFF5185ea;
        for (Transition t: cTran2wt.keySet()) {
            Location ll = BTS2Location.get(t.cur);
            Location cl = BTS2Location.get(t.next);

            float lxy[] = this.map.getScreenPositionFromLocation(ll);
            float cxy[] = this.map.getScreenPositionFromLocation(cl);

            double weight = 0;
            if (cTran2wt.get(t) == null) {
                weight = 0; 
                logger.debug("실제로 그 일이 일어났습니다");
            } else {
                weight = cTran2wt.get(t);
            }

            pushStyle();
            noStroke();
            fill(btsColor, 70);

            ellipse(cxy[0], cxy[1], 20, 20);
            ellipse(lxy[0], lxy[1], 20, 20);

            int thickness = (int) map((float) weight, 0f,
                    (float) max_weight, 5f, 20f);
            int alpha = (int) map((float) weight, 0f, (float) max_weight,
                    50f, 255f);
            
            // lerpColor(from, to, (float) weight/(float) max_weight)
            
            stroke(transitionColor, alpha);
            strokeWeight(thickness);

//            line(lxy[0], lxy[1], cxy[0], cxy[1]);

            if (showLabel && !ll.equals(cl)) {
                if (weight != 0) {
                    textSize(20);
                    fill(100, alpha);

                    String msg = "";
                    int yPos_adjustment = 0;
                    if (lxy[0] < cxy[0]) {
                        msg = "-> ";
                        yPos_adjustment = 10;
                    } else {
                        msg = "<- ";
                        yPos_adjustment = -10;
                    }

                    if (normalize) {
                        text(msg + String.format("%.2f", weight),
                                (lxy[0] + cxy[0]) / 2, (lxy[1] + cxy[1])
                                / 2 + yPos_adjustment);
                    } else {
                        text(msg + String.format("%.0f", weight),
                                (lxy[0] + cxy[0]) / 2, (lxy[1] + cxy[1])
                                / 2 + yPos_adjustment);
                    }
                }
            }

            popStyle();
        }
    }


    public void showHomeAndWork() {
        pushStyle();
        textAlign(CENTER, CENTER);
        textSize(25);
        String moviNum = numbers.get(chainIndex);
        String home = num2home.get(moviNum);
        String work = num2work.get(moviNum);

        Location h = new Location(Float.valueOf(home.split(",")[0]), Float.valueOf(home.split(",")[1]));
        float[] hxy = this.map.getScreenPositionFromLocation(h);
        Location w = new Location(Float.valueOf(work.split(",")[0]), Float.valueOf(work.split(",")[1]));
        float[] wxy = this.map.getScreenPositionFromLocation(w);

        fill(0xFF195e7e);
        ellipse(hxy[0], hxy[1], 25, 25);
        ellipse(wxy[0], wxy[1], 25, 25);
        fill(0xFFffffff);
        text("H", hxy[0], hxy[1]);
        text("W", wxy[0], wxy[1]);

        popStyle();
    }
    
    public void showGoogleRoute() {
        drawGoogleRoute(numbers.get(chainIndex), "hw");
        drawGoogleRoute(numbers.get(chainIndex), "wh");
    }
    
    private void drawGoogleRoute(String number, String identifier) {
        pushStyle();
        fill(0xFF040a37);
        stroke(0xFF040a37);
        try {
            BufferedReader br = new BufferedReader(new FileReader(Constants.RESULT_PATH + File.separator + 
                    "13_parsed_routes_by_google" + File.separator + number + "-" + identifier));
            String line;
            boolean isFirst = true;
            float[] lxy = {0.0f, 0.0f};
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                float lat = Float.valueOf(tokens[0]);
                float lng = Float.valueOf(tokens[1]);
                float[] xy = this.map.getScreenPositionFromLocation(new Location(lat,lng));
                
                ellipse(xy[0], xy[1], 5, 5);
                if (!isFirst) {
                    line(lxy[0], lxy[1], xy[0], xy[1]);
                } else {
                    isFirst = false;
                }
                lxy[0] = xy[0];
                lxy[1] = xy[1];
                // drawing an edge
            }
            br.close();
            
            // near BTS
            br = new BufferedReader(new FileReader(Constants.RESULT_PATH + File.separator + 
                    "14_BTS_near_google_routes" + File.separator + number));
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split("\t");
                float lat = Float.valueOf(tokens[1]);
                float lng = Float.valueOf(tokens[2]);
                float[] xy = this.map.getScreenPositionFromLocation(new Location(lat,lng));
                stroke(0xFF5ffb32);
                fill(0xFF5ffb32);
                ellipse (xy[0], xy[1], 5, 5);
            }
            br.close();
            
            // matched sequences
//            drawMatchedSequence(Constants.RESULT_PATH + File.separator +
//                    "17_1_matched_sequences_with_google_in_home_hours" + File.separator + number, 0xFF2bc6fe);
//            drawMatchedSequence(Constants.RESULT_PATH + File.separator +
//                    "17_2_matched_sequences_with_google_in_work_hours" + File.separator + number, 0xFF0cf40f);
//            drawMatchedSequence(Constants.RESULT_PATH + File.separator +
//                    "16_3_matched_sequences_with_google_in_commuting_hours" + File.separator + number, 0xFFee0eed);
            
        } catch (IOException ioe) {
            logger.error("probably filenotfound", ioe);
        }
        
        popStyle();
    }
    
    private void showBoundingBox() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(Constants.RESULT_PATH + File.separator + 
                    "17_BTS_near_work_home_bounding_box" + File.separator + numbers.get(chainIndex)));
            String line;

            while ((line = br.readLine()) != null) {
                String[] tokens = line.split("\t");
                float lat = Float.valueOf(tokens[1]);
                float lng = Float.valueOf(tokens[2]);
                float[] xy = this.map.getScreenPositionFromLocation(new Location(lat,lng));
                stroke(0xFF5ffb32);
                fill(0xFF5ffb32);
                ellipse (xy[0], xy[1], 5, 5);
            }
            br.close();
        } catch (IOException ioe) {
            logger.error("not happened", ioe);
        }
    }

    private void drawMatchedSequence(String filename, int color) {
        pushStyle();
        stroke(color);
        strokeWeight(3);
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
       
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split("\t");
                String lbts = tokens[0];
                String cbts = tokens[1];
                float[] lp = this.map.getScreenPositionFromLocation(BTS2Location.get(lbts));
                float[] cp = this.map.getScreenPositionFromLocation(BTS2Location.get(cbts));
//                line(lp[0], lp[1], cp[0], cp[1]);
            } 
            br.close();
        } catch (Exception e) {
            logger.error("ioe", e);
        }
        
        popStyle();
    }

    public void showVoronoi() {
        voronoi = new Voronoi();
        for (String btsID : BTS2Location.keySet()) {
            Location location = BTS2Location.get(btsID);
            float xy[] = this.map.getScreenPositionFromLocation(location);
            if (xy[0] > width * 2 || xy[0] < -(width * 2) || xy[1] > height * 2
                    || xy[1] < -(height * 2)) {
                // avoid errors in toxiclib
                continue;
            }
            try {
                voronoi.addPoint(new Vec2D(xy[0], xy[1]));
            } catch (Exception e) {
                logger.debug(e);
            }
        }

        noFill();
        stroke(0xFF40a6dd);
        strokeWeight(1);

        for (Polygon2D polygon : voronoi.getRegions()) {
            gfx.polygon2D(polygon);
        }

        fill(255, 0, 255);
        noStroke();
        for (Vec2D c : voronoi.getSites()) {
            ellipse(c.x, c.y, 5, 5);
        }
    }
}
