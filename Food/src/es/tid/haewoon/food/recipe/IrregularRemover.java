package es.tid.haewoon.food.recipe;

public class IrregularRemover extends Stemmer {
    public IrregularRemover() {
        super();
    }
    
    public IrregularRemover(Stemmer next) {
        super(next);
    }
    
    @Override
    public String refine(String raw) {
        return raw.replaceAll("specially for .*", "").
                replaceAll(".*from the previous preparation", "").
                replaceAll("^s$", "").
                replaceAll("and on the shortest", "").
                replaceAll("^of", "").
                replaceAll("leaf of oregano leaf", "oregano leaf").
                replaceAll("stabiliser", "stabilizer").
                replaceAll(" orange", "orange").
                replaceAll("fleur de sel", "sea salt");
    }

}
