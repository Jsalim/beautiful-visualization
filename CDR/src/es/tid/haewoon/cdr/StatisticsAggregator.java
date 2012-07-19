package es.tid.haewoon.cdr;

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

public class StatisticsAggregator {
    private static Logger logger = Logger.getLogger(StatisticsAggregator.class);
    
    public static void main(String[] args) throws FileNotFoundException {
        StatisticsAggregator sa = new StatisticsAggregator();
        
        String[] extensions = {"cl2c", "dn2c", "nb2c", "du2c", "on2c"}; 
        for (String extension: extensions) {
            logger.debug("processing all [." + extension + "]");
            Map<String, Integer> result = sa.run(Constants.RESULT_PATH + "/count_basic_statistics/", extension);
            sa.printTransposeMap(Constants.RESULT_PATH + "/count_basic_statistics/all." + extension, result);
        }
    }
    
    public Map run(String basePath, String extension) throws FileNotFoundException {
        List<File> files = CDRUtil.loadFiles(basePath, "^.*" + extension + "$");
        
        Map<String, Integer> agg = new HashMap<String, Integer>();
        for (File file: files) {
            logger.debug("processing " + file);
            String line = "";
            BufferedReader br = new BufferedReader(new FileReader(file));
            try {
                while((line = br.readLine()) != null) {
                    String[] keyValue = line.split("\\t");
                    agg = countItem(agg, keyValue);
                    
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
        
        return agg;
    }
    
    private Map<Integer, List<String>> transpose(Map<String, Integer> old) {
        Map<Integer, List<String>> transpose = new HashMap<Integer, List<String>>();
        
        for (String key: old.keySet()) {
            Integer value = old.get(key);
            List<String> correspondingKeys = transpose.get(value);
            if (correspondingKeys == null) {
                correspondingKeys = new ArrayList<String>();
            } 
            correspondingKeys.add(key);
            transpose.put(value, correspondingKeys);
        }
        
        return transpose;
    }
    
    // sort by value
    private void printTransposeMap(String path, Map<String, Integer> map) {
        Map<Integer, List<String>> tp = transpose(map);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(path));

            List<Integer> keys = new ArrayList(tp.keySet());
            
            // descending order
            Collections.sort(keys);
            Collections.reverse(keys);
            
            for (Integer key: keys) {
                for (String value: tp.get(key)) {
                    bw.write(value + "\t" + key);
                    bw.newLine();
                }
            }
            bw.close();
            
        } catch (IOException e) {
            // never happened
            // TODO Auto-generated catch block
            e.printStackTrace();
        }         
    }

    
    private void printMap(String path, Map<String, Integer> map) {
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
    
    private Map<String, Integer> countItem(Map<String, Integer> map, String[] keyValue) {
        Integer i = map.get(keyValue[0]);
        if (i != null) {
            map.put(keyValue[0], i + Integer.valueOf(keyValue[1]));
        } else {
            map.put(keyValue[0], Integer.valueOf(keyValue[1]));   // initialization
        }

        return map;
    }
}
