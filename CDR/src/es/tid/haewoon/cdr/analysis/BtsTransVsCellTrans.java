package es.tid.haewoon.cdr.analysis;

import static es.tid.haewoon.cdr.util.Constants.RESULT_PATH;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import es.tid.haewoon.cdr.util.CDRUtil;
import es.tid.haewoon.cdr.util.Cell;
import es.tid.haewoon.cdr.util.Constants;
import es.tid.haewoon.cdr.util.MarkovChainState;
import es.tid.haewoon.cdr.util.RankComparator;
import es.tid.haewoon.cdr.util.Transition;
import es.tid.haewoon.cdr.util.TransitionComparator;

public class BtsTransVsCellTrans {

    private Map<String, String> cell2bts = new HashMap<String, String>();
    Logger logger = Logger.getLogger(BtsTransVsCellTrans.class);
    private Map<Transition, MarkovChainState<Transition>> btsbts2Trs;
    private TransitionComparator tc = new TransitionComparator();
    
    public static void main(String[] args) throws IOException, ParseException {
        BtsTransVsCellTrans bvc = new BtsTransVsCellTrans();
        bvc.run(RESULT_PATH + File.separator + "5_sequences_threshold_" + FindSequences.THRESHOLD_MIN + "_min", 
                "^.*-.*$", 
                Constants.RESULT_PATH + File.separator + "11_1_BTS_trans_Cell_trans", 
                Constants.RESULT_PATH + File.separator + "11_2_pruned_BTS_trans_cell_trans", 
                Constants.RESULT_PATH + File.separator + "11_3_normalized_BTS_trans_cell_trans");
    }
    
    
    public void run(String inputPath, String pattern, 
                    String targetPath, String targetPPath, String targetNPath) throws IOException, ParseException {
        btsbts2Trs = new HashMap<Transition, MarkovChainState<Transition>>();
        String line = "";
        BufferedReader br = new BufferedReader(new FileReader(Constants.BARCELONA_CELL_INFO_PATH));
        while((line = br.readLine()) != null) {
            if (line.startsWith("cell")) continue;  // skip the first line of column description
            Cell cell = new Cell(line);
            String cellID = cell.getID();
            String btsID = cell.getBTSID();
            cell2bts.put(cellID, btsID);
        }
        br.close();
        
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
            btsbts2Trs.clear();
            
            logger.debug("processing " + file);
            br = new BufferedReader(new FileReader(file));
            
            
            while((line = br.readLine()) != null) {
                String[] tokens = line.split("\\t");
                
                // we should skip first two tokens (date & the length of sequences)
                for (int i=2; i < tokens.length-1; i++) {
                    String cBTS = cell2bts.get(tokens[i]);
                    String nBTS = cell2bts.get(tokens[i+1]);
                    Transition tr = new Transition(cBTS, nBTS);
                    
                    if (!cBTS.equals(nBTS)) {
                        MarkovChainState<Transition> btsT = 
                                (btsbts2Trs.get(tr) == null) ? new MarkovChainState<Transition>() :btsbts2Trs.get(tr);
                        btsT.addNext(new Transition(tokens[i], tokens[i+1]));
                        btsbts2Trs.put(tr, btsT);
                    }
                }
            }
            
            List<Transition> transitions = new ArrayList<Transition>(btsbts2Trs.keySet());
            Collections.sort(transitions, tc);
                  
            BufferedWriter bw = new BufferedWriter(new FileWriter(targetPath + File.separator + file.getName()));
            BufferedWriter pbw = new BufferedWriter(new FileWriter(targetPPath + File.separator + file.getName()));
            BufferedWriter nbw = new BufferedWriter(new FileWriter(targetNPath + File.separator + file.getName()));
            
            for (Transition t: transitions) {
                MarkovChainState<Transition> btsTr = btsbts2Trs.get(t);
                
                List<Transition> celltrs = new ArrayList<Transition>(btsTr.getTransitions().keySet());
                Collections.sort(celltrs, tc);
                
                for (Transition celltr: celltrs) {
                    bw.write(t + "\t" + celltr + "\t" + btsTr.getTransitions().get(celltr));
                    bw.newLine();
                }
                
                btsTr.pruning();
                celltrs = new ArrayList<Transition>(btsTr.getTransitions().keySet());
                Collections.sort(celltrs, tc);
                
                for (Transition celltr: celltrs) {
                    pbw.write(t + "\t" + celltr + "\t" + btsTr.getTransitions().get(celltr));
                    pbw.newLine();
                } 
                
                btsTr.normalize();
                celltrs = new ArrayList<Transition>(btsTr.getTransitions().keySet());
                Collections.sort(celltrs, tc);
                
                for (Transition celltr: celltrs) {
                    nbw.write(t + "\t" + celltr + "\t" + btsTr.getTransitions().get(celltr));
                    nbw.newLine();
                } 
            }
            
            br.close();
            bw.close();
            pbw.close();
            nbw.close();
        }
    }
}
