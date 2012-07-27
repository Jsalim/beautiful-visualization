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
                replaceAll("blanched", "").
                replaceAll("drained canned", "").
                replaceAll("cut into julienne", "").
                replaceAll("freshly", "").
                replaceAll("fresh", "");  
    }

}
