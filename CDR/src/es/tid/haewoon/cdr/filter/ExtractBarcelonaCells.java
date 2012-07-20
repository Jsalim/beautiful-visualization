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

public class ExtractBarcelonaCells {
    private static final Logger logger = Logger.getLogger(ExtractBarcelonaCells.class);
    public static void main(String[] args) throws IOException {
        String line = "";
        
        BufferedWriter bw = new BufferedWriter(new FileWriter(Constants.FILTERED_PATH + File.separator + 
                "4_barcelona_CELLs" + File.separator + Constants.CELL_INFO_FILE_NAME));
        BufferedReader br = new BufferedReader(new FileReader(Constants.BASE_PATH + File.separator + Constants.CELL_INFO_FILE_NAME));
        while((line = br.readLine()) != null) {
            if (line.startsWith("cell")) continue;  // skip the first line of column description
            try {
                Cell cell = new Cell(line);
                if(cell.isIn(Constants.BARCELONA_BOX)) {
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
