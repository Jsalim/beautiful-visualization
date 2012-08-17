package es.tid.haewoon.food.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import es.tid.haewoon.food.util.Constants;

public class FlavorFreeIngredients {
	Logger logger = Logger.getLogger(FlavorFreeIngredients.class);
	public static void main(String[] args) throws IOException {
		(new FlavorFreeIngredients()).run(Constants.RESULT_PATH + File.separator + "11_flavor_free_ingredients");
	}

	private void run(String targetDirectory) throws IOException {
		boolean success = (new File(targetDirectory)).mkdir();
		if (success) {
			logger.debug("[" + targetDirectory + "] is created");
		}
		
		BufferedReader br = new BufferedReader(new FileReader(Constants.RESULT_PATH + File.separator + 
				"5_evolutionary_analysis" + File.separator + "CD2_and_3_preparation"));
		String line;
		BufferedWriter bw = new BufferedWriter(new FileWriter(targetDirectory + File.separator + "flavor_ffi_fnfi"));
		Set<String> allTimesFF = new HashSet<String>();
        
		while ((line = br.readLine()) != null) {
			String[] tokens = line.split("\t");
			String recipe = tokens[0];
			String[] flavors = tokens[3].split("\\|");
			String[] ings = tokens[4].split("\\|");
			Set<String> nonFree = new HashSet<String>();
			
			for (String ing : ings) {
				ing = ing.trim();
				for (String flavor : flavors) {
					flavor = flavor.trim();
					// fully contained?
					if (ing.contains("couverture") ||	// exception couverture <-> chocolate 
						ing.contains(flavor) || // flavor.contains(ing) || 
						ing.contains(flavor.substring(0, flavor.length()-1)) //|| flavor.contains(ing.substring(0, ing.length()-1)) 
						) {
						// this is not flavor-free ingredient
						nonFree.add(ing);
					} 
					// this is flavor-free ingredient
				}
			}
			
			for (String ing : ings) {
				if (!nonFree.contains(ing)) {
					bw.write(recipe + "\t" + tokens[3] + "\tFF\t" + ing);
					bw.newLine();
				} else {
				    bw.write(recipe + "\t" + tokens[3] + "\tFNF\t" + ing);
				    bw.newLine();
				}
			}
		}
		bw.close();
	}
}
