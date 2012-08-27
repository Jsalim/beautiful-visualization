package es.tid.haewoon.cdr.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import es.tid.haewoon.cdr.util.CDRUtil;
import es.tid.haewoon.cdr.util.Constants;
import es.tid.haewoon.cdr.util.RawFileComparator;

public class StatisticsAggregator {
    private static Logger logger = Logger.getLogger(StatisticsAggregator.class);
    String[] extensions = {"callee", "caller", "caller_ee", "cell", "duration"};
    
    public static void main(String[] args) throws IOException {
//        (new StatisticsAggregator()).run(Constants.RESULT_PATH + File.separator + "1_count_basic_statistics");
        (new StatisticsAggregator()).run(Constants.RESULT_PATH + File.separator + "1_3_count_basic_statistics_in_commuting_hours");
    }
    
    public void run(String loadingDirectory) throws IOException {
        for (String extension: extensions) {
            logger.debug("processing all [." + extension + "]");


            List<File> files = CDRUtil.loadFiles(loadingDirectory, "^.*" + extension + "$");
            Collections.sort(files, new RawFileComparator());

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

            CDRUtil.printMapSortedByValue(loadingDirectory + File.separator + "all."+ extension, agg);
        }
    }
}
