package es.tid.haewoon.food.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.tid.haewoon.food.util.Constants;
import es.tid.haewoon.food.util.FoodUtil;

public class GenerateJsonNetwork {
    Map<String, Integer> ingredient2id = new HashMap<String, Integer>();
    Map<Integer, String> id2ingredient = new HashMap<Integer, String>();

    static Logger logger = Logger.getLogger(GenerateJsonNetwork.class);
    static String targetPath = Constants.RESULT_PATH + File.separator + "6_json_networks";

    public static void main(String[] args) throws IOException {
        boolean success = (new File(targetPath)).mkdir();
        if (success) {
            logger.debug("A directory [" + targetPath + "] is created");
        }
        
        GenerateJsonNetwork gjn = new GenerateJsonNetwork();
        gjn.run();
    }

    public GenerateJsonNetwork() {
        nodes = new ArrayList<Node>();
        links = new ArrayList<Link>();
    }

    List<Node> nodes;
    List<Link> links;

    public class Link {
        public Link(int x, int y, int v) {
            this.source = x;
            this.target = y;
            this.value = v;
        }

        public int source;
        public int value;
        public int target;

        @Override
        public boolean equals(Object obj) {
            Link e = (Link) obj;
            // TODO Auto-generated method stub
            return this.source == e.source && this.target == e.target; 
        }
        @Override
        public int hashCode() {
            // TODO Auto-generated method stub
            return (source+target + "").hashCode();
        }

        public String toString() {
            return source + " - (" + value + ")-> " + target;
        }
    }

    public class Node {
        public String name;
        public int id;
        public int group;
        public int degree;

        public Node(String name, int group) {
            this.name = name;
            this.group = group;
        }

        @Override
        public boolean equals(Object obj) {
            Node e = (Node) obj;
            // TODO Auto-generated method stub
            return this.name.equals(e.name); 
        }

        @Override
        public int hashCode() {
            // TODO Auto-generated method stub
            return (name + group).hashCode();
        }       
    }

    private void run() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(
                Constants.RESULT_PATH + File.separator + "2_ingredient_integer_id/ingredient_dictionary.na"));

        String line;
        while((line = br.readLine()) != null) {
            if (line.startsWith("source")) {
                continue;
            }
            String[] tokens = line.split("\t");
            ingredient2id.put(tokens[1], Integer.valueOf(tokens[0]));
            id2ingredient.put(Integer.valueOf(tokens[0]), tokens[1]);
        }
        br.close();

        List<File> files = FoodUtil.loadFiles(Constants.RESULT_PATH + File.separator + "3_construct_networks_of_each_month", "^\\d{4}-\\d{1,2}$");
        logger.debug(files);
        for (File aFile : files) {
            logger.debug(aFile);
            
            nodes.clear();
            links.clear();
            
            br = new BufferedReader(new FileReader(aFile));
            while((line = br.readLine()) != null) {
                if (line.startsWith("source")) {
                    continue;
                }

                String[] tokens = line.split("\t");
                String ing1 = tokens[0];
                String ing2 = tokens[1];

                Node n1 = new Node(ing1, FoodUtil.getCategory(tokens[4]).getID());
                if (nodes.indexOf(n1) != -1) {
                    n1 = nodes.remove(nodes.indexOf(n1));
                }
                n1.degree++;
                nodes.add(n1);
                
                Node n2 = new Node(ing2, FoodUtil.getCategory(tokens[4]).getID());
                if (nodes.indexOf(n2) != -1) {
                    n2 = nodes.remove(nodes.indexOf(n2));
                } 
                n2.degree++;
                nodes.add(n2);
                
                Link l = new Link(ingredient2id.get(ing1), ingredient2id.get(ing2), 1);
                if (links.indexOf(l) != -1) {
                    Link already = links.remove(links.indexOf(l));
                    already.value += 1;
                    links.add(already);
//                    logger.debug(already);
                } else {
                    links.add(l);
                }
            }


            // re-numbering
            List<Link> newLinks = new ArrayList<Link>();
            int index = 0;
            Map<String, Integer> ingredient2newID = new HashMap<String, Integer>();
            for (Node n : nodes) {
                ingredient2newID.put(n.name, index);
                n.id = index;
                index++;
            }

            for (Link l : links) {
                newLinks.add(new Link(ingredient2newID.get(id2ingredient.get(l.source)), 
                        ingredient2newID.get(id2ingredient.get(l.target)),
                        l.value));
            }

            Map result = new HashMap();
            result.put("nodes", nodes);
            result.put("links", newLinks);

            ObjectMapper om = new ObjectMapper();
            om.writeValue(new File(targetPath + File.separator + aFile.getName() + ".json"), result);
        }
    }
}








