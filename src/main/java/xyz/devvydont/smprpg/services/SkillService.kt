package xyz.devvydont.smprpg.services

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.entity.player.LeveledPlayer
import xyz.devvydont.smprpg.skills.SkillGlobals
import xyz.devvydont.smprpg.skills.SkillInstance
import xyz.devvydont.smprpg.skills.SkillType
import xyz.devvydont.smprpg.skills.listeners.*
import xyz.devvydont.smprpg.skills.rewards.AttributeReward
import kotlin.math.max

class SkillService : IService, Listener {

    init {
        CombatExperienceListener()
        MiningExperienceListener()
        ForagingExperienceListener()
        FarmingExperienceListener()
        MagicExperienceListener()
        FishingExperienceListener()

        ExperienceGainNotifier()
    }

    @Throws(RuntimeException::class)
    override fun setup() {
        val plugin = plugin
        var sum = 0
        for (i in 1..SkillGlobals.maxSkillLevel) {
            val xp = SkillGlobals.getExperienceForLevel(i)
            sum += xp
            plugin.logger.fine("Skill Requirement for Level $i: $xp ($sum)")
        }
    }

    override fun cleanup() {
    }

    fun getNewSkillInstance(player: Player, type: SkillType): SkillInstance {
        return SkillInstance(player, type)
    }

    /**
     * Completely removes any attribute bonuses from skills from a player.
     */
    private fun removeAttributeSkillRewards(player: LeveledPlayer) {
        // Remove every skill reward that is an attribute skill.
        for (skill in player.skills)
            for (level in 0..SkillGlobals.maxSkillLevel)
                for (reward in skill.getRewards(level))
                    if (reward is AttributeReward)
                        reward.remove(player.player, skill.type)
    }

    /**
     * Re-applies any attribute rewards from skills to a player
     */
    private fun applyAttributeSkillRewards(player: LeveledPlayer) {
        for (skill in player.skills)
            for (level in 0..skill.level)
                for (reward in skill.getRewards(level))
                    if (skill.level >= level && reward is AttributeReward)
                        reward.apply(player.player, skill.type)
    }

    /**
     * Removes and re-applies any skill attributes to a player.
     */
    fun syncSkillAttributes(player: LeveledPlayer) {

        val hpPercent = max(.01, player.healthPercentage)
        removeAttributeSkillRewards(player)
        applyAttributeSkillRewards(player)

        if (!player.player.isDead)
            player.setHealthPercentage(hpPercent)
    }

    /**
     * When a player joins the server, we need to re-sync their skill attributes just in case we changed them.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    @Suppress("unused")
    private fun onPlayerJoin(event: PlayerJoinEvent) {
        val entityService = SMPRPG.getService(EntityService::class.java)
        syncSkillAttributes(entityService.getPlayerInstance(event.getPlayer()))
    }
}
