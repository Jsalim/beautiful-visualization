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

public class FindBTSinWorkHomeBoundingBox {
    Logger logger = Logger.getLogger(FindBTSinWorkHomeBoundingBox.class);
    Set<BTS> BTSs = new HashSet<BTS>(); 
    int THRESHOLD = 500;    // meter 

    public static void main(String[] args) throws IOException {
        (new FindBTSinWorkHomeBoundingBox()).run(Constants.RESULT_PATH + File.separator + "17_BTS_near_work_home_bounding_box");
    }
    
    private void run(String targetDirectory) throws IOException {
        boolean success = (new File(targetDirectory)).mkdir();
        if (success) {
            logger.debug("A directory [" + targetDirectory + "] is created");
        }
        
        BufferedReader br = new BufferedReader(new FileReader(
                Constants.RESULT_PATH + File.separator + "11_home_work_lat_long" + File.separator + "home_2_work"));
        String line;
        
        while ((line = br.readLine()) != null) {
            BTSs.clear();
            
            String[] tokens = line.split("\t");
            String number = tokens[0];
            String[] home = tokens[1].split(",");
            String[] work = tokens[2].split(",");
            double[] homeLL = {Double.valueOf(home[0]), Double.valueOf(home[1])};
            double[] workLL = {Double.valueOf(work[0]), Double.valueOf(work[1])};
            double[] hwLL = {Double.valueOf(home[0]), Double.valueOf(work[1])};
            double[] whLL = {Double.valueOf(work[0]), Double.valueOf(home[1])};

            CDRUtil.nearBTS(homeLL[0], homeLL[1], BTSs, THRESHOLD);
            CDRUtil.nearBTS(workLL[0], workLL[1], BTSs, THRESHOLD);
            CDRUtil.nearBTS(hwLL[0], hwLL[1], BTSs, THRESHOLD);
            CDRUtil.nearBTS(whLL[0], whLL[1], BTSs, THRESHOLD);

//            homeLL -> hwLL
            if (homeLL[0] != hwLL[0] || homeLL[1] != hwLL[1]) {
                CDRUtil.nearBTS(homeLL[0], homeLL[1], hwLL[0], hwLL[1], BTSs, THRESHOLD);
            }
            
//            hwLL -> workLL
            if (hwLL[0] != workLL[0] || hwLL[1] != workLL[1]) {
                CDRUtil.nearBTS(hwLL[0], hwLL[1], workLL[0], workLL[1], BTSs, THRESHOLD);
            }
            
//            workLL -> whLL
            if (workLL[0] != whLL[0] || workLL[1] != whLL[1]) {
                CDRUtil.nearBTS(workLL[0], workLL[1], whLL[0], whLL[1], BTSs, THRESHOLD);
            }
            
//            whLL -> homeLL
            if (whLL[0] != homeLL[0] || whLL[1] != homeLL[1]) {
                CDRUtil.nearBTS(whLL[0], whLL[1], homeLL[0], homeLL[1], BTSs, THRESHOLD);
            }
            
            // all btss falling into the box
            double leftTopLat, leftTopLong, rightBottomLat, rightBottomLong;
            if (homeLL[0] > workLL[0]) {    // in the northern hemisphere 
                leftTopLat = homeLL[0];
                rightBottomLat = workLL[0];
            } else {
                leftTopLat = workLL[0];
                rightBottomLat = homeLL[0];
            }
            
            if (homeLL[1] > workLL[1]) {    // FIXME: only in europe + asia
                leftTopLong = workLL[1];
                rightBottomLong = homeLL[1];
            } else {
                leftTopLong = homeLL[1];
                rightBottomLong = workLL[1];
            }
            
//            logger.debug(homeLL[0] + ", " + homeLL[1]);
//            logger.debug(workLL[0] + ", " + workLL[1]);
//            logger.debug(leftTopLat + ", " + leftTopLong + ", " + rightBottomLat + ", " + rightBottomLong);
            
            double[][] boundingBox = {{leftTopLat, leftTopLong}, {rightBottomLat, rightBottomLong}};
            for (BTS bts : CDRUtil.getBTSs(Constants.PROVINCE)) {
                if (bts.isIn(boundingBox)) {
//                    logger.debug(bts);
                    BTSs.add(bts);
                }
            }
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(targetDirectory + File.separator + number));
            for (BTS bts : BTSs) {
                bw.write(bts.getID() + "\t" + bts.getLatitude() + "\t" + bts.getLongitude());
                bw.newLine();
            }
            bw.close();
        }        
    }
}

