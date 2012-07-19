package es.tid.haewoon.cdr;

import java.io.File;

public class Constants {
    // no constructor
    private Constants() { throw new AssertionError(); }
    
    public static final String BASE_PATH = "/workspace/CDR_data";
    public static final String RESULT_PATH = BASE_PATH + File.separator + "result";
    public static final String FILTERED_PATH = BASE_PATH + File.separator + "filtered_data";
    
    public static final String BARCELONA_PATH = FILTERED_PATH + File.separator + "1_barcelona";
    public static final String MOVISTAR_TO_OTHERS_PATH = FILTERED_PATH + File.separator + "2_movistar_to_others";
    public static final String COMMUTING_HOURS_PATH = FILTERED_PATH + File.separator + "3_commuting_hours";
    
    public static final String CELL_INFO_FILE_NAME = "GASSET_CELULA_900913.TXT";
}
