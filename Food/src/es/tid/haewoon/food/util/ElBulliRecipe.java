package es.tid.haewoon.food.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class ElBulliRecipe {
    Logger logger = Logger.getLogger(ElBulliRecipe.class);
    String ID;
    String title;
    String year;
    String category;
    String months;
    String temperature;
    String person;
    List<String> ingredients;
    
    
    public ElBulliRecipe(File file) throws IOException {
        this.ID = file.getName().split("\\.")[0];
        String line;
        ingredients = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        while((line = br.readLine()) != null) {
            if (line.startsWith("&titol")) {
                title = parse(line);
            } else if (line.startsWith("&any")) {
                year = parse(line);
            } else if (line.startsWith("&familia")) {
                category = parse(line);
            } else if (line.startsWith("&temporada")) {
                months = parse(line);
            } else if (line.startsWith("&temperatura")) {
                temperature = parse(line);
            } else if (line.startsWith("&pers")) {
                person = parse(line);
            } else if (line.startsWith("&ingredientselaboracio")) {
                String mixed = parse(line);
                String[] tokens = mixed.split("<br>");
                for (int i = 0; i < tokens.length; i++) {
                    String ingredient = tokens[i].trim();
                    if (ingredient.equals("(ready prepared")) {
                        continue;
                    }
                    if (ingredient.indexOf("(") != -1 && ingredient.indexOf(")") == -1) {
                        ingredient = ingredient + " " + tokens[i+1].trim();
                        i++;
                    }
                    if (i+1 < tokens.length && tokens[i+1].trim().equals("tempered")) {
                        ingredient = ingredient + " tempered";
                        i++;
                    }
                    
                    String refined = IngredientRefiner.refine(ingredient);
                    if (refined.length() == 0) {
                        continue;
                    }
                    
                    ingredients.add(refined);
                }
            }
        }
    }
    
    private String parse(String line) {
        return line.split("=")[1];
    }
   
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        String lineDelimeter = "";
        for (String ingredient : ingredients) {
            sb.append(lineDelimeter);
            sb.append(this.ID + "\t" + this.year + "\t" + this.category + "\t" + this.months + "\t" + ingredient);
            lineDelimeter = "\r\n";
        }
        
        return sb.toString();
    }
}
