package es.tid.haewoon.cdr.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import es.tid.haewoon.cdr.util.BTS;
import es.tid.haewoon.cdr.util.CDRUtil;
import es.tid.haewoon.cdr.util.Constants;
import es.tid.haewoon.cdr.util.RankComparator;

// no need to run this any more
@ Deprecated
public class TransformHomeWorkBTStoLatLong {
    Logger logger = Logger.getLogger(TransformHomeWorkBTStoLatLong.class);
    
    public static void main(String[] args) throws IOException {
        (new TransformHomeWorkBTStoLatLong()).run(Constants.RESULT_PATH + File.separator + "11_home_work_lat_long");
        
    }
    
    private void run(String targetDirectory) throws IOException {
        boolean success = (new File(targetDirectory)).mkdir();
        if (success) {
            logger.debug("A directory [" + targetDirectory + "] is created...");
        }
        
        Map<String, String> num2home = new HashMap<String, String>();
        Map<String, String> num2work = new HashMap<String, String>();
        
        BufferedReader br = new BufferedReader(new FileReader(
                Constants.RESULT_PATH + File.separator + "4_1_home_BTS" + File.separator + "telnumber_homeBTS_threshold_1000m"));
        String line;
        
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split("\t");
            String number = tokens[0];
            String bts = tokens[1];
            
            num2home.put(number, bts);
        }
        br.close();
        
        br = new BufferedReader(new FileReader(
                Constants.RESULT_PATH + File.separator + "4_2_work_BTS" + File.separator + "telnumber_workBTS_threshold_1000m"));
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split("\t");
            String number = tokens[0];
            String bts = tokens[1];
            
            num2work.put(number, bts);
        }
        br.close();
        
        BufferedWriter bw = new BufferedWriter(new FileWriter(targetDirectory + File.separator + "home_2_work"));

        int processed = 0;
        for (String number : CDRUtil.getOrderedNumbers()) {
            processed++;
            if (processed % 100 == 0) {
                logger.debug("processing [" + processed + "] files");
            }
            if (num2work.get(number) != null && num2home.get(number) != null) {
                BTS home = CDRUtil.getBTS(num2home.get(number));
                BTS work = CDRUtil.getBTS(num2work.get(number));
                // the separator of comma is for the Google Directions API
                bw.write(number + "\t" + home.getLatitude() + "," + home.getLongitude() + "\t" + work.getLatitude() + "," + work.getLongitude());
                bw.newLine();
            } 
            
        }
        bw.close();
    }
}
