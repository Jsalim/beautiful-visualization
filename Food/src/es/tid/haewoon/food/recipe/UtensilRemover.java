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
                replaceAll(".*soda siphon$", "").
                replaceAll(".*silicon mold$", "").
                replaceAll(".*transparencies", "").
                replaceAll("N.{1,5}cartridge", "").       // N20 Catridge
                replaceAll("ISI.*", "").
                replaceAll(".*spoons", "");         // ISI 1 pint soda siphon

    }

}
