package xyz.devvydont.smprpg.entity.player.settings;

import org.bukkit.persistence.PersistentDataHolder;
import xyz.devvydont.smprpg.util.persistence.KeyStore;
import xyz.devvydont.smprpg.util.persistence.PDCAdapters;

/**
 * Holds a player's personal preferences for how various HUD elements behave. Persisted to the player's
 * {@link org.bukkit.persistence.PersistentDataContainer} via {@link PDCAdapters#PLAYER_SETTINGS}.
 *
 * <p>An instance is cached on the player's {@link xyz.devvydont.smprpg.entity.player.LeveledPlayer} so it can
 * be read cheaply (including from the asynchronous action bar task). Mutating settings should always be followed
 * by a {@link #save(PersistentDataHolder)} so the change survives a relog.
 */
public class PlayerSettings {

    private boolean defenseInActionBar = true;
    private boolean manaInActionBar = true;
    private boolean skillExperienceInActionBar = true;
    private StructureWarningMode structureWarningMode = StructureWarningMode.ALWAYS;
    private HealthDisplayMode healthDisplayMode = HealthDisplayMode.NORMAL;
    private ExperienceBarNumber experienceBarNumber = ExperienceBarNumber.SKILL_AVERAGE;
    private ExperienceBarFill experienceBarFill = ExperienceBarFill.SKILL_AVERAGE;

    public boolean isDefenseInActionBar() {
        return defenseInActionBar;
    }

    public void setDefenseInActionBar(boolean defenseInActionBar) {
        this.defenseInActionBar = defenseInActionBar;
    }

    public boolean isManaInActionBar() {
        return manaInActionBar;
    }

    public void setManaInActionBar(boolean manaInActionBar) {
        this.manaInActionBar = manaInActionBar;
    }

    public boolean isSkillExperienceInActionBar() {
        return skillExperienceInActionBar;
    }

    public void setSkillExperienceInActionBar(boolean skillExperienceInActionBar) {
        this.skillExperienceInActionBar = skillExperienceInActionBar;
    }

    public StructureWarningMode getStructureWarningMode() {
        return structureWarningMode;
    }

    public void setStructureWarningMode(StructureWarningMode structureWarningMode) {
        this.structureWarningMode = structureWarningMode;
    }

    public HealthDisplayMode getHealthDisplayMode() {
        return healthDisplayMode;
    }

    public void setHealthDisplayMode(HealthDisplayMode healthDisplayMode) {
        this.healthDisplayMode = healthDisplayMode;
    }

    public ExperienceBarNumber getExperienceBarNumber() {
        return experienceBarNumber;
    }

    public void setExperienceBarNumber(ExperienceBarNumber experienceBarNumber) {
        this.experienceBarNumber = experienceBarNumber;
    }

    public ExperienceBarFill getExperienceBarFill() {
        return experienceBarFill;
    }

    public void setExperienceBarFill(ExperienceBarFill experienceBarFill) {
        this.experienceBarFill = experienceBarFill;
    }

    /**
     * Persists this settings configuration to a PDC holder (typically the player).
     * @param target The holder to save to.
     */
    public void save(PersistentDataHolder target) {
        target.getPersistentDataContainer().set(KeyStore.PLAYER_SETTINGS, PDCAdapters.PLAYER_SETTINGS, this);
    }
}
