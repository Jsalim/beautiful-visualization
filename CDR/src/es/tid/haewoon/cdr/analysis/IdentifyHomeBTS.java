package es.tid.haewoon.cdr.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import es.tid.haewoon.cdr.util.CDRUtil;
import es.tid.haewoon.cdr.util.Constants;

public class IdentifyHomeBTS {
    Logger logger = Logger.getLogger(IdentifyHomeBTS.class);
    
    private void run(String fileToReadPath, String targetPath) throws IOException {
        String THRESHOLD = fileToReadPath.split("_")[fileToReadPath.split("_").length-1];
        List<File> files = CDRUtil.loadFiles(fileToReadPath, "^\\d+-\\d+-\\d+$");
        logger.debug(files.size());
        
        boolean success = (new File(targetPath)).mkdir();
        if (success) {
            logger.debug("A directory [" + targetPath + "] is created");
        }
        
        String line;
        
        BufferedWriter bw = new BufferedWriter(new FileWriter(
                        targetPath + File.separator + "telnumber_homeBTS_threshold_" + THRESHOLD + ".tsv"));
        
        // what i do here is to find BTS has the highest number of events in HomeHours.       
        for (File aFile : files) {
            logger.debug("processing " + aFile + "...");
            BufferedReader br = new BufferedReader(new FileReader(aFile));
            int max = -1;
            String max_btsID = "N/A";
            String max_prop = "N/A";
            
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split("\t");
                
                String btsID = tokens[0];
                int count = Integer.valueOf(tokens[1]);
                String prop = tokens[2];
                
                if (max < count) {
                    max = count;
                    max_btsID = btsID;
                    max_prop = prop;
                }
            }
            bw.write(aFile.getName().split("-")[1] + "\t" + max_btsID + "\t" + max + "\t" + max_prop);
            bw.newLine();
        }
        bw.close();
    }
    
    public static void main(String[] args) throws IOException {
        (new IdentifyHomeBTS()).run(Constants.RESULT_PATH + File.separator + "16_1_clustered_home_hour_events_threshold_1000m",
                                    Constants.RESULT_PATH + File.separator + "17_1_home_BTS");
        (new IdentifyHomeBTS()).run(Constants.RESULT_PATH + File.separator + "16_1_clustered_home_hour_events_threshold_2000m",
                Constants.RESULT_PATH + File.separator + "17_1_home_BTS");
    }
    
}
