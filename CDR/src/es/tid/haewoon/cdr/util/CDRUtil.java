package es.tid.haewoon.cdr.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;


public class CDRUtil {
    private static HashMap<String, Cell> cellid2Cell;
    private static HashMap<String, BTS> btsid2Bts;
    private static final Logger logger = Logger.getLogger(CDRUtil.class);
    private CDRUtil() { throw new AssertionError(); }
    
    @Deprecated
    public static CDRUtil getInstance() {
        return null;
    }
    
    public static String join(Iterable c, String separator) {
        String delim = "";
        StringBuffer sb = new StringBuffer();
        for (Object o : c) {
            sb.append(delim + o);
            delim = separator;
        }
        return sb.toString();
    }
    
    public static Set<Cell> getCells(Province p) {
        Set<Cell> s = new HashSet<Cell>();
        if (p == Province.BARCELONA) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(Constants.BARCELONA_CELL_INFO_PATH));
                String line;
                while((line = br.readLine()) != null) {
                    // do something with line.
                    if (line.startsWith("cell")) continue;
                    Cell cell = new Cell(line);
                    s.add(cell);
                }   
            } catch (Exception e) {
                logger.error(e);
            }
            logger.debug("loaded Barcelona cell info. [" + s.size() + "]");

        }
        return s;
    }
    
    public static Cell getCell(String cellID) {
        if (cellid2Cell == null) {
            logger.debug("cell/bts info. initialization for the first time running");
            cellid2Cell = new HashMap<String, Cell>();
            BufferedReader br;
            String line;
            try {
                br = new BufferedReader(new FileReader(Constants.BARCELONA_CELL_INFO_PATH));
                while((line = br.readLine()) != null) {
                    // do something with line.
                    if (line.startsWith("cell")) continue;
                    Cell cell = new Cell(line);
                    cellid2Cell.put(cell.getID(), cell);
                }
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                logger.debug("wrong-formatted cell", e);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                logger.debug("No file?", e);
            }
            logger.debug(cellid2Cell.size());
        }
        
        return cellid2Cell.get(cellID);
    }
    
    public static BTS getBTS(String btsID) {
        if (btsid2Bts == null) {
            logger.debug("bts info. initialization for the first time running");
            initializeBTSMap();
        }
        
        return btsid2Bts.get(btsID);
    }
    
    private static void initializeBTSMap() {
        btsid2Bts = new HashMap<String, BTS>();
        BufferedReader br;
        String line;
        try {
            br = new BufferedReader(new FileReader(Constants.BARCELONA_CELL_INFO_PATH));
            while((line = br.readLine()) != null) {
                // do something with line.
                if (line.startsWith("cell")) continue;
                BTS bts = new BTS(line);
                btsid2Bts.put(bts.getID(), bts);
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            logger.debug("wrong-formatted cell", e);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.debug("No file?", e);
        }
    }
    
    public static Set<BTS> getBTSs(Province p) {
        if (p == Province.BARCELONA) {
            if (btsid2Bts == null) {
                logger.debug("bts info. initialization for the first time running");
                initializeBTSMap();
            }
        }
        return new HashSet<BTS>(btsid2Bts.values());
    }
    
    public static double getDistance(BTS b1, BTS b2) {
        double lat1 = b1.getLatitude();
        double lon1 = b1.getLongitude();
        double lat2 = b2.getLatitude();
        double lon2 = b2.getLongitude();

        return getDistance(lat1, lon1, lat2, lon2);
    }
        /**
         * Calculates geodetic distance between two points specified by latitude/longitude using Vincenty inverse formula
         * for ellipsoids
         * 
         * @param lat1
         *            first point latitude in decimal degrees
         * @param lon1
         *            first point longitude in decimal degrees
         * @param lat2
         *            second point latitude in decimal degrees
         * @param lon2
         *            second point longitude in decimal degrees
         * @returns distance in meters between points with 5.10<sup>-4</sup> precision
         * @see <a href="http://www.movable-type.co.uk/scripts/latlong-vincenty.html">Originally posted here</a>
         */
        
    public static double getDistance(double lat1, double lon1, double lat2, double lon2) {
        
        double a = 6378137, b = 6356752.314245, f = 1 / 298.257223563; // WGS-84 ellipsoid params
        double L = Math.toRadians(lon2 - lon1);
        double U1 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat1)));
        double U2 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat2)));
        double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
        double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);

        double sinLambda, cosLambda, sinSigma, cosSigma, sigma, sinAlpha, cosSqAlpha, cos2SigmaM;
        double lambda = L, lambdaP, iterLimit = 100;
        do {
            sinLambda = Math.sin(lambda);
            cosLambda = Math.cos(lambda);
            sinSigma = Math.sqrt((cosU2 * sinLambda) * (cosU2 * sinLambda)
                    + (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda) * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda));
            if (sinSigma == 0)
                return 0; // co-incident points
            cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
            sigma = Math.atan2(sinSigma, cosSigma);
            sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
            cosSqAlpha = 1 - sinAlpha * sinAlpha;
            cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;
            if (Double.isNaN(cos2SigmaM))
                cos2SigmaM = 0; // equatorial line: cosSqAlpha=0 (§6)
            double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
            lambdaP = lambda;
            lambda = L + (1 - C) * f * sinAlpha
                    * (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)));
        } while (Math.abs(lambda - lambdaP) > 1e-12 && --iterLimit > 0);

        if (iterLimit == 0)
            return Double.NaN; // formula failed to converge

        double uSq = cosSqAlpha * (a * a - b * b) / (b * b);
        double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
        double deltaSigma = B
                * sinSigma
                * (cos2SigmaM + B
                        / 4
                        * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B / 6 * cos2SigmaM
                                * (-3 + 4 * sinSigma * sinSigma) * (-3 + 4 * cos2SigmaM * cos2SigmaM)));
        double dist = b * A * (sigma - deltaSigma);

        return dist;
    }
    
    public static List<File> loadAllCDRFiles() {
        List<File> files = CDRUtil.loadFiles(Constants.FILTERED_PATH + File.separator + "5_1_sorted_home_hours", Constants.RAW_DATA_FILE_PATTERN);
        files.addAll(CDRUtil.loadFiles(Constants.FILTERED_PATH + File.separator + "5_2_sorted_work_hours", Constants.RAW_DATA_FILE_PATTERN));
        files.addAll(CDRUtil.loadFiles(Constants.FILTERED_PATH + File.separator + "5_3_sorted_commuting_hours", Constants.RAW_DATA_FILE_PATTERN));
        
        Collections.sort(files, new RawFileComparator());
        return files;
    }
    
    
    @Deprecated
    public static List<File> loadRefinedCDRFiles() {
        List<File> files = CDRUtil.loadFiles(Constants.SORTED_COMMUTING_HOURS_PATH + File.separator + "7-10", Constants.RAW_DATA_FILE_PATTERN);
        files.addAll(CDRUtil.loadFiles(Constants.SORTED_COMMUTING_HOURS_PATH + File.separator + "17-20", Constants.RAW_DATA_FILE_PATTERN));
        
        Comparator<File> mosaic = new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                String f1_dir = f1.getParentFile().getName();
                String f2_dir = f2.getParentFile().getName();
               
                if (f1.getName().equals(f2.getName())) {
                    return Integer.valueOf(f1_dir.split("-")[0]).compareTo(Integer.valueOf(f2_dir.split("-")[0]));
                } else {
                    return f1.getName().compareTo(f2.getName());
                }
            }
        };
        
        Collections.sort(files, mosaic);
        
        return files;
    }
    
    public static List<File> loadFiles(String loadDirectory, String pattern) {
        List<File> filtered = new ArrayList<File>();
        File targetPath = new File(loadDirectory);
        if (targetPath.isDirectory()) {
            File[] files = targetPath.listFiles();
            for (File file : files) {
                String filename = file.getName();
                if (filename.matches(pattern)) {
                    filtered.add(file);
                }
            }
        }

        logger.debug("loading [" + loadDirectory + ", " + filtered.size() + "] files...");
        return filtered;
    }
    
    public static void printMap(String path, Map map) {
        printMap(path, map, false);
    }
    
    public static void printMap(String path, Map map, boolean append) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(path, append));

            List keys = new ArrayList(map.keySet());
            Collections.sort(keys);
            
            for (Object key: keys) {
                if (map.get(key) instanceof Collection) {
                    bw.write(key + "\t" + ((Set) map.get(key)).size() + "\t" + map.get(key));
                } else {
                    bw.write(key + "\t" + map.get(key));
                }
                bw.newLine();
            }
            bw.close();
            
        } catch (IOException e) {
            // never happened
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
    }

    public static Map countItem(Map map, Object item) {
        return countItem(map, item, 1);
    }
    
    public static Map countItem(Map map, Object item, int value) {
        Integer i = (Integer) map.get(item);
        if (i != null) {
            map.put(item, i+value);
        } else {
            map.put(item, value);   // initialization
        }

        return map;
    }
    
    public static Map<Integer, List<String>> transpose(Map<String, Integer> old) {
        Map<Integer, List<String>> transpose = new HashMap<Integer, List<String>>();
        
        for (String key: old.keySet()) {
            Integer value = old.get(key);
            List<String> correspondingKeys = transpose.get(value);
            if (correspondingKeys == null) {
                correspondingKeys = new ArrayList<String>();
            } 
            correspondingKeys.add(key);
            transpose.put(value, correspondingKeys);
        }
        
        return transpose;
    }
    
    // sort by value
    public static void printMapSortedByValue(String outputPath, Map<String, Integer> map) {
        Map<Integer, List<String>> tp = transpose(map);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath));

            List<Integer> keys = new ArrayList<Integer>(tp.keySet());
            
            // descending order
            Collections.sort(keys);
            Collections.reverse(keys);
            
            for (Integer key: keys) {
                for (String value: tp.get(key)) {
                    bw.write(value + "\t" + key);
                    bw.newLine();
                }
            }
            bw.close();
            
        } catch (IOException e) {
            // never happened
            // TODO Auto-generated catch block
            e.printStackTrace();
        }         
    }
    
    public static Set<BTS> nearBTS (double clat, double clon, Set<BTS> BTSs, int THRESHOLD) {
        for (BTS bts : CDRUtil.getBTSs(Constants.PROVINCE)) {
            if (bts.closeEnough(clat, clon, THRESHOLD)) {
//                logger.debug(bts + " near " + clat + "," + clon);
                BTSs.add(bts);
            }
        }
        return BTSs;
    }
    
    public static Set<BTS> nearBTS(double llat, double llon, double clat, double clon, Set<BTS> BTSs, int THRESHOLD) {
        final double latDelta = clat - llat;
        final double lonDelta = clon - llon;
        
        if (latDelta == 0 && lonDelta == 0) {
            return BTSs;
        }
        
        for (BTS bts : CDRUtil.getBTSs(Constants.PROVINCE)) {
            final double u = ((bts.getLatitude() - llat) * latDelta + (bts.getLongitude() - llon) * lonDelta) / (latDelta * latDelta + lonDelta * lonDelta);
            
            if (u < 0 || u > 1) {
                continue;
            } else {
                double[] closePoint = {llat + u * latDelta, llon + u * lonDelta};
                if (bts.closeEnough(closePoint[0], closePoint[1], THRESHOLD)) {
//                    logger.debug(bts + " near " + llat + "," + llon + "->" + clat + "," + clon);
                    BTSs.add(bts);
                }
            }
        }
        
        return BTSs;
    }
    
    public static List<String> getOrderedNumbers() {
        List<String> numbers = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(
                    Constants.RESULT_PATH + File.separator + "1_3_count_basic_statistics_in_commuting_hours" + File.separator + "all.caller_ee"));
            String line;

            while ((line = br.readLine()) != null) {
                String[] tokens = line.split("\t");
                String number = tokens[0];
                int calls = Integer.valueOf(tokens[1]);

                if (calls >= 1 * Constants.DAYS && calls <= 10 * Constants.DAYS) {
                    numbers.add(number);
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }
        return numbers;
    }
}
