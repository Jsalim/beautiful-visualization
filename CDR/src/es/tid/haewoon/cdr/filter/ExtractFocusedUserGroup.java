package es.tid.haewoon.cdr.filter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import es.tid.haewoon.cdr.util.CDR;
import es.tid.haewoon.cdr.util.CDRUtil;
import es.tid.haewoon.cdr.util.Constants;
import es.tid.haewoon.cdr.util.RawFileComparator;

public class ExtractFocusedUserGroup {
    Logger logger = Logger.getLogger(ExtractFocusedUserGroup.class);
    CDRFilter cFilter;
    
    public ExtractFocusedUserGroup(int min, int max) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(
                Constants.RESULT_PATH + File.separator + "1_4_count_basic_statistics_in_weekdays" + File.separator + "all.caller_ee"));
        String line;
        Set<String> s = new HashSet<String>();
        
        (new File(Constants.FILTERED_PATH + File.separator + "6_0_focused_user_group")).mkdir();
        BufferedWriter bw = new BufferedWriter(new FileWriter(
                Constants.FILTERED_PATH + File.separator + "6_0_focused_user_group" + File.separator + "all_numbers.txt"));
        
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split("\t");
            String number = tokens[0];
            int calls = Integer.valueOf(tokens[1]);
            
            if (calls >= min * Constants.NUMBER_OF_WEEKDAYS_IN_BARCELONA && calls <= max * Constants.NUMBER_OF_WEEKDAYS_IN_BARCELONA) {
                s.add(number);
                bw.write(number);
                bw.newLine();
            }
        }
        bw.close();
        logger.debug("# of users in the focused group: " + s.size());
        cFilter = new TelephoneNumberFilter(s);
    }
    
    public static void main(String[] args) throws IOException {
//        ExtractFocusedUserGroup enu = new ExtractFocusedUserGroup(1, 10);
        ExtractFocusedUserGroup enu = new ExtractFocusedUserGroup(2, 50);
        
        enu.run(Constants.FILTERED_PATH + File.separator + "5_1_sorted_home_hours", 
                Constants.FILTERED_PATH + File.separator + "6_1_focused_home_hours");
        enu.run(Constants.FILTERED_PATH + File.separator + "5_2_sorted_work_hours", 
                Constants.FILTERED_PATH + File.separator + "6_2_focused_work_hours");
        enu.run(Constants.FILTERED_PATH + File.separator + "5_3_sorted_commuting_hours", 
                Constants.FILTERED_PATH + File.separator + "6_3_focused_commuting_hours");
    }
    
    private void run(String loadingPath, String targetDirectory) throws IOException {
        boolean success = (new File(targetDirectory)).mkdir();
        if (success) {
            logger.debug("[" + targetDirectory + "] is created...");
        }
        
        List<File> files = CDRUtil.loadFiles(loadingPath, Constants.RAW_DATA_FILE_PATTERN);
        Collections.sort(files, new RawFileComparator());
        
        Map<String, List<String>> number2records = new HashMap<String, List<String>>();
        for (File file : files) {
            logger.debug("processing " + file);
            String line = "";
            BufferedReader br = new BufferedReader(new FileReader(file));
            while((line = br.readLine()) != null) {
                CDR cdr;
                try {
                    cdr = new CDR(line);
                    if (cFilter.filter(cdr)) {
                        String movistarNum = cdr.getMovistarNum();
                        List<String> old = (number2records.get(movistarNum) != null) ?number2records.get(movistarNum) :new ArrayList<String>();
                        old.add(line.trim());
                        number2records.put(movistarNum, old);
                    } 
                } catch (Exception e) {
                    logger.error("wrong-format CDR [" + line + "]", e);
                } catch (AssertionError e) {
                    logger.debug(line);
                    logger.fatal("something wrong / all CDRs here must have at least one Movistar number", e);
                    System.exit(0);
                }
            }
            br.close();
        }
        
        for (String number : number2records.keySet()) {
            BufferedWriter bw = new BufferedWriter(new FileWriter(targetDirectory + File.separator + number));
            for (String lineToWrite : number2records.get(number)) {
                bw.write(lineToWrite);
                bw.newLine();                        
            }
            bw.close();
        }
        
    }
}
