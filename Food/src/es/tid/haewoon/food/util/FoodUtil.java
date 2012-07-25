package es.tid.haewoon.food.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


public class FoodUtil {
    private static final Logger logger = Logger.getLogger(FoodUtil.class);
    public static List<File> loadFiles(String root, String pattern) {
        List<File> filtered = new ArrayList<File>();
        File targetPath = new File(root);
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
    
    public static List<File> loadFiles(String root, String pattern, String keyword) {
        List<File> temp = FoodUtil.loadFiles(root, pattern);
        List<File> filtered = new ArrayList<File>();
        
        for (File aFile : temp) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(aFile));
                String line;
                while((line = br.readLine()) != null) {
                    // do something with line.
                    if (line.indexOf(keyword) != -1) {
                        filtered.add(aFile);
                    }
                }
                br.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                logger.error(e);
            }
        }
        return filtered;
    }
}
