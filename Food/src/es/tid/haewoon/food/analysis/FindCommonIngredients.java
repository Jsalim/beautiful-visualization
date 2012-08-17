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

public class FindCommonIngredients {
    private Logger logger = Logger.getLogger(FindCommonIngredients.class);
    private String targetPath;
    
    Map<String, Integer> ingredient2recipe = new HashMap<String, Integer>(); 
    Map<String, Set<String>> ingredient2neighbors = new HashMap<String, Set<String>>();
    Map<String, Set<String>> ingredient2flavors = new HashMap<String, Set<String>>();
        
    int RECIPE_THRESHOLD = 100;
    int NEIGHBOR_THRESHOLD = 500;
    int FLAVOR_THRESHOLD = 30;    	
    
    
    public static void main(String[] args) throws IOException {
        (new FindCommonIngredients(Constants.RESULT_PATH + File.separator + "8_common_ingredients")).run();
    }
    
    public FindCommonIngredients(String targetPath) {
        this.targetPath = targetPath;
    }
    
    private void run() throws IOException {
        boolean success = (new File(targetPath)).mkdir();
        if (success) {
            logger.debug("[" + targetPath + "] directory is created");
        }
        
        countRecipes();
        countNeighborIngredients();
        countFlavors();
        
        extractCommons();
    }
    
    private void extractCommons() throws IOException {
    	Set<String> commons = new HashSet<String>();
    	
    	for (String ing : ingredient2recipe.keySet()) {
    		if (ingredient2recipe.get(ing) >= RECIPE_THRESHOLD) {
    			commons.add(ing);
    		}
    	}
    	
    	for (String ing : ingredient2neighbors.keySet()) {
    		if (ingredient2neighbors.get(ing).size() >= NEIGHBOR_THRESHOLD) {
    			commons.add(ing);
    		}
    	}
    	
    	for (String ing : ingredient2flavors.keySet()) {
    		if (ingredient2flavors.get(ing).size() >= FLAVOR_THRESHOLD) {
    			commons.add(ing);
    		}
    	}
    	
    	BufferedWriter bw = new BufferedWriter(new FileWriter(targetPath + File.separator + "common_ingredients"));
    	for (String common : commons) {
    		bw.write(common);
    		bw.newLine();
    	}
    	bw.close();
    }
    
    
    private void countRecipes() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(
                Constants.RESULT_PATH + File.separator + "1_extract_ingredients" + File.separator + "CD2_and_3"));
        String line;
        
        String last = "N/A";
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split("\t");
            String recipe = tokens[0];
            String ingredient = tokens[5];
            int old_count = (ingredient2recipe.get(ingredient) != null) ?ingredient2recipe.get(ingredient) :0;
            ingredient2recipe.put(ingredient, old_count+1);
        }
        
        Map<Integer, List<String>> sortedIngredient2recipe = FoodUtil.sortByValue(ingredient2recipe);
        printMap("ingredient2recipes", sortedIngredient2recipe);
    }
    
    private void printMap(String outputFilename, Map<Integer, List<String>> map) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(targetPath + File.separator + outputFilename));
        List<Integer> keys = new ArrayList<Integer>(map.keySet());
        
        // descending order
        Collections.sort(keys);
        Collections.reverse(keys);
        
        for (Integer key: keys) {
            for (String value: map.get(key)) {
                bw.write(value + "\t" + key);
                bw.newLine();
            }
        }
        bw.close();
    }
    
    private void countNeighborIngredients() throws IOException{
        List<File> files = 
                FoodUtil.loadFiles(Constants.RESULT_PATH + File.separator + "3_construct_networks_of_each_month", "^\\d{4}-\\d{1,2}$");
        String line;
       
        
        for (File file : files) {
            BufferedReader br = new BufferedReader(new FileReader(file));
             
            while ((line = br.readLine()) != null) {
                if (line.startsWith("source")) {
                    continue;
                }
                
                String[] tokens = line.split("\t");
                String source = tokens[0];
                String target = tokens[1];
                Set<String> already = (ingredient2neighbors.get(source) != null) ?ingredient2neighbors.get(source) :new HashSet<String>();
                already.add(target);
                ingredient2neighbors.put(source, already);
                
                already = (ingredient2neighbors.get(target) != null) ?ingredient2neighbors.get(target) :new HashSet<String>();
                already.add(source);
                ingredient2neighbors.put(target, already);
            }
        }
        Map<String, Integer> ingredient2numNb = new HashMap<String, Integer>();
        for (String ing : ingredient2neighbors.keySet()) {
            ingredient2numNb.put(ing, ingredient2neighbors.get(ing).size());
        }
        
        Map<Integer, List<String>> sortedIngredient2numNb = FoodUtil.sortByValue(ingredient2numNb);
        printMap("ingredient2neighbors", sortedIngredient2numNb);        
    }
    
    private void countFlavors() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(
                Constants.RESULT_PATH + File.separator + "5_evolutionary_analysis" + File.separator + "CD2_and_3_preparation"));
        String line;
        
         
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split("\t");
            String flavor = tokens[3];
            String ings = tokens[4];
            for (String ing : ings.split("\\|")) {
                Set<String> already = (ingredient2flavors.get(ing) != null) ?ingredient2flavors.get(ing) :new HashSet<String>();
                already.add(flavor);
                ingredient2flavors.put(ing, already);
            }
        }
        
        Map<String, Integer> ingredient2numFv = new HashMap<String, Integer>();
        for (String ing : ingredient2flavors.keySet()) {
            ingredient2numFv.put(ing, ingredient2flavors.get(ing).size());
        }
        
        Map<Integer, List<String>> sortedIngredient2flavors = FoodUtil.sortByValue(ingredient2numFv);
        printMap("ingredient2flavors", sortedIngredient2flavors);        
    }
}
