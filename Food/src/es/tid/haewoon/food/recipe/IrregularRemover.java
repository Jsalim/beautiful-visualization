package es.tid.haewoon.food.recipe;

public class IrregularRemover extends Stemmer {
    public IrregularRemover(Stemmer next) {
        super(next);
    }
    
    @Override
    public String refine(String raw) {
        return raw.replaceAll("specially for .*", "").
                replaceAll(".*from the previous preparation", "").
                replaceAll("fleur de sel", "sea salt");
    }

}
