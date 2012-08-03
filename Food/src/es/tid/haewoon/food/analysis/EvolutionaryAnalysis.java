package es.tid.haewoon.food.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import es.tid.haewoon.food.util.Constants;
import es.tid.haewoon.food.util.FoodUtil;

public class EvolutionaryAnalysis {
    static Logger logger = Logger.getLogger(EvolutionaryAnalysis.class);
    static String targetPath = Constants.RESULT_PATH + File.separator + "5_evolutionary_analysis";
    static BufferedWriter bw;
    static Map<Integer, List<String>> num2str = new HashMap<Integer, List<String>>();
    
    public EvolutionaryAnalysis() throws IOException {
        bw = new BufferedWriter(new FileWriter(targetPath + File.separator + "CD2_and_3_preparation"));
    }
    
    public static void main(String[] args) throws IOException {
        boolean success = (new File(targetPath)).mkdir();
        if (success) {
            logger.debug("A directory [" + targetPath + "] is created");
        }
        
        EvolutionaryAnalysis ea = new EvolutionaryAnalysis();
        
        ea.run(FoodUtil.loadFiles(Constants.CD2_PREPARATION_SWEET_PATH, "^\\d+\\.dat$"), "UTF-8");
        ea.run(FoodUtil.loadFiles(Constants.CD2_PREPARATION_SAVOURY_PATH, "^\\d+\\.dat$"), "UTF-8");
        ea.run(FoodUtil.loadFiles(Constants.CD3_PREPARATION_PATH, "^\\d+\\.txt$"), "WINDOWS-1252");
        
        List<Integer> nums = new ArrayList<Integer>(num2str.keySet());
        Collections.sort(nums);
        
        for (int i : nums) {
            List<String> strs = num2str.get(i);
            for (String str : strs) {
                bw.write(str);
                bw.newLine();
            }
        }
        bw.close();
    }
    
    private void run(List<File> files, String encoding) throws IOException {
        String line;
       
        for (File afile : files) {
            Map<String, String> keyValue = new HashMap<String, String>();
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(afile), encoding));
            int numberOfItems = -1;
            while ((line = br.readLine()) != null ) {
                line = line.substring(1);
                
                String[] tokens = line.split("=");
                if (tokens.length == 2) {
                    if (tokens[0].startsWith("num")) {
                        numberOfItems = Integer.valueOf(tokens[0].substring(3));
                    }
                    keyValue.put(tokens[0], tokens[1]);
                }
                
            }
            
            if (keyValue.get("sabor1") == null || keyValue.get("composicion1") == null) {
                continue;
            }
            
            logger.debug(afile.getName());
            logger.debug(keyValue);
            
            for (int i = 1; i <= numberOfItems; i++) {
                logger.debug(i);
                logger.debug(keyValue.get("mundo"));
                logger.debug(keyValue.get("num" + i));
                logger.debug(keyValue.get("codigoA" + i));
                logger.debug(keyValue.get("sabor" + i));
                logger.debug(keyValue.get("utilizacion" + i));
                logger.debug(Arrays.asList(keyValue.get("composicion" + i).split("\\s*!\\s")));
                List<String> flavors = (num2str.get(Integer.valueOf(keyValue.get("num" + i))) == null) 
                        ? new ArrayList<String>() :num2str.get(Integer.valueOf(keyValue.get("num" + i)));
                        
                flavors.add(keyValue.get("num" + i) + "\t" + keyValue.get("mundo") + "\t" + 
                        keyValue.get("codigoA" + i) + "\t" + keyValue.get("sabor" + i).replaceAll("\\s*!\\s", "|") + "\t" + 
                        keyValue.get("composicion" + i).replaceAll("\\s*!\\s", "|") + "\t" + keyValue.get("utilizacion" + i)); // + "\t" + afile.getPath());
                
                
                num2str.put(Integer.valueOf(keyValue.get("num" + i)), flavors);

            }
        }
    }
}
