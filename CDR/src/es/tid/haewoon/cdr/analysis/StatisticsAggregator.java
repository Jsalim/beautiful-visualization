package es.tid.haewoon.cdr.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import es.tid.haewoon.cdr.util.CDRUtil;
import es.tid.haewoon.cdr.util.Constants;

public class StatisticsAggregator {
    private static Logger logger = Logger.getLogger(StatisticsAggregator.class);
    
    public static void main(String[] args) throws IOException {
        StatisticsAggregator sa = new StatisticsAggregator();
        
        String[] extensions = {"callee", "caller", "caller_ee", "cell", "duration"}; 
        for (String extension: extensions) {
            logger.debug("processing all [." + extension + "]");
            Map<String, Integer> result = sa.run(
                    Constants.RESULT_PATH + File.separator + "1_count_basic_statistics", "^.*" + extension + "$");
            CDRUtil.printMapSortedByValue(
                    Constants.RESULT_PATH + File.separator + "1_count_basic_statistics" + File.separator + "all."+ extension, result);
        }
    }
    
    public Map<String, Integer> run(String basePath, String pattern) throws IOException {
        List<File> files = CDRUtil.loadFiles(basePath, pattern);
        
        Map<String, Integer> agg = new HashMap<String, Integer>();
        for (File file: files) {
            logger.debug("processing " + file);
            String line = "";
            BufferedReader br = new BufferedReader(new FileReader(file));
            while((line = br.readLine()) != null) {
                String[] keyValue = line.split("\\t");
                agg = CDRUtil.countItem(agg, keyValue[0], Integer.valueOf(keyValue[1]));
            }
        }
        
        return agg;
    }
}
