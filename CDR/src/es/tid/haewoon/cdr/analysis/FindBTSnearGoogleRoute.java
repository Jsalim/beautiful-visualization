package es.tid.haewoon.cdr.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import es.tid.haewoon.cdr.util.BTS;
import es.tid.haewoon.cdr.util.CDRUtil;
import es.tid.haewoon.cdr.util.Constants;
import es.tid.haewoon.cdr.util.Province;

public class FindBTSnearGoogleRoute {
    Logger logger = Logger.getLogger(FindBTSnearGoogleRoute.class);
    double THRESHOLD = 500;    // meter 
    Set<BTS> BTSs = new HashSet<BTS>(); 
    
    public static void main(String[] args) throws IOException {
        (new FindBTSnearGoogleRoute()).run(Constants.RESULT_PATH + File.separator + "14_BTS_near_google_routes");
    }
    
    private void run(String targetDirectory) throws IOException {
        boolean success = (new File(targetDirectory)).mkdir();
        if (success) {
            logger.info("A directory[" + targetDirectory + "] is created");
        }
        BufferedReader br = new BufferedReader(new FileReader(
                Constants.RESULT_PATH + File.separator + "13_home_work_lat_long" + File.separator + "home_2_work"));
        String line;
        while ((line = br.readLine()) != null) {
            logger.debug(line);
            BTSs.clear();

            String[] tokens = line.split("\t");
            String number = tokens[0];
            
            if (!load(number, "wh") || !load(number, "hw")) {
                break;
            }
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(targetDirectory + File.separator + number));
            for (BTS bts : BTSs) {
                bw.write(bts.getID() + "\t" + bts.getLatitude() + "\t" + bts.getLongitude());
                bw.newLine();
            }
            bw.close();
        }
    }
    
    private boolean load(String number, String identifier) throws IOException {
        if (!new File(Constants.RESULT_PATH + File.separator + "13_parsed_routes_by_google" + 
                        File.separator + number + "-" + identifier).exists()) {
            logger.error("No [" + Constants.RESULT_PATH + File.separator + "13_parsed_routes_by_google" + 
                    File.separator + number + "-" + identifier + "] file");
            return false;
        }
        BufferedReader br = new BufferedReader(new FileReader(Constants.RESULT_PATH + File.separator + 
                "13_parsed_routes_by_google" + File.separator + number + "-" + identifier));
        String line;
        boolean isFirst = true;
        double[] ll = {0.0, 0.0};
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split(",");
            double[] cl = {Double.valueOf(tokens[0]), Double.valueOf(tokens[1])};

            checkPivot(cl[0], cl[1]);
            
            if (!isFirst) {
                checkPathBetweenPivots(ll[0], ll[1], cl[0], cl[1]);
            } else {
                isFirst = false;
            }
            
            ll[0] = cl[0];
            ll[1] = cl[1];
            // drawing an edge
        }
        
        return true;
        
    }
    
    private void checkPivot(double clat, double clon) {
        for (BTS bts : CDRUtil.getBTSs(Province.BARCELONA)) {
            if (bts.closeEnough(clat, clon, THRESHOLD)) {
                logger.debug(bts + " near " + clat + "," + clon);
                BTSs.add(bts);
            }
        }
    }
    
    private void checkPathBetweenPivots(double llat, double llon, double clat, double clon) {
        final double latDelta = clat - llat;
        final double lonDelta = clon - llon;
        
        if (latDelta == 0 && lonDelta == 0) {
            return;
        }
        
        for (BTS bts : CDRUtil.getBTSs(Province.BARCELONA)) {
            final double u = ((bts.getLatitude() - llat) * latDelta + (bts.getLongitude() - llon) * lonDelta) / (latDelta * latDelta + lonDelta * lonDelta);
            
            if (u < 0 || u > 1) {
                continue;
            } else {
                double[] closePoint = {llat + u * latDelta, llon + u * lonDelta};
                if (bts.closeEnough(closePoint[0], closePoint[1], THRESHOLD)) {
                    logger.debug(bts + " near " + llat + "," + llon + "->" + clat + "," + clon);
                    BTSs.add(bts);
                }
            }
        }
    }
}
