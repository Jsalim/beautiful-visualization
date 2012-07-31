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
                replaceAll("and on the shortest", "").
                replaceAll("^of", "").
                replaceAll("leaf of oregano leaf", "oregano leaf").
                replaceAll("stabiliser", "stabilizer").
                replaceAll("fleur de sel", "sea salt").
                replaceAll("nb use other herbs if these are not", "").
                replaceAll("nb during the green almond season", "").
                replaceAll("nb these ingredients are subject to seasonal availability", "").    // three consecutive rows
                replaceAll("nb the flower can be replaced by", "").
                replaceAll("^year$", "").
                replaceAll("seasonably available", "").
                replaceAll("st georges mushroom large and", "St. george's mushroom").
                replaceAll("st georges mushroom stock", "").
                replaceAll("bfor the amaretto toffeeb", "").
                replaceAll("can be added to each dish", "").
                replaceAll("see prevous step", "").
                replaceAll("tspcaper", "caper").
                replaceAll("^and long$", "").
                replaceAll("^and\\b", "").
                replaceAll("approx oz$", "").
                replaceAll("sheet gelatin", "gelatin sheet").
                replaceAll("agaragar", "agar-agar").
                replaceAll("and just ripe", "mango").   // FIXME, but not easy
                replaceAll("from the last step", "");    // typos in DVD
    }

}
