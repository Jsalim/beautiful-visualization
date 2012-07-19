package es.tid.haewoon.cdr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class ExtractCommutingHours {
    public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
        CDRFilter hToWFilter = new HourFilter(7, 10);   // from 7 to 10 o'clock
        CDRFilter wToHFilter = new HourFilter(17, 20);     // from 17 to 20 o'clock
        CDRFilter weekdayFilter = new WeekdayFilter();
        
        ExtractCommutingHours eodm = new ExtractCommutingHours();
        List<File> files = eodm.loadFiles(Constants.BASE_PATH);
        String line;
        
        for (File file: files) {
            System.out.println("processing " + file);
            BufferedReader br = new BufferedReader(new FileReader(file));
            BufferedWriter hToWBW = new BufferedWriter(new FileWriter(file.getParent() + File.separator + file.getName().split("\\.")[0] + 
                    ".bcn." + hToWFilter.toString()));
            BufferedWriter wToHBW = new BufferedWriter(new FileWriter(file.getParent() + File.separator + file.getName().split("\\.")[0] + 
                    ".bcn." + wToHFilter.toString()));
            
            
            while((line = br.readLine()) != null) {
                try {
                    CDR cdr = new CDR(line);
                    if (weekdayFilter.filter(cdr)) {
                        // only for weekday
                        if (hToWFilter.filter(cdr)) {
                            hToWBW.write(line.trim());
                            hToWBW.newLine();
                        } else if (wToHFilter.filter(cdr)) {
                            wToHBW.write(line.trim());
                            wToHBW.newLine();
                        }
                    }
                } catch (Exception e) {
                    System.out.println(line);
                    e.printStackTrace();    // something wrong
                }
            }
            br.close();
            hToWBW.close();
            wToHBW.close();
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
                if (filename.matches("^.*\\.bcn$")) {
                    filtered.add(file);
                }
            }
        }
        return filtered;
    }
}
