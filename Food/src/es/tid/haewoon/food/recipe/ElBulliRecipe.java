package es.tid.haewoon.food.recipe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import es.tid.haewoon.food.util.FoodUtil;


public class ElBulliRecipe {
    Logger logger = Logger.getLogger(ElBulliRecipe.class);
    String ID;
    String title;
    String year;
    String category;
    String months;
    String temperature;
    String person;
    Set<String> ingredients;

    
    public ElBulliRecipe(File file) throws IOException {
        this(file, "UTF-8");
    }
    
    public ElBulliRecipe(File file, String encoding) throws IOException {
        this.ID = file.getName().split("\\.")[0];
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
        fill(br);
    }

    public void fill(BufferedReader br) throws IOException {
        ingredients = new HashSet<String>();
        String line;
        
        while((line = br.readLine()) != null) {
            if (line.startsWith("titol")) {
                title = parse(line);
            } else if (line.startsWith("&any")) {
                year = parse(line);
            } else if (line.startsWith("&familia")) {
                category = parse(line);
            } else if (line.startsWith("&temporada")) {
                if(parse(line).equals("ALL YEAR")) {
                    months = "1,2,3,4,5,6,7,8,9,10,11,12";
                } else {
                    months = parse(line).replaceAll("\\.", ",");
                }
            } else if (line.startsWith("&temperatura")) {
                temperature = parse(line);
            } else if (line.startsWith("&pers")) {
                person = parse(line);
            } else if (line.startsWith("&ingredients")) {
                String mixed = parse(line);
                String[] tokens;
                
                if (mixed.indexOf("<br>") != -1) {
                    tokens = mixed.split("<br>");
                } else {
                    tokens = mixed.split("#");
                }
                
                for (int i = 0; i < tokens.length; i++) {
                    String ingredient = tokens[i].trim();
                    if (i < tokens.length-1 && tokens[i+1].trim().equals("(ready prepared)")) {
                        ingredient = ingredient + tokens[i+1].trim();
                        i++;
                    }
                    
                    if (i < tokens.length - 1 && tokens[i].trim().endsWith("previous")) {
                        ingredient = ingredient + " " + tokens[i+1].trim();
                        i++;
                    }
                    
                    if (i < tokens.length - 1 && tokens[i+1].trim().startsWith("and")) {
                        ingredient = ingredient + " " + tokens[i+1].trim();
                        i++;
                    }
                    
                    if (i < tokens.length - 1 && tokens[i].trim().endsWith("yellow")) {
                        ingredient = ingredient + " " + tokens[i+1].trim();
                        i++;
                    }

                    if (ingredient.indexOf("(") != -1 && ingredient.indexOf(")") == -1) {
                        int j = i+1;
                        for (; j < tokens.length; j++) {
                            ingredient = ingredient + tokens[j].trim();
                            if (tokens[j].indexOf(")") != -1) {
                                break;
                            } 
                        }
                        i = j;
                    }
                    if (i+1 < tokens.length && tokens[i+1].trim().equals("tempered")) {
                        ingredient = ingredient + " tempered";
                        i++;
                    }

                    String refined = FoodUtil.stemElBulli(ingredient);

                    if (refined.length() == 0) {
                        continue;
                    }
                    ingredients.add(refined);
                }
            }
        }
    }

    private String parse(String line) {
        try {
            return line.split("=")[1];
        } catch (Exception e) { // pers=
            return "N/A";
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        String lineDelimeter = "";
        for (String ingredient : ingredients) {
            sb.append(lineDelimeter);
            sb.append(this.ID + "\t" + this.year + "\t" + this.category + "\t" + this.temperature + "\t" + this.months + "\t" + ingredient);
            lineDelimeter = "\r\n";
        }

        return sb.toString();
    }
}
