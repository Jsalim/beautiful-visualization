package es.tid.haewoon.food.util;

import java.io.File;
import java.util.Map;

import org.apache.log4j.Logger;

public class Constants {
    static Logger logger = Logger.getLogger(Constants.class);
    
    static {
        if (System.getProperty("os.name").startsWith("Windows")) {
            ENGLISH_ROOT = "F:\\Dropbox\\elBulli\\English";
        } else {
            ENGLISH_ROOT = "/workspace/Food/elBulli/";
        }
        
        if (System.getProperty("os.name").startsWith("Windows")) {
//            RESULT_PATH = "F:\\Dropbox\\elBulli\\result";
        	RESULT_PATH = "C:\\Users\\usuario\\Dropbox\\elBulli\\result";
        } else {
            RESULT_PATH = "/workspace/Food/result";
        }
    }
    
    public static void main(String[] args) {
        for (Map.Entry<Object, Object> e : System.getProperties().entrySet()) {
            logger.debug(e);
        }
        
        logger.debug(ENGLISH_ROOT);
        logger.debug(CD2_DATA_PATH);
    }
    
    public final static String ENGLISH_ROOT;
    
    public final static String CD2_ROOT = ENGLISH_ROOT + File.separator + "CD2";
    
    
    public final static String CD2_DATA_PATH = CD2_ROOT + File.separator + "us" + File.separator + "data";
    public final static String CD2_RECIPES_PATH = CD2_DATA_PATH + File.separator + "recetas";
    public final static String CD2_PREPARATION_SWEET_PATH = CD2_DATA_PATH + File.separator + "elaboraciones" + File.separator + "dulce";
    public final static String CD2_PREPARATION_SAVOURY_PATH = CD2_DATA_PATH + File.separator + "elaboraciones" + File.separator + "salado";
    public final static String CD2_STYLE_SWEET_PATH = CD2_DATA_PATH + File.separator + "estilos" + File.separator + "dulce";
    public final static String CD2_STYLE_SAVOURY_PATH = CD2_DATA_PATH + File.separator + "estilos" + File.separator + "salado";
    
    // CD3 is completely different from CD2, especially the path of an evolutionary analysis
    public final static String CD3_ROOT = ENGLISH_ROOT + File.separator + "CD3";
    public final static String CD3_DATA_PATH = CD3_ROOT + File.separator + "us" + File.separator + "data";
    public final static String CD3_RECIPES_PATH = CD3_DATA_PATH;
    public final static String CD3_PREPARATION_PATH = CD3_DATA_PATH + File.separator + "elaboraciones";
    public final static String CD3_STYLE_SWEET_PATH = CD3_DATA_PATH + File.separator + "estilos";
    public final static String CD3_SWEET_KEYWORD = "IN THE SWEET WORLD";    // titulo=... IN THE SWEET WORLD
    public final static String CD3_SAVOURY_KEYWORLD = "IN THE SAVOURY WORLD";    // titulo=... IN THE SAVOURY WORLD
    
    public final static String RECIPE_PATTERN = "^\\d{3}\\.dat";
    public final static String CD3_PREPARATION_PATTERN ="^\\d{1,2}\\.txt";
    
    public final static String RESULT_PATH;
}
