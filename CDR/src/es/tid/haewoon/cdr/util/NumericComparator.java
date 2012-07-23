package es.tid.haewoon.cdr.util;

import java.util.Comparator;

public class NumericComparator implements Comparator<String> {

    @Override
    public int compare(String o1, String o2) {
        // TODO Auto-generated method stub
        o1 = (String) o1;
        o2 = (String) o2;
        
        return Integer.valueOf(o1).compareTo(Integer.valueOf(o2));
    }

}
