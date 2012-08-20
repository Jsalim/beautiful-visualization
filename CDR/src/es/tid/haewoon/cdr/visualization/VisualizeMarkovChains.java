package es.tid.haewoon.cdr.visualization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
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
import es.tid.haewoon.cdr.analysis.FindSequences;
import es.tid.haewoon.cdr.util.CDRUtil;
import es.tid.haewoon.cdr.util.Cell;
import es.tid.haewoon.cdr.util.Constants;
import es.tid.haewoon.cdr.util.RankComparator;
import es.tid.haewoon.cdr.util.Transition;

public class VisualizeMarkovChains extends PApplet {
    private static Logger logger = Logger.getLogger(VisualizeMarkovChains.class);

    private static final long serialVersionUID = -4085039003253887831L;
    java.util.Map<String, Location> BTS2Location = new HashMap<String, Location>();
    int fileIndex = 0;
    
    List<File> hFiles;
    List<File> wFiles;
    List<File> cFiles;
    
    PFont font;
    int from = 0xFFd91901;
    int to = 0xFF2e3dbf;
    ToxiclibsSupport gfx;
    Map map;

    double max_weight = -1;

    boolean normalize = false;
    Random random = new Random(0);

    boolean saveFile = false;
    boolean showAgg = false;
    boolean showHome = false;
    boolean showLabel = false;
    boolean showSeq = false;
    boolean showMarkov = false;
    boolean showVoronoi = false;
    String targetPath;

    java.util.Map<String, String> num2home = new HashMap<String, String>();
    java.util.Map<String, String> num2work = new HashMap<String, String>();
    
    java.util.Map<Transition, Double> hTran2wt = new HashMap<Transition, Double>(); // for home
    java.util.Map<Transition, Double> wTran2wt = new HashMap<Transition, Double>(); // for work
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
        text("Press H to toggle the home/work location", 15, yLoc);
        yLoc += 20;
        text("Press N to proceed to the next people", 15, yLoc);
        yLoc += 20;
        text("Press P to return to the last people", 15, yLoc);
        yLoc += 20;
        text("Press A to show the aggregated Markov chain", 15, yLoc);
        yLoc += 20;
        text("Press Z to toggle the normalization of the chain", 15, yLoc);
        yLoc += 20;
        text("Press L to toggle the label of transitions", 15, yLoc);

        if (showVoronoi) {
            showVoronoi();
        }

//        if (saveFile) {
//            Location center = map.getLocationFromScreenPosition(width / 2,
//                    height / 2);
//            save(targetPath + File.separator + files.get(fileIndex).getName()
//                    + "_level" + map.getZoomLevel() + "_" + +center.x + "_"
//                    + center.y + ".png");
//            saveFile = false;
//        }

        if (showMarkov) {
            showMarkov();
            pushStyle();
            textSize(30);
            fill(0xFFf52a76);
            textAlign(RIGHT);
            if (showAgg) {
                text("The aggregated Markov chain", width, 30);
            } else {
                text("[" + hFiles.get(fileIndex).getName() + "]", width, 30);
            }
            popStyle();
        }

