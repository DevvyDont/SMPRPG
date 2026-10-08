package xyz.devvydont.smprpg.entity.slayer

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.event.Listener
import org.bukkit.scoreboard.Team
import xyz.devvydont.smprpg.entity.CustomEntityType
import xyz.devvydont.smprpg.entity.base.CustomEntityInstance
import xyz.devvydont.smprpg.skills.SkillType
import xyz.devvydont.smprpg.skills.utils.SkillExperienceReward
import java.util.UUID

open class SlayerCreature<T : LivingEntity?>
/**
 * An unsafe constructor to use to allow dynamic creation of custom entities.
 * This is specifically used as a casting hack for the CustomEntityType enum in order to dynamically create
 * entities.
 *
 * @param entity     The entity that should map the T type parameter.
 * @param entityType The entity type.
 */
    (entity: T?, entityType: CustomEntityType?) : CustomEntityInstance<T?>(entity, entityType), Listener {
    /**
     * Get the entity ID responsible for spawning this sea creature. Can be null.
     * @return The entity ID.
     */
    /**
     * Set the entity ID responsible for spawning this sea creature. Can pass in null to clear.
     * @param spawnedBy Who spawned the entity.
     */
    var spawnedBy: UUID? = null

    private val _team = "smprpg:slayer_creatures"

    override fun getNameColor(): TextColor {
        return NAME_COLOR
    }

    override fun getSkillExperienceMultiplier(): Double {
        return 1.0
    }

    override fun generateSkillExperienceReward(): SkillExperienceReward {
        return SkillExperienceReward.Companion.of(
            SkillType.COMBAT,
            (getLevel() * 20 * getSkillExperienceMultiplier()).toInt()
        )
    }

    private val team: Team
        get() {
            var team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(_team)
            if (team == null) team = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(_team)
            team.color(NamedTextColor.DARK_RED)
            return team
        }

    companion object {
        val NAME_COLOR: TextColor = TextColor.color(0x9E1919)
    }
}