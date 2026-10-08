package xyz.devvydont.smprpg.entity.player.settings;

/**
 * Controls how a player's health is rendered on their action bar.
 */
public enum HealthDisplayMode {

    /**
     * Show the player's raw current and maximum health.
     */
    NORMAL("Normal", "Your current and max health."),

    /**
     * Show only the player's raw current health, without the maximum.
     */
    HP_ONLY("Health Only", "Your current health, without the max."),

    /**
     * Show the player's effective current and maximum health, i.e. health scaled by their defense to reflect
     * how much raw damage they can actually absorb.
     */
    EFFECTIVE("Effective HP", "Effective current and max health (scaled by defense)."),

    /**
     * Show only the player's effective current health, without the maximum.
     */
    EHP_ONLY("Effective HP Only", "Your effective current health, without the max."),

    /**
     * Show nothing for health at all.
     */
    HIDDEN("Hidden", "Don't show health at all."),
    ;

    public final String display;
    public final String description;

    HealthDisplayMode(String display, String description) {
        this.display = display;
        this.description = description;
    }

    /**
     * The next mode in the cycle, wrapping back to the first. Used for click-to-toggle in the settings menu.
     * @return The next display mode.
     */
    public HealthDisplayMode next() {
        HealthDisplayMode[] values = values();
        return values[(ordinal() + 1) % values.length];
    }
}
