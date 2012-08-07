package es.tid.haewoon.cdr.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import es.tid.haewoon.cdr.util.BTS;
import es.tid.haewoon.cdr.util.CDRUtil;
import es.tid.haewoon.cdr.util.Constants;

public class ClusterBTS {
    private static Logger logger = Logger.getLogger(ClusterBTS.class);
    private Map<BTS, Set<BTS>> center2neighbors = new HashMap<BTS, Set<BTS>>();
    private Map<BTS, Integer> bts2count = new HashMap<BTS, Integer>();
    
    private void run(String fileToReadPath, String targetPath, final int THRESHOLD) throws IOException {
        List<File> files = CDRUtil.loadFiles(fileToReadPath, "^\\d+-\\d+-\\d+$");
        logger.debug(files.size());
        
        boolean success = (new File(targetPath + "_threshold_" + THRESHOLD + "m")).mkdir();
        if (success) {
            logger.debug("A directory [" + targetPath + "] is created");
        }
        
        String line;
        
        // the input data is already sorted
        // we use the linear-time clustering algorithm, Hartigan's leader algorithm        
        for (File aFile : files) {
            logger.debug("processing " + aFile + "...");
            BufferedReader br = new BufferedReader(new FileReader(aFile));
            
            // initialize two maps
            center2neighbors.clear();
            bts2count.clear();
            
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split("\t");
                
                String num = tokens[0];
                String cell = tokens[1];
                String btsID = tokens[2];
                int bts_count = Integer.valueOf(tokens[4]);
                
                BTS bts = CDRUtil.getBTS(btsID);
                
                bts2count.put(bts, bts_count);
                
                boolean closeEnoughToExistingClusters = false;
                for (BTS center : center2neighbors.keySet()) {
                    if (center.closeEnough(bts, THRESHOLD)) {
                        center2neighbors.get(center).add(bts);
                        closeEnoughToExistingClusters = true;
                        break;
                    }
                }
                
                if (!closeEnoughToExistingClusters) {
                    center2neighbors.put(bts, new HashSet<BTS>());
                }
            }
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(
                    targetPath + "_threshold_" + THRESHOLD + "m" + File.separator + aFile.getName()));
            
            double sum = 0.0;
            for (BTS center : center2neighbors.keySet()) {
                int count = bts2count.get(center);
                Set<BTS> neighbors = center2neighbors.get(center);
                for (BTS neighbor : neighbors) {
                    count += bts2count.get(neighbor);
                }
                sum += count;
            }
            
            for (BTS center : center2neighbors.keySet()) {
                int count = bts2count.get(center);
                
                String delim = "";
                StringBuffer sb = new StringBuffer();
                Set<BTS> neighbors = center2neighbors.get(center);
                for (BTS neighbor : neighbors) {
                    count += bts2count.get(neighbor);
                    sb.append(delim + neighbor.getID());
                    delim = "|";
                }
                
                bw.write(center.getID() + "\t" + count + "\t" + (count/sum) + "\t" + neighbors.size() + "\t" + sb.toString());
                bw.newLine();
            }
            bw.close();
        }
    }
    
    public static void main(String[] args) throws IOException {
        (new ClusterBTS()).run(
                Constants.RESULT_PATH + File.separator + "15_1_sorted_home_hour_events",
                Constants.RESULT_PATH + File.separator + "16_1_clustered_home_hour_events", 1000);
        
        (new ClusterBTS()).run(
                Constants.RESULT_PATH + File.separator + "15_2_sorted_work_hour_events",
                Constants.RESULT_PATH + File.separator + "16_2_clustered_work_hour_events", 1000);

        (new ClusterBTS()).run(
                Constants.RESULT_PATH + File.separator + "15_1_sorted_home_hour_events",
                Constants.RESULT_PATH + File.separator + "16_1_clustered_home_hour_events", 2000);
        
        (new ClusterBTS()).run(
                Constants.RESULT_PATH + File.separator + "15_2_sorted_work_hour_events",
                Constants.RESULT_PATH + File.separator + "16_2_clustered_work_hour_events", 2000);
    }
}
