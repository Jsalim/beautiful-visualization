package es.tid.haewoon.cdr.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
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

import es.tid.haewoon.cdr.analysis.MarkovChainofCells.State;
import es.tid.haewoon.cdr.util.CDRUtil;
import es.tid.haewoon.cdr.util.Constants;

public class AggregateMarkovChains {
    private static Logger logger = Logger.getLogger(AggregateMarkovChains.class);
    private Map<String, State> bigChains = new HashMap<String, State>();
    
    public class State {
        private static final int THRESHOLD = 2; // here we set 2, because 2 means one person
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
        
        public void addNext(String next, double value) {
            Double curWt = transitions.get(next);
            if (curWt == null) {
                curWt = 0.0;
            }
            curWt += value;
            transitions.put(next, curWt);
        }
        
        public Map<String, Double> getTransitions() {
            return transitions;
        }
        
        // remove transitions of weight equal or less than threshold
        public void pruning() {
            Map<String, Double> pruned = new HashMap<String, Double>();
            for (String next: transitions.keySet()) {
                double weight = transitions.get(next);
                if (weight > THRESHOLD) {
                   pruned.put(next, weight);
                }
            }
            transitions.clear();    // (care memory?)
            transitions = pruned;
        }
    }
    
    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        AggregateMarkovChains amc = new AggregateMarkovChains();
        amc.run();
    }
    
    public void run() throws NumberFormatException, IOException {
        String targetPath = Constants.RESULT_PATH + File.separator + "7_one_big_markov_chain";
        boolean success = (new File(targetPath)).mkdir();
        if (success) {
            logger.debug("A directory [" + targetPath + "] is created");
        }
        
        // TODO Auto-generated method stub
        List<File> files = CDRUtil.loadFiles(Constants.RESULT_PATH + File.separator + "6_2_pruned_markov_chain_of_cells", "^.*-.*$");
        for (File file: files) {
            logger.debug("processing " + file);
            String line = "";
            BufferedReader br = new BufferedReader(new FileReader(file));
            while((line = br.readLine()) != null) {
                String[] tokens = line.split("\\t");
                String cur = tokens[0];
                String next = tokens[1];
                double wt = Double.valueOf(tokens[2]);
                
                State s = bigChains.get(cur);
                if (s == null) {
                    s = new State();
                }
                s.addNext(next, wt);
                bigChains.put(cur, s);
            }
        }
        
        List<String> cells = new ArrayList<String>(bigChains.keySet());
        Comparator<String> strInt = new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                return Integer.valueOf(o1).compareTo(Integer.valueOf(o2));
            }
            
        };
        Collections.sort(cells, strInt);
        
        BufferedWriter bw = new BufferedWriter(new FileWriter(targetPath + File.separator + "1_big_chain"));
        BufferedWriter pbw = new BufferedWriter(new FileWriter(targetPath + File.separator + "2_pruned_big_chain"));
        BufferedWriter nbw = new BufferedWriter(new FileWriter(targetPath + File.separator + "3_normalized_big_chain"));
        
        for (String cell: cells) {
            State s = bigChains.get(cell);
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
        nbw.close();
        pbw.close();

    }

}
