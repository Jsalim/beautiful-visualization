package es.tid.haewoon.food.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import es.tid.haewoon.food.util.Constants;

public class MakeBigSimilarityTable {
    Logger logger = Logger.getLogger(MakeBigSimilarityTable.class);
    
    public static void main(String[] args) throws IOException {
        (new MakeBigSimilarityTable()).run(Constants.RESULT_PATH + File.separator + "14_big_similarity_table");
    }
    
    private void run(String targetDirectory) throws IOException {
        boolean success = (new File(targetDirectory)).mkdir();
        if (success) {
            logger.debug("A directory[" + targetDirectory + "] is created");
        }
        
        BufferedReader br = new BufferedReader(new FileReader(
                Constants.RESULT_PATH + File.separator + "1_extract_ingredients" + File.separator + "CD2_and_3"));
        
        Map<String, String> recipe2year = new HashMap<String, String>();
        Map<String, String> recipe2cat = new HashMap<String, String>();
        Map<String, String> recipe2temp = new HashMap<String, String>();
        Map<String, String> recipe2months = new HashMap<String, String>();
        String line;
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split("\t");
            String recipe = tokens[0];
            String year = tokens[2];
            String category = tokens[3];
            String temperature = tokens[4];
            String months = tokens[5];
            
            recipe2year.put(recipe, year);
            recipe2cat.put(recipe, category);
            recipe2temp.put(recipe, temperature);
            recipe2months.put(recipe, months);
        }
        br.close();
        
        br = new BufferedReader(new FileReader(
                Constants.RESULT_PATH + File.separator + "13_recipe_fingerprint_by_ffi" + File.separator + "recipe_similarity_based_on_ffi"));
        
        BufferedWriter bw = new BufferedWriter(new FileWriter(targetDirectory + File.separator + "big_similarity_table"));
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split("\t");
            String rec1 = tokens[0];
            String rec2 = tokens[1];
            Double sim = Double.valueOf(tokens[2]);
            
            bw.write(rec1 + "\t" + recipe2year.get(rec1) + "\t" + recipe2cat.get(rec1) + "\t" + 
                                   recipe2temp.get(rec1) + "\t" + recipe2months.get(rec1) + "\t" + 
                     rec2 + "\t" + recipe2year.get(rec2) + "\t" + recipe2cat.get(rec2) + "\t" + 
                                   recipe2temp.get(rec2) + "\t" + recipe2months.get(rec2) + "\t" + sim);
            bw.newLine();
        }
        
        br.close();
        bw.close();
    }
}
