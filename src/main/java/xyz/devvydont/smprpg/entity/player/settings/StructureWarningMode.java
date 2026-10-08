package xyz.devvydont.smprpg.entity.player.settings;

/**
 * Controls when the "currently in a structure" notice is shown on the action bar.
 */
public enum StructureWarningMode {

    /**
     * Always show the notice whenever the player is inside a leveled structure.
     */
    ALWAYS("Always", "Always show while inside a structure."),

    /**
     * Only show the notice when the structure's level is above the player's, i.e. when the red "WARNING!"
     * prefix would appear.
     */
    UNDERLEVELED("When Underleveled", "Only show when the structure is above your level."),

    /**
     * Never show the notice.
     */
    HIDDEN("Hidden", "Never show the structure notice."),
    ;

    public final String display;
    public final String description;

    StructureWarningMode(String display, String description) {
        this.display = display;
        this.description = description;
    }

    /**
     * The next value in the cycle, wrapping back to the first. Used for click-to-toggle in the settings menu.
     * @return The next mode.
     */
    public StructureWarningMode next() {
        StructureWarningMode[] values = values();
        return values[(ordinal() + 1) % values.length];
    }
}
