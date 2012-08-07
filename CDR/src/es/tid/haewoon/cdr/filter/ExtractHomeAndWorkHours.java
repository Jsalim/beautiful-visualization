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

public class ExtractHomeAndWorkHours {
    private final static Logger logger = Logger.getLogger(ExtractHomeAndWorkHours.class);
    private final static String homeTargetPath = Constants.FILTERED_PATH + File.separator + "3_1_home_hours";
    private final static String workTargetPath = Constants.FILTERED_PATH + File.separator + "3_2_work_hours";
    public static void main(String[] args) throws IOException, ParseException {
        boolean success = (new File(homeTargetPath)).mkdir();
        if (success) {
            logger.debug("A directory [" + homeTargetPath + "] is created");
        }
        
        success = (new File(workTargetPath)).mkdir();
        if (success) {
            logger.debug("A directory [" + workTargetPath + "] is created");
        }
        
        ExtractHomeAndWorkHours ehh = new ExtractHomeAndWorkHours();
        ehh.run();
    }
    
    private void run() throws IOException {
        List<File> files = CDRUtil.loadFiles(Constants.MOVISTAR_TO_OTHERS_PATH, "^F1_GASSET_VOZ_\\d{1,2}092009$");

        CDRFilter hFilter1 = new HourFilter(19, 24);     // from 7pm to 24
        CDRFilter hFilter2 = new HourFilter(0, 7);     // from 0 to 7am
        CDRFilter wFilter = new HourFilter(13, 17);   // from 1pm to 5pm
        CDRFilter weekdayFilter = new WeekdayFilter();
 
        String line;
        for (File file: files) {
            logger.debug("processing " + file);
            BufferedReader br = new BufferedReader(new FileReader(file));
            BufferedWriter hbw = new BufferedWriter(new FileWriter(homeTargetPath + File.separator + file.getName()));
            BufferedWriter wbw = new BufferedWriter(new FileWriter(workTargetPath + File.separator + file.getName()));

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
        }
    }
}
