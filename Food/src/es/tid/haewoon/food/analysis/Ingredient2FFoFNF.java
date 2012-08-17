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

@Deprecated
public class Ingredient2FFoFNF {
    Logger logger = Logger.getLogger(Ingredient2FFoFNF.class);
    
    public static void main(String[] args) throws IOException {
        (new Ingredient2FFoFNF()).run(Constants.RESULT_PATH + File.separator + "12_ingredient_2_ff_or_fnf");
    }
    
    private void run(String targetDirectory) throws IOException {
        boolean success = (new File(targetDirectory)).mkdir();
        if (success) {
            logger.debug("A directory [" + targetDirectory + "] is created...");
        }
        
        BufferedReader br = new BufferedReader(new FileReader(Constants.RESULT_PATH + File.separator + "11_flavor_free_ingredients" + File.separator + "flavor_ffi_fnfi"));
        String line;
        
        Map<String, Set<String>> ing2ff_fnf = new HashMap<String, Set<String>>();
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split("\t");
            String ff_fnf = tokens[2];
            String ing = tokens[3];
            if (ing.equals("rabbit")) {
                ff_fnf = "FNF";
            } else if (ing.equals("aga-agar")) {    // typo
                ing = "agar-agar";
            } else if (ing.contains(" and ")) {
                continue;
            }
            Set<String> old = (ing2ff_fnf.get(ing) != null) ?ing2ff_fnf.get(ing) :new HashSet<String>();
            old.add(ff_fnf);
            ing2ff_fnf.put(ing, old);
        }
        
        List<String> ings = new ArrayList(ing2ff_fnf.keySet());
        Collections.sort(ings);
        
        BufferedWriter bw = new BufferedWriter(new FileWriter(targetDirectory + File.separator + "ing2ff_or_fnf"));
        BufferedWriter ffbw = new BufferedWriter(new FileWriter(targetDirectory + File.separator + "ing2onlyff"));
        for (String ing : ings) {
            if (ing2ff_fnf.get(ing).size() == 1) {
                bw.write(ing + "\t" + ing2ff_fnf.get(ing).toArray()[0]);
                if (ing2ff_fnf.get(ing).toArray()[0].equals("FF")) {
                    ffbw.write(ing + "\tFF");
                    ffbw.newLine();
                }
            } else {
                bw.write(ing + "\tFF|FNF");
            }
            bw.newLine();
        }
        bw.close();
        ffbw.close();
    }
}
