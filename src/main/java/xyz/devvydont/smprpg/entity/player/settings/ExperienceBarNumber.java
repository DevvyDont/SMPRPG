package xyz.devvydont.smprpg.entity.player.settings;

/**
 * Controls what value the number on the (purely visual) vanilla experience bar displays. This is independent
 * of what the bar itself fills with ({@link ExperienceBarFill}), so players can mix and match.
 */
public enum ExperienceBarNumber {

    /**
     * The player's average level across all of their skills.
     */
    SKILL_AVERAGE("Average Skill Level", "Your average level across all skills."),

    /**
     * The player's overall power rating (their internal computed level from skills and gear).
     */
    POWER_RATING("Power Rating", "Your overall power level."),

    /**
     * The level of the skill the player most recently gained experience in.
     */
    LAST_SKILL("Last Skill Gained", "The level of the skill you most recently gained XP in."),

    /**
     * The player's current mana, as a flat amount.
     */
    MANA("Mana", "Your current mana amount."),

    /**
     * The player's current mana as a percentage of their maximum (0-100).
     */
    MANA_PERCENT("Mana %", "Your current mana as a percentage."),

    /**
     * No number is shown on the bar at all.
     */
    HIDDEN("Hidden", "Show no number on the bar."),
    ;

    public final String display;
    public final String description;

    ExperienceBarNumber(String display, String description) {
        this.display = display;
        this.description = description;
    }

    /**
     * The next value in the cycle, wrapping back to the first. Used for click-to-toggle in the settings menu.
     * @return The next number display.
     */
    public ExperienceBarNumber next() {
        ExperienceBarNumber[] values = values();
        return values[(ordinal() + 1) % values.length];
    }
}
