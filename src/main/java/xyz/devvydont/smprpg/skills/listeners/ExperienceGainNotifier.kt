package xyz.devvydont.smprpg.skills.listeners

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.scheduler.BukkitRunnable
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.events.skills.SkillExperienceGainEvent
import xyz.devvydont.smprpg.events.skills.SkillExperiencePostGainEvent
import xyz.devvydont.smprpg.events.skills.SkillLevelUpEvent
import xyz.devvydont.smprpg.services.ActionBarService
import xyz.devvydont.smprpg.skills.SkillInstance
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.MinecraftStringUtils
import xyz.devvydont.smprpg.util.formatting.Symbols

class ExperienceGainNotifier : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    fun messagePlayerSkillLevelUp(player: Player, skill: SkillInstance, newLevel: Int) {
        val type = skill.type
        val oldLevel = (newLevel - 1).toString()
        val newLevelStr = newLevel.toString()
        player.sendMessage(ComponentUtils.EMPTY)
        player.sendMessage(
            ComponentUtils.alert(
                ComponentUtils.create("SKILL LEVEL UP!!!", NamedTextColor.GOLD).decoration(TextDecoration.BOLD, true),
                NamedTextColor.AQUA
            )
        )
        player.sendMessage(ComponentUtils.create("--------------------------"))
        player.sendMessage(
            ComponentUtils.create("   " + type.displayName + " ", NamedTextColor.AQUA)
                .append(ComponentUtils.upgrade(oldLevel, newLevelStr, NamedTextColor.AQUA))
        )
        player.sendMessage(ComponentUtils.EMPTY)
        player.sendMessage(ComponentUtils.create("   Rewards:", NamedTextColor.GREEN))
        for (reward in skill.getRewards(newLevel)) player.sendMessage(
            ComponentUtils.create("    " + Symbols.POINT + " ").append(reward.generateRewardComponent(player))
        )
        player.sendMessage(ComponentUtils.create("--------------------------"))
        player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, .25f, 2f)
    }

    @EventHandler
    @Suppress("unused")
    fun onLevelUpSkill(event: SkillLevelUpEvent) {
        // Award the skills

        for (reward in event.skill.type.rewards.getRewardsForLevels(
            event.oldLevel + 1,
            event.newLevel
        )) reward.apply(event.player, event.skillType)

        // Tell the player all their level ups
        var delay = 0
        for (level in event.oldLevel + 1..event.newLevel) {
            val iter = level
            object : BukkitRunnable() {
                override fun run() {
                    messagePlayerSkillLevelUp(event.player, event.skill, iter)
                }
            }.runTaskLater(plugin, delay.toLong())
            delay += 10
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @Suppress("unused")
    fun onGainExperience(event: SkillExperiencePostGainEvent) {
        if (event.source == SkillExperienceGainEvent.ExperienceSource.XP) return

        val component: Component =
            ComponentUtils.create(event.skillType.displayName + " " + event.skill.level, NamedTextColor.AQUA)
                .append(ComponentUtils.create(" | "))
                .append(
                    ComponentUtils.create(
                        MinecraftStringUtils.formatNumber(event.skill.experienceProgress.toLong()),
                        NamedTextColor.GREEN
                    )
                )
                .append(
                    ComponentUtils.create(
                        "/" + MinecraftStringUtils.formatNumber(event.skill.nextExperienceThreshold.toLong()),
                        NamedTextColor.DARK_GRAY
                    )
                )
                .append(
                    ComponentUtils.create(
                        " (+" + MinecraftStringUtils.formatNumber(
                            event.skill.getCombo().toLong()
                        ) + ")", NamedTextColor.GOLD
                    )
                )

        // Send the player an action bar of their experience progress
        SMPRPG.getService(ActionBarService::class.java)
            .addActionBarComponent(event.player, ActionBarService.ActionBarSource.SKILL, component, 5)
        event.player.playSound(event.player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, .15f, 2f)
    }
}
