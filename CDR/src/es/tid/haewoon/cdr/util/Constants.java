package es.tid.haewoon.cdr.util;

import java.io.File;

import org.apache.log4j.Logger;

public class Constants {
    static Logger logger = Logger.getLogger(Constants.class); 
    // no constructor
    private Constants() { throw new AssertionError(); }
    
    public static final String RAW_DATA_FILE_PATTERN = "^F1_GASSET_VOZ_.*2009$";
    
    public static final String BASE_PATH = "/workspace/CDR";
    public static final String RAW_DATA_PATH = BASE_PATH + File.separator + "raw";
    public static final String RESULT_PATH = BASE_PATH + File.separator + "result";
    public static final String FILTERED_PATH = BASE_PATH + File.separator + "filtered";
    
    static {
        DAYS = CDRUtil.loadAllCDRFiles().size()/3;  // 3 is the number of hour types (home / work / commuting hours)
    }
    
    public static void main(String[] args) {
        logger.debug(DAYS);
    }
    
    public static final int DAYS;
    
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
    
    public static final double[][] BARCELONA_BOX = {{2.05513, 41.452505}, {2.261124, 41.336607}};
}
