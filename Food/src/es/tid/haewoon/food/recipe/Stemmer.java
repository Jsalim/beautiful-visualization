package es.tid.haewoon.food.recipe;

public abstract class Stemmer {
    
    protected Stemmer next;
    
    public Stemmer() {
    }
    
    public Stemmer(Stemmer next) {
        this.next = next;
    }
    
    public abstract String refine(String raw); 
    
    public String stem(String raw) {
        
        String result = refine(raw);
        if (next != null) {
            return next.stem(result);
        } 
        
        return result;      // if the next Stemmer does not exist, the result of the current stemmer is returned.
    }
}
