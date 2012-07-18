package es.tid.haewoon.cdr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class ExtractBarcelonaCDR {
    public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
        double[][] barcelonaBox = new double[2][2];
        
        barcelonaBox[0][0] = 2.05513;        // left-top longitude
        barcelonaBox[0][1] = 41.452505;      // left-top latitude 
        
        barcelonaBox[1][0] = 2.261124;       // right-bottom longitude
        barcelonaBox[1][1] = 41.336607;      // right-bottom latitude
        
        CDRFilter barcelonaFilter = new LocationFilter(barcelonaBox);
        
        ExtractBarcelonaCDR ebCDR = new ExtractBarcelonaCDR();
        List<File> archives = ebCDR.loadFiles(Constants.BASE_PATH);
        String line;
        
        for (File archive: archives) {
            System.out.println("processing " + archive);
            BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(archive))));
            BufferedWriter bw = new BufferedWriter(new FileWriter(archive.getPath()+".bcn"));
            while((line = br.readLine()) != null) {
                try {
                    CDR cdr = new CDR(line);
                    if (barcelonaFilter.filter(cdr)) {
                        bw.write(line.trim());
                        bw.newLine();
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    //e.printStackTrace();
                }
            }
        }
    }
    

    private List<File> loadFiles(String string) {
        // TODO Auto-generated method stub
        List<File> filtered = new ArrayList<File>();
        File targetPath = new File(string);
        if (targetPath.isDirectory()) {
            File[] files = targetPath.listFiles();
            for (File file : files) {
                String filename = file.getName();
                if (filename.matches("^.*tar\\.gz$")) {
                    filtered.add(file);
                }
            }
        }
        return filtered;
    }

}
