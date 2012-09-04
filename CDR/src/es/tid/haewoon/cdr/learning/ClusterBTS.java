package es.tid.haewoon.cdr.learning;

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

public class ClusterBTS {
    Logger logger = Logger.getLogger(ClusterBTS.class);
    
    public static void main(String[] args) throws IOException {
        (new ClusterBTS()).run(
                Constants.RESULT_PATH + File.separator + "2_3_tower_days",
                Constants.RESULT_PATH + File.separator + "3_BTS_clusters", 500);
    }
    
    private void run(String fileToReadPath, String targetPath, final int THRESHOLD) throws IOException {
        List<File> files = CDRUtil.loadFiles(fileToReadPath, Constants.TELNUM_FILE_PATTERN);
        logger.debug(files.size());
        
        boolean success = (new File(targetPath + "_threshold_" + THRESHOLD + "m")).mkdir();
        if (success) {
            logger.debug("A directory [" + targetPath + "] is created");
        }
        
        String line;

        Map<BTS, Set<BTS>> center2neighbors = new HashMap<BTS, Set<BTS>>();
        Map<BTS, Set<Integer>> bts2days = new HashMap<BTS, Set<Integer>>();
        
        // the input data is already sorted
        // we use the linear-time clustering algorithm, Hartigan's leader algorithm        
        int processed = 0;
        for (File aFile : files) {
            processed++;
            if (processed % 100 == 0) {
                logger.debug("processing [" + processed + "] files");
            }
//            logger.debug("processing " + aFile + "...");
            BufferedReader br = new BufferedReader(new FileReader(aFile));
            
            // initialize two maps
            center2neighbors.clear();
            bts2days.clear();
            
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split("\t");
                
                String num = tokens[0];
//                String cell = tokens[1];
                String btsID = tokens[1];
                
                BTS bts = CDRUtil.getBTS(btsID);
                
                Set<Integer> i_days = new HashSet<Integer>();
                for (String day : tokens[2].split("\\|")) {
                    i_days.add(Integer.valueOf(day));
                }
                bts2days.put(bts, i_days);
                
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
            
            Map<BTS, Integer> bts2cnt = new HashMap<BTS, Integer>();
            Map<BTS, Integer> bts2duration = new HashMap<BTS, Integer>();
            
            for (BTS center : center2neighbors.keySet()) {
                Set<Integer> days = bts2days.get(center);
                Set<BTS> neighbors = center2neighbors.get(center);
                for (BTS neighbor : neighbors) {
                    Set<Integer> n_days = bts2days.get(neighbor);
                    days.addAll(n_days);
                }
                bts2cnt.put(center, days.size());
                List<Integer> union_days = new ArrayList<Integer>(days);
                Collections.sort(union_days);
                bts2duration.put(center, union_days.get(union_days.size()-1) - union_days.get(0));
            }
            
         // sort by value
            Map<Integer, List<BTS>> transpose = transpose(bts2cnt);
            List<Integer> counts = new ArrayList<Integer>(transpose.keySet());
            Collections.sort(counts);
            Collections.reverse(counts);
            
            for (int lcount : counts) {
                List<BTS> btss = transpose.get(lcount);
                for (BTS center : btss) {
                    String delim = "";
                    StringBuffer sb = new StringBuffer();
                    Set<BTS> neighbors = center2neighbors.get(center);
                    for (BTS neighbor : neighbors) {
                        sb.append(delim + neighbor.getID());
                        delim = "|";
                    }

                    bw.write(center.getID() + "\t" + bts2cnt.get(center) + "\t" + ((double)bts2cnt.get(center)/(double)Constants.NUMBER_OF_DAYS) + 
                            "\t" + bts2duration.get(center) + "\t" + neighbors.size() + "\t" + sb.toString());
                    bw.newLine();
                }

            }
            bw.close();
        }
    }
    
    
    public static Map<Integer, List<BTS>> transpose(Map<BTS, Integer> old) {
        Map<Integer, List<BTS>> transpose = new HashMap<Integer, List<BTS>>();
        
        for (BTS key: old.keySet()) {
            Integer value = old.get(key);
            List<BTS> correspondingKeys = transpose.get(value);
            if (correspondingKeys == null) {
                correspondingKeys = new ArrayList<BTS>();
            } 
            correspondingKeys.add(key);
            transpose.put(value, correspondingKeys);
        }
        
        return transpose;
    }
    
}
