package xyz.devvydont.smprpg.util.persistence.adapters;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import xyz.devvydont.smprpg.entity.player.settings.ExperienceBarFill;
import xyz.devvydont.smprpg.entity.player.settings.ExperienceBarNumber;
import xyz.devvydont.smprpg.entity.player.settings.HealthDisplayMode;
import xyz.devvydont.smprpg.entity.player.settings.PlayerSettings;
import xyz.devvydont.smprpg.entity.player.settings.StructureWarningMode;

/**
 * Serializes {@link PlayerSettings} to and from a {@link PersistentDataContainer}. Each field is stored under
 * its own key and read back with the current default as a fallback, so adding new settings in the future does
 * not invalidate previously stored data.
 */
public class PlayerSettingsAdapter implements PersistentDataType<PersistentDataContainer, PlayerSettings> {

    private static final String NAMESPACE = "smprpg";

    private static final NamespacedKey DEFENSE_IN_ACTION_BAR = new NamespacedKey(NAMESPACE, "defense_in_action_bar");
    private static final NamespacedKey MANA_IN_ACTION_BAR = new NamespacedKey(NAMESPACE, "mana_in_action_bar");
    private static final NamespacedKey SKILL_EXPERIENCE_IN_ACTION_BAR = new NamespacedKey(NAMESPACE, "skill_experience_in_action_bar");
    private static final NamespacedKey STRUCTURE_WARNING_MODE = new NamespacedKey(NAMESPACE, "structure_warning_mode");
    private static final NamespacedKey HEALTH_DISPLAY_MODE = new NamespacedKey(NAMESPACE, "health_display_mode");
    private static final NamespacedKey EXPERIENCE_BAR_NUMBER = new NamespacedKey(NAMESPACE, "experience_bar_number");
    private static final NamespacedKey EXPERIENCE_BAR_FILL = new NamespacedKey(NAMESPACE, "experience_bar_fill");

    @Override
    public @NotNull Class<PersistentDataContainer> getPrimitiveType() {
        return PersistentDataContainer.class;
    }

    @Override
    public @NotNull Class<PlayerSettings> getComplexType() {
        return PlayerSettings.class;
    }

    @Override
    public @NotNull PersistentDataContainer toPrimitive(@NotNull PlayerSettings complex, @NotNull PersistentDataAdapterContext context) {
        PersistentDataContainer container = context.newPersistentDataContainer();
        container.set(DEFENSE_IN_ACTION_BAR, PersistentDataType.BOOLEAN, complex.isDefenseInActionBar());
        container.set(MANA_IN_ACTION_BAR, PersistentDataType.BOOLEAN, complex.isManaInActionBar());
        container.set(SKILL_EXPERIENCE_IN_ACTION_BAR, PersistentDataType.BOOLEAN, complex.isSkillExperienceInActionBar());
        container.set(STRUCTURE_WARNING_MODE, PersistentDataType.STRING, complex.getStructureWarningMode().name());
        container.set(HEALTH_DISPLAY_MODE, PersistentDataType.STRING, complex.getHealthDisplayMode().name());
        container.set(EXPERIENCE_BAR_NUMBER, PersistentDataType.STRING, complex.getExperienceBarNumber().name());
        container.set(EXPERIENCE_BAR_FILL, PersistentDataType.STRING, complex.getExperienceBarFill().name());
        return container;
    }

    @Override
    public @NotNull PlayerSettings fromPrimitive(@NotNull PersistentDataContainer primitive, @NotNull PersistentDataAdapterContext context) {
        PlayerSettings settings = new PlayerSettings();
        settings.setDefenseInActionBar(primitive.getOrDefault(DEFENSE_IN_ACTION_BAR, PersistentDataType.BOOLEAN, settings.isDefenseInActionBar()));
        settings.setManaInActionBar(primitive.getOrDefault(MANA_IN_ACTION_BAR, PersistentDataType.BOOLEAN, settings.isManaInActionBar()));
        settings.setSkillExperienceInActionBar(primitive.getOrDefault(SKILL_EXPERIENCE_IN_ACTION_BAR, PersistentDataType.BOOLEAN, settings.isSkillExperienceInActionBar()));
        settings.setStructureWarningMode(parseStructureWarningMode(primitive.getOrDefault(STRUCTURE_WARNING_MODE, PersistentDataType.STRING, settings.getStructureWarningMode().name())));
        settings.setHealthDisplayMode(parseHealthDisplayMode(primitive.getOrDefault(HEALTH_DISPLAY_MODE, PersistentDataType.STRING, settings.getHealthDisplayMode().name())));
        settings.setExperienceBarNumber(parseExperienceBarNumber(primitive.getOrDefault(EXPERIENCE_BAR_NUMBER, PersistentDataType.STRING, settings.getExperienceBarNumber().name())));
        settings.setExperienceBarFill(parseExperienceBarFill(primitive.getOrDefault(EXPERIENCE_BAR_FILL, PersistentDataType.STRING, settings.getExperienceBarFill().name())));
        return settings;
    }

    private HealthDisplayMode parseHealthDisplayMode(String stored) {
        try {
            return HealthDisplayMode.valueOf(stored);
        } catch (IllegalArgumentException ignored) {
            return HealthDisplayMode.NORMAL;
        }
    }

    private ExperienceBarNumber parseExperienceBarNumber(String stored) {
        try {
            return ExperienceBarNumber.valueOf(stored);
        } catch (IllegalArgumentException ignored) {
            return ExperienceBarNumber.SKILL_AVERAGE;
        }
    }

    private ExperienceBarFill parseExperienceBarFill(String stored) {
        try {
            return ExperienceBarFill.valueOf(stored);
        } catch (IllegalArgumentException ignored) {
            return ExperienceBarFill.SKILL_AVERAGE;
        }
    }

    private StructureWarningMode parseStructureWarningMode(String stored) {
        try {
            return StructureWarningMode.valueOf(stored);
        } catch (IllegalArgumentException ignored) {
            return StructureWarningMode.ALWAYS;
        }
    }
}
