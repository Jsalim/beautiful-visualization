package es.tid.haewoon.cdr.learning;

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

import es.tid.haewoon.cdr.util.CDR;
import es.tid.haewoon.cdr.util.CDRUtil;
import es.tid.haewoon.cdr.util.Constants;

public class CountHomeAndWorkHourEvents {
    private static Logger logger = Logger.getLogger(CountHomeAndWorkHourEvents.class);
    
    public static void main(String[] args) throws IOException, ParseException {
        (new CountHomeAndWorkHourEvents()).run(Constants.FILTERED_PATH + File.separator + "6_1_focused_home_hours", 
                Constants.RESULT_PATH + File.separator + "2_1_count_home_hour_events");
        (new CountHomeAndWorkHourEvents()).run(Constants.FILTERED_PATH + File.separator + "6_2_focused_work_hours", 
                Constants.RESULT_PATH + File.separator + "2_2_count_work_hour_events");
    }
    
    private void run(String loadDirectory, String targetDirectory) throws IOException, ParseException {
        boolean success = (new File(targetDirectory)).mkdir();
        if (success) {
            logger.debug("A directory[" + targetDirectory + "] is created...");
        }
        
        List<File> files = CDRUtil.loadFiles(loadDirectory, Constants.TELNUM_FILE_PATTERN);
        logger.debug("loading " + files.size() + " files...");
        Map<String, Integer> bts2cnt = new HashMap<String, Integer>();
        
        int processed = 0;
        for (File file: files) {
            bts2cnt.clear();
            
            processed++;
            if (processed % 100 == 0) {
                logger.debug("processing [" + processed + "] files");
            }
            String line = "";
            BufferedReader br = new BufferedReader(new FileReader(file));
            
            while((line = br.readLine()) != null) {
                CDR cdr;
                cdr = new CDR(line);
                
                try {
                    handlingCount(bts2cnt, cdr);
                } catch (Exception e) {
                    logger.debug(file.getAbsolutePath());
                    logger.error(line, e);
                }
            }
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(targetDirectory + File.separator + file.getName()));

            // sort by value
            Map<Integer, List<String>> transpose = CDRUtil.transpose(bts2cnt);
            List<Integer> counts = new ArrayList<Integer>(transpose.keySet());
            Collections.sort(counts);
            Collections.reverse(counts);
                    
            for (int count : counts) {
                List<String> btss = transpose.get(count);
                for (String bts : btss) {
                    bw.write(file.getName() + "\t" + bts + "\t" + bts2cnt.get(bts));
                    bw.newLine();
                }
            }
            bw.close();
        }
    }
    
    private void handlingCount(Map<String, Integer> where2cnt, CDR cdr) {
        String initPoint = cdr.getInitCellID();
        String finPoint = cdr.getFinCellID();

        initPoint = CDRUtil.getCell(initPoint).getBTSID();
        finPoint = CDRUtil.getCell(finPoint).getBTSID();

        int old = ((where2cnt.get(initPoint)) != null) ?where2cnt.get(initPoint) :0;
        where2cnt.put(initPoint, old+1);
        
        if (!initPoint.equals(finPoint)) {
            old = ((where2cnt.get(finPoint)) != null) ?where2cnt.get(finPoint) :0;
            where2cnt.put(finPoint, old+1);
        }
    }
}
