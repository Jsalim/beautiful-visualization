package es.tid.haewoon.food.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import es.tid.haewoon.food.util.Constants;
import es.tid.haewoon.food.util.FoodUtil;

public class EvolutionaryAnalysis {
    Logger logger = Logger.getLogger(EvolutionaryAnalysis.class);
    
    public static void main(String[] args) throws IOException {
        EvolutionaryAnalysis ea = new EvolutionaryAnalysis();
        ea.cd2_run(FoodUtil.loadFiles(Constants.CD2_PREPARATION_SWEET_PATH, "^\\d+\\.dat$"), "UTF-8");
        ea.cd2_run(FoodUtil.loadFiles(Constants.CD2_PREPARATION_SAVOURY_PATH, "^\\d+\\.dat$"), "UTF-8");

    }
    
    private void cd2_run(List<File> files, String encoding) throws IOException {
        String line;
       
        for (File afile : files) {
            Map<String, String> keyValue = new HashMap<String, String>();
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(afile), encoding));
            int numberOfItems = -1;
            while ((line = br.readLine()) != null ) {
                line = line.substring(1);
                
                String[] tokens = line.split("=");
                if (tokens.length == 2) {
                    if (tokens[0].startsWith("num")) {
                        numberOfItems = Integer.valueOf(tokens[0].substring(3));
                    }
                    keyValue.put(tokens[0], tokens[1]);
                }
                
            }
            
            if (keyValue.get("composicion1") == null) {
                continue;
            }
            
            logger.debug(afile.getName());
            logger.debug(keyValue);
            
            for (int i = 1; i <= numberOfItems; i++) {
                logger.debug(i);
                logger.debug(keyValue.get("mundo"));
                logger.debug(keyValue.get("num" + i));
                logger.debug(keyValue.get("codigoA" + i));
                logger.debug(keyValue.get("sabor" + i));
                logger.debug(keyValue.get("utilizacion" + i));
                logger.debug(Arrays.asList(keyValue.get("composicion" + i).split("\\s*!\\s")));
            }
        }
    }
}
