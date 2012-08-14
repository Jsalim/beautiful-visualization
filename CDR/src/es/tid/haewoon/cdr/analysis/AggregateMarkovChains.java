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
import es.tid.haewoon.cdr.util.StateBuilder;
import es.tid.haewoon.cdr.util.StringStateBuilder;
import es.tid.haewoon.cdr.util.Transition;

public class AggregateMarkovChains<T> {
    private static Logger logger = Logger.getLogger(AggregateMarkovChains.class);
    private Map<T, MarkovChainState<T>> bigChains = new HashMap<T, MarkovChainState<T>>();
    
    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
//        amc.run(Constants.RESULT_PATH + File.separator + "6_2_pruned_markov_chain_of_cells", 
//                "^.*-.*$", 
//                Constants.RESULT_PATH + File.separator + "7_one_big_markov_chain_of_cells", 
//                new StringStateBuilder(),
//                new NumericComparator());
        
        (new AggregateMarkovChains<Transition>()).
        run(Constants.RESULT_PATH + File.separator + "9_2_pruned_markov_chain_of_BTS", 
                "^.*-.*$", 
                Constants.RESULT_PATH + File.separator + "10_one_big_markov_chain_of_BTS",
                new StringStateBuilder(),
                new NumericComparator());
        
//        (new AggregateMarkovChains<Transition>()).run(Constants.RESULT_PATH + File.separator + "11_2_pruned_BTS_trans_cell_trans", 
//                "^.*-.*$", 
//                Constants.RESULT_PATH + File.separator + "10_one_big_BTS_trans_cell_trans", 
//                new TransitionStateBuilder(),
//                new TransitionComparator());
    }
    
    
//    public class StringStateBuilder implements StateBuilder {
//        public MarkovChainState<String> build(String line) {
//            String[] tokens = line.split("\\t");
//            String cur = tokens[0];
//            String next = tokens[1];
//            double wt = Double.valueOf(tokens[2]);
//            
//            MarkovChainState<String> s = bigChains.get(cur);
//            if (s == null) {
//                s = new MarkovChainState<String>();
//            }
//            s.setID(cur);
//            s.addNext(next, wt);
//            return s;
//        }
//    }
//    
//    public class TransitionStateBuilder implements StateBuilder {
//        public MarkovChainState<Transition> build(String line) {
//            String[] tokens = line.split("\\t");
//            String aBTS = tokens[0];
//            String bBTS = tokens[1];
//            Transition btsTR = new Transition(aBTS, bBTS);
//            
//            String aCell = tokens[2];
//            String bCell = tokens[3];
//            Transition cellTR = new Transition(aCell, bCell);
//            
//            double wt = Double.valueOf(tokens[4]);
//            
//            MarkovChainState<Transition> s = bigChains.get(btsTR);
//            if (s == null) {
//                s = new MarkovChainState<Transition>();
//            }
//            s.setID(btsTR);
//            s.addNext(cellTR, wt);
//            return s;
//        }
//    }
    
    public void run(String inputPath, String pattern, String targetDirectory, StateBuilder sb, Comparator cp) throws NumberFormatException, IOException {
        boolean success = (new File(targetDirectory)).mkdir();
        if (success) {
            logger.debug("A directory [" + targetDirectory + "] is created");
        }
        
        // TODO Auto-generated method stub
        List<File> files = CDRUtil.loadFiles(inputPath, pattern);
        logger.debug(files.size() + " files loaded...");
        for (File file: files) {
            logger.debug("processing " + file);
            String line = "";
            BufferedReader br = new BufferedReader(new FileReader(file));
            while((line = br.readLine()) != null) {
                MarkovChainState<T> s = sb.build(line, bigChains);
                bigChains.put(s.getID(), s);
            }
        }
        
        List<T> cells = new ArrayList<T>(bigChains.keySet());
        Collections.sort(cells, cp);
        
        BufferedWriter bw = new BufferedWriter(new FileWriter(targetDirectory + File.separator + "1_big_chain"));
        BufferedWriter pbw = new BufferedWriter(new FileWriter(targetDirectory + File.separator + "2_pruned_big_chain"));
        BufferedWriter nbw = new BufferedWriter(new FileWriter(targetDirectory + File.separator + "3_normalized_big_chain"));
        
        for (T cell: cells) {
            MarkovChainState<T> s = bigChains.get(cell);
            List<T> states = new ArrayList<T>(s.getTransitions().keySet());
            Collections.sort(states, cp);
            
            for (T next: states) {
                bw.write(cell + "\t" + next + "\t" + s.getTransitions().get(next));
                bw.newLine();
            } 
            
            s.setThreshold(2);  // 2 means one person
            s.pruning();
            states = new ArrayList<T>(s.getTransitions().keySet());
            Collections.sort(states, cp);
            
            for (T next: states) {
                pbw.write(cell + "\t" + next + "\t" + s.getTransitions().get(next));
                pbw.newLine();
            } 
            
            s.normalize();
            states = new ArrayList<T>(s.getTransitions().keySet());
            Collections.sort(states, cp);
            
            for (T next: states) {
                nbw.write(cell + "\t" + next + "\t" + s.getTransitions().get(next));
                nbw.newLine();
            }
        }
        bw.close();
        nbw.close();
        pbw.close();

    }

}
