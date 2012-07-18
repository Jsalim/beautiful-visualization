package es.tid.haewoon.cdr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExtractTelefonicaToOtherOP {
    public static void main(String[] args) throws IOException {
        CDRFilter opFilter = new OtherOperatorFilter();
        ExtractTelefonicaToOtherOP etOther = new ExtractTelefonicaToOtherOP();
        List<File> files = etOther.loadFiles(Constants.BASE_PATH);
        
        String line;
        for (File file: files) {
            System.out.println("processing " + file);
            BufferedReader br = new BufferedReader(new FileReader(file));
            BufferedWriter bw = new BufferedWriter(new FileWriter(file.getPath() + ".opr"));
            
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
                    System.out.println(line);
                    e.printStackTrace();    // something wrong
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
                if (filename.matches("^.*\\.bcn\\.\\d{1,2}-\\d{1,2}$")) {
                    filtered.add(file);
                }
            }
        }
        return filtered;
    }
}
