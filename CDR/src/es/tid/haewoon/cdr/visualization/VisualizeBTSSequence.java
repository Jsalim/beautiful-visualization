package es.tid.haewoon.cdr.visualization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import es.tid.haewoon.cdr.util.NumericComparator;
import es.tid.haewoon.cdr.util.RankComparator;
import es.tid.haewoon.cdr.util.Transition;

public class VisualizeBTSSequence extends PApplet {

    PFont font;
    Map map;
    Voronoi voronoi;
    ToxiclibsSupport gfx;
    private static Logger logger = Logger.getLogger(VisualizeBTSSequence.class);
    List<File> files;
    int fileIndex = 0;
    Random random = new Random(0);
    String targetPath;

    public void setup() {
        size(1500, 1000, GLConstants.GLGRAPHICS);
        smooth();
        gfx = new ToxiclibsSupport( this );
        font = createFont("Arial",14);
        textFont(font);

        files = CDRUtil.loadFiles(Constants.RESULT_PATH + File.separator + "8_sequences_of_BTS_threshold_" + 
                FindSequences.THRESHOLD_MIN + "_min", "^.*-.*$");
        Collections.sort(files, new RankComparator());

        targetPath = Constants.RESULT_PATH + File.separator + "13_visiualize_BTS_sequence";
        boolean success = (new File(targetPath)).mkdir();
        if (success) {
            logger.debug("A directory [" + targetPath + "] is created");
        }

        map = new Map(this, new Google.GoogleMapProvider());
        //         map = new Map(this, new Microsoft.RoadProvider());
        //         map = new Map(this, new Yahoo.HybridProvider());

        map.zoomAndPanTo(new Location(41.387628f, 2.1698f), 13); // lat-long
        MapUtils.createDefaultEventDispatcher(this, map);

        String line = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(Constants.BARCELONA_CELL_INFO_PATH));
            while((line = br.readLine()) != null) {
                if (line.startsWith("cell")) continue;  // skip the first line of column description
                Cell cell = new Cell(line);
                String btsID = cell.getBTSID();
                BTS2Location.put(btsID, new Location((float) cell.getLatitude(), (float) cell.getLongitude()));
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        loadSeq();

    }

    java.util.Map<String, Location> BTS2Location = new HashMap<String, Location>();
    java.util.Map<Transition, Double> tran2wt = new HashMap<Transition, Double>();

    boolean showVoronoi = false;
    boolean saveFile = false;
    boolean showSeq = false;

    int from = 0xFFe9b20e;
    int to = 0xFFef1628;

    public void draw() {
        //        showVoronoi();
        //        batchMode();
        map.draw();
        
        interactiveMode();
    }

    public void loadSeq() {
        File file = files.get(fileIndex);
        tran2wt.clear();
        

        max_weight = -1;
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(
                    Constants.RESULT_PATH + File.separator + "9_2_pruned_markov_chain_of_BTS" + File.separator + file.getName()));

            String line = "";
            while((line = br.readLine()) != null) {
                String[] tokens = line.split("\\t");
                if (tokens[0].equals(tokens[1])) {
                    continue;
                }
                if (max_weight < Double.valueOf(tokens[2])) {
                    max_weight = Double.valueOf(tokens[2]);
                }
                tran2wt.put(new Transition(tokens[0], tokens[1]), Double.valueOf(tokens[2]));
            }
            br.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        color2seq.clear();

        try {
            br = new BufferedReader(new FileReader(files.get(fileIndex)));
            String line;

            while((line = br.readLine()) != null) {
                String[] tokens = line.split("\\t");
                List<String> locations = new ArrayList<String>();

                // map one edgeColor into one sequence 
                int edgeColor = color(random.nextInt(255), random.nextInt(255), random.nextInt(255));

                // we should skip first two tokens (date & the length of sequences)
                for (int i=2; i < tokens.length; i++) {
                    locations.add(tokens[i]);
                }

                color2seq.put(edgeColor, locations);
            }
            br.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.error(e);
        }
    }

    java.util.Map<Integer, List<String>> color2seq = new HashMap<Integer, List<String>>();
    double max_weight=-1;

    public void showSeq() {
        for (int edgeColor : color2seq.keySet()) {
            List<String> locations = color2seq.get(edgeColor);
            for (int i = 1; i < locations.size(); i++) {
                Location ll = BTS2Location.get(locations.get(i-1));
                Location cl = BTS2Location.get(locations.get(i));
                
                float lxy[] = this.map.getScreenPositionFromLocation(ll);
                float cxy[] = this.map.getScreenPositionFromLocation(cl);

                Transition t = new Transition(locations.get(i-1), locations.get(i));

                double weight = 0;
                if (tran2wt.get(t) == null) {
                    weight = 0;
                } else {
                    weight = tran2wt.get(t);
                }

                pushStyle();
                noStroke();
                fill(0xFF1f1adb, 50);

                ellipse(cxy[0], cxy[1], 30, 30);
                ellipse(lxy[0], lxy[1], 30, 30);

                int thickness = (int) map((float) weight, 0f, (float) max_weight, 20f, 50f);
                int alpha = (int) map((float) weight, 0f, (float) max_weight, 100f, 255f);
                
                stroke(edgeColor, alpha);
                strokeWeight(thickness);
                
                line(lxy[0], lxy[1], cxy[0], cxy[1]);
                popStyle();
            }
        }
    }


    public void showVoronoi() {
        voronoi = new Voronoi();
        for (String btsID : BTS2Location.keySet()) {
            Location location = BTS2Location.get(btsID);
            float xy[] = this.map.getScreenPositionFromLocation(location);
            if (xy[0] > width*2 || xy[0] < -(width*2) || xy[1] > height*2 || xy[1] < -(height*2)) {
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

        for ( Polygon2D polygon : voronoi.getRegions() ) {
            gfx.polygon2D( polygon );
        }
    }

    public void interactiveMode() {
        fill(100);
        text("Press V to toggle the Voronoi diagram on the map", 15, 30);  
        text("Press S to save the current screen", 15, 50);
        text("Press Q to toggle the sequence", 15, 70);
        text("Press N to proceed to the next sequence", 15, 90);

        if (showVoronoi) {
            showVoronoi();
        }

        if (saveFile) {
            saveFrame(targetPath + File.separator + files.get(fileIndex).getName() + ".png");
            saveFile = false;
        }

        if (showSeq) {
            text("[" + files.get(fileIndex).getName() + "]", 15, 110);
            showSeq();
        }
    }

    public void keyPressed() {
        switch(key) {
        case 'v':
            showVoronoi = !showVoronoi;
            break;

        case 's':
            saveFile = true;
            break;

        case 'q':
            showSeq = !showSeq;
            break;

        case 'n':
            fileIndex++;
            loadSeq();
            break;
        }

    }
}
