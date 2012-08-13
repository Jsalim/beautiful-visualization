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

public class CharacterizeNewComingIngerdient {
	private Logger logger = Logger.getLogger(CharacterizeNewComingIngerdient.class);
	public static void main(String[] args) throws IOException {
		(new CharacterizeNewComingIngerdient()).run(
				Constants.RESULT_PATH + File.separator + "10_new_coming_ingredients");
	}
	
	private void run(String targetDirectory) throws IOException {
		boolean success = (new File(targetDirectory)).mkdir();
		if (success) {
			logger.debug("[" + targetDirectory + "] is created...");
		}
		
		// ingredient-# of recipe loaded
		// new incoming -> how many times it is used
		// output ingredient \t timepoint \t # of recipes
		// the computation for the boxplot is handled by ggplot2
		
		BufferedReader br = new BufferedReader(new FileReader(
				Constants.RESULT_PATH + File.separator + "8_ingredient_commonality" + File.separator + "ingredient2recipes"));
		String line;
		Map<String, Integer> ing2rcp = new HashMap<String, Integer>();
		while ((line = br.readLine()) != null) {
			String[] tokens = line.split("\t");
			String ingredient = tokens[0];
			int count = Integer.valueOf(tokens[1]);
			ing2rcp.put(ingredient, count);
		}
		br.close();
		
		br = new BufferedReader(new FileReader(Constants.RESULT_PATH + File.separator + 
				"9_number_of_recipes_in_each_month" + File.separator + "unitTime2recipes"));
		Map<Integer, Integer> time2rec = new HashMap<Integer, Integer>();
		while ((line = br.readLine()) != null) {
			String[] tokens = line.split("\t");
			time2rec.put(Integer.valueOf(tokens[0]), Integer.valueOf(tokens[4]));
		}
		br.close();
		
		Set<String> old = new HashSet<String>();
		br = new BufferedReader(new FileReader(
                Constants.RESULT_PATH + File.separator + "1_extract_ingredients" + File.separator + "CD2_and_3"));
		BufferedWriter bw = new BufferedWriter(new FileWriter(
				targetDirectory + File.separator + "new_coming_ingredients"));
		
		while ((line = br.readLine()) != null) {
			String[] tokens = line.split("\t");
//            String recipe = tokens[0];
            int year = Integer.valueOf(tokens[1]);
//            String category = tokens[2];
//            String temperature = tokens[3];
            String[] months = tokens[4].split(",");
            int month = Integer.valueOf(months[0]);
            int timeUnit = (year-1994)*12 + month;
            
            String ingredient = tokens[5];
	
            if (old.contains(ingredient)) {
            	continue;	// already discovered ingredient
            }
            
            int rcp_count = ing2rcp.get(ingredient);
            bw.write(ingredient + "\t" + timeUnit + "\t" + rcp_count + "\t" + ((double) rcp_count)/time2rec.get(timeUnit) + "\t" + time2rec.get(timeUnit));
            bw.newLine();
            old.add(ingredient);
		}
		
		br.close();
		bw.close();
	}
}
