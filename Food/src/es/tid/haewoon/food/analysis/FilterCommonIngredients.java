package es.tid.haewoon.food.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import es.tid.haewoon.food.util.Constants;

public class FilterCommonIngredients {
    Logger logger = Logger.getLogger(FilterCommonIngredients.class);
        
    public static void main(String[] args) throws IOException {
        (new FilterCommonIngredients()).run(Constants.RESULT_PATH + File.separator + "15_recipes_without_common_ingredients");
    }
    
    private void run(String targetDir) throws IOException {
        boolean success = (new File(targetDir)).mkdir();
        if (success) {
            logger.info("A directory [" + targetDir + "] is created");
        }
        String basePath = Constants.RESULT_PATH + File.separator + "8_common_ingredients";
        
        Set<String> commons = new HashSet<String>();
        
        readInfo(basePath + File.separator + "ingredient2recipes", commons, 100);
        readInfo(basePath + File.separator + "ingredient2neighbors", commons, 500);
        readInfo(basePath + File.separator + "ingredient2flavors", commons, 10);
        
        BufferedReader br = new BufferedReader(new FileReader(Constants.RESULT_PATH + File.separator + "1_extract_ingredients" + File.separator + "CD2_and_3"));
        BufferedWriter bw = new BufferedWriter(new FileWriter(targetDir + File.separator + "recipes_without_commons.txt"));
        String line;
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split("\t");
            String ing = tokens[6];
            if (!commons.contains(ing)) {
                bw.write(line.trim());
                bw.newLine();
            }
        }
        bw.close();
    }
    
    private void readInfo(String filePath, Set<String> commons, int THRESHOLD) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split("\t");
            String ing = tokens[0];
            int recipe = Integer.valueOf(tokens[1]);
            if (recipe > THRESHOLD) {
                commons.add(ing);
            }
        }
        br.close();
    }
}
