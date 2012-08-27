package es.tid.haewoon.cdr.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import es.tid.haewoon.cdr.util.BTS;
import es.tid.haewoon.cdr.util.CDRUtil;
import es.tid.haewoon.cdr.util.Constants;

public class FractionOfMatchedSequences {
    Logger logger = Logger.getLogger(FractionOfMatchedSequences.class);
    public static void main(String[] args) throws IOException {
        (new FractionOfMatchedSequences()).run(Constants.RESULT_PATH + File.separator + "16_fraction_of_matched_sequences");
    }
    
    private void run(String targetDirectory) throws IOException {
        boolean success = (new File(targetDirectory)).mkdir();
        if (success) {
            logger.debug("A directory [" + targetDirectory + "] is created");
        }
        
        String loadPath = Constants.RESULT_PATH + File.separator + "7_3_markov_chain_of_BTS_in_commuting_hours";
        
        BufferedWriter bw = new BufferedWriter(new FileWriter(targetDirectory + File.separator + "result"));
        for (String number : CDRUtil.getOrderedNumbers()) {
            String pruned = Constants.RESULT_PATH + File.separator + "8_3_pruned_markov_chain_of_BTS_in_commuting_hours" + File.separator + number;
            String matched = Constants.RESULT_PATH + File.separator + "15_3_matched_sequences_with_google_in_commuting_hours" + File.separator + number;
            if (!(new File(matched)).exists()) {
                continue;
            }
            
            int wholeSeq = countSequence(loadPath + File.separator + number);
            int pruneSeq = countSequence(pruned);
            int matchedSeq = countSequence(matched);
            int matchedSeq_pruned = countSequence(matched, 1);
            
            if (wholeSeq != 0) {
                bw.write(number + "\t" + wholeSeq + "\t" + matchedSeq + "\t" + (double) matchedSeq / (double) wholeSeq);
            } else {
                bw.write(number + "\t" + wholeSeq + "\t" + matchedSeq + "\t0");
            }
            
            if (pruneSeq != 0) {
                bw.write("\t" + pruneSeq + "\t" + (double) matchedSeq_pruned / (double) pruneSeq);
            } else {
                bw.write("\t" + pruneSeq + "\t0");
            }
            bw.newLine();
        }
        bw.close();
    }
    
    private int countSequence(String filename) throws IOException {
        return countSequence(filename, 0);
    }
    
    private int countSequence(String filename, int THRESHOLD) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        
        int count = 0;
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split("\t");
            BTS lb = CDRUtil.getBTS(tokens[0]);
            BTS cb = CDRUtil.getBTS(tokens[1]);
            if (Double.valueOf(tokens[2]) <= THRESHOLD) {
                continue;
            }
//            if (lb.getLatitude() == cb.getLatitude() && lb.getLongitude() == cb.getLongitude()) {
//                continue;
//            }
            count ++;
        }
        br.close();
        
        return count;
    }
    
    
}
