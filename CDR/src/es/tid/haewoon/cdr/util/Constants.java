package es.tid.haewoon.cdr.util;

import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.FRIDAY;
import static java.util.Calendar.MONDAY;
import static java.util.Calendar.THURSDAY;
import static java.util.Calendar.TUESDAY;
import static java.util.Calendar.WEDNESDAY;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

public class Constants {
    static Logger logger = Logger.getLogger(Constants.class); 
    // no constructor
    private Constants() { throw new AssertionError(); }
    
    public static final String RAW_DATA_FILE_PATTERN = "^F1_GASSET_VOZ_.*2009$";
    public static final String TELNUM_FILE_PATTERN = "^\\d+$";
    
    public static final String BASE_PATH = "/workspace/CDR";
    public static final String RAW_DATA_PATH = BASE_PATH + File.separator + "raw";
    public static final String RESULT_PATH = BASE_PATH + File.separator + "result";
    public static final String FILTERED_PATH = BASE_PATH + File.separator + "filtered";
    
    public static final Province PROVINCE = Province.BARCELONA;
    
    
    public static void main(String[] args) throws Exception {
        logger.debug(CDRUtil.loadAllCDRFiles().size()/3);
        
        List<File> files = CDRUtil.loadFiles(Constants.RESULT_PATH + File.separator + "1_count_basic_statistics", 
                            "^F1.*caller$");
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        Calendar calendar = Calendar.getInstance();

        int count = 0;
        for (File file: files) {
            Date date = sdf.parse(file.getName().split("\\.")[0].split("_")[3]);
            calendar.setTime(date);
            int day_of_week = calendar.get(DAY_OF_WEEK);
            
            if (!(day_of_week == MONDAY || day_of_week == TUESDAY || day_of_week == WEDNESDAY || 
                    day_of_week == THURSDAY || day_of_week == FRIDAY)) {
                continue;
            }
            count++;
        }
        logger.info("weekday count: " + count);
    }
    
    public static final int NUMBER_OF_DAYS = 55;
    public static final int NUMBER_OF_WEEKDAYS_IN_BARCELONA = 38;
    
    @Deprecated
    public static final String BARCELONA_PATH = FILTERED_PATH + File.separator + "1_barcelona";
    
    @Deprecated
    public static final String MOVISTAR_TO_OTHERS_PATH = FILTERED_PATH + File.separator + "2_movistar_to_others";
    
    @Deprecated
    public static final String COMMUTING_HOURS_PATH = FILTERED_PATH + File.separator + "3_commuting_hours";
    
    @Deprecated
    public static final String SORTED_COMMUTING_HOURS_PATH = FILTERED_PATH + File.separator + "5_sorted_commuting_hours";
    
    @Deprecated
    public static final String CELL_INFO_FILE_NAME = "GASSET_CELULA_900913.TXT";
//    public static final String CELL_INFO_FILE_NAME = "GASSET_CELULA.TXT";
//    public static final String CELL_INFO_FILE_NAME = "cells_spain.txt";
//    public static final String BARCELONA_CELL_INFO_PATH = BASE_PATH + File.separator + "GASSET_CELULA_BCN_haewoon.TXT";
    public static final String BARCELONA_CELL_INFO_PATH = BASE_PATH + File.separator + "GASSET_CELULA_BCN_Box_haewoon.TXT";
    
    public static final double[][] BARCELONA_BOX = {{41.452505, 2.05513}, {41.336607, 2.261124}};
}
