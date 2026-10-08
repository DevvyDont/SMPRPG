package xyz.devvydont.smprpg.entity.player.settings;

/**
 * Controls what the filled (green) portion of the (purely visual) vanilla experience bar represents. This is
 * independent of the number shown on the bar ({@link ExperienceBarNumber}), so players can mix and match.
 */
public enum ExperienceBarFill {

    /**
     * The bar fills with progress toward the player's next average skill level, creeping up as skill
     * experience is earned.
     */
    SKILL_AVERAGE("Skill Progress", "Progress toward your next average skill level."),

    /**
     * The bar fills with progress within the skill the player most recently gained experience in.
     */
    LAST_SKILL("Last Skill Progress", "Progress in the skill you most recently gained XP in."),

    /**
     * The bar fills with the player's current mana pool percentage.
     */
    MANA("Mana", "Your current mana pool."),

    /**
     * The bar is kept empty.
     */
    HIDDEN("Hidden", "Keep the bar empty."),
    ;

    public final String display;
    public final String description;

    ExperienceBarFill(String display, String description) {
        this.display = display;
        this.description = description;
    }

    /**
     * The next value in the cycle, wrapping back to the first. Used for click-to-toggle in the settings menu.
     * @return The next bar fill.
     */
    public ExperienceBarFill next() {
        ExperienceBarFill[] values = values();
        return values[(ordinal() + 1) % values.length];
    }
}
