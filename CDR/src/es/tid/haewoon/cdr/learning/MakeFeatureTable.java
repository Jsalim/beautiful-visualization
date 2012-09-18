package es.tid.haewoon.cdr.learning;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import es.tid.haewoon.cdr.util.BTS;
import es.tid.haewoon.cdr.util.CDRUtil;
import es.tid.haewoon.cdr.util.Constants;

public class MakeFeatureTable {
    Logger logger = Logger.getLogger(MakeFeatureTable.class);
    public static void main(String[] args) throws IOException {
        (new MakeFeatureTable()).run(Constants.RESULT_PATH + File.separator + "4_feature_table");
    }
    
    private void run(String targetDirectory) throws IOException {
        boolean success = (new File(targetDirectory)).mkdir();
        if (success) {
            logger.info("A directory [" + targetDirectory + "] is created");
        }
        
        String line ="";
        BufferedWriter bbw = new BufferedWriter(new FileWriter(targetDirectory + File.separator + "5682_GT_Extracted_Cluster_Features.txt"));
        for (String number : CDRUtil.getOrderedNumbers()) {

            Map<String, String> center2cluster = new HashMap<String, String>();
            Map<String, String> bts2center = new HashMap<String, String>();
            
            // 5 features
            Map<String, String> center2towerdays = new HashMap<String, String>();
            Map<String, Integer> center2rank = new HashMap<String, Integer>();
            Map<String, Integer> center2duration = new HashMap<String, Integer>();
            Map<String, Integer> center2home = new HashMap<String, Integer>();
            Map<String, Integer> center2work = new HashMap<String, Integer>();
            
            
            
            try {
                BufferedReader br = new BufferedReader(new FileReader(
                        Constants.RESULT_PATH + File.separator + "3_BTS_clusters_threshold_500m" + File.separator + number));

                int rank = 0;
                while ((line = br.readLine()) != null) {
                    String[] tokens = line.split("\t");
                    String bts = tokens[0];
                    String prop = tokens[2];
                    center2towerdays.put(bts, prop);
                    center2duration.put(bts, Integer.valueOf(tokens[3]));

                    String[] neighbors = {};
                    if (tokens.length == 5) {
                        neighbors = tokens[4].split("\\|");
                    }

                    Set<String> cluster = new HashSet<String>(Arrays.asList(neighbors));
                    cluster.add(bts);
                    for (String one : cluster) {
                        bts2center.put(one, bts);
                    }
                    center2cluster.put(bts, CDRUtil.join(cluster, "|"));

                    rank++;
                    center2rank.put(bts, rank);
                }
                br.close();

                br = new BufferedReader(new FileReader(
                        Constants.RESULT_PATH + File.separator + "2_1_count_home_hour_events" + File.separator + number));
                while ((line = br.readLine()) != null) {
                    String[] tokens = line.split("\t");
                    String bts = tokens[1];
                    int count = Integer.valueOf(tokens[2]);
                    String center = bts2center.get(bts);
                    int old = (center2home.get(center) != null) ?center2home.get(center) :0;
                    center2home.put(center, old + count);
                }
                br.close();

                br = new BufferedReader(new FileReader(
                        Constants.RESULT_PATH + File.separator + "2_2_count_work_hour_events" + File.separator + number));
                while ((line = br.readLine()) != null) {
                    String[] tokens = line.split("\t");
                    String bts = tokens[1];
                    int count = Integer.valueOf(tokens[2]);
                    String center = bts2center.get(bts);
                    int old = (center2work.get(center) != null) ?center2work.get(center) :0;
                    center2work.put(center, old + count);
                }
                br.close();


                BufferedWriter bw = new BufferedWriter(new FileWriter(targetDirectory + File.separator + number));
                for (String center : center2cluster.keySet()) {
                    String prop = center2towerdays.get(center);
                    int duration = center2duration.get(center); 
                    rank = center2rank.get(center);
                    int home = (center2home.get(center) != null) ?center2home.get(center) :0;
                    int work = (center2work.get(center) != null) ?center2work.get(center) :0;
                    bw.write(center + "," + prop + "," + duration + "," + rank + "," + home + "," + work + "," + center2cluster.get(center));
                    bw.newLine();
                }
                bw.close();
                
                bbw.write(number + "\t" + center2cluster.keySet().size());
                bbw.newLine();
                for (String center : center2cluster.keySet()) {
                    String prop = center2towerdays.get(center);
                    int duration = center2duration.get(center); 
                    rank = center2rank.get(center);
                    int home = (center2home.get(center) != null) ?center2home.get(center) :0;
                    int work = (center2work.get(center) != null) ?center2work.get(center) :0;
                    BTS cBTS = CDRUtil.getBTS(center);
                    bbw.write(cBTS.getLatitude() + "," + cBTS.getLongitude() + "\t" + prop + "," + duration + "," + rank + "," + home + "," + work);
                    bbw.newLine();
                }
                
            } catch (FileNotFoundException fnfe) {
                logger.error("no file for [" + number + "]");
                continue;
            }
        }
        bbw.close();
    }
}
