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
        
        Map<String, String> month2season = new HashMap<String, String>();
        month2season.put("1", "WINTER");
        month2season.put("2", "WINTER");
        month2season.put("3", "SPRING");
        month2season.put("4", "SPRING");
        month2season.put("5", "SPRING");
        month2season.put("6", "SUMMER");
        month2season.put("7", "SUMMER");
        month2season.put("8", "SUMMER");
        month2season.put("9", "FALL");
        month2season.put("10", "FALL");
        month2season.put("11", "FALL");
        month2season.put("12", "WINTER");

        
        Map<String, Map<String, Integer>> year2ff2count = new HashMap<String, Map<String, Integer>>();
        Map<String, Map<String, Integer>> cat2ff2count = new HashMap<String, Map<String, Integer>>();
        Map<String, Map<String, Integer>> month2ff2count = new HashMap<String, Map<String, Integer>>();
        Map<String, Map<String, Integer>> season2ff2count = new HashMap<String, Map<String, Integer>>();
        Map<String, Map<String, Integer>> year_cat2ff2count = new HashMap<String, Map<String, Integer>>();
        Map<String, Map<String, Integer>> month_cat2ff2count = new HashMap<String, Map<String, Integer>>();
        Map<String, Map<String, Integer>> season_cat2ff2count = new HashMap<String, Map<String, Integer>>();
        Map<String, Map<String, Integer>> year_month_cat2ff2count = new HashMap<String, Map<String, Integer>>();
        
        
        // some combinations
        Map<String, Map<String, Integer>> year2ff_ff2count = new HashMap<String, Map<String, Integer>>();
        Map<String, Map<String, Integer>> month2ff_ff2count = new HashMap<String, Map<String, Integer>>();
        Map<String, Map<String, Integer>> cat2ff_ff2count = new HashMap<String, Map<String, Integer>>();
        Map<String, Map<String, Integer>> season2ff_ff2count = new HashMap<String, Map<String, Integer>>();
        Map<String, Map<String, Integer>> year_cat2ff_ff2count = new HashMap<String, Map<String, Integer>>();
        Map<String, Map<String, Integer>> month_cat2ff_ff2count = new HashMap<String, Map<String, Integer>>();
        Map<String, Map<String, Integer>> season_cat2ff_ff2count = new HashMap<String, Map<String, Integer>>();
        Map<String, Map<String, Integer>> year_month_cat2ff_ff2count = new HashMap<String, Map<String, Integer>>();
        
        
        // loading the fingerprint table
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split("\t");
            String recipe = tokens[0];
            String year = tokens[2];
            String category = tokens[3];
            String[] months = tokens[5].split(",");
            String[] ingredients = tokens[6].split("\\|");
            
            for (String ff_ing : ingredients) {
                count(year2ff2count, year, ff_ing);
                count(cat2ff2count, category, ff_ing);
                count(year_cat2ff2count, year + "_" + category, ff_ing);
                
                for (String month : months) {
                    count(month2ff2count, month, ff_ing);
                    count(month_cat2ff2count, month + "_" + category, ff_ing);
                    count(season2ff2count, month2season.get(month), ff_ing);
                    count(season_cat2ff2count, month2season.get(month) + "_" + category, ff_ing);
                    count(year_month_cat2ff2count, year + "_" + month + "_" + category, ff_ing);
                }
                
                for (String ff_ing2 : ingredients) {
                    if (commons.contains(ff_ing) || commons.contains(ff_ing2)) continue;
                    if (ff_ing.equals(ff_ing2)) continue;
                    String joint = (ff_ing.compareTo(ff_ing2) < 0) ?ff_ing + "|" + ff_ing2 :ff_ing2 + "|" + ff_ing;
                    count(year2ff_ff2count, year, joint);
                    count(cat2ff_ff2count, category, joint);
                    count(year_cat2ff_ff2count, year + "_" + category, joint);
                    
                    for (String month : months) {
                        count(month2ff_ff2count, month, joint);
                        count(month_cat2ff_ff2count, month + "_" + category, joint);
                        count(season2ff_ff2count, month2season.get(month), joint);
                        count(season_cat2ff_ff2count, month2season.get(month) + "_" + category, joint);
                        count(year_month_cat2ff_ff2count, year + "_" + month + "_" + category, joint);
                    }
                }
            }  
            
            // just for counting the number of recipes
            for (String month : months) {
                count(month2ff2count, month, "RECIPE"); 
                count(month_cat2ff2count, month + "_" + category, "RECIPE");
                count(season2ff2count, month2season.get(month), "RECIPE");
                count(season2ff_ff2count, month2season.get(month), "RECIPE");
                count(season_cat2ff2count, month2season.get(month) + "_" + category, "RECIPE");
                count(month2ff_ff2count, month, "RECIPE");
                count(month_cat2ff_ff2count, month + "_" + category, "RECIPE");
                count(year_month_cat2ff2count, year + "_" + month + "_" + category, "RECIPE");
                count(season_cat2ff_ff2count, month2season.get(month) + "_" + category, "RECIPE");
                count(year_month_cat2ff_ff2count, year + "_" + month + "_" + category, "RECIPE");
            }
            
            count(year2ff2count, year, "RECIPE");
            count(cat2ff2count, category, "RECIPE");
            count(year_cat2ff2count, year + "_" + category, "RECIPE");
            
            count(year2ff_ff2count, year, "RECIPE");
            count(cat2ff_ff2count, category, "RECIPE");
            count(year_cat2ff_ff2count, year + "_" + category, "RECIPE");   
        }
        statisticalTest(month2ff2count, 1, 3, 0.1, 0.1,
                new BufferedWriter(new FileWriter(targetDirectory + File.separator + "d_ing_by_month.txt")),
                new BufferedWriter(new FileWriter(targetDirectory + File.separator + "full_ing_by_month.txt")));
        statisticalTest(season2ff2count, 0, 3, 0.05, 0.05,
                new BufferedWriter(new FileWriter(targetDirectory + File.separator + "d_ing_by_season.txt")),
                new BufferedWriter(new FileWriter(targetDirectory + File.separator + "full_ing_by_season.txt")));
        statisticalTest(year2ff2count, 1, 3, 0.1, 0.1,
                new BufferedWriter(new FileWriter(targetDirectory + File.separator + "d_ing_by_year.txt")),
                new BufferedWriter(new FileWriter(targetDirectory + File.separator + "full_ing_by_year.txt")));
        statisticalTest(cat2ff2count, 1, 3, 0.1, 0.1,
                new BufferedWriter(new FileWriter(targetDirectory + File.separator + "d_ing_by_category.txt")),
                new BufferedWriter(new FileWriter(targetDirectory + File.separator + "full_ing_by_category.txt")));
        statisticalTest(year_cat2ff2count, 1, 3, 0.1, 0.1,
                new BufferedWriter(new FileWriter(targetDirectory + File.separator + "d_ing_by_year_and_category.txt")),
                new BufferedWriter(new FileWriter(targetDirectory + File.separator + "full_ing_by_year_and_category.txt")));
        statisticalTest(season_cat2ff2count, 1, 3, 0.1, 0.1,
                new BufferedWriter(new FileWriter(targetDirectory + File.separator + "d_ing_by_season_and_category.txt")),
                new BufferedWriter(new FileWriter(targetDirectory + File.separator + "full_ing_by_season_and_category.txt")));
        statisticalTest(month_cat2ff2count, 1, 3, 0.1, 0.1,
                new BufferedWriter(new FileWriter(targetDirectory + File.separator + "d_ing_by_month_and_category.txt")),
                new BufferedWriter(new FileWriter(targetDirectory + File.separator + "full_ing_by_month_and_category.txt")));
        statisticalTest(year_month_cat2ff2count, 8, 3, 0.1, 0.1,
                new BufferedWriter(new FileWriter(targetDirectory + File.separator + "d_ing_by_year_and_month_and_category.txt")),
                new BufferedWriter(new FileWriter(targetDirectory + File.separator + "full_ing_by_year_and_month_and_category.txt")));
        
        
        statisticalTest(year2ff_ff2count, 1, 3, 0.1, 0.1,
                new BufferedWriter(new FileWriter(targetDirectory + File.separator + "d_combi_by_year.txt")),
                new BufferedWriter(new FileWriter(targetDirectory + File.separator + "full_combi_by_year.txt")));
        statisticalTest(cat2ff_ff2count, 1, 3, 0.1, 0.1,
                new BufferedWriter(new FileWriter(targetDirectory + File.separator + "d_combi_by_category.txt")),
                new BufferedWriter(new FileWriter(targetDirectory + File.separator + "full_combi_by_category.txt")));
        statisticalTest(year_cat2ff_ff2count, 1, 3, 0.1, 0.1,
                new BufferedWriter(new FileWriter(targetDirectory + File.separator + "d_combi_by_year_and_category.txt")),
                new BufferedWriter(new FileWriter(targetDirectory + File.separator + "full_combi_by_year_and_category.txt")));
        statisticalTest(month2ff_ff2count, 1, 3, 0.1, 0.1,
                new BufferedWriter(new FileWriter(targetDirectory + File.separator + "d_combi_by_month.txt")),
                new BufferedWriter(new FileWriter(targetDirectory + File.separator + "full_combi_by_month.txt")));
        statisticalTest(season2ff_ff2count, 1, 3, 0.1, 0.1,
                new BufferedWriter(new FileWriter(targetDirectory + File.separator + "d_combi_by_season.txt")),
                new BufferedWriter(new FileWriter(targetDirectory + File.separator + "full_combi_by_season.txt")));
        statisticalTest(month_cat2ff_ff2count, 1, 3, 0.1, 0.1,
                new BufferedWriter(new FileWriter(targetDirectory + File.separator + "d_combi_by_month_and_category.txt")),
                new BufferedWriter(new FileWriter(targetDirectory + File.separator + "full_combi_by_month_and_category.txt")));
        statisticalTest(season_cat2ff_ff2count, 1, 3, 0.1, 0.1,
                new BufferedWriter(new FileWriter(targetDirectory + File.separator + "d_combi_by_season_and_category.txt")),
                new BufferedWriter(new FileWriter(targetDirectory + File.separator + "full_combi_by_season_and_category.txt")));
        statisticalTest(season_cat2ff_ff2count, 1, 3, 0.1, 0.1,
                new BufferedWriter(new FileWriter(targetDirectory + File.separator + "d_combi_by_season_and_category.txt")),
                new BufferedWriter(new FileWriter(targetDirectory + File.separator + "full_combi_by_season_and_category.txt")));
    }
    
    private void statisticalTest(Map<String, Map<String, Integer>> key2ff2count, int OCCURRED_IN_OTHERS, int MINIMUM_USED, double SUPPORT_THRESHOLD, double O_SUPPORT_THRESHOLD, BufferedWriter bw, BufferedWriter fbw) throws IOException {
        List<String> keys = new ArrayList<String>(key2ff2count.keySet());
        Collections.sort(keys);
        
        for (String key : keys) {
            Map<String, Integer> ff2count = key2ff2count.get(key);
            int number = ff2count.get("RECIPE");
            for (String ff : ff2count.keySet()) {
                List<String> others = new ArrayList<String>();
                if (ff.equals("RECIPE")) continue;
                int count = ff2count.get(ff);
                double support = (double) count / (double) number;
                if (support < SUPPORT_THRESHOLD || count < MINIMUM_USED) continue;
                int common_in_others = 0;
                for (String o_key : keys) {
                    if (key.equals(o_key)) continue;     // only for other years/categories
                    Map<String, Integer> o_ff2count = key2ff2count.get(o_key);
                    int o_count = (o_ff2count.get(ff) != null) ?o_ff2count.get(ff) :0;
                    double o_support = (double) o_count / (double) o_ff2count.get("RECIPE");
                    if (o_support > O_SUPPORT_THRESHOLD) {
                        others.add(o_key);
                        common_in_others++;
                    }
                }
                if (common_in_others <= OCCURRED_IN_OTHERS) {
                    bw.write(key + "\t" + ff + "\t" + count + "\t" + support + "\t" + others.size() + "\t" + FoodUtil.join(others, "|"));
                    bw.newLine();
                } 
                fbw.write(key + "\t" + ff + "\t" + count + "\t" + support + "\t" + others.size() + "\t" + FoodUtil.join(others, "|"));
                fbw.newLine();
            }
        }
        bw.close();
        fbw.close();
    }
    
    private void count(Map<String, Map<String, Integer>> map, String key1, String key2) {
        Map<String, Integer> old = (map.get(key1) != null) ?map.get(key1) :new HashMap<String, Integer>();
        int old_count = (old.get(key2) != null) ?old.get(key2) :0;
        old_count++;
        old.put(key2, old_count);
        map.put(key1, old);
    }
}
