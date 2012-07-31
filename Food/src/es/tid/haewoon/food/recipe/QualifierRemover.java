package es.tid.haewoon.food.recipe;

public class QualifierRemover extends Stemmer {
    public QualifierRemover(Stemmer next) {
        super(next);
    }
    
    @Override
    public String refine(String raw) {
        return raw.replaceAll("fat", "").       // indicate for 35% fat (always)
                replaceAll("chopped", "").
                replaceAll("grated", "").
                replaceAll("trimmings", "").
                replaceAll("obtained", "").
                replaceAll("tempered", "").
                replaceAll("pasteurized", "").
                replaceAll("ground ", "").
                replaceAll("toasted ", "").
                replaceAll("peeled", "").
                replaceAll("shavings", "").
                replaceAll("blanched", "").
                replaceAll("drained canned", "").
                replaceAll("cut into julienne", "").
                replaceAll("medium-sized", "").
                replaceAll("for frying", "").
                replaceAll("thins$", "thin").
                replaceAll("oven-ready", "").
//                replaceAll("peel of", "").
//                replaceAll("zest of", "").
//                replaceAll("(leaf|leaves) of", "").
//                replaceAll("(sprig|sprigs) of", "").
                replaceAll("freshly", "").
                replaceAll("fresh", "");  
    }

}
