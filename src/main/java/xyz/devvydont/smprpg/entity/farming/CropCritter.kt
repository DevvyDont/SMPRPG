package xyz.devvydont.smprpg.entity.farming

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.scoreboard.Team
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.entity.CustomEntityType
import xyz.devvydont.smprpg.entity.base.CustomEntityInstance
import xyz.devvydont.smprpg.services.CropCritterService
import xyz.devvydont.smprpg.skills.SkillType
import xyz.devvydont.smprpg.skills.utils.SkillExperienceReward
import xyz.devvydont.smprpg.skills.utils.SkillExperienceReward.Companion.of
import java.util.*
import javax.inject.Singleton

@Singleton
open class CropCritter<T : LivingEntity?>
/**
 * An unsafe constructor to use to allow dynamic creation of custom entities.
 * This is specifically used as a casting hack for the CustomEntityType enum in order to dynamically create
 * entities.
 *
 * @param entity     The entity that should map the T type parameter.
 * @param entityType The entity type.
 */
    (entity: T, entityType: CustomEntityType) : CustomEntityInstance<T>(entity, entityType), Listener {

    /**
     * Get the entity ID responsible for spawning this sea creature. Can be null.
     * @return The entity ID.
     */
    /**
     * Set the entity ID responsible for spawning this sea creature. Can pass in null to clear.
     * @param spawnedBy Who spawned the entity.
     */
    var spawnedBy: UUID? = null

    override fun getNameColor(): TextColor {
        return NAME_COLOR
    }

    override fun getSkillExperienceMultiplier(): Double {
        return 1.0
    }

    override fun generateSkillExperienceReward(): SkillExperienceReward {
        return of(SkillType.FARMING, (level * 20 * skillExperienceMultiplier).toInt())
    }

    /**
     * When a Crop Critter dies, unregister it from the service so that players can spawn more crop critters
     */
    @EventHandler(ignoreCancelled = true)
    fun onEntityRemoval(event: EntityRemoveFromWorldEvent) {
        if (event.getEntity() === _entity) {
            val critterService = SMPRPG.getService(CropCritterService::class.java)
            val spawner = critterService.activeCritters.getOrDefault(this.spawnedBy, null)
            if (spawner != null) {
                critterService.activeCritters.remove(this.spawnedBy)
            }
        }
    }

    companion object {
        val NAME_COLOR: TextColor = TextColor.color(0x1E8C00)

        private const val _team = "smprpg:crop_critters"

        val team: Team
            get() {
                var team =
                    Bukkit.getScoreboardManager().mainScoreboard.getTeam(_team)
                if (team == null) team =
                    Bukkit.getScoreboardManager().mainScoreboard.registerNewTeam(_team)
                team.color(NamedTextColor.YELLOW)
                return team
            }
    }
}
