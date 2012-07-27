package es.tid.haewoon.food.recipe;

public class SpecialCharRemover extends Stemmer {
    public SpecialCharRemover(Stemmer next) {
        super(next);
    }
    
    @Override
    public String refine(String raw) {
        return raw.replaceAll(".*\\(ready prepared\\)", "").
                replaceAll("\\(.*\\)", "").
                replaceAll("\\d+", "").     // any number(s)
                replaceAll("\\.", "").
                replaceAll("\\?", "").
                replaceAll("º", "").
                replaceAll("½", "").
                replaceAll("¼", "").
                replaceAll(" b ", "").      // DVD's typo
                replaceAll("\"", "").
                replaceAll("/", "").
                replaceAll("'", "").
                replaceAll("\\.", "").
                replaceAll(" x ", "");
    }

}
