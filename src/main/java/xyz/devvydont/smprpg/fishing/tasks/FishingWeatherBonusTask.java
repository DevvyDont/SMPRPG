package xyz.devvydont.smprpg.fishing.tasks;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.attribute.CustomAttributeInstance;
import xyz.devvydont.smprpg.fishing.FishingConstants;
import xyz.devvydont.smprpg.items.interfaces.IFishingRod;
import xyz.devvydont.smprpg.services.AttributeService;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;
import xyz.devvydont.smprpg.util.persistence.KeyStore;

/**
 * Keeps every online player's environmental fishing speed bonuses in sync with the weather they are standing in.
 * <p>
 * Rather than hardcoding the bonus into the fishing wait time calculation, we express the weather effects as
 * {@link AttributeModifier}s on the {@link AttributeWrapper#FISHING_SPEED} attribute. This means the bonus is picked up
 * automatically wherever the attribute is read (e.g. the fishing wait time logic) and, crucially, is visible to the
 * player in their stat overview menu.
 * <ul>
 *     <li>Standing in rain grants a flat additive fishing speed bonus.</li>
 *     <li>Standing in a thunderstorm grants an additional multiplicative bonus that stacks with the rain bonus.</li>
 * </ul>
 * Because weather and a player's location both change over time, this task polls once per second and applies/removes the
 * modifiers as players move in and out of the rain.
 */
public class FishingWeatherBonusTask extends BukkitRunnable {

    /**
     * Volume used for the personal weather notification sounds. Kept low so it reads as a subtle stat cue rather than an
     * environmental effect.
     */
    private static final float NOTIFICATION_VOLUME = 0.6f;

    /**
     * Sends a player a personal (client-side) themed notification with an accompanying sound.
     * @param player The player to notify.
     * @param message The pre-styled chat message to send.
     * @param sound The sound to play for the player only.
     * @param pitch The pitch of the sound. Higher pitches read as "gained", lower pitches read as "lost".
     */
    private static void announce(Player player, Component message, Sound sound, float pitch) {

        // Skip the message if they don't have a fishing rod anywhere in their inventory.
        var hasRod = false;
        if (ItemService.blueprint(player.getInventory().getItemInMainHand()) instanceof IFishingRod)
            hasRod = true;
        if (ItemService.blueprint(player.getInventory().getItemInOffHand()) instanceof IFishingRod)
            hasRod = true;
        if (!hasRod)
            return;
        
        player.sendMessage(message);
        player.playSound(player.getLocation(), sound, NOTIFICATION_VOLUME, pitch);
    }

    /**
     * Builds the additive modifier granted while standing in the rain.
     * @return The rain fishing speed modifier.
     */
    private static AttributeModifier rainModifier() {
        return new AttributeModifier(
                KeyStore.FISHING_RAIN_BONUS,
                FishingConstants.RAIN_FISHING_SPEED_BONUS,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlotGroup.ANY
        );
    }

    /**
     * Builds the multiplicative modifier granted while standing in a thunderstorm.
     * @return The thunderstorm fishing speed modifier.
     */
    private static AttributeModifier thunderstormModifier() {
        return new AttributeModifier(
                KeyStore.FISHING_THUNDERSTORM_BONUS,
                FishingConstants.THUNDERSTORM_FISHING_SPEED_MULTIPLIER,
                AttributeModifier.Operation.MULTIPLY_SCALAR_1,
                EquipmentSlotGroup.ANY
        );
    }

    /**
     * Checks whether an attribute instance currently has a modifier registered under the given key.
     * @param instance The attribute instance to query.
     * @param key The modifier key to look for.
     * @return True if a modifier with the key is present.
     */
    private static boolean hasModifier(CustomAttributeInstance instance, NamespacedKey key) {
        for (var modifier : instance.getModifiers())
            if (modifier.getKey().equals(key))
                return true;
        return false;
    }

    /**
     * Removes any weather fishing speed bonuses from a player. Used so the bonuses do not linger in persistent data when
     * the feature is shut down.
     * @param player The player to strip weather bonuses from.
     */
    public static void removeBonuses(Player player) {
        var attribute = AttributeService.getInstance().getOrCreateAttribute(player, AttributeWrapper.FISHING_SPEED);
        boolean changed = false;
        if (hasModifier(attribute, KeyStore.FISHING_RAIN_BONUS)) {
            attribute.removeModifier(KeyStore.FISHING_RAIN_BONUS);
            changed = true;
        }
        if (hasModifier(attribute, KeyStore.FISHING_THUNDERSTORM_BONUS)) {
            attribute.removeModifier(KeyStore.FISHING_THUNDERSTORM_BONUS);
            changed = true;
        }
        if (changed)
            attribute.save(player, AttributeWrapper.FISHING_SPEED);
    }

