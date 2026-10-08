package xyz.devvydont.smprpg.fishing;

public class FishingConstants {

    /**
     * The base chance of fishing up junk. (Sticks, lily pads, etc.)
     */
    public static final double BASE_JUNK_CHANCE = 10;

    /**
     * Flat fishing speed granted while a player is standing in the rain. Applied as an additive modifier so it is
     * visible in the player's stat overview.
     */
    public static final double RAIN_FISHING_SPEED_BONUS = 50;

    /**
     * Multiplicative fishing speed granted while a player is standing in a thunderstorm. Expressed as a
     * {@link org.bukkit.attribute.AttributeModifier.Operation#MULTIPLY_SCALAR_1} amount, so {@code 0.5} equates to a
     * x1.5 multiplier. This stacks on top of the rain bonus, as a thunderstorm is also rain.
     */
    public static final double THUNDERSTORM_FISHING_SPEED_MULTIPLIER = 0.5;

}
