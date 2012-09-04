package es.tid.haewoon.cdr.analysis;

import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.FRIDAY;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.THURSDAY;
import static java.util.Calendar.TUESDAY;
import static java.util.Calendar.WEDNESDAY;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import es.tid.haewoon.cdr.util.CDRUtil;
import es.tid.haewoon.cdr.util.Constants;
import es.tid.haewoon.cdr.util.RawFileComparator;

public class StatisticsAggregator {
    Logger logger = Logger.getLogger(StatisticsAggregator.class);
    String[] extensions = {"callee", "caller", "caller_ee", "cell", "duration"};
    
    SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
    
    public static void main(String[] args) throws IOException, ParseException {
//        (new StatisticsAggregator()).run(Constants.RESULT_PATH + File.separator + "1_count_basic_statistics");
//        (new StatisticsAggregator()).run(Constants.RESULT_PATH + File.separator + "1_3_count_basic_statistics_in_commuting_hours");
        Set<Integer> WEEKDAYS = new HashSet<Integer>();
        WEEKDAYS.add(MONDAY);
        WEEKDAYS.add(TUESDAY);
        WEEKDAYS.add(WEDNESDAY);
        WEEKDAYS.add(THURSDAY);
        WEEKDAYS.add(FRIDAY);
        (new StatisticsAggregator()).run(Constants.RESULT_PATH + File.separator + "1_count_basic_statistics", 
                                         Constants.RESULT_PATH + File.separator + "1_4_count_basic_statistics_in_weekdays", 
                                         WEEKDAYS);
    }
    
    public void run(String loadingDirectory, String targetDirectory, Set<Integer> DAY_OF_THE_WEEK) throws IOException, ParseException {
        boolean success = (new File(targetDirectory)).mkdir();
        if (success) {
            logger.info("A directory [" + targetDirectory + "] is created");
        }
        
        for (String extension: extensions) {
            logger.debug("processing all [." + extension + "]");

            List<File> files = CDRUtil.loadFiles(loadingDirectory, "^F1.*" + extension + "$");
            Collections.sort(files, new RawFileComparator());
            
            Map<String, Integer> agg = new HashMap<String, Integer>();
            Calendar calendar = Calendar.getInstance();
            for (File file: files) {
                Date date = sdf.parse(file.getName().split("\\.")[0].split("_")[3]);
                calendar.setTime(date);
                int day_of_week = calendar.get(DAY_OF_WEEK);
                
                if (!(DAY_OF_THE_WEEK.contains(day_of_week))) {
                    continue;
                }
                
                logger.debug("processing " + file);
                String line = "";
                BufferedReader br = new BufferedReader(new FileReader(file));
                while((line = br.readLine()) != null) {
                    String[] keyValue = line.split("\\t");
                    agg = CDRUtil.countItem(agg, keyValue[0], Integer.valueOf(keyValue[1]));
                }
            }

            CDRUtil.printMapSortedByValue(targetDirectory + File.separator + "all."+ extension, agg);
        }
    }
}
