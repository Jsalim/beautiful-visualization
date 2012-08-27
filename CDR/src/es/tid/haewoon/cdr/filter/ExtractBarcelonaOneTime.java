package es.tid.haewoon.cdr.filter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import es.tid.haewoon.cdr.util.CDR;
import es.tid.haewoon.cdr.util.CDRUtil;
import es.tid.haewoon.cdr.util.Constants;
import es.tid.haewoon.cdr.util.RawFileComparator;
import es.tid.haewoon.cdr.util.Province;


/*
 * One time class
 * All Barcelona cells -> cells in Barcelona Box
 */
public class ExtractBarcelonaOneTime {
    Logger logger = Logger.getLogger(ExtractBarcelonaOneTime.class);
    
    CDRFilter barcelonaFilter = new CellFilter(CDRUtil.getCells(Province.BARCELONA));
    public static void main(String[] args) throws IOException {
        (new ExtractBarcelonaOneTime()).run(Constants.FILTERED_PATH + File.separator + "bak" + File.separator + "5_1_sorted_home_hours",
                                            Constants.FILTERED_PATH + File.separator + "5_1_sorted_home_hours");
        (new ExtractBarcelonaOneTime()).run(Constants.FILTERED_PATH + File.separator + "bak" + File.separator + "5_2_sorted_work_hours",
                Constants.FILTERED_PATH + File.separator + "5_2_sorted_work_hours");
        (new ExtractBarcelonaOneTime()).run(Constants.FILTERED_PATH + File.separator + "bak" + File.separator + "5_3_sorted_commuting_hours",
                Constants.FILTERED_PATH + File.separator + "5_3_sorted_commuting_hours");
    }
    
    private void run(String loadPath, String targetDirectory) throws IOException {
        boolean success = (new File(targetDirectory)).mkdir();
        if (success) {
            logger.debug("A directory [" + targetDirectory + "] is created");
        }
        
        List<File> files = CDRUtil.loadFiles(loadPath, Constants.RAW_DATA_FILE_PATTERN);
        Collections.sort(files, new RawFileComparator());
        String line;
        
        
        for (File file : files) {
            logger.debug("processing " + file);
            BufferedReader br = new BufferedReader(new FileReader(file));
            BufferedWriter bw = new BufferedWriter(new FileWriter(targetDirectory + File.separator + file.getName()));
            while((line = br.readLine()) != null) {
                try {
                    CDR cdr = new CDR(line);
                    if (barcelonaFilter.filter(cdr)) {
                        bw.write(line.trim());
                        bw.newLine();
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    logger.error("CDR Parse error? [" + line + "]", e);
                }
            }
        }
        
    }
}
