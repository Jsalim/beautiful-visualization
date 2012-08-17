package es.tid.haewoon.food.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import es.tid.haewoon.food.util.Constants;

public class ConstructIngredientNetworks {
    static Logger logger = Logger.getLogger(ConstructIngredientNetworks.class);
    private Map<String, String> label2numeric = new HashMap<String, String>();
    public static void main(String[] args) throws IOException {
        String targetPath = Constants.RESULT_PATH + File.separator + "3_construct_networks_of_each_month";
        boolean success = (new File(targetPath)).mkdir();
        if (success) {
            logger.debug("A directory [" + targetPath + "] is created");
        }
        
        ConstructIngredientNetworks cin = new ConstructIngredientNetworks();
        cin.printHeaders(targetPath, 1994, 2001, "source\ttarget\tyear\tmonth\tcategory\ttemperature\ttype");
        cin.readIngredientDictionary();
        cin.printNetworks(targetPath, 1994, 2001);
    }
    
    private void printHeaders(String targetPath, int startYear, int endYear, String header) throws IOException {
        for (int year = startYear; year <= endYear; year++) {
            for (int month = 1; month <= 12; month++) {
                BufferedWriter bw = new BufferedWriter(new FileWriter(targetPath + File.separator + year + "-" + month));
                bw.write(header);
                bw.newLine();
                bw.close();
            }
        }
    }
    
    private void readIngredientDictionary() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(
                Constants.RESULT_PATH + File.separator + "2_ingredient_integer_id/ingredient_dictionary.na"));
        String line; 
        
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split("\\t");
            label2numeric.put(tokens[1].trim(), tokens[0].trim());
        }
        
        logger.debug("read [" + label2numeric.size() + "] ingredients");
    }
    
    private void printNetworks(String targetPath, int startYear, int endYear) throws IOException {
        Set<String> ings = new HashSet<String>();
        String recipe = "N/A";
        String title = "N/A";
        String[] months = {};
        int year = -1;
        String category = "N/A";
        String ingredient = "N/A";
        String temperature = "N/A";
        
        BufferedReader br = new BufferedReader(new FileReader(
                Constants.RESULT_PATH + File.separator + "1_extract_ingredients" + File.separator + "CD2_and_3"));
        String line;
        
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split("\\t");
            
            if (!recipe.equals(tokens[0]) && ings.size() != 0) {
                for (String month : months) {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(targetPath + File.separator + year + "-" + month, true));
                    for (String ing1 : ings) {
                        for (String ing2 : ings) {
                            if (ing1.equals(ing2)) {
                                continue;       // don't count self-edges
                            }
                            bw.write(ing1 + "\t" + ing2 + "\t" + year + "\t" + month + 
                                    "\t" + category + "\t" + temperature + "\tUndirected\r\n");
                        }
                    }
                    bw.close();
                }
                ings.clear();
            }
            
            if (Integer.valueOf(tokens[1]) >= startYear && Integer.valueOf(tokens[1]) <= endYear) {
                recipe = tokens[0];
                title = tokens[1];
                year = Integer.valueOf(tokens[2]);
                category = tokens[3];
                temperature = tokens[4];
                months = tokens[5].split(",");
                ingredient = tokens[6];
                ings.add(ingredient);
            }
        }
    }


//  for line in codecs.open("../1_extract_ingredients/CD2_and_3", "r", "UTF-8"):
//      tokens = [term.strip() for term in line.split("\t")]
//
//      if recipe != tokens[0]:
//          # flush
//          for month in months:
//              fo = codecs.open(year + "-" + month, "a", "UTF-8")
//              for edges in [(i,j) for i in ings for j in ings if i != j]:
//  ##                fo.write(label2numeric[edges[0]] + "\t" + \
//  ##                         label2numeric[edges[1]] + "\t" + \
//  ##                         year + "\t" + month + "\t" + \
//  ##                         category + "\r\n")
//                  fo.write(edges[0] + "\t" + \
//                           edges[1] + "\t" + \
//                           year + "\t" + month + "\t" + \
//                           category + "\tundirected\r\n")
//
//              fo.close()
//          ings = set()
//      
//      recipe = tokens[0]
//      year = tokens[1]
//      category = tokens[2]
//      months = tokens[3].split(",")
//      ingredient = tokens[4]
//      ings.add(ingredient)
//
//          
//      

    
    
}
