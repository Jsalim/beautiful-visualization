package es.tid.haewoon.food.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import es.tid.haewoon.food.util.Constants;
import es.tid.haewoon.food.util.FoodUtil;

/* 
 * working 
 * 
 */

public class RecipeFingerPrintByFFI {
    private Logger logger = Logger.getLogger(RecipeFingerPrintByFFI.class);
    
    public static void main(String[] args) throws IOException {
        (new RecipeFingerPrintByFFI()).run(Constants.RESULT_PATH + File.separator + "13_recipe_fingerprint_by_ffi");
    }
    
    private void run(String targetDirectory) throws IOException {
        boolean success = (new File(targetDirectory)).mkdir();
        if (success) {
            logger.debug("A directory[" + targetDirectory + "] is created...");
        }
        BufferedReader br = new BufferedReader(new FileReader(
                Constants.RESULT_PATH + File.separator + "11_flavor_free_ingredients" + File.separator + "flavor_ffi_fnfi"));
        String line;

        Map<String, Set<String>> recipe2ffs = new HashMap<String, Set<String>>(); 
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split("\t");
            String recipe = tokens[0];
            String ff_fnf = tokens[2];
            String ing = tokens[3];
            
            if (ff_fnf.equals("FF")) {
                Set<String> old = (recipe2ffs.get(recipe) != null) ?recipe2ffs.get(recipe) :new HashSet<String>();
                old.add(ing);
                recipe2ffs.put(recipe, old);
            }
        }
        br.close();
        
        br = new BufferedReader(new FileReader(
                Constants.RESULT_PATH + File.separator + "1_extract_ingredients" + File.separator + "CD2_and_3"));
        
        Map<String, String> recipe2year = new HashMap<String, String>();
        Map<String, String> recipe2cat = new HashMap<String, String>();
        Map<String, String> recipe2temp = new HashMap<String, String>();
        Map<String, String> recipe2months = new HashMap<String, String>();
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
        
        List<String> recipes = new ArrayList<String>(recipe2ffs.keySet());
        Collections.sort(recipes);
        
        BufferedWriter bw = new BufferedWriter(new FileWriter(targetDirectory + File.separator + "recipe_fingerprint.txt"));
        for (String recipe : recipes) {
            Set<String> ff = recipe2ffs.get(recipe);
            List<String> ff_l = new ArrayList<String>(ff);
            Collections.sort(ff_l);
            bw.write(recipe + "\t" + recipe2year.get(recipe) + "\t" + recipe2cat.get(recipe) + "\t" + FoodUtil.join(ff_l, "|"));
            bw.newLine();
        }
        bw.close();
        
        bw = new BufferedWriter(new FileWriter(targetDirectory + File.separator + "recipe_similarity_based_on_ffi"));
        
        for (String recipe1 : recipes) {
            Set<String> ff1 = recipe2ffs.get(recipe1);
            for (String recipe2 : recipes) {
                if (recipe1.equals(recipe2)) {
                    continue;
                }
                
                Set<String> ff2 = recipe2ffs.get(recipe2);
                Set<String> union = new HashSet<String>(ff1);
                union.addAll(ff2);
                int un_s = union.size();
                int ff1_s = ff1.size();
                int ff2_s = ff2.size();
                int in_s = (ff1_s + ff2_s) - un_s;
                
                double jaccard_index = (double) in_s / (double) un_s; 
                bw.write(recipe1 + "\t" + recipe2 + "\t" + jaccard_index + "\t" + ff1_s + "\t" + ff2_s + "\t" + in_s);
                bw.newLine();
            }
        }
        bw.close();
    }
}
