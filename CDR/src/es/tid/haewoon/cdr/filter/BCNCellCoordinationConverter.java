package es.tid.haewoon.cdr.filter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

import es.tid.haewoon.cdr.util.Constants;
import es.tid.haewoon.cdr.util.CoordinationConverter;

public class BCNCellCoordinationConverter {
    static Logger logger = Logger.getLogger(BCNCellCoordinationConverter.class);
    public static void main(String[] args) throws IOException {
        CoordinationConverter cc = new CoordinationConverter();
        String line = "";
        
        BufferedWriter bw = new BufferedWriter(new FileWriter(Constants.BASE_PATH + File.separator + "GASSET_CELULA_BCN_haewoon.TXT")); 
        BufferedWriter be = new BufferedWriter(new FileWriter(Constants.FILTERED_PATH + File.separator + "error_GASSET_CELULA_BCN_haewoon.TXT"));
        
        BufferedReader br = new BufferedReader(new FileReader(Constants.BASE_PATH + File.separator + "GASSET_CELULA.TXT"));
        while((line = br.readLine()) != null) {
            String[] tokens = line.split("\\|");
            
            if (tokens[3].equals("Barcelona")) {
                try {
                String easting = tokens[4];
                String northing = tokens[5];
                double[] latlong = cc.utm2LatLon("31 T " + easting + " " + northing);
                
                // warning! 
                if (latlong[1] > 4.0) {
                    latlong = cc.utm2LatLon("30 T " + easting + " " + northing);
                }
                
                String delim = "|";
                StringBuffer sb = new StringBuffer();
                for (String token : tokens) {
                    sb.append(token);
                    sb.append(delim);
                }
                sb.append(latlong[1]);
                sb.append("|");
                sb.append(latlong[0]);

                bw.write(sb.toString());
                bw.newLine();
                } catch (Exception e) {
                    be.write(line);
                    be.newLine();
                }

            } else {
                // this coordinator does not work with towers in other province
            }
            
        }
        br.close();
        bw.close();
        be.close();
    }

}
