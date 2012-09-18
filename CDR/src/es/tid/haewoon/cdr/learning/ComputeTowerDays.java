package es.tid.haewoon.cdr.learning;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import es.tid.haewoon.cdr.util.CDR;
import es.tid.haewoon.cdr.util.CDRUtil;
import es.tid.haewoon.cdr.util.Constants;

public class ComputeTowerDays {
    Logger logger = Logger.getLogger(ComputeTowerDays.class);
    
    public static void main(String[] args) throws IOException {
        (new ComputeTowerDays()).run(Constants.RESULT_PATH + File.separator + "2_3_tower_days");
    }
    
    private void run(String towerDaysDirectory) throws IOException {
        boolean success = (new File(towerDaysDirectory)).mkdir();
        if (success) {
            logger.info("A directory [" + towerDaysDirectory + "] is created");
        }
        int processed = 0;
        for (String number : CDRUtil.getOrderedNumbers()) {
            processed++;
            if (processed % 100 == 0) {
                logger.debug("processing [" + processed + "] files");
            }
            Map<String, List<Integer>> bts2days = new HashMap<String, List<Integer>>();
            
            count_days(bts2days, Constants.FILTERED_PATH + File.separator + "6_1_focused_home_hours", number);
            count_days(bts2days, Constants.FILTERED_PATH + File.separator + "6_2_focused_work_hours", number);
            count_days(bts2days, Constants.FILTERED_PATH + File.separator + "6_3_focused_commuting_hours", number);
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(towerDaysDirectory + File.separator + number));
            Map<String, Integer> bts2cnt = new HashMap<String, Integer>();
            for (String bts : bts2days.keySet()) {
//                logger.debug(bts2days.get(bts) + "\t" + bts2days.get(bts).size());
                bts2cnt.put(bts, bts2days.get(bts).size());
            }
            
            // sort by value
            Map<Integer, List<String>> transpose = CDRUtil.transpose(bts2cnt);
            List<Integer> counts = new ArrayList<Integer>(transpose.keySet());
            Collections.sort(counts);
            Collections.reverse(counts);
            
            for (int count : counts) {
                List<String> btss = transpose.get(count);
                for (String bts : btss) {
                    StringBuffer sb = new StringBuffer();
                    String delim = "";
                    List<Integer> days = bts2days.get(bts); 
                    Collections.sort(days);
                    for (int day : days) {
                        sb.append(delim + day);
                        delim = "|";
                    }
                    bw.write(number + "\t" + bts + "\t" + bts2cnt.get(bts) + "\t" + sb.toString());
                    bw.newLine();
                }
            }
            bw.close();
            
            
            // compute duration for cluster
            
//            bw = new BufferedWriter(new FileWriter(durationDirectory + File.separator + number));
//            Map<String, Integer> bts2duration = new HashMap<String, Integer>();
//            for (String bts : bts2days.keySet()) {
//                List<Integer> durations = bts2days.get(bts);
//                Collections.sort(durations);
//                bts2duration.put(bts, durations.get(durations.size()-1) - durations.get(0));
//            }
//            
//            // sort by value
//            transpose = CDRUtil.transpose(bts2duration);
//            counts = new ArrayList<Integer>(transpose.keySet());
//            Collections.sort(counts);
//            Collections.reverse(counts);
//            
//            for (int count : counts) {
//                List<String> btss = transpose.get(count);
//                for (String bts : btss) {
//                    bw.write(number + "\t" + bts + "\t" + bts2duration.get(bts));
//                    bw.newLine();
//                }
//            }
//            bw.close();
        }
    }
    
    private void count_days(Map<String, List<Integer>> bts2days, String path, String number) throws IOException {
        BufferedReader br;
        String line;
        try {
            br = new BufferedReader(new FileReader(path + File.separator + number));
        } catch (FileNotFoundException fnfe) {
            logger.error("File Not Found [" + path + File.separator + number + "]");
            return;
        }
        while((line = br.readLine()) != null) {
            CDR cdr;
            try {
                cdr = new CDR(line);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(cdr.getDatetime());
                
                String initPoint = cdr.getInitCellID();
                String finPoint = cdr.getFinCellID();

                initPoint = CDRUtil.getCell(initPoint).getBTSID();
                finPoint = CDRUtil.getCell(finPoint).getBTSID();

                List<Integer> old = (bts2days.get(initPoint) != null) ?bts2days.get(initPoint) :new ArrayList<Integer>();
                if (!old.contains(calendar.get(Calendar.DAY_OF_YEAR))) {
                    old.add(calendar.get(Calendar.DAY_OF_YEAR));
                }
                bts2days.put(initPoint, old);
                
                if (initPoint != finPoint) {
                    old = (bts2days.get(finPoint) != null) ?bts2days.get(finPoint) :new ArrayList<Integer>();
                    if (!old.contains(calendar.get(Calendar.DAY_OF_YEAR))) {
                        old.add(calendar.get(Calendar.DAY_OF_YEAR));
                    }
                    bts2days.put(finPoint, old);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                logger.error("Happens at [" + line + "]", e);
            }
        }
    }
}
