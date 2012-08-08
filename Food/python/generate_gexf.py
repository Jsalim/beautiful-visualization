import networkx as nx
import codecs
import glob
import os
from collections import defaultdict

files = glob.glob("/workspace/Food/result/3_construct_networks_of_each_month/*-*")




for afile in files:
    edge2weight = {}
    print afile
    G = nx.Graph()

    for line in codecs.open(afile, "r", "UTF-8"):
        if line.startswith("source"):
            continue
        
        tokens = [term.strip() for term in line.split("\t")]
        ing1 = tokens[0]
        ing2 = tokens[1]
                      
        edge2weight[(ing1, ing2)] = edge2weight.get((ing1, ing2), 0) + 1

    for edge in edge2weight:
        G.add_edge(edge[0], edge[1], weight=edge2weight[(edge[0], edge[1])]);
        
    nx.write_gexf(G, "/workspace/Food/result/7_gexf_networks/" + os.path.basename(afile) + ".gexf", "utf-8", True)
