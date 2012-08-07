package es.tid.haewoon.cdr.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;


public class CDRUtil {
    private static HashMap<String, Cell> cellid2Cell;
    private static final Logger logger = Logger.getLogger(CDRUtil.class);
    private CDRUtil() { throw new AssertionError(); }
    
    @Deprecated
    public static CDRUtil getInstance() {
        return null;
    }
    
    public static Cell getCell(String cellID) {
        if (cellid2Cell == null) {
            logger.debug("cell/bts info. initialization for the first time running");
            cellid2Cell = new HashMap<String, Cell>();
            BufferedReader br;
            String line;
            try {
                br = new BufferedReader(new FileReader(Constants.BASE_PATH + File.separator + Constants.CELL_INFO_FILE_NAME));
                while((line = br.readLine()) != null) {
                    // do something with line.
                    if (line.startsWith("cell")) continue;
                    Cell cell = new Cell(line);
                    cellid2Cell.put(cell.getID(), cell);
                }
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                logger.debug("wrong-formatted cell", e);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                logger.debug("No file?", e);
            }
        }
        
        return cellid2Cell.get(cellID);
    }
    
    public static void main(String[] args) {
        System.out.println(CDRUtil.getCell("181562"));
    }
    
    public static List<File> loadRefinedCDRFiles() {
        List<File> files = CDRUtil.loadFiles(Constants.SORTED_COMMUTING_HOURS_PATH + File.separator + "7-10", Constants.RAW_DATA_FILE_PATTERN);
        files.addAll(CDRUtil.loadFiles(Constants.SORTED_COMMUTING_HOURS_PATH + File.separator + "17-20", Constants.RAW_DATA_FILE_PATTERN));
        
        Comparator<File> mosaic = new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                String f1_dir = f1.getParentFile().getName();
                String f2_dir = f2.getParentFile().getName();
               
                if (f1.getName().equals(f2.getName())) {
                    return Integer.valueOf(f1_dir.split("-")[0]).compareTo(Integer.valueOf(f2_dir.split("-")[0]));
                } else {
                    return f1.getName().compareTo(f2.getName());
                }
            }
        };
        
        Collections.sort(files, mosaic);
        
        return files;
    }
    
    public static List<File> loadFiles(String string, String pattern) {
        // TODO Auto-generated method stub
        List<File> filtered = new ArrayList<File>();
        File targetPath = new File(string);
        if (targetPath.isDirectory()) {
            File[] files = targetPath.listFiles();
            for (File file : files) {
                String filename = file.getName();
                if (filename.matches(pattern)) {
                    filtered.add(file);
                }
            }
        }
        return filtered;
    }
    
    public static void printMap(String path, Map map) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(path));

            List keys = new ArrayList(map.keySet());
            Collections.sort(keys);
            
            for (Object key: keys) {
                if (map.get(key) instanceof Collection) {
                    bw.write(key + "\t" + ((Set) map.get(key)).size() + "\t" + map.get(key));
                } else {
                    bw.write(key + "\t" + map.get(key));
                }
                bw.newLine();
            }
            bw.close();
            
        } catch (IOException e) {
            // never happened
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
    }

    public static Map countItem(Map map, Object item) {
        return countItem(map, item, 1);
    }
    
    public static Map countItem(Map map, Object item, int value) {
        Integer i = (Integer) map.get(item);
        if (i != null) {
            map.put(item, i+value);
        } else {
            map.put(item, value);   // initialization
        }

        return map;
    }
    
    public static Map<Integer, List<String>> transpose(Map<String, Integer> old) {
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
    public static void printMapSortedByValue(String outputPath, Map<String, Integer> map) {
        Map<Integer, List<String>> tp = transpose(map);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath));

            List<Integer> keys = new ArrayList<Integer>(tp.keySet());
            
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
}
