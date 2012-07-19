package es.tid.haewoon.cdr.statistics;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import es.tid.haewoon.cdr.util.CDRUtil;
import es.tid.haewoon.cdr.util.Constants;

public class StatisticsAggregator {
    private static Logger logger = Logger.getLogger(StatisticsAggregator.class);
    
    /* test */
    public static void main(String[] args) throws FileNotFoundException {
        StatisticsAggregator sa = new StatisticsAggregator();
        
        String[] extensions = {"cl2c", "dn2c", "nb2c", "du2c", "on2c"}; 
        for (String extension: extensions) {
            logger.debug("processing all [." + extension + "]");
            Map<String, Integer> result = sa.run(Constants.RESULT_PATH + "/count_basic_statistics/", "^.*" + extension + "$");
            CDRUtil.printMapSortedByValue(Constants.RESULT_PATH + "/count_basic_statistics/all." + extension, result);
        }
    }
    
    public static Map run(String basePath, String pattern) throws FileNotFoundException {
        List<File> files = CDRUtil.loadFiles(basePath, pattern);
        
        Map<String, Integer> agg = new HashMap<String, Integer>();
        for (File file: files) {
            logger.debug("processing " + file);
            String line = "";
            BufferedReader br = new BufferedReader(new FileReader(file));
            try {
                while((line = br.readLine()) != null) {
                    String[] keyValue = line.split("\\t");
                    agg = CDRUtil.countItem(agg, keyValue[0], Integer.valueOf(keyValue[1]));
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
        
        return agg;
    }
}
