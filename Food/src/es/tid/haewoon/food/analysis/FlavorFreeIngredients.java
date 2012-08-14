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

public class FlavorFreeIngredients {
	Logger logger = Logger.getLogger(FlavorFreeIngredients.class);
	public static void main(String[] args) throws IOException {
		(new FlavorFreeIngredients()).run(Constants.RESULT_PATH + File.separator + "11_flavor_free_ingredients");
	}
	
	Map<String, Integer> ffi2number = new HashMap<String, Integer>();
		
	private void run(String targetDirectory) throws IOException {
		boolean success = (new File(targetDirectory)).mkdir();
		if (success) {
			logger.debug("[" + targetDirectory + "] is created");
		}
		
		BufferedReader br = new BufferedReader(new FileReader(Constants.RESULT_PATH + File.separator + 
				"5_evolutionary_analysis" + File.separator + "CD2_and_3_preparation"));
		String line;
		BufferedWriter bw = new BufferedWriter(new FileWriter(targetDirectory + File.separator + "flavor_ffi"));
		BufferedWriter nbw = new BufferedWriter(new FileWriter(targetDirectory + File.separator + "fnfi"));
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
						ing.contains(flavor) || flavor.contains(ing) || 
						ing.contains(flavor.substring(0, flavor.length()-1)) || 
						flavor.contains(ing.substring(0, ing.length()-1))) {
						// this is not flavor-free ingredient
						nonFree.add(ing);
						
						if (ing.equals("cream")) logger.debug(line);
						nbw.write(ing);
						nbw.newLine();
					} 
					// this is flavor-free ingredient
				}
			}
			
			for (String ing : ings) {
				if (!nonFree.contains(ing)) {
					bw.write(recipe + "\t" + tokens[3] + "\t" + ing);
					bw.newLine();
					
					int old = (ffi2number.get(ing) != null) ?ffi2number.get(ing) :0;
					ffi2number.put(ing, old+1);
				}
			}
		}
		bw.close();
		
		bw = new BufferedWriter(new FileWriter(targetDirectory + File.separator + "ffi"));
		Map<Integer, List<String>> sffi = FoodUtil.sortByValue(ffi2number);
		List<Integer> numbers = new ArrayList(sffi.keySet());
		Collections.sort(numbers);
		Collections.reverse(numbers);
		for (int number : numbers) {
			List<String> ffis = sffi.get(number);
			for (String ffi : ffis) {
				bw.write(ffi + "\t" + number);
				bw.newLine();
			}
		}
		bw.close();
		nbw.close();
		
	}
}
