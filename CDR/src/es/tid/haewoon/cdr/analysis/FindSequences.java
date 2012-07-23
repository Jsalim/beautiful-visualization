package es.tid.haewoon.cdr.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import es.tid.haewoon.cdr.filter.ExtractActiveWalkers;
import es.tid.haewoon.cdr.util.CDR;
import es.tid.haewoon.cdr.util.CDRUtil;
import es.tid.haewoon.cdr.util.Constants;
import es.tid.haewoon.cdr.util.RankComparator;


public class FindSequences {
    private static final Logger logger = Logger.getLogger(FindSequences.class);
    
    public static final int THRESHOLD_MIN = 60;
    private static final int THRESHOLD = THRESHOLD_MIN * 60 * 1000;    // 60 minutes -> milliseconds
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd hh:mm:ss");
    
    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stub
        List<File> files = CDRUtil.loadFiles(Constants.RESULT_PATH + File.separator + "4_most_frequent_" + 
                                            ExtractActiveWalkers.TOP_K + "_active_walkers", "^.*-.*$");
        
        Collections.sort(files, new RankComparator());

        String targetPath = Constants.RESULT_PATH + File.separator + "5_sequences_threshold_" + THRESHOLD_MIN + "_min";
        boolean success = (new File(targetPath)).mkdir();
        if (success) {
            logger.debug("A directory [" + targetPath + "] is created");
        }
        
        for (File file: files) {
            logger.debug("processing " + file);
            String line = "";
            BufferedReader br = new BufferedReader(new FileReader(file));
            
            List<String> cellSeq = new ArrayList<String>();
            long lastTime = -1; // epoch
            
            while((line = br.readLine()) != null) {
                try {
                    CDR cdr = new CDR(line);
                    
                    if (lastTime == -1) {
                        lastTime = cdr.getDatetime().getTime();
                        cellSeq.clear();
                    }
                    
                    if (cdr.getDatetime().getTime()-lastTime < THRESHOLD) {
                        cellSeq.add(cdr.getInitCellID());
                        if (cdr.getInitCellID() != cdr.getFinCellID()) {
                            cellSeq.add(cdr.getFinCellID());
                        }
                    } else {
                        if (cellSeq.size() > 0) {
                            String delim = "";
                            StringBuffer sb = new StringBuffer();
                            for (String cell: cellSeq) {
                                sb.append(delim).append(cell);
                                delim = "\t";
                            }
                            BufferedWriter bw = new BufferedWriter(new FileWriter(targetPath + File.separator + file.getName(), true));
                            bw.write(sdf.format(cdr.getDatetime()) + "\t" + cellSeq.size() + "\t" + sb.toString());
                            bw.newLine();
                            bw.close();
                            
                            cellSeq.clear();
                        }
                    }
                    lastTime = cdr.getDatetime().getTime();

                } catch (ParseException pe) {
                    // TODO Auto-generated catch block
                    logger.error("wrong-format CDR", pe);
                } catch (AssertionError e) {
                    // TODO Auto-generated catch block
                    logger.debug(line);
                    logger.fatal("something wrong / all CDRs here must have at least one Movistar number", e);
                    System.exit(0);
                }
            }
        }
    }
}
