package es.tid.haewoon.food.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import es.tid.haewoon.food.recipe.ElBulliRecipeCategory;
import es.tid.haewoon.food.util.Constants;
import es.tid.haewoon.food.util.FoodUtil;

public class GenerateDynamicNetworks {
    int startYear;
    int endYear;
    ElBulliRecipeCategory category;
    Map<Edge, Map<Integer, Integer>> edge2timeweight = new HashMap<Edge, Map<Integer, Integer>>();
    Map<String, Set<Integer>> node2time = new HashMap<String, Set<Integer>>();
    
    Logger logger = Logger.getLogger(GenerateDynamicNetworks.class);
    
    public GenerateDynamicNetworks(int startYear, int endYear, ElBulliRecipeCategory category) {
        // TODO Auto-generated constructor stub
        this.startYear = startYear;
        this.endYear = endYear;
        this.category = category;
    }
    
    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stub
        (new GenerateDynamicNetworks(1994, 1996, ElBulliRecipeCategory.ALL)).run();
        (new GenerateDynamicNetworks(1997, 1999, ElBulliRecipeCategory.ALL)).run();
        (new GenerateDynamicNetworks(2000, 2001, ElBulliRecipeCategory.ALL)).run();
        (new GenerateDynamicNetworks(1994, 2001, ElBulliRecipeCategory.AVANT_POSTRES)).run();
        (new GenerateDynamicNetworks(1994, 2001, ElBulliRecipeCategory.COCKTAILS)).run();
        (new GenerateDynamicNetworks(1994, 2001, ElBulliRecipeCategory.DESSERTS)).run();
        (new GenerateDynamicNetworks(1994, 2001, ElBulliRecipeCategory.DISHES)).run();
        (new GenerateDynamicNetworks(1994, 2001, ElBulliRecipeCategory.FOLLIES)).run();
        (new GenerateDynamicNetworks(1994, 2001, ElBulliRecipeCategory.MORPHINGS)).run();
        (new GenerateDynamicNetworks(1994, 2001, ElBulliRecipeCategory.PETITS_FOURS)).run();
        (new GenerateDynamicNetworks(1994, 2001, ElBulliRecipeCategory.PRE_DESSERTS)).run();
        (new GenerateDynamicNetworks(1994, 2001, ElBulliRecipeCategory.SNACKS)).run();
        (new GenerateDynamicNetworks(1994, 2001, ElBulliRecipeCategory.TAPAS)).run();
    }
    
    private void run() throws IOException {
        logger.debug(startYear + "-" + endYear + ", " + category);
        
        String targetPath = Constants.RESULT_PATH + File.separator + "4_dynamic_networks";
        printHeaders(targetPath + File.separator + this.category + "-" + this.startYear + "-to-" + this.endYear + "-dynamics-edges.csv", 
                "Source\tTarget\tType\tTime Interval");
        printHeaders(targetPath + File.separator + this.category + "-" + this.startYear + "-to-" + this.endYear + "-dynamics-nodes.csv", 
                "Id\tLabel\tTime Interval");
        readNetworks();
        printDynamicNetwork(Constants.RESULT_PATH + File.separator + "4_dynamic_networks");
    }

    private void printHeaders(String output, String header) throws IOException {
        // actually tsv (tab-separated values) 
        BufferedWriter bw = new BufferedWriter(new FileWriter(output));
        bw.write(header);
        bw.newLine();
        bw.close();
    }
    
    private class Edge {
        public Edge(String x, String y) {
            this.x = x;
            this.y = y;
        }
        public String x;
        public String y;
        @Override
        public boolean equals(Object obj) {
            Edge e = (Edge) obj;
            // TODO Auto-generated method stub
            return this.x.equals(e.x) && this.y.equals(e.y); 
        }
        @Override
        public int hashCode() {
            // TODO Auto-generated method stub
            return (x+y).hashCode();
        }
    }

    private void readNetworks() throws IOException {
        for (int year = this.startYear; year <= this.endYear; year++) {
            for (int month = 1; month <= 12; month++) {
                BufferedReader br = new BufferedReader(new FileReader(
                        Constants.RESULT_PATH + File.separator + "3_construct_networks_of_each_month" + File.separator + year + "-" + month));
                
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith("source")) {
                        continue;
                    }
                    
                    // category check
                    String[] tokens = line.split("\\t");
                    String ing1 = tokens[0];
                    String ing2 = tokens[1];
                    ElBulliRecipeCategory category = FoodUtil.getCategory(tokens[4]);
                    int timeUnit = (year - startYear) * 12 + month;
                    
                    if (this.category == ElBulliRecipeCategory.ALL || this.category == category) {
                        Map<Integer, Integer> time2weight = (edge2timeweight.get(new Edge(ing1, ing2)) == null) 
                                ?new HashMap<Integer, Integer>() :edge2timeweight.get(new Edge(ing1, ing2));
                                
                        int weight = (time2weight.get(timeUnit) == null) ?0 :time2weight.get(timeUnit);
                        time2weight.put(timeUnit, weight+1);
                        
                        edge2timeweight.put(new Edge(ing1, ing2), time2weight);   
                        
                        Set<Integer> times = (node2time.get(ing1) == null) ?new HashSet<Integer>() :node2time.get(ing1);
                        times.add(timeUnit);
                        node2time.put(ing1, times);
                        
                        times = (node2time.get(ing2) == null) ?new HashSet<Integer>() :node2time.get(ing2);
                        times.add(timeUnit);
                        node2time.put(ing2, times);
                    }
                }
            }
        }
    }
    
    private void printDynamicNetwork(String targetPath) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(
                targetPath + File.separator + this.category + "-" + this.startYear + "-to-" + this.endYear + "-dynamics-edges.csv", true));
        
        for (Edge edge : edge2timeweight.keySet()) {
            StringBuffer sb = new StringBuffer();
            sb.append("<");
            
            List<Integer> times = new ArrayList<Integer>(edge2timeweight.get(edge).keySet());
            Collections.sort(times);

            String delim = "";
            for (int timeunit : times) {
                sb.append(delim);
                sb.append("[" + timeunit + ", " + timeunit + ", " + edge2timeweight.get(edge).get(timeunit) + "]");
                delim = "; ";
            }
            sb.append(">");
            
            bw.write(edge.x + "\t" + edge.y + "\tUndirected\t" + sb.toString() + "\r\n");
        }
        bw.close();
        
        bw = new BufferedWriter(new FileWriter(
                targetPath + File.separator + this.category + "-" + this.startYear + "-to-" + this.endYear + "-dynamics-nodes.csv", true));
        
        for (String node : node2time.keySet()) {
            StringBuffer sb = new StringBuffer();
            sb.append("<");
            List<Integer> times = new ArrayList<Integer>(node2time.get(node));
            Collections.sort(times);

            String delim = "";
            for (int timeunit : times) {
                sb.append(delim);
                sb.append("[" + timeunit + ", " + timeunit + "]");
                delim = "; ";
            }
            sb.append(">");
           
            bw.write(node + "\t" + node + "\t" + sb.toString() + "\r\n");
        }
        bw.close();
    }
    

    
}
