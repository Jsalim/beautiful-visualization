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

import es.tid.haewoon.cdr.util.CDRUtil;
import es.tid.haewoon.cdr.util.Constants;
import es.tid.haewoon.cdr.util.MarkovChainState;
import es.tid.haewoon.cdr.util.NumericComparator;

public class AggregateMarkovChains {
    private static Logger logger = Logger.getLogger(AggregateMarkovChains.class);
    private Map<String, MarkovChainState> bigChains = new HashMap<String, MarkovChainState>();
    
    
    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        AggregateMarkovChains amc = new AggregateMarkovChains();
//        amc.run(Constants.RESULT_PATH + File.separator + "6_2_pruned_markov_chain_of_cells", 
//                "^.*-.*$", 
//                Constants.RESULT_PATH + File.separator + "7_one_big_markov_chain_of_cells");
        
        amc.run(Constants.RESULT_PATH + File.separator + "9_2_pruned_markov_chain_of_BTS", 
                "^.*-.*$", 
                Constants.RESULT_PATH + File.separator + "10_one_big_markov_chain_of_BTS");
        
    }
    
    public void run(String inputPath, String pattern, String targetPath) throws NumberFormatException, IOException {
        boolean success = (new File(targetPath)).mkdir();
        if (success) {
            logger.debug("A directory [" + targetPath + "] is created");
        }
        
        // TODO Auto-generated method stub
        List<File> files = CDRUtil.loadFiles(inputPath, pattern);
        for (File file: files) {
            logger.debug("processing " + file);
            String line = "";
            BufferedReader br = new BufferedReader(new FileReader(file));
            while((line = br.readLine()) != null) {
                String[] tokens = line.split("\\t");
                String cur = tokens[0];
                String next = tokens[1];
                double wt = Double.valueOf(tokens[2]);
                
                MarkovChainState s = bigChains.get(cur);
                if (s == null) {
                    s = new MarkovChainState();
                }
                s.addNext(next, wt);
                bigChains.put(cur, s);
            }
        }
        
        List<String> cells = new ArrayList<String>(bigChains.keySet());
        Comparator<String> strInt = new NumericComparator();
        Collections.sort(cells, strInt);
        
        BufferedWriter bw = new BufferedWriter(new FileWriter(targetPath + File.separator + "1_big_chain"));
        BufferedWriter pbw = new BufferedWriter(new FileWriter(targetPath + File.separator + "2_pruned_big_chain"));
        BufferedWriter nbw = new BufferedWriter(new FileWriter(targetPath + File.separator + "3_normalized_big_chain"));
        
        for (String cell: cells) {
            MarkovChainState s = bigChains.get(cell);
            List<String> states = new ArrayList<String>(s.getTransitions().keySet());
            Collections.sort(states, strInt);
            
            for (String next: states) {
                bw.write(cell + "\t" + next + "\t" + s.getTransitions().get(next));
                bw.newLine();
            } 
            
            s.setThreshold(2);  // 2 means one person
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