        if (showHome) {
            showHomeAndWork();
        }
        
    }

    public void keyPressed() {
        switch (key) {
        case 'v':
            showVoronoi = !showVoronoi;
            break;

        case 's':
            saveFile = true;
            break;

        case 'm':
            showMarkov = !showMarkov;
            break;

        case 'h':
            showHome = !showHome;
            break;

        case 'n':
            fileIndex++;
            loadIndividualMarkov();
            showAgg = false;
            break;

        case 'p':
            fileIndex = Math.max(fileIndex - 1, 0);
            loadIndividualMarkov();
            showAgg = false;
            break;

        case 'a':
            loadEntireBCN();
            //            showSeq = !showSeq;
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
        max_weight = -1;
//        load(Constants.RESULT_PATH + File.separator + "12_1_one_big_markov_chain_of_BTS_in_home_hours" + File.separator + "3_normalized_big_chain", 
//             new File(Constants.RESULT_PATH + File.separator + "12_1_one_big_markov_chain_of_BTS_in_home_hours" + File.separator + "2_pruned_big_chain"), 
//             hTran2wt);
//        load(Constants.RESULT_PATH + File.separator + "12_2_one_big_markov_chain_of_BTS_in_work_hours" + File.separator + "3_normalized_big_chain", 
//                new File(Constants.RESULT_PATH + File.separator + "12_2_one_big_markov_chain_of_BTS_in_work_hours" + File.separator + "2_pruned_big_chain"), 
//                wTran2wt);
        load(Constants.RESULT_PATH + File.separator + "12_3_one_big_markov_chain_of_BTS_in_commuting_hours" + File.separator + "3_normalized_big_chain", 
                new File(Constants.RESULT_PATH + File.separator + "12_3_one_big_markov_chain_of_BTS_in_commuting_hours" + File.separator + "2_pruned_big_chain"), 
                cTran2wt);
    }


    public void loadIndividualMarkov() {
        // home
        max_weight = -1;

        while (true) {
            if (new File(Constants.RESULT_PATH + File.separator + "9_1_markov_chain_of_BTS_in_home_hours" + File.separator + hFiles.get(fileIndex).getName()).exists() && 
                new File(Constants.RESULT_PATH + File.separator + "9_2_markov_chain_of_BTS_in_work_hours" + File.separator + hFiles.get(fileIndex).getName()).exists() && 
                new File(Constants.RESULT_PATH + File.separator + "9_3_markov_chain_of_BTS_in_commuting_hours" + File.separator + hFiles.get(fileIndex).getName()).exists()) {
                logger.debug((new File(Constants.RESULT_PATH + File.separator + "9_1_markov_chain_of_BTS_in_home_hours" + File.separator + hFiles.get(fileIndex).getName()).exists()));
                load(Constants.RESULT_PATH + File.separator + "11_1_normalized_markov_chain_of_BTS_in_home_hours" + File.separator + hFiles.get(fileIndex).getName(),
                     new File(Constants.RESULT_PATH + File.separator + "9_1_markov_chain_of_BTS_in_home_hours" + File.separator + hFiles.get(fileIndex).getName()), 
                     hTran2wt);
                load(Constants.RESULT_PATH + File.separator + "11_2_normalized_markov_chain_of_BTS_in_work_hours" + File.separator + hFiles.get(fileIndex).getName(),
                        new File(Constants.RESULT_PATH + File.separator + "9_2_markov_chain_of_BTS_in_work_hours" + File.separator + hFiles.get(fileIndex).getName()), 
                        wTran2wt);
                load(Constants.RESULT_PATH + File.separator + "11_3_normalized_markov_chain_of_BTS_in_commuting_hours" + File.separator + hFiles.get(fileIndex).getName(),
                        new File(Constants.RESULT_PATH + File.separator + "9_3_markov_chain_of_BTS_in_commuting_hours" + File.separator + hFiles.get(fileIndex).getName()), 
                        cTran2wt);
                break;
            } 
            
            fileIndex++;
        }

    }

    private void load(String normalizedFilePath, File file, java.util.Map<Transition, Double> tran2wt) {
        logger.debug(file.getName());
        
        BufferedReader br;
        try {
            if (normalize) {
                br = new BufferedReader(new FileReader(normalizedFilePath));
            } else {
                br = new BufferedReader(new FileReader(file));
            }
            tran2wt.clear();

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
                tran2wt.put(t, Double.valueOf(tokens[2]));
            }
            br.close();
            
            logger.debug("size(): " + tran2wt.size());
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

        hFiles = CDRUtil.loadFiles(Constants.RESULT_PATH + File.separator + "9_1_markov_chain_of_BTS_in_home_hours", "^.*-.*$");
        Collections.sort(hFiles, new RankComparator());
        
        wFiles = CDRUtil.loadFiles(Constants.RESULT_PATH + File.separator + "9_2_markov_chain_of_BTS_in_work_hours", "^.*-.*$");
        Collections.sort(wFiles, new RankComparator());
        
        cFiles = CDRUtil.loadFiles(Constants.RESULT_PATH + File.separator + "9_3_markov_chain_of_BTS_in_commuting_hours", "^.*-.*$");
        Collections.sort(cFiles, new RankComparator());
        
        // this is for the screenshots, but can't work due to the problem of libraries.
        //        targetPath = Constants.RESULT_PATH + File.separator
        //                + "13_visiualize_BTS_sequence";
        //        boolean success = (new File(targetPath)).mkdir();
        //        if (success) {
        //            logger.debug("A directory [" + targetPath + "] is created");
        //        }

        map = new Map(this, new Google.GoogleMapProvider());
        // map = new Map(this, new Microsoft.RoadProvider());
        // map = new Map(this, new Yahoo.HybridProvider());

        map.zoomAndPanTo(new Location(41.387628f, 2.1698f), 13); // lat-long
        MapUtils.createDefaultEventDispatcher(this, map);

        String line = "";
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


        try {
            BufferedReader br = new BufferedReader(new FileReader(
                    Constants.RESULT_PATH + File.separator + "5_1_home_BTS" + File.separator + "telnumber_homeBTS_threshold_1000m"));
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split("\t");
                String moviNum = tokens[0];
                String bts = tokens[1];
                num2home.put(moviNum, bts);
            }
            br.close();

            br = new BufferedReader(new FileReader(
                    Constants.RESULT_PATH + File.separator + "5_2_work_BTS" + File.separator + "telnumber_workBTS_threshold_1000m"));
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split("\t");
                String moviNum = tokens[0];
                String bts = tokens[1];
                num2work.put(moviNum, bts);
            }
            br.close();
        } catch (IOException ioe) {
            logger.error(ioe);
        }

    }

    public void showMarkov() {
        drawMarkov(hTran2wt, 0xFFe12a33, 0xFF50f766);   // home     // blue
        drawMarkov(wTran2wt, 0xFF03b73b, 0xFFe17302);   // work     // green
        drawMarkov(cTran2wt, 0xFF331de8, 0xFF5185ea);   // commuting    // red
    }
    
    public void drawMarkov(java.util.Map<Transition, Double> tran2wt, int btsColor, int transitionColor) {
        for (Transition t: tran2wt.keySet()) {
            Location ll = BTS2Location.get(t.cur);
            Location cl = BTS2Location.get(t.next);

            float lxy[] = this.map.getScreenPositionFromLocation(ll);
            float cxy[] = this.map.getScreenPositionFromLocation(cl);

            double weight = 0;
            if (tran2wt.get(t) == null) {
                weight = 0; 
                logger.debug("실제로 그 일이 일어났습니다");
            } else {
                weight = tran2wt.get(t);
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

            line(lxy[0], lxy[1], cxy[0], cxy[1]);

            if (showLabel && !ll.equals(cl)) {
                if (weight != 0) {
                    pushMatrix();
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
                    popMatrix();
                }
            }

            popStyle();
        }
    }


    public void showHomeAndWork() {
        pushStyle();
        textAlign(CENTER);
        textSize(25);
        fill(0xFFfb4b1d);
        String moviNum = hFiles.get(fileIndex).getName().split("-")[1];
        String home = num2home.get(moviNum);
        String work = num2work.get(moviNum);

        if (home != null) {
            Location h = BTS2Location.get(home);
            float[] hxy = this.map.getScreenPositionFromLocation(h);
            text("H", hxy[0], hxy[1]);
        }

        if (work != null) {
            Location w = BTS2Location.get(work);
            float[] wxy = this.map.getScreenPositionFromLocation(w);
            text("W", wxy[0], wxy[1]);
        }
        
//        logger.debug("home[" + home + "], work[" + work + "]");

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
