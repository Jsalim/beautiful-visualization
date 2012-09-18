package es.tid.haewoon.cdr.filter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;

import org.apache.log4j.Logger;

import es.tid.haewoon.cdr.util.Cell;
import es.tid.haewoon.cdr.util.Constants;

@Deprecated
public class ExtractBarcelonaCells {
    private static final Logger logger = Logger.getLogger(ExtractBarcelonaCells.class);
    public static void main(String[] args) throws IOException {
        String line = "";
        
        BufferedWriter bw = new BufferedWriter(new FileWriter(Constants.BASE_PATH + File.separator + 
                "GASSET_CELULA_BCN_CITY.TXT"));
        BufferedReader br = new BufferedReader(new FileReader(Constants.BASE_PATH + File.separator + "GASSET_CELULA.TXT"));
        while((line = br.readLine()) != null) {
            if (line.startsWith("cell") || line.startsWith("CELL")) continue;  // skip the first line of column description
            try {
                Cell cell = new Cell(line);
                if(cell.getCity().equals("BARCELONA")) {
                    bw.write(line.trim());
                    bw.newLine();
                }     
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                logger.debug("wrong-formatted cell info.", e);
            }
        }
        br.close();
        bw.close();
    }
}
