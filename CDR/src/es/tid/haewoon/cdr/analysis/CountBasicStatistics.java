package es.tid.haewoon.cdr.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import es.tid.haewoon.cdr.util.CDR;
import es.tid.haewoon.cdr.util.CDRUtil;
import es.tid.haewoon.cdr.util.Constants;
import es.tid.haewoon.cdr.util.Operator;
import es.tid.haewoon.cdr.util.RawFileComparator;

public class CountBasicStatistics {
    private static Logger logger = Logger.getLogger(CountBasicStatistics.class); 

    public static void main(String[] args) throws IOException {
        (new CountBasicStatistics()).run(CDRUtil.loadAllCDRFiles(), 
                Constants.RESULT_PATH + File.separator + "1_count_basic_statistics");
//        (new CountBasicStatistics()).run(CDRUtil.loadFiles("5_1_sorted_home_hours", Constants.RAW_DATA_FILE_PATTERN), 
//                Constants.RESULT_PATH + File.separator + "1_1_count_basic_statistics_in_home_hours");
//        (new CountBasicStatistics()).run(
//                CDRUtil.loadFiles(Constants.FILTERED_PATH + File.separator + "5_3_sorted_commuting_hours", Constants.RAW_DATA_FILE_PATTERN), 
//                Constants.RESULT_PATH + File.separator + "1_3_count_basic_statistics_in_commuting_hours");
    }
    
    private void run(List<File> files, String targetDirectory) throws IOException {
        boolean success = (new File(targetDirectory)).mkdir();
        if (success) {
            logger.debug("[" + targetDirectory + "] is created...");
        }
        
        Map<String, Integer> origin2Count = new HashMap<String, Integer>();
        Map<String, Integer> dest2Count = new HashMap<String, Integer>();
        Map<String, Integer> cell2Count = new HashMap<String, Integer>();
        Map<String, Integer> duration2Count = new HashMap<String, Integer>();
        Map<String, Integer> number2Count = new HashMap<String, Integer>();
        
        String line;
        Collections.sort(files, new RawFileComparator());

        for (File file: files) {
            origin2Count.clear();
            dest2Count.clear();
            cell2Count.clear();
            duration2Count.clear();
            number2Count.clear();
            
            logger.debug("processing " + file);
            BufferedReader br = new BufferedReader(new FileReader(file));

            while((line = br.readLine()) != null) {
                try {
                    CDR cdr = new CDR(line);
                    
                    // count mobile phones serviced by movistar only
                    // we already filtered movistar-movistar; thus if-else if.
                    if (cdr.getOrigOpr() == Operator.MOVISTAR) {
                        origin2Count = CDRUtil.countItem(origin2Count, cdr.getOrigNum());
                        number2Count = CDRUtil.countItem(number2Count, cdr.getOrigNum());
                    } else if (cdr.getDestOpr() == Operator.MOVISTAR) {
                        dest2Count = CDRUtil.countItem(dest2Count, cdr.getDestNum());
                        number2Count = CDRUtil.countItem(number2Count, cdr.getDestNum());
                    }
                    
                    cell2Count = CDRUtil.countItem(cell2Count, cdr.getInitCellID());
                    if (cdr.getInitCellID() != cdr.getFinCellID()) {
                        cell2Count = CDRUtil.countItem(cell2Count, cdr.getFinCellID());
                    }
                    duration2Count = CDRUtil.countItem(duration2Count, cdr.getDuration());

                } catch (Exception e) {
                    logger.error(line, e);
                } 
            }
       
            String basePath = targetDirectory + File.separator + file.getName();
            CDRUtil.printMap(basePath + ".caller", origin2Count, true);
            CDRUtil.printMap(basePath + ".callee", dest2Count, true);
            CDRUtil.printMap(basePath + ".cell", cell2Count, true);
            CDRUtil.printMap(basePath + ".duration", duration2Count, true);
            CDRUtil.printMap(basePath + ".caller_ee", number2Count, true);
        }
    }
    

}
