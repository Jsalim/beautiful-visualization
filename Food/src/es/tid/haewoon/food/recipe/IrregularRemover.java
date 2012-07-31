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
                replaceAll("fleur de sel", "sea salt").
                replaceAll("nb use other herbs if these are not", "").
                replaceAll("seasonably available", "").
                replaceAll("st georges mushroom large and", "St. george's mushroom").
                replaceAll("st georges mushroom stock", "").
                replaceAll("bfor the amaretto toffeeb", "");    // typos in DVD
    }

}
