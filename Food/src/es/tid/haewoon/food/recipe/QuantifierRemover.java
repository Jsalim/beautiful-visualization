package es.tid.haewoon.food.recipe;

public class QuantifierRemover extends Stemmer {
    String unitsPattern = ".*(cups|tbsps|tsps|pints)\\s";
    String unitPattern = ".*(cup|tbsp|oz|tsp|pint|Ib|lb|diameter)\\s";
    
    public QuantifierRemover(Stemmer next) {
        super(next);
    }
    

    @Override
    public String refine(String raw) {
        return raw.replaceAll("mg\\.?$", "").
                replaceAll("\\sg\\s", "").
                replaceAll(unitsPattern, "").
                replaceAll(unitPattern, "").
                replaceAll("approx.", "").
                replaceAll("per 2 cups of .*", "").
                replaceAll("a few drops of", "").
                replaceAll("$a\\s|\\sa\\s", "").
                replaceAll("cube of", "").
                replaceAll("drop of", "").
                replaceAll("drops of", "").
                replaceAll("pieces of", "").
                replaceAll("semicircles of", "").
                replaceAll("slices of", "").
                replaceAll("sliced in half", "").
                replaceAll("semi-circlular", "").
                replaceAll("piece of", "").
                replaceAll("large pinch of", "").
                replaceAll("^of", "").
                replaceAll("cans of", "").
                replaceAll("diced into .? (cubes|cube)", "").
                replaceAll("segments", "").
                replaceAll("by inches high", "").
                replaceAll("of \" diameter", "").
                replaceAll("sheet of", "").
                replaceAll("strips of", "").
                replaceAll("stick of", "");
    }

}
