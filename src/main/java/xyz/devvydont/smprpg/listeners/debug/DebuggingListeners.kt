package xyz.devvydont.smprpg.listeners.debug

import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import xyz.devvydont.smprpg.events.skills.SkillExperiencePostGainEvent
import xyz.devvydont.smprpg.skills.SkillGlobals.getCumulativeExperienceForLevel
import xyz.devvydont.smprpg.skills.SkillGlobals.maxSkillLevel
import xyz.devvydont.smprpg.skills.SkillGlobals.totalExperienceCap
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.listeners.ToggleableListener
import java.text.DecimalFormat
import kotlin.math.min

/**
 * Listeners intended to be used for debugging. Very useful to make sure certain game functions
 * are working correctly.
 */
class DebuggingListeners : ToggleableListener() {
    /**
     * A useful chat function that tells you how much experience you earned, the source, how many
     * more of that exact instance you need to level up, and how many you need to max. This is meant
     * to check balancing of skill experience.
     * @param event The [SkillExperiencePostGainEvent] event that provides relevant context.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    @Suppress("unused")
    private fun onSkillExperienceEarn(event: SkillExperiencePostGainEvent) {
        /*
                Extract all the relevant numbers we need.
                We want the following 3 things:
                - EXP to level up.
                - EXP to level up 10 times. (Only if 10 level ups are lower than skill cap!)
                - EXP to max.
                 */

        val expToLevelUp = event.skill.experienceForNextLevel
        val intermediateLevelTarget = min(maxSkillLevel, event.skill.level + 10)
        val intermediateLevelTarget2 = min(maxSkillLevel, event.skill.level + 50)
        val expToIntermediateLevelTarget =
            getCumulativeExperienceForLevel(intermediateLevelTarget) - event.skill.experience
        val expToIntermediateLevelTarget2 =
            getCumulativeExperienceForLevel(intermediateLevelTarget2) - event.skill.experience
        val expToMax = totalExperienceCap - event.skill.experience

        // Now calculate how many times we need to earn this experience AGAIN to hit the thresholds.
        val oneLevelRepetitions = expToLevelUp / event.experienceEarned
        val intermediateRepetitions = expToIntermediateLevelTarget / event.experienceEarned
        val intermediateRepetitions2 = expToIntermediateLevelTarget2 / event.experienceEarned
        val maxRepetitions = expToMax / event.experienceEarned

        val df = DecimalFormat("#,###,###")

        // Show them the data.
        event.player.sendMessage(
            ComponentUtils.merge(
                ComponentUtils.create("------------------------------\n", NamedTextColor.GRAY),
                ComponentUtils.create("Gained "),
                ComponentUtils.create(df.format(event.experienceEarned.toLong()) + "EXP", NamedTextColor.GREEN),
                ComponentUtils.create(" for "),
                ComponentUtils.create(event.skillType.displayName, NamedTextColor.GOLD),
                ComponentUtils.create(" from "),
                ComponentUtils.create(event.source.name, NamedTextColor.AQUA),
                ComponentUtils.create("!\n"),
                ComponentUtils.create("To level  1x: "),
                ComponentUtils.create(df.format(expToLevelUp.toLong()) + " ", NamedTextColor.YELLOW),
                ComponentUtils.create("~" + df.format(oneLevelRepetitions.toLong()) + " reps\n", NamedTextColor.GRAY),
                ComponentUtils.create("To level 10x: "),
                ComponentUtils.create(df.format(expToIntermediateLevelTarget.toLong()) + " ", NamedTextColor.YELLOW),
                ComponentUtils.create(
                    "~" + df.format(intermediateRepetitions.toLong()) + " reps\n",
                    NamedTextColor.GRAY
                ),
                ComponentUtils.create("To level 50x: "),
                ComponentUtils.create(df.format(expToIntermediateLevelTarget2.toLong()) + " ", NamedTextColor.YELLOW),
                ComponentUtils.create(
                    "~" + df.format(intermediateRepetitions2.toLong()) + " reps\n",
                    NamedTextColor.GRAY
                ),
                ComponentUtils.create("To level MAX: "),
                ComponentUtils.create(df.format(expToMax.toLong()) + " ", NamedTextColor.YELLOW),
                ComponentUtils.create("~" + df.format(maxRepetitions.toLong()) + " reps\n", NamedTextColor.GRAY),
                ComponentUtils.create("------------------------------", NamedTextColor.GRAY)
            )
        )
    }
}
