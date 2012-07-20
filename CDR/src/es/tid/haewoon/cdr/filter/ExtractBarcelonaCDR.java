package es.tid.haewoon.cdr.filter;

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

import org.apache.log4j.Logger;

import es.tid.haewoon.cdr.util.CDR;
import es.tid.haewoon.cdr.util.Constants;

/*
 * This reads *.tar.gz and extract CDRs in Barcelona.
 */
public class ExtractBarcelonaCDR {
    private static final Logger logger = Logger.getLogger(ExtractBarcelonaCDR.class);
    public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
//        double[][] barcelonaBox = new double[2][2];
//        
//        barcelonaBox[0][0] = 2.05513;        // left-top longitude
//        barcelonaBox[0][1] = 41.452505;      // left-top latitude 
//        
//        barcelonaBox[1][0] = 2.261124;       // right-bottom longitude
//        barcelonaBox[1][1] = 41.336607;      // right-bottom latitude
//        
        CDRFilter barcelonaFilter = new LocationFilter(Constants.BARCELONA_BOX);
        
        ExtractBarcelonaCDR ebCDR = new ExtractBarcelonaCDR();
        List<File> archives = ebCDR.loadFiles(Constants.BASE_PATH);
        String line;
        
        String targetDirectory = Constants.FILTERED_PATH + File.separator + "1_barcelona";
        boolean success = (new File(targetDirectory)).mkdir();
        if (success) {
            logger.debug("A directory [" + targetDirectory + "] is created");
        }
        
        for (File archive: archives) {
            logger.debug("processing " + archive);
            BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(archive))));
            BufferedWriter bw = new BufferedWriter(new FileWriter(targetDirectory + File.separator + archive.getName().split("\\.")[0]));
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
            br.close();
            bw.close();
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
