package es.tid.haewoon.cdr.filter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.apache.log4j.Logger;

import es.tid.haewoon.cdr.util.CDR;
import es.tid.haewoon.cdr.util.CDRUtil;
import es.tid.haewoon.cdr.util.Constants;
import es.tid.haewoon.cdr.util.Province;

/*
 * Home / Work / Commuting hours
 */
public class ExtractHomeWorkAndCommutingHours {
    private final static Logger logger = Logger.getLogger(ExtractHomeWorkAndCommutingHours.class);
    private final static String homeTargetPath = Constants.FILTERED_PATH + File.separator + "3_1_home_hours";
    private final static String workTargetPath = Constants.FILTERED_PATH + File.separator + "3_2_work_hours";
    private final static String commTargetPath = Constants.FILTERED_PATH + File.separator + "3_3_commuting_hours";
    
    public static void main(String[] args) throws IOException, ParseException {
        boolean success = (new File(homeTargetPath)).mkdir();
        if (success) {
            logger.debug("A directory [" + homeTargetPath + "] is created");
        }
        
        success = (new File(workTargetPath)).mkdir();
        if (success) {
            logger.debug("A directory [" + workTargetPath + "] is created");
        }
        
        success = (new File(commTargetPath)).mkdir();
        if (success) {
            logger.debug("A directory [" + workTargetPath + "] is created");
        }
        
        (new ExtractHomeWorkAndCommutingHours()).run();
    }
    
    private void run() throws IOException {
        List<File> files = CDRUtil.loadFiles(Constants.FILTERED_PATH + File.separator + "1_barcelona", "^F1_GASSET_VOZ_\\d{4}2009$");

        CDRFilter hFilter1 = new HourFilter(19, 24);     // from 7pm to 24
        CDRFilter hFilter2 = new HourFilter(0, 7);     // from 0 to 7am
        CDRFilter wFilter = new HourFilter(13, 17);   // from 1pm to 5pm
        CDRFilter weekdayFilter = new WeekdayFilter();

        CDRFilter cFilter1 = new HourFilter(7, 9);   // from 7 to 9am
        CDRFilter cFilter2 = new HourFilter(17, 19);     // from 5 to 7pm
        
        String line;
        for (File file: files) {
            logger.debug("processing " + file);
            BufferedReader br = new BufferedReader(new FileReader(file));
            BufferedWriter hbw = new BufferedWriter(new FileWriter(homeTargetPath + File.separator + file.getName()));
            BufferedWriter wbw = new BufferedWriter(new FileWriter(workTargetPath + File.separator + file.getName()));
            BufferedWriter cbw = new BufferedWriter(new FileWriter(commTargetPath + File.separator + file.getName()));

            while((line = br.readLine()) != null) {
                try {
                    CDR cdr = new CDR(line);
                    if (weekdayFilter.filter(cdr)) {
                        // weekday
                        if (hFilter1.filter(cdr) || hFilter2.filter(cdr)) {
                            hbw.write(line.trim());
                            hbw.newLine();
                        } else if (wFilter.filter(cdr)) {
                            wbw.write(line.trim());
                            wbw.newLine();
                        } else if (cFilter1.filter(cdr) || cFilter2.filter(cdr)) {
                            cbw.write(line.trim());
                            cbw.newLine();
                        }
                    } else {
                        // weekend
                        if (hFilter1.filter(cdr) || hFilter2.filter(cdr)) {
                            hbw.write(line.trim());
                            hbw.newLine();
                        }
                    }
                } catch (Exception e) {
                    logger.error(line);
                    logger.error(e);
                }
            }
            br.close();
            hbw.close();
            wbw.close();
            cbw.close();
        }
    }
}
