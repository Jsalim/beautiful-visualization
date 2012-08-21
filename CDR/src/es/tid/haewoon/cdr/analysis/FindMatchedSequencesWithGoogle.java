package es.tid.haewoon.cdr.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import es.tid.haewoon.cdr.util.CDRUtil;
import es.tid.haewoon.cdr.util.Constants;
import es.tid.haewoon.cdr.util.RankComparator;

public class FindMatchedSequencesWithGoogle {
    Logger logger = Logger.getLogger(FindMatchedSequencesWithGoogle.class);
    
    public static void main(String[] args) throws IOException {
        (new FindMatchedSequencesWithGoogle()).run(
                Constants.RESULT_PATH + File.separator + "17_1_matched_sequences_with_google_in_home_hours", 
                Constants.RESULT_PATH + File.separator + "17_2_matched_sequences_with_google_in_work_hours", 
                Constants.RESULT_PATH + File.separator + "17_3_matched_sequences_with_google_in_commuting_hours");
    }
    
    Set<String> BTSs = new HashSet<String>();
    
    private void run(String hTar, String wTar, String cTar) throws IOException {
        boolean success = (new File(hTar)).mkdir();
        if (success) {
            logger.debug("A directory[" + hTar + "] is created");
        }
        success = (new File(wTar)).mkdir();
        if (success) {
            logger.debug("A directory[" + wTar + "] is created");
        }
        success = (new File(cTar)).mkdir();
        if (success) {
            logger.debug("A directory[" + cTar + "] is created");
        }
        
        List<File> hfiles = CDRUtil.loadFiles(Constants.RESULT_PATH + File.separator + "9_1_markov_chain_of_BTS_in_home_hours", "^.*-.*$");
        Collections.sort(hfiles, new RankComparator());
        String line;
        
        for (File file : hfiles) {
            String number = file.getName().split("-")[1];
            if ((new File(Constants.RESULT_PATH + File.separator + "16_BTS_near_google_routes" + File.separator + number)).exists()) {
                logger.debug(file);
                BTSs.clear();
                BufferedReader br = new BufferedReader(new FileReader(Constants.RESULT_PATH + File.separator + 
                        "16_BTS_near_google_routes" + File.separator + file.getName().split("-")[1]));
                while ((line = br.readLine()) != null) {
                    String[] tokens = line.split("\t");
                    String bts = tokens[0];
                    BTSs.add(bts);
                }
                br.close();
                
                findSequence(number, file, hTar);
                findSequence(number, new File(Constants.RESULT_PATH + File.separator + 
                        "9_2_markov_chain_of_BTS_in_work_hours" + File.separator + file.getName()), wTar);
                findSequence(number, new File(Constants.RESULT_PATH + File.separator + 
                        "9_3_markov_chain_of_BTS_in_commuting_hours" + File.separator + file.getName()), cTar);
            }
        }
    }
    
    private void findSequence(String number, File file, String targetDirectory) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        BufferedWriter bw = new BufferedWriter(new FileWriter(targetDirectory + File.separator + number));
        String line;
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
    }
}
