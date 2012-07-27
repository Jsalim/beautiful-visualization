package es.tid.haewoon.food.recipe;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;

public class TestReader {
    static Logger logger = Logger.getLogger(TestReader.class);
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("/workspace/Food/elBulli/CD3/us/data/455.dat"));
        String line;
        while ((line = br.readLine()) != null) {
            
            logger.debug(line);
            logger.debug(line.length());
            if (line.length() > 1) {
                logger.debug(line.trim().charAt(0));
            }
        }
    }
}
