package es.tid.haewoon.food.analysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import es.tid.haewoon.food.recipe.ElBulliRecipe;
import es.tid.haewoon.food.util.Constants;
import es.tid.haewoon.food.util.FoodUtil;

public class ExtractIngredientsFromRecipes {
    private static Logger logger = Logger.getLogger(ExtractIngredientsFromRecipes.class);
    private static String targetPath = Constants.RESULT_PATH + File.separator + "1_extract_ingredients";
    
    
    public void run(String root, String pattern, boolean append) throws IOException {
        run(root, pattern, append, "UTF-8");
    }
    
    public void run(String root, String pattern, boolean append, String encoding) throws IOException {
        List<File> recipes = FoodUtil.loadFiles(root, pattern);
        logger.debug(recipes.size() + " files loaded...");
        BufferedWriter bw = new BufferedWriter(new FileWriter(targetPath + File.separator + "CD2_and_3", append));
        BufferedWriter bw2 = new BufferedWriter(new FileWriter(targetPath + File.separator + "concise_CD2_and_3.txt", append));
        for (File file : recipes) {
            logger.debug(file + " is processing...");
            try {
                ElBulliRecipe recipe = new ElBulliRecipe(file, encoding);
                if (recipe.toString().trim().length() == 0) {
                    logger.debug(file.getName() + " is length 0?");
                    continue;
                }
                
                bw.write(recipe.toString());
                bw.newLine();
                
                bw2.write(recipe.toConciseString());
                bw2.newLine();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                logger.error(e);
            }
        }
        bw.close();
        bw2.close();
    }
    
    public static void main(String[] args) throws IOException {
        boolean success = (new File(targetPath)).mkdir();
        if (success) {
            logger.debug("A directory [" + targetPath + "] is created");
        }
        
        ExtractIngredientsFromRecipes eifr = new ExtractIngredientsFromRecipes();
        eifr.run(Constants.CD2_RECIPES_PATH, Constants.RECIPE_PATTERN, false);
        eifr.run(Constants.CD3_RECIPES_PATH, Constants.RECIPE_PATTERN, true, "UTF-16LE");       // some problems in comparing strings.
    }
}
