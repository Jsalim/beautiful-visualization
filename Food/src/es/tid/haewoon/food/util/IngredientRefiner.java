package es.tid.haewoon.food.util;

public class IngredientRefiner {
    static String unitsPattern = ".*(cups|tbsps|tsps|pints)\\s";
    static String unitPattern = ".*(cup|tbsp|oz|tsp|pint|Ib|lb|diameter)\\s";
    public static String refine(String rawIngredient) {
        return rawIngredient.replaceAll("\\(.*\\)", "").
                replaceAll("\\d\\.\\d\\?", ""). // 0.4% fat 
                replaceAll("olive oil 0.?4.*", "olive oil").
                replaceAll(unitsPattern, "").replaceAll(unitPattern, "").
                replaceAll("\\d+\\? fat", "").   // 35%
                replaceAll("\\d/\\d+", "").
                replaceAll("\\d+", "").     // any number(s)
                replaceAll("N.{1,5}cartridge", "").       // N20 Catridge
                replaceAll("soda siphon", "").          // ISI 1 pint soda siphon
                replaceAll("piece of", "").
                replaceAll("^ of", "").
                replaceAll("trimmings", "").
                replaceAll("tempered", "").
                replaceAll("approx.", "").
                replaceAll("½", "").
                replaceAll("¼", "").
                replaceAll("cans of", "").
                replaceAll(".*PVC.*", "").
                replaceAll(".*transparency$", "").
                replaceAll(".*metal frame$", "").
                replaceAll(".*wooden skewers$", "").
                replaceAll(".* silicon mold$", "").
                replaceAll(".*from the previous preparation", "").
                replaceAll("mg\\.$", "").
                replaceAll("diced into .{1,5} cubes", "").
                replaceAll("by inches high", "").
                replaceAll("\\.", "").
                replaceAll("\\sg\\s", "").
                replaceAll("\\?", "").
                replaceAll("\\s+", " ").
                replaceAll("sheets", "sheet").          // plural -> singular libraries do not work well... :'(
                replaceAll("eggs", "egg").
                replaceAll("yolks", "yolk").
                replaceAll("leaves", "leaf").
                replaceAll("tomatoes", "tomato").
                replaceAll("onions", "onion").
                replaceAll("apples", "apple").
                replaceAll("plums", "plum").
                replaceAll("segments", "").
                replaceAll("carrots", "carrot").
                replaceAll("rounds", "").
                replaceAll("brains", "brain").
                replaceAll("yogurts", "yogurt").
                replaceAll("potatoes", "potato").
                replaceAll("pistachios", "pistachio").
                replaceAll("noodles", "noodle").
                replaceAll("mushrooms", "mushroom").
                trim();
    }
}
