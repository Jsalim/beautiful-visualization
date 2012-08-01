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
                replaceAll("avocados", "avocado").
                replaceAll("barnacles", "barnacle").
                replaceAll("batons", "baton").
                replaceAll("beans", "bean").
                replaceAll("beets", "beet").
                replaceAll("berries", "berry").
                replaceAll("bones", "bone").
                replaceAll("brains", "brain").
                replaceAll("capers", "caper").
                replaceAll("caps", "cap").
                replaceAll("caramels", "caramel").
                replaceAll("carrots", "carrot").
                replaceAll("ceps", "cep").
                replaceAll("chanterelles", "chanterelle").
                replaceAll("cheeses", "cheese").
                replaceAll("cherries", "cheery").
                replaceAll("chillies", "chilli").
                replaceAll("clams", "clam").
                replaceAll("cloves", "clove").
                replaceAll("cones", "cone").
                replaceAll("cookies", "cookie").
                replaceAll("cubes", "cube").
                replaceAll("cucumbers", "cucumber").
                replaceAll("eggs", "egg").
                replaceAll("egg whites", "egg white").
                replaceAll("filaments", "filament").
                replaceAll("flowers", "flower").
                replaceAll("gonads", "gonad").
                replaceAll("grains", "grain").
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
                replaceAll("nuts", "nut").
                replaceAll("rashers", "rasher").
                replaceAll("olives", "olive").
                replaceAll("onions", "onion").
                replaceAll("oranges", "orange").
                replaceAll("oysters", "oyster").
                replaceAll("peanuts", "peanut").
                replaceAll("pears", "pear").
                replaceAll("peas", "pea").
                replaceAll("peppercorns", "peppercorn").
                replaceAll("petals", "petal").
                replaceAll("pistachios", "pistachio").
                replaceAll("plums", "plum").
                replaceAll("pods", "pod").
                replaceAll("pois", "poi").
                replaceAll("potatoes", "potato").
                replaceAll("raspberries", "raspberry").
                replaceAll("redcurrants", "redcurrant").
                replaceAll("rinds", "rind").
                replaceAll("rounds", "").
                replaceAll("scallops", "scallop").
                replaceAll("seeds", "seed").
                replaceAll("sheets", "sheet").         
                replaceAll("shoots", "shoot").
                replaceAll("shells", "shell").
                replaceAll("sherbets", "sherbet").
                replaceAll("shoots", "shoot").
                replaceAll("snails", "snail").
                replaceAll("sprigs", "sprig").
                replaceAll("squids", "squid").
                replaceAll("stamens", "stamen").
                replaceAll("stems of", "stem of").
                replaceAll("sticks", "stick").
                replaceAll("strawberries", "strawberry").
                replaceAll("tails", "tail").
                replaceAll("tentacles", "tentacle").
                replaceAll("tomatoes", "tomato").
                replaceAll("trotters", "trotter").
                replaceAll("trunks", "trunk").
                replaceAll("winglets", "winglet").
                replaceAll("yogurts", "yogurt").
                replaceAll("yolks", "yolk").
                replaceAll("zests", "zest").
                replaceAll("zucchinis", "zucchini");
    }

}
