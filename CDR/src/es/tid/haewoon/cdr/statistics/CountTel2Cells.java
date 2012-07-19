package es.tid.haewoon.cdr.statistics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import es.tid.haewoon.cdr.util.CDR;
import es.tid.haewoon.cdr.util.CDRUtil;
import es.tid.haewoon.cdr.util.Constants;
import es.tid.haewoon.cdr.util.Operator;

public class CountTel2Cells {
    private static final Logger logger = Logger.getLogger(CountTel2Cells.class);
    
    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stub
        List<File> files = CDRUtil.loadRefinedCDRFiles();
        Map<String, Set<String>> num2Cell = new HashMap<String, Set<String>>();
        String line;
        
        String targetPath = Constants.RESULT_PATH + File.separator + "3_count_telnum_2_cells";
        boolean success = (new File(targetPath)).mkdir();
        if (success) {
            logger.debug("A directory [" + targetPath + "] is created");
        }

        for (File file: files) {
            num2Cell.clear();
            logger.debug("processing " + file);
            BufferedReader br = new BufferedReader(new FileReader(file));

            int lineCount = 0;
            while((line = br.readLine()) != null) {
                if (lineCount % 10000 == 0) {
                    logger.debug("[" + lineCount + "] lines loaded");
                }
                lineCount++;
                
                try {
                    CDR cdr = new CDR(line);

                    // count mobile phones serviced by movistar only
                    // we already filtered movistar-movistar; thus if-else if.
                    if (cdr.getOrigOpr() == Operator.MOVISTAR) {
                        String origNum = cdr.getOrigNum();
                        String initCell = cdr.getInitCellID();
                        Set<String> cells = num2Cell.get(origNum);
                        if (cells == null) {
                            cells = new HashSet<String>();
                        }
                        cells.add(initCell);
                        
                        if (cdr.getInitCellID() != cdr.getFinCellID()) {
                            String finCell = cdr.getFinCellID();
                            cells.add(finCell);
                        }
                        
                        num2Cell.put(origNum, cells);
                    } else if (cdr.getDestOpr() == Operator.MOVISTAR) {
                        String destNum = cdr.getDestNum();
                        String initCell = cdr.getInitCellID();
                        Set<String> cells = num2Cell.get(destNum);
                        if (cells == null) {
                            cells = new HashSet<String>();
                        }
                        cells.add(initCell);
                        
                        if (cdr.getInitCellID() != cdr.getFinCellID()) {
                            String finCell = cdr.getFinCellID();
                            cells.add(finCell);
                        }
                        
                        num2Cell.put(destNum, cells);
                    }
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    logger.error("wrong-formatted CDR", e);
                }
                
            }

            CDRUtil.printMap(targetPath + File.separator + file.getName(), num2Cell);
        }
    }
}
