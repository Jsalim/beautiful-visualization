package es.tid.haewoon.food.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import es.tid.haewoon.food.recipe.ElBulliRecipeCategory;
import es.tid.haewoon.food.recipe.IrregularRemover;
import es.tid.haewoon.food.recipe.LowerCaser;
import es.tid.haewoon.food.recipe.NeedlessWhiteSpaceRemover;
import es.tid.haewoon.food.recipe.PluralToSingular;
import es.tid.haewoon.food.recipe.QualifierRemover;
import es.tid.haewoon.food.recipe.QuantifierRemover;
import es.tid.haewoon.food.recipe.SpecialCharRemover;
import es.tid.haewoon.food.recipe.Stemmer;
import es.tid.haewoon.food.recipe.UtensilRemover;


public class FoodUtil {
    private static class NumericComparator implements Comparator<File> {

        @Override
        public int compare(File o1, File o2) {
            File f1 = (File) o1;
            File f2 = (File) o2;
            
            if (f1.getName().split("\\.")[0].indexOf("-") != -1) {
                int f1_year = Integer.valueOf(f1.getName().split("\\.")[0].split("-")[0]);
                int f1_month = Integer.valueOf(f1.getName().split("\\.")[0].split("-")[1]);
                int f2_year = Integer.valueOf(f2.getName().split("\\.")[0].split("-")[0]);
                int f2_month = Integer.valueOf(f2.getName().split("\\.")[0].split("-")[1]);
                if (f1_year == f2_year) {
                    return new Integer(f1_month).compareTo(new Integer(f2_month));
                } else {
                    return new Integer(f1_year).compareTo(new Integer(f2_year));
                }
            } else {
                return (new Integer(f1.getName().split("\\.")[0])).compareTo(new Integer(f2.getName().split("\\.")[0]));
            }
        }
    }
    
    private static final Logger logger = Logger.getLogger(FoodUtil.class);
    private static final Comparator<File> nc = new NumericComparator();
    

    public static String join(Iterable c, String separator) {
        String delim = "";
        StringBuffer sb = new StringBuffer();
        for (Object o : c) {
            sb.append(delim + o);
            delim = separator;
        }
        return sb.toString();
    }
    
    
    public static List<File> loadFiles(String root, String pattern) {
        List<File> filtered = new ArrayList<File>();
        File targetPath = new File(root);
        if (targetPath.isDirectory()) {
            File[] files = targetPath.listFiles();
            for (File file : files) {
                String filename = file.getName();
                if (filename.matches(pattern)) {
                    filtered.add(file);
                }
            }
        }
        
        Collections.sort(filtered, nc);
        return filtered;
    }
    
    private static Stemmer stemmer;
    
    public static String stemElBulli(String raw) {
        if (stemmer == null) {
            stemmer = new SpecialCharRemover(new QuantifierRemover(new QualifierRemover(new UtensilRemover(
                    new PluralToSingular(new NeedlessWhiteSpaceRemover(new LowerCaser(new IrregularRemover())))))));
        }
        return stemmer.stem(raw).trim();
    }
    
    public static ElBulliRecipeCategory getCategory(String category) {
        if (category.equals("AVANT POSTRES")) {
            return ElBulliRecipeCategory.AVANT_POSTRES;
        } else if (category.equals("COCKTAILS")) {
            return ElBulliRecipeCategory.COCKTAILS;
        } else if (category.equals("DESSERTS")) {
            return ElBulliRecipeCategory.DESSERTS;
        } else if (category.equals("DISHES")) {
            return ElBulliRecipeCategory.DISHES;
        } else if (category.equals("FOLLIES")) {
            return ElBulliRecipeCategory.FOLLIES;
        } else if (category.equals("MORPHINGS")) {
            return ElBulliRecipeCategory.MORPHINGS;
        } else if (category.equals("PETITS FOURS")) {
            return ElBulliRecipeCategory.PETITS_FOURS;
        } else if (category.equals("PRE-DESSERTS")) {
            return ElBulliRecipeCategory.PRE_DESSERTS;
        } else if (category.equals("SNACKS")) {
            return ElBulliRecipeCategory.SNACKS;
        } else if (category.equals("TAPAS")) {
            return ElBulliRecipeCategory.TAPAS;
        } else {
            throw new AssertionError("not matched string [" + category + "]");
        }
    }
    
    
    public static List<File> loadFiles(String root, String pattern, String keyword) {
        List<File> temp = FoodUtil.loadFiles(root, pattern);
        List<File> filtered = new ArrayList<File>();
        
        for (File aFile : temp) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(aFile));
                String line;
                while((line = br.readLine()) != null) {
                    // do something with line.
                    if (line.indexOf(keyword) != -1) {
                        filtered.add(aFile);
                    }
                }
                br.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                logger.error(e);
            }
        }
        Collections.sort(filtered, nc);
        return filtered;
    }
    
    public static Map<Integer, List<String>> sortByValue(Map<String, Integer> old) {
        Map<Integer, List<String>> transpose = new HashMap<Integer, List<String>>();
        
        for (String key: old.keySet()) {
            Integer value = old.get(key);
            List<String> correspondingKeys = transpose.get(value);
            if (correspondingKeys == null) {
                correspondingKeys = new ArrayList<String>();
            } 
            correspondingKeys.add(key);
            transpose.put(value, correspondingKeys);
        }
        
        return transpose;
    }
}
