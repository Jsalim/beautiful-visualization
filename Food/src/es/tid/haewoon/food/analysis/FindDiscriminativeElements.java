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

public class FindDiscriminativeElements {
    Logger logger = Logger.getLogger(FindDiscriminativeElements.class);
    final double SUPPORT_THRESHOLD = 0.1;
    
    public static void main(String[] args) throws IOException {
        (new FindDiscriminativeElements()).run(Constants.RESULT_PATH + File.separator + "16_discriminative_elements");
    }
    
    private void run(String targetDirectory) throws IOException {
        boolean success = (new File(targetDirectory)).mkdir();
        if (success) {
            logger.info("A directory [" + targetDirectory + "] is created");
        }
        Set<String> commons = new HashSet<String>();
        
        BufferedReader br = new BufferedReader(new FileReader(Constants.RESULT_PATH + File.separator + "8_common_ingredients" + 
                    File.separator + "ingredient2recipes"));
        String line = "";

        while ((line = br.readLine()) != null) {
            String[] tokens = line.split("\t");
            String ing = tokens[0];
            if (Integer.valueOf(tokens[1]) > 59) { // 10% of recipes
                commons.add(ing);
            }
        }
        br.close();
        
        br = new BufferedReader(new FileReader(Constants.RESULT_PATH + File.separator + "1_extract_ingredients" + 
                    File.separator + "concise_CD2_and_3.txt"));
        
        Map<String, Map<String, Integer>> year2ff2count = new HashMap<String, Map<String, Integer>>();
        Map<String, Map<String, Integer>> cat2ff2count = new HashMap<String, Map<String, Integer>>();
        Map<String, Map<String, Integer>> year_cat2ff2count = new HashMap<String, Map<String, Integer>>();
        
        Map<String, Map<String, Integer>> year2ff_ff2count = new HashMap<String, Map<String, Integer>>();
        Map<String, Map<String, Integer>> cat2ff_ff2count = new HashMap<String, Map<String, Integer>>();
        Map<String, Map<String, Integer>> year_cat2ff_ff2count = new HashMap<String, Map<String, Integer>>();
        
        // loading the fingerprint table
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split("\t");
            String recipe = tokens[0];
            String year = tokens[2];
            String category = tokens[3];
            String[] ingredients = tokens[6].split("\\|");
            for (String ff_ing : ingredients) {
                count(year2ff2count, year, ff_ing);
                count(cat2ff2count, category, ff_ing);
                count(year_cat2ff2count, year + "_" + category, ff_ing);
                
                for (String ff_ing2 : ingredients) {
                    if (commons.contains(ff_ing) || commons.contains(ff_ing2)) continue;
                    if (ff_ing.equals(ff_ing2)) continue;
                    String joint = (ff_ing.compareTo(ff_ing2) < 0) ?ff_ing + "|" + ff_ing2 :ff_ing2 + "|" + ff_ing;
                    count(year2ff_ff2count, year, joint);
                    count(cat2ff_ff2count, category, joint);
                    count(year_cat2ff_ff2count, year + "_" + category, joint);
                }
            }        
            count(year2ff2count, year, "RECIPE");
            count(cat2ff2count, category, "RECIPE");
            count(year_cat2ff2count, year + "_" + category, "RECIPE");
            
            count(year2ff_ff2count, year, "RECIPE");
            count(cat2ff_ff2count, category, "RECIPE");
            count(year_cat2ff_ff2count, year + "_" + category, "RECIPE");   
        }
        statisticalTest(year2ff2count, 1, 3, new BufferedWriter(new FileWriter(targetDirectory + File.separator + "ing_by_year.txt")));
        statisticalTest(cat2ff2count, 1, 3, new BufferedWriter(new FileWriter(targetDirectory + File.separator + "ing_by_category.txt")));
        statisticalTest(year_cat2ff2count, 1, 2, new BufferedWriter(new FileWriter(targetDirectory + File.separator + "ing_by_year_and_category.txt")));
        
        statisticalTest(year2ff_ff2count, 0, 3, new BufferedWriter(new FileWriter(targetDirectory + File.separator + "combi_by_year.txt")));
        statisticalTest(cat2ff_ff2count, 0, 3, new BufferedWriter(new FileWriter(targetDirectory + File.separator + "combi_by_category.txt")));
        statisticalTest(year_cat2ff_ff2count, 0, 2, new BufferedWriter(new FileWriter(targetDirectory + File.separator + "combi_by_year_and_category.txt")));
    }
    
    private void statisticalTest(Map<String, Map<String, Integer>> key2ff2count, int OCCURRED_IN_OTHERS, int MINIMUM_USED, BufferedWriter bw) throws IOException {
        List<String> keys = new ArrayList<String>(key2ff2count.keySet());
        Collections.sort(keys);
        
        for (String key : keys) {
            Map<String, Integer> ff2count = key2ff2count.get(key);
            int number = ff2count.get("RECIPE");
            for (String ff : ff2count.keySet()) {
                if (ff.equals("RECIPE")) continue;
                int count = ff2count.get(ff);
                double support = (double) count / (double) number;
                if (support < SUPPORT_THRESHOLD || count < MINIMUM_USED) continue;
                int common_in_others = 0;
                for (String o_key : keys) {
                    if (key.equals(o_key)) continue;     // only for other seasons/categories
                    Map<String, Integer> o_ff2count = key2ff2count.get(o_key);
                    int o_count = (o_ff2count.get(ff) != null) ?o_ff2count.get(ff) :0;
                    double o_support = (double) o_count / (double) o_ff2count.get("RECIPE");
                    if (o_support >= SUPPORT_THRESHOLD) {
                        common_in_others++;
                    }
                }
                if (common_in_others <= OCCURRED_IN_OTHERS) {
                    bw.write(key + "\t" + ff + "\t" + count + "\t" + support);
                    bw.newLine();
                }
            }
        }
        bw.close();
    }
    
    private void count(Map<String, Map<String, Integer>> map, String key1, String key2) {
        Map<String, Integer> old = (map.get(key1) != null) ?map.get(key1) :new HashMap<String, Integer>();
        int old_count = (old.get(key2) != null) ?old.get(key2) :0;
        old_count++;
        old.put(key2, old_count);
        map.put(key1, old);
    }
}
