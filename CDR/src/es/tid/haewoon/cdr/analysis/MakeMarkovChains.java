package es.tid.haewoon.cdr.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import es.tid.haewoon.cdr.util.CDRUtil;
import es.tid.haewoon.cdr.util.Constants;
import es.tid.haewoon.cdr.util.MarkovChainState;
import es.tid.haewoon.cdr.util.NumericComparator;
import es.tid.haewoon.cdr.util.RankComparator;

public class MakeMarkovChains {
    private static final Logger logger = Logger.getLogger(MakeMarkovChains.class);
    /**
     * @param args
     */
    private Map<String, MarkovChainState> markovChain = new HashMap<String, MarkovChainState>();

    public static void main(String[] args) throws IOException {
        MakeMarkovChains mcc = new MakeMarkovChains();
//        mcc.run(Constants.RESULT_PATH + File.separator + "5_sequences_threshold_" + FindSequences.THRESHOLD_MIN + "_min", 
//                "^.*-.*$", 
//                Constants.RESULT_PATH + File.separator + "6_1_markov_chain_of_cells", 
//                Constants.RESULT_PATH + File.separator + "6_2_pruned_markov_chain_of_cells", 
//                Constants.RESULT_PATH + File.separator + "6_3_normalized_markov_chain_of_cells");
        
        mcc.run(Constants.RESULT_PATH + File.separator + "8_sequences_of_BTS_threshold_" + FindSequences.THRESHOLD_MIN + "_min", 
                "^.*-.*$", 
                Constants.RESULT_PATH + File.separator + "9_1_markov_chain_of_BTS", 
                Constants.RESULT_PATH + File.separator + "9_2_pruned_markov_chain_of_BTS", 
                Constants.RESULT_PATH + File.separator + "9_3_normalized_markov_chain_of_BTS");
        
    }
    
    public void run(String inputPath, String pattern, String targetPath, String targetPPath, String targetNPath) throws IOException {
        
        // TODO Auto-generated method stub
        List<File> files = CDRUtil.loadFiles(inputPath, pattern);
        Collections.sort(files, new RankComparator());
        
        logger.debug(files.size() + " files loaded...");

        boolean success = (new File(targetPath)).mkdir();
        if (success) {
            logger.debug("A directory [" + targetPath + "] is created");
        }
        
        success = (new File(targetPPath)).mkdir();
        if (success) {
            logger.debug("A directory [" + targetPPath + "] is created");
        }
        
        success = (new File(targetNPath)).mkdir();
        if (success) {
            logger.debug("A directory [" + targetNPath + "] is created");
        }
        
        for (File file: files) {
            markovChain.clear();
            logger.debug("processing " + file);
            
            BufferedReader br = new BufferedReader(new FileReader(file));
            
            String line = "";
            while((line = br.readLine()) != null) {
                String[] tokens = line.split("\\t");
                
                // we should skip first two tokens (date & the length of sequences)
                for (int i=2; i < tokens.length-1; i++) {
                    String state = tokens[i];
                    String next = tokens[i+1];
                    
                    MarkovChainState s = markovChain.get(state);
                    if (s == null) {
                        s = new MarkovChainState();
                    }
                    
                    s.addNext(next);
                    markovChain.put(state, s);
                }
            }
            
            
            List<String> cells = new ArrayList<String>(markovChain.keySet());
            Comparator<String> strInt = new NumericComparator();
            Collections.sort(cells, strInt);
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(targetPath + File.separator + file.getName()));
            BufferedWriter pbw = new BufferedWriter(new FileWriter(targetPPath + File.separator + file.getName()));
            BufferedWriter nbw = new BufferedWriter(new FileWriter(targetNPath + File.separator + file.getName()));
            
            for (String cell: cells) {
                MarkovChainState s = markovChain.get(cell);
                List<String> states = new ArrayList<String>(s.getTransitions().keySet());
                Collections.sort(states, strInt);
                
                for (String next: states) {
                    bw.write(cell + "\t" + next + "\t" + s.getTransitions().get(next));
                    bw.newLine();
                }   
                
                s.pruning();
                states = new ArrayList<String>(s.getTransitions().keySet());
                Collections.sort(states, strInt);
                
                for (String next: states) {
                    pbw.write(cell + "\t" + next + "\t" + s.getTransitions().get(next));
                    pbw.newLine();
                }   
                
                s.normalize();
                states = new ArrayList<String>(s.getTransitions().keySet());
                Collections.sort(states, strInt);
                
                for (String next: states) {
                    nbw.write(cell + "\t" + next + "\t" + s.getTransitions().get(next));
                    nbw.newLine();
                }
            }
            bw.close();
            pbw.close();
            nbw.close();
        }
    }

}
