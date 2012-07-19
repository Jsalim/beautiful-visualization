package es.tid.haewoon.cdr.filter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import es.tid.haewoon.cdr.util.CDR;
import es.tid.haewoon.cdr.util.CDRUtil;
import es.tid.haewoon.cdr.util.Constants;

public class ExtractFrequentNumber {
    TelephoneNumberFilter tnFilter;
    private final String allnb2c = Constants.RESULT_PATH + "/count_basic_statistics/all.nb2c";
    private static Logger logger = Logger.getLogger(ExtractFrequentNumber.class);
    private final int TOP_K = 300;
    private final String targetDirectory;
    private Map<String, Integer> rank2Num = new HashMap<String, Integer>();

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

                String number = line.split("\\t")[0].trim();
                s.add(number);
                i++;
                rank2Num.put(number, i);
            }        
        } catch (Exception e) {
            logger.debug(line);
            e.printStackTrace();    // something wrong
        }
        
        logger.info(s.size());
        
        tnFilter = new TelephoneNumberFilter(s);
        
        targetDirectory = Constants.RESULT_PATH + "/most_frequent_" + TOP_K + "_numbers";
        boolean success = (new File(targetDirectory)).mkdir();
        if (success) {
            logger.debug("[" + targetDirectory + "] directory created");
        }
    }
    
    public void run() throws IOException {
        List<File> files = CDRUtil.loadRefinedCDRFiles();
        
        for (File file: files) {
            logger.debug("processing " + file);
            String line = "";
            BufferedReader br = new BufferedReader(new FileReader(file));
            
            while((line = br.readLine()) != null) {
                CDR cdr;
                try {
                    cdr = new CDR(line);
                    if (tnFilter.filter(cdr)) {
                        String movistarNum = cdr.getMovistarNum();
                        BufferedWriter bw = new BufferedWriter(new FileWriter(
                                targetDirectory + File.separator + rank2Num.get(movistarNum) + "-" + cdr.getMovistarNum(), true));
                        bw.write(line.trim());
                        bw.newLine();
                        bw.close();
                    } else {
                        // filter infrequent numbers
                    }
                } catch (ParseException pe) {
                    // TODO Auto-generated catch block
                    logger.error("wrong-format CDR", pe);
                } catch (AssertionError e) {
                    // TODO Auto-generated catch block
                    logger.debug(line);
                    logger.fatal("something wrong / all CDRs here must have at least one Movistar number", e);
                    System.exit(0);
                }
            }
            
            br.close();
            
        }
    }
    
    public static void main(String[] args) throws IOException {
        ExtractFrequentNumber efn = new ExtractFrequentNumber();
        efn.run();
    }
}
