package es.tid.haewoon.cdr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

public class ExtractFrequentNumber {
    TelephoneNumberFilter tnFilter;
    private final String allnb2c = "/workspace/CDR_data/result/count_basic_statistics/all.nb2c";
    private static Logger logger = Logger.getLogger(ExtractFrequentNumber.class);
    private final int TOP_K = 100;

    public ExtractFrequentNumber() {
        // read all.nb2c: the statistics sorted by the number of calls in descending order
        String line = "";
        Set<String> s = new HashSet<String>();

        int i = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(allnb2c));

            while((line = br.readLine()) != null) {

                // loading TOP_K users into Set s
                if (i >= TOP_K) {
                    break;
                }

                s.add(line.split("\\t")[0].trim());
                i++;
            }        
        } catch (Exception e) {
            logger.debug(line);
            e.printStackTrace();    // something wrong
        }
        
        logger.info(s.size());
        
        tnFilter = new TelephoneNumberFilter(s);
    }
    
    public void run() throws IOException {
        List<File> files = loadFiles("/workspace/CDR_data/", "opr");
        
        for (File file: files) {
            logger.debug("processing " + file);
            String line = "";
            BufferedReader br = new BufferedReader(new FileReader(file));
            BufferedWriter bw = new BufferedWriter(new FileWriter("/workspace/CDR_data/result/most_frequent_100_numbers/" + file.getName()));

            while((line = br.readLine()) != null) {
                CDR cdr;
                try {
                    cdr = new CDR(line);
                    if (tnFilter.filter(cdr)) {
                        bw.write(line.trim());
                        bw.newLine();
                    } else {
                        // filter infrequent numbers
                    }
                } catch (ParseException pe) {
                    // TODO Auto-generated catch block
                    logger.error("wrong-format CDR", pe);
                }
            }
            
            br.close();
            bw.close();
        }
    }
    
    public static void main(String[] args) throws IOException {
        ExtractFrequentNumber efn = new ExtractFrequentNumber();
        efn.run();
    }
    
    private List<File> loadFiles(String string, String extension) {
        // TODO Auto-generated method stub
        List<File> filtered = new ArrayList<File>();
        File targetPath = new File(string);
        if (targetPath.isDirectory()) {
            File[] files = targetPath.listFiles();
            for (File file : files) {
                String filename = file.getName();
                if (filename.matches("^.*\\." + extension + "$")) {
                    filtered.add(file);
                }
            }
        }
        return filtered;
    }
}
