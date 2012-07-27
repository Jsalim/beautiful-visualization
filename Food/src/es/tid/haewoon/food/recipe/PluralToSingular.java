package es.tid.haewoon.food.recipe;

public class PluralToSingular extends Stemmer {
    public PluralToSingular(Stemmer next) {
        super(next);
    }
    
    @Override
    public String refine(String raw) {
        return raw.replaceAll("almonds", "almond").
                replaceAll("anises", "anise").
                replaceAll("apples", "apple").
                replaceAll("apricots", "apricot").
                replaceAll("artichokes", "artichoke").
                replaceAll("asparaguses", "asparagus").
                replaceAll("batons", "baton").
                replaceAll("beans", "bean").
                replaceAll("bones", "bone").
                replaceAll("brains", "brain").
                replaceAll("caps", "cap").
                replaceAll("carrots", "carrot").
                replaceAll("ceps", "cep").
                replaceAll("cherries", "cheery").
                replaceAll("chillies", "chilli").
                replaceAll("clams", "clam").
                replaceAll("cloves", "clove").
                replaceAll("cookies", "cookie").
                replaceAll("cubes", "cube").
                replaceAll("cucumbers", "cucumber").
                replaceAll("eggs", "egg").
                replaceAll("egg whites", "egg white").
                replaceAll("filaments", "filament").
                replaceAll("gonads", "gonad").
                replaceAll("hazelnuts", "hazelnut").
                replaceAll("heads", "head").
                replaceAll("hearts", "heart").
                replaceAll("involtinis", "involtini").
                replaceAll("knucklebones", "knucklebone").
                replaceAll("leaves", "leaf").
                replaceAll("leeks", "leek").
                replaceAll("lemons", "lemon").
                replaceAll("lychees", "lychee").
                replaceAll("marrowbones", "marrowbone").
                replaceAll("mushrooms", "mushroom").
                replaceAll("mussels", "mussel").
                replaceAll("noodles", "noodle").
                replaceAll("rashers", "rasher").
                replaceAll("olives", "olive").
                replaceAll("onions", "onion").
                replaceAll("oranges", "orange").
                replaceAll("oysters", "oyster").
                replaceAll("peanuts", "peanut").
                replaceAll("peppercorns", "peppercorn").
                replaceAll("petals", "petal").
                replaceAll("pistachios", "pistachio").
                replaceAll("plums", "plum").
                replaceAll("pods", "pod").
                replaceAll("pois", "poi").
                replaceAll("potatoes", "potato").
                replaceAll("redcurrants", "redcurrant").
                replaceAll("rounds", "").
                replaceAll("scallops", "scallop").
                replaceAll("seeds", "seed").
                replaceAll("sheets", "sheet").          // plural -> singular libraries do not work well... :'(
                replaceAll("shells", "shell").
                replaceAll("squids", "squid").
                replaceAll("stamens", "stamen").
                replaceAll("sticks", "stick").
                replaceAll("strawberries", "strawberry").
                replaceAll("tails", "tail").
                replaceAll("tomatoes", "tomato").
                replaceAll("trotters", "trotter").
                replaceAll("winglets", "winglet").
                replaceAll("yogurts", "yogurt").
                replaceAll("yolks", "yolk").
                replaceAll("zucchinis", "zucchini");
    }

}
