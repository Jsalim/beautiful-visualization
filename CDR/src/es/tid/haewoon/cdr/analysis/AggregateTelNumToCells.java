package es.tid.haewoon.cdr.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;

import es.tid.haewoon.cdr.util.CDRUtil;
import es.tid.haewoon.cdr.util.Constants;

@Deprecated
public class AggregateTelNumToCells {
    private static final Logger logger = Logger.getLogger(AggregateTelNumToCells.class);
    
    /**
     * @param args
     * @throws FileNotFoundException 
     */
    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stub
        String targetPath = Constants.RESULT_PATH + File.separator + "3_count_telnum_2_cells";
//        Map agg = (new StatisticsAggregator()).run(targetPath, Constants.RAW_DATA_FILE_PATTERN);
//        CDRUtil.printMapSortedByValue(targetPath + File.separator + "all", agg);
    }

}
