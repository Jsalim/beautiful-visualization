package es.tid.haewoon.cdr;

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
                        origin2Count = cc.countItem(origin2Count, cdr.getOrigNum());
                        number2Count = cc.countItem(number2Count, cdr.getOrigNum());
                    } else if (cdr.getDestOpr() == Operator.MOVISTAR) {
                        dest2Count = cc.countItem(dest2Count, cdr.getDestNum());
                        number2Count = cc.countItem(number2Count, cdr.getDestNum());
                    }
                    
                    cell2Count = cc.countItem(cell2Count, cdr.getInitCellID());
                    if (cdr.getInitCellID() != cdr.getFinCellID()) {
                        cell2Count = cc.countItem(cell2Count, cdr.getFinCellID());
                    }
                    duration2Count = cc.countItem(duration2Count, cdr.getDuration());

                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } 
            }
       
            String basePath = Constants.RESULT_PATH + "/count_basic_statistics/" + file.getName();
            cc.printMap(basePath + ".on2c", origin2Count);
            cc.printMap(basePath + ".dn2c", dest2Count);
            cc.printMap(basePath + ".cl2c", cell2Count);
            cc.printMap(basePath + ".du2c", duration2Count);
            cc.printMap(basePath + ".nb2c", number2Count);
        }
    }
    
    private void printMap(String path, Map map) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(path));

            List keys = new ArrayList(map.keySet());
            Collections.sort(keys);
            
            for (Object key: keys) {
                bw.write(key + "\t" + map.get(key));
                bw.newLine();
            }
            bw.close();
            
        } catch (IOException e) {
            // never happened
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
    }

    private Map countItem(Map map, Object item) {
        Integer i = (Integer) map.get(item);
        if (i != null) {
            map.put(item, i+1);
        } else {
            map.put(item, 1);   // initialization
        }

        return map;
    }
}
