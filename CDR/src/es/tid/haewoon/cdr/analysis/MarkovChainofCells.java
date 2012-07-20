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

public class MarkovChainofCells {
    private static final Logger logger = Logger.getLogger(MarkovChainofCells.class);
    /**
     * @param args
     */
    private Map<String, State> markovChain = new HashMap<String, State>();
    
    public class State {
        Map<String, Double> transitions; 
        
        public State() {
            transitions = new HashMap<String, Double>(); 
        }
        
        public void clear() {
            transitions.clear();
        }
        
        public void normalize() {
            double sum = 0.0;
            for (String next: transitions.keySet()) {
                sum += transitions.get(next);
            }
            Map<String, Double> nzTrans = new HashMap<String, Double>();
            for (String next: transitions.keySet()) {
                nzTrans.put(next, transitions.get(next)/sum);
            }
            
            this.transitions = nzTrans;
        }
        
        public void addNext(String next) {
            Double curWt = transitions.get(next);
            if (curWt == null) {
                curWt = 1.0;
            }
            curWt++;
            transitions.put(next, curWt);
        }
        
        public Map<String, Double> getTransitions() {
            return transitions;
        }
    }
    
    public static void main(String[] args) throws IOException {
        MarkovChainofCells mcc = new MarkovChainofCells();
        mcc.run();
    }
    
    public void run() throws IOException {
        
        // TODO Auto-generated method stub
        List<File> files = CDRUtil.loadFiles(Constants.RESULT_PATH + File.separator + "5_sequences_threshold_60_min", "^.*-.*$");
        Comparator<File> rankC = new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                // TODO Auto-generated method stub
                return Integer.valueOf(o1.getName().split("-")[0]).compareTo(Integer.valueOf(o2.getName().split("-")[0]));
            }
        };
        Collections.sort(files, rankC);
        logger.debug(files.size() + " files loaded...");

        String targetPath = Constants.RESULT_PATH + File.separator + "6_markov_chain_of_cells";
        boolean success = (new File(targetPath)).mkdir();
        if (success) {
            logger.debug("A directory [" + targetPath + "] is created");
        }
        
        String targetNPath = Constants.RESULT_PATH + File.separator + "6_normalized_markov_chain_of_cells";
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
                    
                    State s = markovChain.get(state);
                    if (s == null) {
                        s = new State();
                    }
                    
                    s.addNext(next);
                    markovChain.put(state, s);
                }
            }
            
            
            List<String> cells = new ArrayList<String>(markovChain.keySet());
            Comparator<String> strInt = new Comparator<String>() {

                @Override
                public int compare(String o1, String o2) {
                    return Integer.valueOf(o1).compareTo(Integer.valueOf(o2));
                }
                
            };
            Collections.sort(cells, strInt);
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(targetPath + File.separator + file.getName()));
            BufferedWriter nbw = new BufferedWriter(new FileWriter(targetNPath + File.separator + file.getName()));
            
            for (String cell: cells) {
                State s = markovChain.get(cell);
                List<String> states = new ArrayList<String>(s.getTransitions().keySet());
                Collections.sort(states, strInt);
                
                for (String next: states) {
                    bw.write(cell + "\t" + next + "\t" + s.getTransitions().get(next));
                    bw.newLine();
                }   
                
                s.normalize();
                for (String next: states) {
                    nbw.write(cell + "\t" + next + "\t" + s.getTransitions().get(next));
                    nbw.newLine();
                }
            }
            bw.close();
            nbw.close();
        }
    }

}
