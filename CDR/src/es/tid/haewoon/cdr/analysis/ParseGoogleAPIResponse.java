package es.tid.haewoon.cdr.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.tid.haewoon.cdr.util.Constants;

public class ParseGoogleAPIResponse {
    Logger logger = Logger.getLogger(ParseGoogleAPIResponse.class);
    public static void main(String[] args) throws IOException {
        (new ParseGoogleAPIResponse()).run(Constants.RESULT_PATH + File.separator + "12_directions_from_google", 
                                           Constants.RESULT_PATH + File.separator + "13_parsed_routes_by_google");
    }
    
    private void run(String readPath, String targetDirectory) throws IOException {
        boolean success = (new File(targetDirectory)).mkdir();
        if (success) {
            logger.debug("A directory[" + targetDirectory + "] is created");
        }
        
        // index for reading files
        BufferedReader br = new BufferedReader(new FileReader(
                Constants.RESULT_PATH + File.separator + "11_home_work_lat_long" + File.separator + "home_2_work"));
        
        String line;
        while ((line = br.readLine()) != null) {
            try {
                logger.debug(line);
                String[] tokens = line.split("\t");
                String number = tokens[0];
   
                if (!load(readPath, targetDirectory, number, "hw") || !load(readPath, targetDirectory, number, "wh")) {
                    break;
                }
            } catch (Exception e) {
                logger.error(line, e);
            }
        }
    }
    
    private boolean load(String readPath, String targetDirectory, String number, String identifier) throws IOException {
        List<String> latlngSequences = new ArrayList<String>();
        if (!(new File(readPath + File.separator + number + "-" + identifier + ".json")).exists()) {
            return false;
        }

        // Below codes are largely inspired from http://wiki.fasterxml.com/JacksonInFiveMinutes
        ObjectMapper m = new ObjectMapper();
        // can either use mapper.readTree(JsonParser), or bind to JsonNode
        JsonNode rootNode = m.readValue(new File(readPath + File.separator + number + "-" + identifier + ".json"), JsonNode.class);
        JsonNode routesNode = rootNode.path("routes");
        JsonNode legsNode = routesNode.get(0).path("legs");
        
        boolean first = true;
        for (JsonNode step : legsNode.get(0).path("steps")) {
            JsonNode start = step.path("start_location");
            JsonNode end = step.get("end_location");
            if (first) {
                latlngSequences.add(start.get("lat").asDouble() + "," + start.get("lng").asDouble());
                latlngSequences.add(end.get("lat").asDouble() + "," + end.get("lng").asDouble());
                first = false;
            } else {
                latlngSequences.add(end.get("lat").asDouble() + "," + end.get("lng").asDouble());
            }
        }
            
        BufferedWriter bw = new BufferedWriter(new FileWriter(targetDirectory + File.separator + number + "-" + identifier));
        for (String latlng : latlngSequences) {
            bw.write(latlng);
            bw.newLine();
        }
        bw.close();
        return true;
    }
}
