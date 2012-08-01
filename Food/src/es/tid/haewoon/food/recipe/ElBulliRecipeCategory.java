package es.tid.haewoon.food.recipe;

public enum ElBulliRecipeCategory {
    AVANT_POSTRES("AVANT_POSTRES"), 
    COCKTAILS("COCKTAILS"),
    DESSERTS("DESSERTS"),
    DISHES("DISHES"),
    FOLLIES("FOLLIES"),
    MORPHINGS("MORPHINGS"),
    PETITS_FOURS("PETITS_FOURS"),
    PRE_DESSERTS("PRE_DESSERTS"),   // PRE-DESSERTS
    SNACKS("SNACKS"),
    TAPAS("TAPAS"), 
    ALL("ALL");                    // NOT IN THE MENU, JUST FOR FILTERING
    
    private final String name; 
    
    ElBulliRecipeCategory(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
