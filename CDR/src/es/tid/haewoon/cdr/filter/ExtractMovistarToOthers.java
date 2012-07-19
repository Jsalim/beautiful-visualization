package es.tid.haewoon.cdr.filter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import es.tid.haewoon.cdr.util.CDR;
import es.tid.haewoon.cdr.util.Constants;

public class ExtractMovistarToOthers {
    private static final Logger logger = Logger.getLogger(ExtractMovistarToOthers.class);
    
    public static void main(String[] args) throws IOException {
        CDRFilter opFilter = new OtherOperatorFilter();
        ExtractMovistarToOthers etOther = new ExtractMovistarToOthers();
        List<File> files = etOther.loadFiles(Constants.FILTERED_PATH + File.separator + "1_barcelona");
        
        String targetDirectory = Constants.FILTERED_PATH + File.separator + "2_movistar_to_others";
        boolean success = (new File(targetDirectory)).mkdir();
        if (success) {
            logger.debug("A directory [" + targetDirectory + "] is created");
        }
        
        String line;
        for (File file: files) {
            System.out.println("processing " + file);
            BufferedReader br = new BufferedReader(new FileReader(file));
            BufferedWriter bw = new BufferedWriter(new FileWriter(targetDirectory + File.separator + file.getName()));
            
            while((line = br.readLine()) != null) {
                try {
                    CDR cdr = new CDR(line);
                    if (opFilter.filter(cdr)) {
                        bw.write(line.trim());
                        bw.newLine();
                    } else {
                        // we skip calls between movistar - movistar, because of duplicate entities
                    }
                } catch (Exception e) {
                    logger.error("wrong-formatted CDR", e);    // many times
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
                if (filename.matches("^F1_GASSET_VOZ_\\d{1,2}092009$")) {
                    filtered.add(file);
                }
            }
        }
        return filtered;
    }
}
