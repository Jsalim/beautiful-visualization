package es.tid.haewoon.cdr.filter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import es.tid.haewoon.cdr.util.CDR;
import es.tid.haewoon.cdr.util.CDRUtil;
import es.tid.haewoon.cdr.util.Constants;


public class ExtractActiveWalkers {
    TelephoneNumberFilter tnFilter;
    private final String active_walker_path = Constants.RESULT_PATH + "/3_count_telnum_2_cells/all";
    private static Logger logger = Logger.getLogger(ExtractActiveWalkers.class);
    public static final int TOP_K = 10000;
    private final String targetDirectory;
    private Map<String, Integer> num2Rank = new HashMap<String, Integer>();
    private Map<String, String> num2Cell = new HashMap<String, String>();
    
    public ExtractActiveWalkers() {
        String line = "";
        Set<String> s = new HashSet<String>();

        int i = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(active_walker_path));

            while((line = br.readLine()) != null) {

                // loading TOP_K users into Set s
                if (i >= TOP_K) {
                    break;
                }

                String number = line.split("\\t")[0].trim();
                s.add(number);
                i++;
                num2Rank.put(number, i);
                num2Cell.put(number, line.split("\\t")[1].trim());
            }        
        } catch (Exception e) {
            logger.debug(line);
            e.printStackTrace();    // something wrong
        }
        
        logger.info(s.size());
        tnFilter = new TelephoneNumberFilter(s);
        
        targetDirectory = Constants.RESULT_PATH + File.separator + "4_most_frequent_" + TOP_K + "_active_walkers";
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
                                targetDirectory + File.separator + num2Rank.get(movistarNum) + "-" + cdr.getMovistarNum() + 
                                "-" + num2Cell.get(movistarNum), true));
                        bw.write(line.trim());
                        bw.newLine();
                        bw.close();
                    } else {
                        // filter inactive numbers
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
    
    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stub
        ExtractActiveWalkers eaw = new ExtractActiveWalkers();
        eaw.run();
    }

}
