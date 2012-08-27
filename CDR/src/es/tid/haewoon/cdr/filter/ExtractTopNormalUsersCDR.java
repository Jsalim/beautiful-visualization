package es.tid.haewoon.cdr.filter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
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

@Deprecated
public class ExtractTopNormalUsersCDR {
    TelephoneNumberFilter tnf;
    private static Logger logger = Logger.getLogger(ExtractTopNormalUsersCDR.class);
    public static final int TOP_K = 10000;
    private Map<String, Integer> num2Rank = new HashMap<String, Integer>();
    private Map<String, Integer> num2Count = new HashMap<String, Integer>();
    
    public ExtractTopNormalUsersCDR() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(
                Constants.RESULT_PATH + File.separator + "1_count_basic_statistics" + File.separator + "all.caller"));
        String line;
        Set<String> s = new HashSet<String>();
        
        while((line = br.readLine()) != null) {
            if (s.size() >= 10000) {    // up to 10000 users
                break;
            }
            
            String[] tokens = line.split("\t");
            String number = tokens[0].trim();
            int count = Integer.valueOf(tokens[1]);
            if (count <= Constants.DAYS * 3) {
                s.add(number);
                num2Rank.put(number, s.size());
                num2Count.put(number, count);
            }
        }
        
        logger.info(s.size());
        tnf = new TelephoneNumberFilter(s);
    }
    
    public void run(String loadPath, String targetDirectory) throws IOException {
        boolean success = (new File(targetDirectory)).mkdir();
        if (success) {
            logger.debug("[" + targetDirectory + "] directory created");
        }
        
        List<File> files = CDRUtil.loadFiles(loadPath, Constants.RAW_DATA_FILE_PATTERN);
        Collections.sort(files, new RawFileComparator());
        logger.debug(files.size());
        
        for (File file: files) {
            logger.debug("processing " + file);
            String line = "";
            BufferedReader br = new BufferedReader(new FileReader(file));
            
            while((line = br.readLine()) != null) {
                CDR cdr;
                try {
                    cdr = new CDR(line);
                    if (tnf.filter(cdr)) {
                        String movistarNum = cdr.getMovistarNum();
                        BufferedWriter bw = new BufferedWriter(new FileWriter(
                                targetDirectory + File.separator + num2Rank.get(movistarNum) + "-" + movistarNum + 
                                "-" + num2Count.get(movistarNum), true));
                        bw.write(line.trim());
                        bw.newLine();
                        bw.close();
                    } else {
                        // filter inactive numbers
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
            }
            
            br.close();
            
        }

    }
    
    public static void main(String[] args) throws IOException {
        (new ExtractTopNormalUsersCDR()).run(
                Constants.FILTERED_PATH + File.separator + "6_1_normal_home_hours",
                Constants.FILTERED_PATH + File.separator + "7_1_top_" + TOP_K + "_nusers_home_hours");
        (new ExtractTopNormalUsersCDR()).run(
                Constants.FILTERED_PATH + File.separator + "6_2_normal_work_hours",
                Constants.FILTERED_PATH + File.separator + "7_2_top_" + TOP_K + "_nusers_work_hours");
        (new ExtractTopNormalUsersCDR()).run(
                Constants.FILTERED_PATH + File.separator + "6_3_normal_commuting_hours",
                Constants.FILTERED_PATH + File.separator + "7_3_top_" + TOP_K + "_nusers_commuting_hours");
    }

}
