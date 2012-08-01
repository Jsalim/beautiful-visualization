package es.tid.haewoon.food.recipe;

public class UtensilRemover extends Stemmer {
    public UtensilRemover(Stemmer next) {
        super(next);
    }
    
    @Override
    public String refine(String raw) {
        return raw.replaceAll(".*PVC.*", "").
                replaceAll(".*transparency$", "").
                replaceAll(".*metal frame$", "").
                replaceAll(".*wooden skewers$", "").
                replaceAll(".*siphon$", "").
                replaceAll(".*silicon mold$", "").
                replaceAll(".*\\bcellophane\\b.*", "").
                replaceAll(".*transparencies$", "").
                replaceAll(".*pipettes$", "").
                replaceAll("\\bcup\\b", "").
                replaceAll("in an icing bag$", "").
                replaceAll("in a shaker$", "").
                replaceAll(".*mould", "").
                replaceAll("Cocktail\\s*Master", "").
                replaceAll("N.{1,5}cartridge$", "").       // N20 Catridge
                replaceAll("ISI.*", "").
                replaceAll(".*spoons$", "");         // ISI 1 pint soda siphon

    }

}
