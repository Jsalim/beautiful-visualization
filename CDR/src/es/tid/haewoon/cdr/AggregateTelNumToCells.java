package es.tid.haewoon.cdr;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import org.apache.log4j.Logger;

public class AggregateTelNumToCells {
    private static final Logger logger = Logger.getLogger(AggregateTelNumToCells.class);
    
    /**
     * @param args
     * @throws FileNotFoundException 
     */
    public static void main(String[] args) throws FileNotFoundException {
        // TODO Auto-generated method stub
        String targetPath = Constants.RESULT_PATH + File.separator + "3_count_telnum_2_cells";
        Map agg = StatisticsAggregator.run(targetPath, Constants.RAW_FILE_PATTERN);
        CDRUtil.printMapSortedByValue(targetPath + File.separator + "all", agg);
    }

}
