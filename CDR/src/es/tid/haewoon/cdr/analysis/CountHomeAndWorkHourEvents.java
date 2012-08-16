package es.tid.haewoon.cdr.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import es.tid.haewoon.cdr.filter.ExtractTopNormalUsersCDR;
import es.tid.haewoon.cdr.filter.TelephoneNumberFilter;
import es.tid.haewoon.cdr.util.CDR;
import es.tid.haewoon.cdr.util.CDRUtil;
import es.tid.haewoon.cdr.util.Constants;

public class CountHomeAndWorkHourEvents {
    private static Logger logger = Logger.getLogger(CountHomeAndWorkHourEvents.class);
    
    private Map<String, Integer> num2Rank = new HashMap<String, Integer>();
    private Map<String, Integer> num2Count = new HashMap<String, Integer>();
    TelephoneNumberFilter tnFilter;
    
    private String fileToReadPath;
    private String targetPath;
    Map<String, Map<String, Integer>> num2cell2cnt = new HashMap<String, Map<String, Integer>>();
    Map<String, Map<String, Integer>> num2bts2cnt = new HashMap<String, Map<String, Integer>>();
    
    public static void main(String[] args) throws IOException, ParseException {
        (new CountHomeAndWorkHourEvents(Constants.FILTERED_PATH + File.separator + "6_1_normal_home_hours", 
                Constants.RESULT_PATH + File.separator + "2_1_count_home_hour_events")).run();
        
        (new CountHomeAndWorkHourEvents(Constants.FILTERED_PATH + File.separator + "6_2_normal_work_hours", 
                Constants.RESULT_PATH + File.separator + "2_2_count_work_hour_events")).run();
        
    }
    
    public CountHomeAndWorkHourEvents(String fileToReadPath, String targetPath) throws IOException {
        
        this.fileToReadPath = fileToReadPath;
        this.targetPath = targetPath;
        
        boolean success = (new File(targetPath)).mkdir();
        if (success) {
            logger.debug("A directory [" + targetPath + "] is created");
        }
        
        String line = "";
        Set<String> s = new HashSet<String>();

        BufferedReader br = new BufferedReader(new FileReader(
                Constants.RESULT_PATH + File.separator + "1_count_basic_statistics" + File.separator + "all.caller"));

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
        tnFilter = new TelephoneNumberFilter(s);
    }
    
    private void run() throws IOException, ParseException {
        List<File> files = CDRUtil.loadFiles(this.fileToReadPath, Constants.RAW_DATA_FILE_PATTERN);
        
        for (File file: files) { 
            logger.debug("processing " + file);
            String line = "";
            BufferedReader br = new BufferedReader(new FileReader(file));
            
            while((line = br.readLine()) != null) {
                CDR cdr;
                cdr = new CDR(line);
                if (tnFilter.filter(cdr)) {
                    try {
                    handlingCount(num2cell2cnt, cdr, true);
                    handlingCount(num2bts2cnt, cdr, false);
                    } catch (Exception e) {
                        logger.error(line);
                        logger.error(cdr.getMovistarNum());
                        logger.error(cdr.getInitCellID());
                        logger.error(cdr.getFinCellID());
                    }
                }
            }
        }
        printNum2Where2Cnt();
    }
    
    // if & only if num2XXX2cnt is num2cell2cnt, isCell is true 
    private void handlingCount(Map<String, Map<String, Integer>> num2where2cnt, CDR cdr, boolean isCell) {
        String movistarNum = cdr.getMovistarNum();
        String initPoint = cdr.getInitCellID();
        String finPoint = cdr.getFinCellID();
        
        if (!isCell) {
            initPoint = CDRUtil.getCell(initPoint).getBTSID();
            finPoint = CDRUtil.getCell(finPoint).getBTSID();
        }
        
        Map<String, Integer> candidate = num2where2cnt.get(movistarNum);
        Map<String, Integer> where2cnt = ((candidate != null)) ?candidate :new HashMap<String, Integer>();
        
        int old = ((where2cnt.get(initPoint)) != null) ?where2cnt.get(initPoint) :0;
        where2cnt.put(initPoint, old+1);
        
        if (!initPoint.equals(finPoint)) {
            old = ((where2cnt.get(finPoint)) != null) ?where2cnt.get(finPoint) :0;
            where2cnt.put(finPoint, old+1);
        }
        
        num2where2cnt.put(movistarNum, where2cnt);
    }
    
    private void printNum2Where2Cnt() throws IOException {
        for (String movistarNum : num2cell2cnt.keySet()) {
            Map<String, Integer> cell2cnt = num2cell2cnt.get(movistarNum);
            Map<String, Integer> bts2cnt = num2bts2cnt.get(movistarNum);
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(
                    this.targetPath + File.separator + num2Rank.get(movistarNum) + "-" + movistarNum + "-" + num2Count.get(movistarNum)));
            
            for (String cell : cell2cnt.keySet()) {
                String bts = CDRUtil.getCell(cell).getBTSID();
                bw.write(movistarNum + "\t" + cell + "\t" + bts + "\t" + cell2cnt.get(cell) + "\t" + bts2cnt.get(bts));
                bw.newLine();
            }
            
            bw.close();
        }
    }

}
