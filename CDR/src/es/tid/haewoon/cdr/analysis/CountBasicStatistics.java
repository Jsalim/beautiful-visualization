package es.tid.haewoon.cdr.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import es.tid.haewoon.cdr.util.CDR;
import es.tid.haewoon.cdr.util.CDRUtil;
import es.tid.haewoon.cdr.util.Constants;
import es.tid.haewoon.cdr.util.Operator;

public class CountBasicStatistics {
    private static Logger logger = Logger.getLogger(CountBasicStatistics.class); 

    public static void main(String[] args) throws IOException {
        CountBasicStatistics cc = new CountBasicStatistics();

        Map origin2Count = new HashMap();
        Map dest2Count = new HashMap();
        Map cell2Count = new HashMap();
        Map duration2Count = new HashMap();
        Map number2Count = new HashMap();
        
        List<File> files = CDRUtil.loadRefinedCDRFiles();
        
        String line;

        for (File file: files) {
            origin2Count.clear();
            dest2Count.clear();
            cell2Count.clear();
            duration2Count.clear();
            number2Count.clear();
            
            logger.debug("processing " + file);
            BufferedReader br = new BufferedReader(new FileReader(file));

            while((line = br.readLine()) != null) {
                try {
                    CDR cdr = new CDR(line);
                    
                    // count mobile phones serviced by movistar only
                    // we already filtered movistar-movistar; thus if-else if.
                    if (cdr.getOrigOpr() == Operator.MOVISTAR) {
                        origin2Count = CDRUtil.countItem(origin2Count, cdr.getOrigNum());
                        number2Count = CDRUtil.countItem(number2Count, cdr.getOrigNum());
                    } else if (cdr.getDestOpr() == Operator.MOVISTAR) {
                        dest2Count = CDRUtil.countItem(dest2Count, cdr.getDestNum());
                        number2Count = CDRUtil.countItem(number2Count, cdr.getDestNum());
                    }
                    
                    cell2Count = CDRUtil.countItem(cell2Count, cdr.getInitCellID());
                    if (cdr.getInitCellID() != cdr.getFinCellID()) {
                        cell2Count = CDRUtil.countItem(cell2Count, cdr.getFinCellID());
                    }
                    duration2Count = CDRUtil.countItem(duration2Count, cdr.getDuration());

                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } 
            }
       
            String basePath = Constants.RESULT_PATH + "/count_basic_statistics/" + file.getName();
            CDRUtil.printMap(basePath + ".on2c", origin2Count);
            CDRUtil.printMap(basePath + ".dn2c", dest2Count);
            CDRUtil.printMap(basePath + ".cl2c", cell2Count);
            CDRUtil.printMap(basePath + ".du2c", duration2Count);
            CDRUtil.printMap(basePath + ".nb2c", number2Count);
        }
    }
    

}
