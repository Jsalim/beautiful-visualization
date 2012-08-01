package es.tid.haewoon.food.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import es.tid.haewoon.food.util.Constants;

public class IngredientDictionaryMaker {
    static Logger logger = Logger.getLogger(IngredientDictionaryMaker.class);
    static String targetPath = Constants.RESULT_PATH + File.separator + "2_ingredient_integer_id";
    
    Set<String> ingredients = new HashSet<String>();
    
    public void run() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(
                Constants.RESULT_PATH + File.separator + "1_extract_ingredients" + File.separator + "CD2_and_3"));
        
        int id = 0;
        String line;
        while ((line = br.readLine()) != null) {
            if (line.length() == 0) {
                continue;
            }
            
            String ingredient = line.split("\\t")[4];
            ingredients.add(ingredient);
        }
        
        List<String> ingList = new ArrayList<String>(ingredients);
        Collections.sort(ingList);
        
        BufferedWriter bw = new BufferedWriter(new FileWriter(targetPath + File.separator + "ingredient_dictionary.na"));
        
//        bw.write("ID (numeric)\tLabel");
//        bw.newLine();
        
        for (String ing : ingList) {
            bw.write(id + "\t" + ing);
            bw.newLine();
            id++;
        }
        
        bw.close();
    }
    
    
    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        boolean success = (new File(targetPath)).mkdir();
        if (success) {
            logger.debug("A directory [" + targetPath + "] is created");
        }
        
        IngredientDictionaryMaker citi = new IngredientDictionaryMaker();
        citi.run();
    }

}
