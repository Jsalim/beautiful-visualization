package es.tid.haewoon.cdr.util;

import java.io.File;
import java.util.Comparator;

public class RawFileComparator implements Comparator<File> {
    @Override
    public int compare(File f1, File f2) {
        // month * 100 + day
        int fingerprint1 = Integer.valueOf(f1.getName().substring(14,16))*1 + Integer.valueOf(f1.getName().substring(16,18))*100;
        int fingerprint2 = Integer.valueOf(f2.getName().substring(14,16))*1 + Integer.valueOf(f2.getName().substring(16,18))*100;

        return (new Integer(fingerprint1)).compareTo(new Integer(fingerprint2));
    }
}
