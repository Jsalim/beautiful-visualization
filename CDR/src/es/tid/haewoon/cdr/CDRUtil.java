package es.tid.haewoon.cdr;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;


public class CDRUtil {
    private static CDRUtil cdrUtil = new CDRUtil();
    private static HashMap<String, Cell> cellid2Cell;
    private CDRUtil() {
        cellid2Cell = new HashMap<String, Cell>();
        BufferedReader br;
        String line;
        try {
            br = new BufferedReader(new FileReader("/workspace/CDR_data/GASSET_CELULA_900913.TXT"));
            while((line = br.readLine()) != null) {
                // do something with line.
                String[] tokens = line.split("\\|");
                if (tokens[0].equals("cell")) {
                    // skip this line
                    continue;
                }
                String cell = tokens[0];
                double longitude = Double.valueOf(tokens[6]);
                double latitude = Double.valueOf(tokens[7]);
                
                cellid2Cell.put(cell, new Cell(longitude, latitude));
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public static CDRUtil getInstance() {
        return cdrUtil;
    }
    
    public Cell getCell(String cellID) {
        return cellid2Cell.get(cellID);
    }
    
    public static void main(String[] args) {
        System.out.println(CDRUtil.getInstance().getCell("181562"));
    }
}
