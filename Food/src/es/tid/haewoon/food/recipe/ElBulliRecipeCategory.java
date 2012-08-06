package es.tid.haewoon.food.recipe;

public enum ElBulliRecipeCategory {
    AVANT_POSTRES("AVANT_POSTRES", 1), 
    COCKTAILS("COCKTAILS", 2),
    DESSERTS("DESSERTS", 3),
    DISHES("DISHES", 4),
    FOLLIES("FOLLIES", 5),
    MORPHINGS("MORPHINGS", 6),
    PETITS_FOURS("PETITS_FOURS", 7),
    PRE_DESSERTS("PRE_DESSERTS", 8),   // PRE-DESSERTS
    SNACKS("SNACKS", 9),
    TAPAS("TAPAS", 10), 
    ALL("ALL", 0);                    // NOT IN THE MENU, JUST FOR FILTERING
    
    private final String name; 
    private final int id;
    
    ElBulliRecipeCategory(String name, int id) {
        this.name = name;
        this.id = id;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    public int getID() {
        return id;
    }
}
