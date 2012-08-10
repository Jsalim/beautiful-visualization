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
import es.tid.haewoon.cdr.util.CDRUtil;
import es.tid.haewoon.cdr.util.Constants;
import es.tid.haewoon.cdr.util.Province;

/*
 * This reads *.tar.gz and extract CDRs in Barcelona & filter Movistar to others.
 * The reason why we filter two things together is, reading raw files takes too long time.
 */
public class ExtractBarelonaAndMovistarToOthers {
    private static final Logger logger = Logger.getLogger(ExtractBarelonaAndMovistarToOthers.class);
    public static void main(String[] args) throws IOException, ParseException {
        (new ExtractBarelonaAndMovistarToOthers(Constants.FILTERED_PATH + File.separator + "1_barcelona")).run();
    }
    
    private String targetDirectory;
    
    public ExtractBarelonaAndMovistarToOthers(String targetPath) {
        this.targetDirectory = targetPath;
    }
    
    private void run() throws IOException { 
        boolean success = (new File(targetDirectory)).mkdir();
        if (success) {
            logger.debug("A directory [" + targetDirectory + "] is created");
        }
        
        CDRFilter barcelonaFilter = new CellFilter(CDRUtil.getCells(Province.BARCELONA));
        CDRFilter opFilter = new OtherOperatorFilter();
        
        List<File> files = CDRUtil.loadFiles(Constants.RAW_DATA_PATH, "^.*\\.gz$");
        logger.debug(files.size());
        String line;

        for (File archive: files) {
            logger.debug("processing " + archive);
            BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(archive))));
            BufferedWriter bw = new BufferedWriter(new FileWriter(targetDirectory + File.separator + archive.getName().split("\\.")[0]));
            while((line = br.readLine()) != null) {
                try {
                    CDR cdr = new CDR(line);
                    if (barcelonaFilter.filter(cdr) && opFilter.filter(cdr)) {
                        bw.write(line.trim());
                        bw.newLine();
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    logger.error("CDR Parse error? [" + line + "]", e);
                }
            }
            br.close();
            bw.close();
        }
    }
}
