package es.tid.haewoon.cdr.filter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import es.tid.haewoon.cdr.util.CDR;
import es.tid.haewoon.cdr.util.CDRUtil;
import es.tid.haewoon.cdr.util.Constants;

public class ExtractNormalUsers {
    Logger logger = Logger.getLogger(ExtractNormalUsers.class);
    CallerFilter cFilter;
    
    public ExtractNormalUsers(int min, int max) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(
                Constants.RESULT_PATH + File.separator + "1_count_basic_statistics" + File.separator + "all.caller"));
        String line;
        Set<String> s = new HashSet<String>();
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split("\t");
            String number = tokens[0];
            int calls = Integer.valueOf(tokens[1]);
            
            if (calls >= min * Constants.DAYS && calls <= max * Constants.DAYS) {
                s.add(number);
            }
        }
        logger.debug("# of people who have 1 to 3 calls per day: " + s.size());
        cFilter = new CallerFilter(s);
    }
    
    public static void main(String[] args) throws IOException {
        int minimum = 1;
        int maximum = 3;
        ExtractNormalUsers enu = new ExtractNormalUsers(minimum, maximum);
        enu.run(Constants.FILTERED_PATH + File.separator + "5_1_sorted_home_hours", 
                Constants.FILTERED_PATH + File.separator + "6_1_normal_home_hours");
        enu.run(Constants.FILTERED_PATH + File.separator + "5_2_sorted_work_hours", 
                Constants.FILTERED_PATH + File.separator + "6_2_normal_work_hours");
        enu.run(Constants.FILTERED_PATH + File.separator + "5_3_sorted_commuting_hours", 
                Constants.FILTERED_PATH + File.separator + "6_3_normal_commuting_hours");
    }
    
    private void run(String loadingPath, String targetDirectory) throws IOException {
        boolean success = (new File(targetDirectory)).mkdir();
        if (success) {
            logger.debug("[" + targetDirectory + "] is created...");
        }
        
        List<File> files = CDRUtil.loadFiles(loadingPath, Constants.RAW_DATA_FILE_PATTERN);
        for (File file : files) {
            logger.debug("processing " + file);
            String line = "";
            BufferedReader br = new BufferedReader(new FileReader(file));
            BufferedWriter bw = new BufferedWriter(new FileWriter(targetDirectory + File.separator + file.getName()));
            
            while((line = br.readLine()) != null) {
                CDR cdr;
                try {
                    cdr = new CDR(line);
                    if (cFilter.filter(cdr)) {
                        bw.write(line.trim());
                        bw.newLine();
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
            bw.close();
        }
    }
}
