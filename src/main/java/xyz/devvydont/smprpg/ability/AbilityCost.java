package xyz.devvydont.smprpg.ability;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.LivingEntity;
import xyz.devvydont.smprpg.entity.base.LeveledEntity;
import xyz.devvydont.smprpg.entity.player.LeveledPlayer;
import xyz.devvydont.smprpg.util.formatting.Symbols;

/**
 * A cost associated with an ability.
 * @param resource
 * @param amount
 */
public record AbilityCost(Resource resource, int amount) {

    public enum Resource {
        MANA(Symbols.MANA, NamedTextColor.AQUA),
        HEALTH(Symbols.HEART, NamedTextColor.RED),
        ;

        private final String symbol;
        private final TextColor color;

        Resource(String symbol, TextColor color) {
            this.symbol = symbol;
            this.color = color;
        }

        public String getSymbol() {
            return symbol;
        }

        public TextColor getColor() {
            return color;
        }
    }

    public static AbilityCost of(Resource resource, int amount) {
        return new AbilityCost(resource, amount);
    }

    /**
     * Checks if the entity can afford this.
     * @param entity The entity to check.
     * @return True if they can afford the cost.
     */
    public boolean canUse(LeveledEntity<?> entity) {

        return switch (resource) {

            // Handle mana. Only players can use mana currently.
            case MANA -> {
                if (!(entity instanceof LeveledPlayer player))
                    yield false;
                yield player.getMana() >= amount;
            }

            // Handle health. Simply just need enough HP.
            case HEALTH -> entity.getEntity() instanceof LivingEntity living && living.getHealth() > amount;

        };
    }

    /**
     * Spends the resource. Assumes that they were already checked to have enough.
     * @param entity The entity to modify.
     */
    public void spend(LeveledEntity<?> entity) {

        switch (resource) {
            case MANA -> {
                if (!(entity instanceof LeveledPlayer player))
                    return;
                player.useMana(amount);
            }
            case HEALTH -> {
                if (!(entity.getEntity() instanceof LivingEntity living))
                    return;
                living.setHealth(living.getHealth() - amount);
            }
        }

    }
}
