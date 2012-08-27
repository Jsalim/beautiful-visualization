package es.tid.haewoon.cdr.util;

import java.io.File;
import java.util.Comparator;

@Deprecated
public class RankComparator implements Comparator<File> {

    @Override
    public int compare(File o1, File o2) {
        // TODO Auto-generated method stub
        File f1 = (File) o1;
        File f2 = (File) o2;
        
        return Integer.valueOf(o1.getName().split("-")[0]).compareTo(Integer.valueOf(o2.getName().split("-")[0]));
        
    }
    
}
