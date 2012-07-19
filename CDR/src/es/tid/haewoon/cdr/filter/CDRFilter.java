package es.tid.haewoon.cdr.filter;

import es.tid.haewoon.cdr.util.CDR;

public interface CDRFilter {
    boolean filter(CDR cdr);
}
