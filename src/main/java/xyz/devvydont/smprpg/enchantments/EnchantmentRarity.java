package xyz.devvydont.smprpg.enchantments;

/*
 * Wrapper enum class for enchantments to use. Mainly used as a preset for weight calculations if desired
 */
public enum EnchantmentRarity {

    COMMON(6),
    UNCOMMON(5),
    RARE(4),

    CURSE(3),     // Bad enchants, kinda rare kinda not usually disappear after certain levels anyway
    ARTIFICE(2),  // Gambit enchantments, have a really good upside, but with a downside attached
    BLESSING(1),  // Rarest enchants to get
    ARTIFACT(0);  // Unobtainable

    private int weight;

    EnchantmentRarity(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

}
