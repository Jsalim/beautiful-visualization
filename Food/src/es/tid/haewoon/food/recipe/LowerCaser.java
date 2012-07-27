package es.tid.haewoon.food.recipe;

public class LowerCaser extends Stemmer {
    public LowerCaser() {
        super();
    }
    
    public LowerCaser(Stemmer next) {
        super(next);
    }
    
    @Override
    public String refine(String raw) {
        return raw.toLowerCase();
    }

}
