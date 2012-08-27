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

import es.tid.haewoon.cdr.util.CDRUtil;
import es.tid.haewoon.cdr.util.Constants;

public class FindMatchedSequencesWithGoogle {
    Logger logger = Logger.getLogger(FindMatchedSequencesWithGoogle.class);
    
    public static void main(String[] args) throws IOException {
        (new FindMatchedSequencesWithGoogle()).run(
                Constants.RESULT_PATH + File.separator + "15_3_matched_sequences_with_google_in_commuting_hours");
    }
    
    Set<String> BTSs = new HashSet<String>();
    
    private void run(String targetDirectory) throws IOException {
        boolean success = (new File(targetDirectory)).mkdir();
        if (success) {
            logger.debug("A directory[" + targetDirectory + "] is created");
        }
        String line;
        for (String number : CDRUtil.getOrderedNumbers()) {
            if ((new File(Constants.RESULT_PATH + File.separator + "16_BTS_near_google_routes" + File.separator + number)).exists()) {
                BTSs.clear();
                BufferedReader br = new BufferedReader(new FileReader(Constants.RESULT_PATH + File.separator + 
                        "16_BTS_near_google_routes" + File.separator + number));
                while ((line = br.readLine()) != null) {
                    String[] tokens = line.split("\t");
                    String bts = tokens[0];
                    BTSs.add(bts);
                }
                br.close();
                
                br = new BufferedReader(new FileReader(Constants.RESULT_PATH + File.separator + 
                    "9_3_markov_chain_of_BTS_in_commuting_hours" + File.separator + number));
                BufferedWriter bw = new BufferedWriter(new FileWriter(targetDirectory + File.separator + number));
                while ((line = br.readLine()) != null) {
                    String[] tokens = line.split("\t");
                    String bts1 = tokens[0];
                    String bts2 = tokens[1];

                    if (BTSs.contains(bts1) && BTSs.contains(bts2)) {
                        bw.write(line.trim());
                        bw.newLine();
                    }
                }
                bw.close();
                br.close();
            } else {
                logger.debug("no [" + Constants.RESULT_PATH + File.separator + "16_BTS_near_google_routes" + File.separator + number + "] file");
                break;
            }
        }
    }
}
