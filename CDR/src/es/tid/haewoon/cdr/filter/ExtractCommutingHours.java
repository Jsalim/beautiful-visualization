package es.tid.haewoon.cdr.filter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Logger;

import es.tid.haewoon.cdr.util.CDR;
import es.tid.haewoon.cdr.util.CDRUtil;
import es.tid.haewoon.cdr.util.Constants;

public class ExtractCommutingHours {
    private final static Logger logger = Logger.getLogger(ExtractCommutingHours.class);
    public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
        CDRFilter hToWFilter = new HourFilter(7, 10);   // from 7 to 10 o'clock
        CDRFilter wToHFilter = new HourFilter(17, 20);     // from 17 to 20 o'clock
        CDRFilter weekdayFilter = new WeekdayFilter();
        
        ExtractCommutingHours eodm = new ExtractCommutingHours();
        List<File> files = CDRUtil.loadFiles(Constants.MOVISTAR_TO_OTHERS_PATH, "^F1_GASSET_VOZ_\\d{1,2}092009$");
        
        String targetPath = Constants.FILTERED_PATH + File.separator + "3_commuting_hours";
        boolean success = (new File(targetPath)).mkdir();
        if (success) {
            logger.debug("A directory [" + targetPath + "] is created");
        }
        
        String hToWPath = targetPath + File.separator + hToWFilter.toString();
        success = (new File(hToWPath)).mkdir();
        if (success) {
            logger.debug("A directory [" + hToWPath + "] is created");
        }      
        
        String wToHPath = targetPath + File.separator + wToHFilter.toString();
        success = (new File(wToHPath)).mkdir();
        if (success) {
            logger.debug("A directory [" + wToHPath + "] is created");
        }  
        
        String line;
        
        for (File file: files) {
            logger.debug("processing " + file);
            BufferedReader br = new BufferedReader(new FileReader(file));
            BufferedWriter hToWBW = new BufferedWriter(new FileWriter(hToWPath + File.separator + file.getName()));
            BufferedWriter wToHBW = new BufferedWriter(new FileWriter(wToHPath + File.separator + file.getName()));
            
            while((line = br.readLine()) != null) {
                try {
                    CDR cdr = new CDR(line);
                    if (weekdayFilter.filter(cdr)) {
                        // only for weekday
                        if (hToWFilter.filter(cdr)) {
                            hToWBW.write(line.trim());
                            hToWBW.newLine();
                        } else if (wToHFilter.filter(cdr)) {
                            wToHBW.write(line.trim());
                            wToHBW.newLine();
                        }
                    }
                } catch (Exception e) {
                    System.out.println(line);
                    e.printStackTrace();    // something wrong
                }
            }
            br.close();
            hToWBW.close();
            wToHBW.close();
        }
    }
}
