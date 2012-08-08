package es.tid.haewoon.cdr.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import es.tid.haewoon.cdr.util.CDRUtil;
import es.tid.haewoon.cdr.util.Constants;

// most part of this class is coming from IdentifyHomeBTS.class
public class IdentifyWorkBTS {
    Logger logger = Logger.getLogger(IdentifyHomeBTS.class);
    
    private void run(String wPath, String hPath, String targetPath) throws IOException {
        String THRESHOLD = wPath.split("_")[wPath.split("_").length-1];
        List<File> wfiles = CDRUtil.loadFiles(wPath, "^\\d+-\\d+-\\d+$");
        logger.debug(wfiles.size());
        
        boolean success = (new File(targetPath)).mkdir();
        if (success) {
            logger.debug("A directory [" + targetPath + "] is created");
        }
        
        String line;
        BufferedWriter bw = new BufferedWriter(new FileWriter(
                        targetPath + File.separator + "telnumber_workBTS_threshold_" + THRESHOLD));
        
        
        // what i do here is to find BTS has the highest number of events in HomeHours.       
        for (File aFile : wfiles) {
            Map<String, Double> bts2wprop = new HashMap<String, Double>();
            
            logger.debug("processing " + aFile + "...");
            BufferedReader wbr = new BufferedReader(new FileReader(aFile));
            
            while ((line = wbr.readLine()) != null) {
                String[] tokens = line.split("\t");
                
                String btsID = tokens[0];
                int count = Integer.valueOf(tokens[1]);
                double prop = Double.valueOf(tokens[2]);
                
                bts2wprop.put(btsID, prop);
            }
            
            Map<String, Double> bts2hprop = new HashMap<String, Double>();
            try {
                BufferedReader hbr = new BufferedReader(new FileReader(hPath + File.separator + aFile.getName()));
                
                while ((line = hbr.readLine()) != null) {
                    String[] tokens = line.split("\t");
                    
                    String btsID = tokens[0];
                    double prop = Double.valueOf(tokens[2]);
                   
                    bts2hprop.put(btsID, prop);        
                }
            } catch (FileNotFoundException fnfe) {
                logger.error("no file [" + hPath + File.separator + aFile.getName() + "]");
            }
                
            double mScore = -1;
            double mwprop = -1;
            double mhprop = -1;
            
            String mBTS = "N/A";
            
            for (String bts : bts2wprop.keySet()) {
                double wprop = bts2wprop.get(bts);
                double hprop = (bts2hprop.get(bts) != null) ?bts2hprop.get(bts) :0;
               
                double score = wprop + (1-hprop);  // we weigh low home hour events.
                
                if (mScore < score) {
                    mScore = score;
                    mwprop = wprop;
                    mhprop = hprop;
                    mBTS = bts;
                }
            }
            
            bw.write(aFile.getName().split("-")[1] + "\t" + mBTS + "\t" + mScore + "\t" + mwprop + "\t" + mhprop);
            bw.newLine();
        }
        bw.close();
    }
    
    public static void main(String[] args) throws IOException {
        (new IdentifyWorkBTS()).run(Constants.RESULT_PATH + File.separator + "16_2_clustered_work_hour_events_threshold_1000m",
                                    Constants.RESULT_PATH + File.separator + "16_1_clustered_home_hour_events_threshold_1000m",
                                    Constants.RESULT_PATH + File.separator + "17_2_work_BTS");
        
        (new IdentifyWorkBTS()).run(Constants.RESULT_PATH + File.separator + "16_2_clustered_work_hour_events_threshold_2000m",
                                    Constants.RESULT_PATH + File.separator + "16_1_clustered_home_hour_events_threshold_2000m",
                                    Constants.RESULT_PATH + File.separator + "17_2_work_BTS");
    }
}
