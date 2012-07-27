package es.tid.haewoon.food.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import es.tid.haewoon.food.util.Constants;

public class ConstructIngredientNetworks {
    private static Logger logger = Logger.getLogger(ConstructIngredientNetworks.class);
    private static String targetPath = Constants.RESULT_PATH + File.separator + "2_ingredient_networks";
    private static Map<String, Set<String>> when2nodes = new HashMap<String, Set<String>>();
    
    public void flush(String id, String year, String category, String months, Set<String> ingredients) throws IOException {
        List<String> ings = new ArrayList<String>(ingredients);
        for (String month : months.split(",")) {

            BufferedWriter bw = new BufferedWriter(new FileWriter(targetPath + File.separator + year + "-" + month + "-edges.csv", true));
            for (int outer = 0; outer < ings.size(); outer++) {
                for (int inner = outer; inner < ings.size(); inner++) { 
                    if (outer == inner) {
                        continue;
                    }
                    Set<String> already = (when2nodes.get(year+"-"+month) == null) ?new HashSet<String>() :when2nodes.get(year+"-"+month);
                    already.add(ings.get(outer));
                    already.add(ings.get(inner));
                    when2nodes.put(year + "-" + month, already);
                    
//                    bw.write(id + "\t" + year + "\t" + category + "\t" + month + "\t" + ings.get(outer) + "\t" + ings.get(inner));
                    bw.write(decoration(ings.get(outer)) + "," + decoration(ings.get(inner)) + "," + category + ",Undirected");
                    bw.newLine();
                }
            }
            bw.close();
        }
    }
    
    private String decoration(String raw) {
        if (raw.indexOf(" ") != -1) {
            return "\"" + raw + "\"";
        } else {
            return raw;
        }
    }
    
    public void run() throws FileNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader("/workspace/Food/result/1_extract_ingredients/all"));
        String line = "";
        Set<String> ingredients = new HashSet<String>();
        
        try {
            String id = "";
            String year = "";
            String category = "";
            String month = "";
            
            while((line = br.readLine()) != null) {
                if (line.trim().length() == 0) {
                    continue;
                }
                String[] tokens = line.split("\\t");
                
                if (!id.equals(tokens[0]) && ingredients.size() != 0) {    // check whether this is the first-time loop
                    logger.debug("processing recipe[" + id + "]");
                    flush(id, year, category, month, ingredients);
                    ingredients.clear();
                } else { 
                    id = tokens[0];
                    year = tokens[1];
                    category = tokens[2];
                    month = tokens[3];
                    ingredients.add(tokens[4]);
                }
            }
        } catch (IOException e) {
            logger.error(e);
        } catch (Exception e) {
            logger.error(line, e);
        }
  
    }
    public static void main(String[] args) throws IOException {
        boolean success =  
                (new File(targetPath)).mkdir();
        if (success) {
            logger.debug("A directory [" + targetPath + "] is created");
        }
        
        for (int year=1994;year<=1997;year++) {
            for (int month=1;month<=12;month++) {
                BufferedWriter bw = new BufferedWriter(new FileWriter(targetPath + File.separator + year + "-" + month + "-edges.csv"));
                bw.write("Source,Target,Category,Type");
                bw.newLine();
                bw.close();
            }
        }

        ConstructIngredientNetworks cin = new ConstructIngredientNetworks();
        cin.run();
        
        for (String key : when2nodes.keySet()) {
            BufferedWriter bw = new BufferedWriter(new FileWriter(targetPath + File.separator + key + "-nodes.csv"));
            bw.write("Id,Label");
            bw.newLine();
            Set<String> ingredients = when2nodes.get(key);
            for (String ing: ingredients) {
                bw.write(ing + "," + ing);
                bw.newLine();
            }
            bw.close();
        }
       
    }
}
