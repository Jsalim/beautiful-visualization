package es.tid.haewoon.geotaggedtweets;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import twitter4j.GeoLocation;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.json.DataObjectFactory;

public class ExtractUserNameAndLocation {


    private void readJson(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        BufferedWriter bw = new BufferedWriter(new FileWriter(file.getPath()+".users"));
        String line = "";
        int count = 0;
        while((line = br.readLine()) != null) {
            if (line.length() == 0) {
                continue;
            }
            try {
                Status s = readStatus(line);
                GeoLocation gl = s.getGeoLocation();
                if (gl != null) {
                    User u = s.getUser();
                    String screenName = u.getScreenName();
                    String sLocation = u.getLocation();
                    double lat = gl.getLatitude();
                    double lon = gl.getLongitude();

                    bw.write(screenName + "\t" + sLocation + "\t" + lat + "\t" + lon);
                    bw.newLine();
                }
            } catch (TwitterException e) {
                e.printStackTrace();
                System.out.println("unexpected json string: " + line);
                continue;
            }
            count++;
        }

        br.close();
        bw.close();
        bw = new BufferedWriter(new FileWriter(file.getPath()+".done"));
        bw.write(count);
        bw.close();
    }

    private Status readStatus(String s) throws TwitterException {
        return DataObjectFactory.createStatus(s);
    }
    
    public static void main(String[] args) throws IOException {
        ExtractUserNameAndLocation enal = new ExtractUserNameAndLocation();
        for (File file:enal.loadFiles("/workspace/twitter/")) {
            System.out.println("processing " + file + "...");
            enal.readJson(file);
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
                if (filename.matches("^.*(UK|london)\\d$") && !(new File(file.getPath() + ".done").exists())) {
                    filtered.add(file);
                }
            }
        }
        return filtered;
    }

}
