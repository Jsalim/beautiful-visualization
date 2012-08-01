package es.tid.haewoon.food.recipe;

import org.apache.log4j.Logger;

public class NeedlessWhiteSpaceRemover extends Stemmer {
    static Logger logger = Logger.getLogger(NeedlessWhiteSpaceRemover.class);
    public NeedlessWhiteSpaceRemover() {
        super();
    }
    public NeedlessWhiteSpaceRemover(Stemmer next) {
        super(next);
    }
    @Override
    public String refine(String raw) {
        return raw.replaceAll("\\s+", " ").trim();
    }

}
