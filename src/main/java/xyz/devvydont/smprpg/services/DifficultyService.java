package xyz.devvydont.smprpg.services;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jetbrains.annotations.NotNull;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.entity.player.ProfileDifficulty;
import xyz.devvydont.smprpg.events.skills.SkillExperienceGainEvent;
import xyz.devvydont.smprpg.gui.player.MenuDifficultyChooser;

public class DifficultyService implements IService, Listener {

    public final static NamespacedKey DIFFICULTY_MODIFIER_KEY = new NamespacedKey(SMPRPG.getInstance(), "difficulty_modifier");


    /**
     * Given a difficulty, determine the incoming damage multiplier.
     * @param difficulty The difficulty a player is on.
     * @return The multiplier of incoming damage they receive.
     */
    public static float getDamageMultiplier(ProfileDifficulty difficulty) {
        return switch (difficulty) {
            case EASY -> .5f;
            case HARD -> 2f;
            default -> 1.0f;
        };
    }

    /**
     * Given a difficulty, determine the luck boost.
     * @param difficulty The difficulty a player is on.
     * @return The luck boost they receive.
     */
    public static int getAdditiveDropRateFor(ProfileDifficulty difficulty) {
        return switch (difficulty) {
            case EASY -> -50;
            case HARD -> 100;
            default -> 0;
        };
    }

    private final NamespacedKey difficultyKey;

    public DifficultyService() {
        this.difficultyKey = new NamespacedKey(SMPRPG.getInstance(), "profile_type");
    }

    @Override
    public void setup() throws RuntimeException {
    }

    @Override
    public void cleanup() {

    }

    /**
     * Queries for the difficulty that this player is currently playing on.
     * @param player The player to check difficulty for.
     * @return The ProfileDifficulty enum that they are currently set to.
     */
    @NotNull
    public ProfileDifficulty getDifficulty(Player player) {
        return player.getPersistentDataContainer().getOrDefault(difficultyKey, ProfileDifficulty.ADAPTER, ProfileDifficulty.NOT_CHOSEN);
    }

    @NotNull
    public ProfileDifficulty getDifficulty(OfflinePlayer player) {
        return player.getPersistentDataContainer().getOrDefault(difficultyKey, ProfileDifficulty.ADAPTER, ProfileDifficulty.NOT_CHOSEN);
    }

    /**
     * Sets the difficulty of a player. Also handles any necessary side effects that a difficulty change would require.
     * @param player The player to set a difficulty for.
     * @param difficulty The difficulty to set.
     */
    public void setDifficulty(Player player, ProfileDifficulty difficulty) {

        // Don't do anything if the difficulty is not changing.
        var oldDifficulty = getDifficulty(player);
        if (oldDifficulty.equals(difficulty))
            return;

        // Store the difficulty on the player.
        player.getPersistentDataContainer().set(difficultyKey, ProfileDifficulty.ADAPTER, difficulty);
        var playerWrapper = SMPRPG.getService(EntityService.class).getPlayerInstance(player);

        // Set the state of the player necessary for this difficulty.
        // For now, we just have to make sure their stats are sanity checked, as everything else is dynamically handled.
        applyDifficultyModifiers(player, difficulty);
        SMPRPG.getService(SkillService.class).syncSkillAttributes(playerWrapper);
        playerWrapper.setConfiguration(playerWrapper.getDefaultConfiguration());
    }

    /**
     * Removes all difficulty related attribute modifiers, and adds new ones based on the difficulty they are on.
     * @param player The player to tweak attributes of.
     * @param difficulty The difficulty they want modifiers for.
     */
    public void applyDifficultyModifiers(Player player, ProfileDifficulty difficulty) {

        // As of now, the only global difficulty modifier is luck. First remove it.
        var luck = AttributeService.getInstance().getOrCreateAttribute(player, AttributeWrapper.LUCK);
        luck.removeModifier(DIFFICULTY_MODIFIER_KEY);

        // If we don't have a multiplier to give, no reason to add a modifier.
        var boost = getAdditiveDropRateFor(difficulty);
        if (boost == 0) {
            luck.save(player, AttributeWrapper.LUCK);
            return;
        }

        // Apply a luck modifier based on the difficulty.
        luck.addModifier(new AttributeModifier(DIFFICULTY_MODIFIER_KEY, boost, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ANY));
        luck.save(player, AttributeWrapper.LUCK);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void __onPlayerJoin(PlayerJoinEvent event) {

        // When a player joins, we need to make sure they have a difficulty selected so they can play.
        var difficulty = getDifficulty(event.getPlayer());
        if (!difficulty.equals(ProfileDifficulty.NOT_CHOSEN)) {
            applyDifficultyModifiers(event.getPlayer(), difficulty);
            return;
        }
        
        // They haven't chosen! Open the interface.
        var gui = new MenuDifficultyChooser(event.getPlayer());
        gui.openMenu();
        gui.lock();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void __onPlayerEarnMinecraftExperience(PlayerPickupExperienceEvent event) {
        if (getDifficulty(event.getPlayer()).equals(ProfileDifficulty.HARD))
            event.getExperienceOrb().setExperience((int) (event.getExperienceOrb().getExperience() * 1.5));
    }
}