    /**
     * Notifies a player that the rain has begun boosting their fishing speed.
     * @param player The player to notify.
     */
    private void announceRainGained(Player player) {
        announce(player, ComponentUtils.alert(
                ComponentUtils.create("Rain", NamedTextColor.AQUA),
                ComponentUtils.merge(
                        ComponentUtils.create("The falling rain quickens your bobber. ", NamedTextColor.GRAY),
                        ComponentUtils.create("(+" + (int) FishingConstants.RAIN_FISHING_SPEED_BONUS + " Fishing Speed)", NamedTextColor.GREEN)
                )
        ), Sound.ENTITY_DOLPHIN_SPLASH, 1.2f);
    }

    /**
     * Notifies a player that the rain has stopped and their fishing speed bonus has been removed.
     * @param player The player to notify.
     */
    private void announceRainLost(Player player) {
        announce(player, ComponentUtils.alert(
                ComponentUtils.create("Rain", NamedTextColor.AQUA),
                ComponentUtils.create("The rain lets up, and your bobber slows.", NamedTextColor.GRAY)
        ), Sound.ENTITY_DOLPHIN_SPLASH, 0.8f);
    }

    /**
     * Notifies a player that the thunderstorm is now multiplying their fishing speed.
     * @param player The player to notify.
     */
    private void announceThunderstormGained(Player player) {
        announce(player, ComponentUtils.alert(
                ComponentUtils.create("Storm", NamedTextColor.YELLOW),
                ComponentUtils.merge(
                        ComponentUtils.create("The thunderstorm electrifies your line! ", NamedTextColor.GRAY),
                        ComponentUtils.create("(x" + (1 + FishingConstants.THUNDERSTORM_FISHING_SPEED_MULTIPLIER) + " Fishing Speed)", NamedTextColor.GREEN)
                )
        ), Sound.ITEM_TRIDENT_THUNDER, 1.0f);
    }

    /**
     * Notifies a player that the thunderstorm has passed and their fishing speed multiplier has been removed.
     * @param player The player to notify.
     */
    private void announceThunderstormLost(Player player) {
        announce(player, ComponentUtils.alert(
                ComponentUtils.create("Storm", NamedTextColor.YELLOW),
                ComponentUtils.create("The thunderstorm passes, and the charge leaves your line.", NamedTextColor.GRAY)
        ), Sound.ITEM_TRIDENT_THUNDER, 0.7f);
    }

    /**
     * Synchronizes a single player's weather fishing speed bonuses with their current surroundings.
     * @param player The player to update.
     */
    private void updatePlayer(Player player) {
        // isInRain() mirrors vanilla's "is it raining on this entity" check: the world must be storming, the player must
        // be exposed to the sky, and they must be in a biome that actually rains (not arid/snowy biomes).
        // The bonuses stack: a thunderstorm is also rain, so it grants the multiplicative bonus on top of the flat one.
        boolean inRain = player.isInRain();
        boolean inThunderstorm = inRain && player.getWorld().isThundering();

        var attribute = AttributeService.getInstance().getOrCreateAttribute(player, AttributeWrapper.FISHING_SPEED);
        boolean hasRain = hasModifier(attribute, KeyStore.FISHING_RAIN_BONUS);
        boolean hasThunderstorm = hasModifier(attribute, KeyStore.FISHING_THUNDERSTORM_BONUS);

        // Only touch (and re-save) the attribute when the desired state differs from the current state. This avoids
        // thrashing the player's persistent data and firing redundant attribute update events every tick.
        boolean changed = false;

        if (inRain && !hasRain) {
            attribute.addModifier(rainModifier());
            announceRainGained(player);
            changed = true;
        } else if (!inRain && hasRain) {
            attribute.removeModifier(KeyStore.FISHING_RAIN_BONUS);
            announceRainLost(player);
            changed = true;
        }

        if (inThunderstorm && !hasThunderstorm) {
            attribute.addModifier(thunderstormModifier());
            announceThunderstormGained(player);
            changed = true;
        } else if (!inThunderstorm && hasThunderstorm) {
            attribute.removeModifier(KeyStore.FISHING_THUNDERSTORM_BONUS);
            announceThunderstormLost(player);
            changed = true;
        }

        if (changed)
            attribute.save(player, AttributeWrapper.FISHING_SPEED);
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers())
            updatePlayer(player);
    }
}
