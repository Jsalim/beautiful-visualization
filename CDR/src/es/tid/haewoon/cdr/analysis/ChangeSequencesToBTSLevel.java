package es.tid.haewoon.cdr.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import es.tid.haewoon.cdr.util.CDRUtil;
import es.tid.haewoon.cdr.util.Cell;
import es.tid.haewoon.cdr.util.Constants;
import es.tid.haewoon.cdr.util.MarkovChainState;
import es.tid.haewoon.cdr.util.RankComparator;

public class ChangeSequencesToBTSLevel {
    
    private Logger logger = Logger.getLogger(ChangeSequencesToBTSLevel.class); 
    Map<String, String> cell2bts = new HashMap<String, String>();

    public static void main(String[] args) throws IOException, ParseException {
        ChangeSequencesToBTSLevel csbts = new ChangeSequencesToBTSLevel();
        csbts.run();
    }

    public void run() throws IOException, ParseException {
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
        
        List<File> files = CDRUtil.loadFiles(Constants.RESULT_PATH + File.separator + "5_sequences_threshold_" + FindSequences.THRESHOLD_MIN 
                + "_min", "^.*-.*$");
       
        Collections.sort(files, new RankComparator());
        logger.debug(files.size() + " files loaded...");

        String targetPath = Constants.RESULT_PATH + File.separator + "8_sequences_of_BTS_threshold_" + FindSequences.THRESHOLD_MIN + "_min";
        boolean success = (new File(targetPath)).mkdir();
        if (success) {
            logger.debug("A directory [" + targetPath + "] is created");
        }

        for (File file: files) {
            logger.debug("processing " + file);
            br = new BufferedReader(new FileReader(file));
            BufferedWriter bw = new BufferedWriter(new FileWriter(targetPath + File.separator + file.getName()));
            
            while((line = br.readLine()) != null) {
                String[] tokens = line.split("\\t");
                List<String> newTokens = new ArrayList<String>();
                newTokens.add(tokens[0]);
                newTokens.add(tokens[1]);
                
                // we should skip first two tokens (date & the length of sequences)
                for (int i=2; i < tokens.length; i++) {
                    newTokens.add(cell2bts.get(tokens[i]));
                }
                
                String delim = "";
                StringBuffer sb = new StringBuffer();
                for (String nt: newTokens) {
                    sb.append(delim).append(nt);
                    delim = "\t";
                }
                
                bw.write(sb.toString());
                bw.newLine();
            }
            br.close();
            bw.close();
        }
    }
}