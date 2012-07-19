package es.tid.haewoon.cdr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CDRUtil {
    private static CDRUtil cdrUtil = new CDRUtil();
    private static HashMap<String, Cell> cellid2Cell;
    private CDRUtil() { throw new AssertionError(); }
    
    @Deprecated
    public static CDRUtil getInstance() {
        return cdrUtil;
    }
    
    public static Cell getCell(String cellID) {
        if (cellid2Cell == null) {
            cellid2Cell = new HashMap<String, Cell>();
            BufferedReader br;
            String line;
            try {
                br = new BufferedReader(new FileReader(Constants.BASE_PATH + File.separator + Constants.CELL_INFO_FILE_NAME));
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
        
        return cellid2Cell.get(cellID);
    }
    
    public static void main(String[] args) {
        System.out.println(CDRUtil.getCell("181562"));
    }
    
    public static List<File> loadRefinedCDRFiles() {
        List<File> files = CDRUtil.loadFiles(Constants.COMMUTING_HOURS_PATH + File.separator + "7-10", "^F1_GASSET_VOZ_\\d{1,2}092009$");
        files.addAll(CDRUtil.loadFiles(Constants.COMMUTING_HOURS_PATH + File.separator + "17-20", "^F1_GASSET_VOZ_\\d{1,2}092009$"));
        
        return files;
    }
    
    public static List<File> loadFiles(String string, String pattern) {
        // TODO Auto-generated method stub
        List<File> filtered = new ArrayList<File>();
        File targetPath = new File(string);
        if (targetPath.isDirectory()) {
            File[] files = targetPath.listFiles();
            for (File file : files) {
                String filename = file.getName();
                if (filename.matches(pattern)) {
                    filtered.add(file);
                }
            }
        }
        return filtered;
    }
}
