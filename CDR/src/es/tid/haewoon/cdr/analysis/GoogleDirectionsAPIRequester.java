package es.tid.haewoon.cdr.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import org.apache.log4j.Logger;

import es.tid.haewoon.cdr.util.Constants;

public class GoogleDirectionsAPIRequester {
    Logger logger = Logger.getLogger(GoogleDirectionsAPIRequester.class);
    
    public static void main(String[] args) throws IOException {
        (new GoogleDirectionsAPIRequester()).run(Constants.RESULT_PATH + File.separator + "12_directions_from_google");
    }
    
    private void run(String targetDirectory) throws IOException {
        boolean success = (new File(targetDirectory)).mkdir();
        if (success) {
            logger.debug("A directory[" + targetDirectory + "] is created...");
        }
        
        int DAILY_THRESHOLD = 2000; // request limits per day
        
        BufferedReader br = new BufferedReader(new FileReader(
                Constants.RESULT_PATH + File.separator + "11_home_work_lat_long" + File.separator + "home_2_work"));
        String line;
        int count = 0;
        
        while ((line = br.readLine()) != null) {
            try {
                String[] tokens = line.split("\t");
                String number = tokens[0];
                String home = tokens[1];
                String work = tokens[2];

                if (count >= DAILY_THRESHOLD) {
                    logger.debug("sleeping [" + (24 * 60 * 60 - DAILY_THRESHOLD * 30 + 3 * 60 * 60) / 60 + "] minutes");
                    Thread.sleep(24 * 60 * 60 * 1000 - DAILY_THRESHOLD * 5 * 1000 + 3 * 60 * 60 * 1000); // sleep for 3 hours more 
                    count = 0;
                }
                
                logger.debug(number + "\t" + home + "->" + work);
                boolean res1 = sendRequest(targetDirectory, home, work, number, "hw");
                if (res1) {
                    Thread.sleep(5 * 1000);
                    count++;
                }
                logger.debug(number + "\t" + work + "->" + home);
                boolean res2 = sendRequest(targetDirectory, work, home, number, "wh");
                if (res2) {
                    Thread.sleep(5 * 1000);
                    count++;
                }
            } catch (Exception e) {
                logger.error(line, e);
            }
        }
    }
    
    private boolean sendRequest(String targetDirectory, String origin, String destination, String number, String identifier) throws IOException {
        if ((new File(targetDirectory + File.separator + number + "-" + identifier + ".json")).exists()) {
            logger.debug("already crawled [" + number + ":" + origin + "->" + destination + "]");
            return false;
        }

        URL request = new URL("http://maps.googleapis.com/maps/api/directions/json?origin=" + origin + 
                "&destination=" + destination + "&sensor=false");
        URLConnection conn = request.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        BufferedWriter bw = new BufferedWriter(new FileWriter(targetDirectory + File.separator + number + "-" + identifier + ".json"));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            bw.write(inputLine);
            bw.newLine();
        }
        bw.close();
        in.close();
        
        return true;
    }
}
