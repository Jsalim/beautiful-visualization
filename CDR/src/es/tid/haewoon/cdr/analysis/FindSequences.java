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

import es.tid.haewoon.cdr.filter.ExtractTopNormalUsersCDR;
import es.tid.haewoon.cdr.util.CDR;
import es.tid.haewoon.cdr.util.CDRUtil;
import es.tid.haewoon.cdr.util.Constants;
import es.tid.haewoon.cdr.util.RankComparator;


public class FindSequences {
    private static final Logger logger = Logger.getLogger(FindSequences.class);
    
    public static final int THRESHOLD_MIN = 60;
    private static final int THRESHOLD = THRESHOLD_MIN * 60 * 1000;    // 60 minutes -> milliseconds
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
    
    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
//        new FindSequences().run(
//                Constants.FILTERED_PATH + File.separator + "6_1_focused_home_hours",
//                Constants.RESULT_PATH + File.separator + "7_1_cell_sequences_in_home_hours_interval_less_than_" + THRESHOLD_MIN + "_min");
//        new FindSequences().run(
//                Constants.FILTERED_PATH + File.separator + "6_2_focused_work_hours",
//                Constants.RESULT_PATH + File.separator + "7_2_cell_sequences_in_work_hours_interval_less_than_" + THRESHOLD_MIN + "_min");
        
        // only focus on the sequences during commuting hours
        new FindSequences().run(
                Constants.FILTERED_PATH + File.separator + "6_3_focused_commuting_hours",
                Constants.RESULT_PATH + File.separator + "5_3_cell_sequences_in_commuting_hours_interval_less_than_" + THRESHOLD_MIN + "_min");
    }
    
    private void run(String loadingPath, String targetDirectory) throws IOException {
        List<File> files = CDRUtil.loadFiles(loadingPath, Constants.TELNUM_FILE_PATTERN);    
        boolean success = (new File(targetDirectory)).mkdir();
        if (success) {
            logger.debug("A directory [" + targetDirectory + "] is created");
        }
        
        int processed = 0;
        for (File file: files) {
            processed ++;
            
            if (processed % 100 == 0) {
                logger.debug("processing [" + processed + "] files");
            }
//            logger.debug("processing " + file);
            BufferedWriter bw = new BufferedWriter(new FileWriter(targetDirectory + File.separator + file.getName()));
            String line = "";
            BufferedReader br = new BufferedReader(new FileReader(file));
            
            List<String> cellSeq = new ArrayList<String>();
            long lastTime = -1; // epoch
            try {
                while((line = br.readLine()) != null) {

                    CDR cdr = new CDR(line);

                    if (lastTime == -1) {
                        lastTime = cdr.getDatetime().getTime();
                        cellSeq.clear();
                    }

                    if (cdr.getDatetime().getTime()-lastTime > THRESHOLD) {
                        if (cellSeq.size() > 0) {
                            String delim = "";
                            StringBuffer sb = new StringBuffer();
                            for (String cell: cellSeq) {
                                sb.append(delim).append(cell);
                                delim = "\t";
                            }

                            bw.write(sdf.format(cdr.getDatetime()) + "\t" + cellSeq.size() + "\t" + sb.toString());
                            bw.newLine();
                            cellSeq.clear();
                        }
                    }
                    lastTime = cdr.getDatetime().getTime();

                    cellSeq.add(cdr.getInitCellID());
                    if (cdr.getInitCellID() != cdr.getFinCellID()) {
                        cellSeq.add(cdr.getFinCellID());
                    }
                }

                // the final row
                if (cellSeq.size() > 0) {
                    String delim = "";
                    StringBuffer sb = new StringBuffer();
                    for (String cell: cellSeq) {
                        sb.append(delim).append(cell);
                        delim = "\t";
                    }
                    bw.write(sdf.format(lastTime) + "\t" + cellSeq.size() + "\t" + sb.toString());
                    bw.newLine();
                    cellSeq.clear();
                }
            } catch (ParseException pe) {
                // TODO Auto-generated catch block
                logger.error("wrong-format CDR", pe);
            } catch (AssertionError e) {
                // TODO Auto-generated catch block
                logger.debug(line);
                logger.fatal("something wrong / all CDRs here must have at least one Movistar number", e);
                System.exit(0);
            }
            bw.close();
        }
    }
}
