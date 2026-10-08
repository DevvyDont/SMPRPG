package xyz.devvydont.smprpg.skills

import net.momirealms.craftengine.core.entity.player.Player
import net.momirealms.craftengine.core.plugin.compatibility.LevelerProvider
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.events.skills.SkillExperienceGainEvent
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.skills.utils.SkillExperienceReward

class CraftEngineLevelerProvider(val pluginId: String) : LevelerProvider {
    override fun plugin(): String {
        return pluginId
    }

    override fun addExp(player: Player, target: String, amount: Double) {
        val player = player.platformPlayer() as org.bukkit.entity.Player
        val leveledPlayer = SMPRPG.getService(EntityService::class.java).getPlayerInstance(player)
        val xpReward = SkillExperienceReward()
        for (skill in SkillType.entries) {
            if (skill.name.lowercase() == target) {
                xpReward.add(skill, amount.toInt())
                break
            }
        }
        xpReward.apply(leveledPlayer, SkillExperienceGainEvent.ExperienceSource.CRAFTENGINE_FUNCTION)
    }

    override fun getLevel(player: Player, target: String): Int {
        val player = player.platformPlayer() as org.bukkit.entity.Player
        val leveledPlayer = SMPRPG.getService(EntityService::class.java).getPlayerInstance(player)
        for (skill in leveledPlayer.skills) {
            if (skill.type.name.lowercase() == target) {
                return skill.level
            }
        }
        return -1
    }
}