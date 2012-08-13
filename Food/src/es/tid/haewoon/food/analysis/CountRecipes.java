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

public class CountRecipes {
	Logger logger = Logger.getLogger(CountRecipes.class);
	public static void main(String[] args) throws IOException {
		(new CountRecipes()).run(Constants.RESULT_PATH + File.separator + "9_number_of_recipes_in_each_month");
	}
	
	private void run(String targetDirectory) throws IOException {
		boolean success = (new File(targetDirectory)).mkdir();
		if (success) {
			logger.debug("[" + targetDirectory + "] is created...");
		}
		
		BufferedReader br = new BufferedReader(new FileReader(
                Constants.RESULT_PATH + File.separator + "1_extract_ingredients" + File.separator + "CD2_and_3"));
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(
				targetDirectory + File.separator + "unitTime2recipes"));
		
		String line;
		Map<Integer, Set<String>> time2Count = new HashMap<Integer, Set<String>>();
				
		while ((line = br.readLine()) != null) {
			String[] tokens = line.split("\t");
            String recipe = tokens[0];
            int year = Integer.valueOf(tokens[1]);
            String[] months = tokens[4].split(",");
            
            for (String month : months) {
            	int month_i = Integer.valueOf(month);
            	int timeUnit = (year-1994)*12 + month_i;
            	Set<String> old = (time2Count.get(timeUnit) != null) ?time2Count.get(timeUnit) :new HashSet<String>();
            	old.add(recipe);
            	time2Count.put(timeUnit, old);	
            }
		}
		
		List<Integer> times = new ArrayList<Integer>(time2Count.keySet());
		Collections.sort(times);
		
		Map<Integer, Set<String>> time2Remains = new HashMap<Integer, Set<String>>();
		for (int index = 0; index < times.size(); index++) {
			Set<String> cur = new HashSet<String>();
			for (int fromCur = index; fromCur < times.size(); fromCur++) {
				cur.addAll(time2Count.get(times.get(fromCur)));
			}
			time2Remains.put(times.get(index), cur);
		}
		
		
		for (int timeUnit : times) {
			if (timeUnit != 1) {
				Set<String> cur = new HashSet<String>(time2Count.get(timeUnit));
				Set<String> last = new HashSet<String>(time2Count.get(timeUnit-1));
				cur.removeAll(last);
				int new_coming_rec = cur.size();
				last.removeAll(time2Count.get(timeUnit));
				int disappear_rec = last.size();
				
				bw.write(timeUnit + "\t" + time2Count.get(timeUnit).size() + "\t+" + new_coming_rec + "\t-" + disappear_rec + "\t" + time2Remains.get(timeUnit).size());
			} else {
				bw.write(timeUnit + "\t" + time2Count.get(timeUnit).size() + "\t+" + time2Count.get(timeUnit).size() + "\t-0\t" + time2Remains.get(timeUnit).size());
			}
			bw.newLine();
		}
		bw.close();
	
	}
}
