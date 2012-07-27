package es.tid.haewoon.food.recipe;

public class LowerCaser extends Stemmer {
    public LowerCaser() {
        super();
    }
    
    @Override
    public String refine(String raw) {
        return raw.toLowerCase();
    }

}
