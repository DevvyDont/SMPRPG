package xyz.devvydont.smprpg.entity.slayer

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import xyz.devvydont.smprpg.entity.CustomEntityType
import xyz.devvydont.smprpg.entity.base.CustomBossInstance
import xyz.devvydont.smprpg.events.slayer.SlayerBossDeathEvent
import xyz.devvydont.smprpg.skills.SkillType
import xyz.devvydont.smprpg.skills.utils.SkillExperienceReward
import xyz.devvydont.smprpg.skills.utils.SkillExperienceReward.Companion.of
import xyz.devvydont.smprpg.slayer.quest.SlayerQuest
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

open class SlayerBossInstance<T>(T : LivingEntity?, entityType: CustomEntityType?) : CustomBossInstance<LivingEntity?>(T, entityType), Listener {

    var quest : SlayerQuest? = null
    var spawner: Player? = null

    override fun getNameColor(): TextColor? {
        return NamedTextColor.DARK_PURPLE
    }

    override fun getNameComponent(): Component {
        return ComponentUtils.create(getEntityName(), getNameColor(), TextDecoration.BOLD)
    }

    override fun generateSkillExperienceReward(): SkillExperienceReward? {
        val slayerXp = quest?.classification!!.slayerXpReward
        val reward = of(SkillType.COMBAT, (slayerXp / 2.0).toInt())
        reward.add(SkillType.SLAYER, slayerXp)
        return reward
    }

    @EventHandler()
    fun onSlayerBossDeath(event : EntityDeathEvent) {
        if (_entity != event.getEntity())
            return

        _entity.world.playSound(_entity.location, Sound.BLOCK_TRIAL_SPAWNER_SPAWN_MOB, 1f, 0.05f)
        _entity.world.playSound(_entity.location, Sound.BLOCK_TRIAL_SPAWNER_SPAWN_MOB, 1f, 0.2f)
        _entity.world.playSound(_entity.location, Sound.BLOCK_TRIAL_SPAWNER_SPAWN_MOB, 1f, 0.4f)
        _entity.world.playSound(_entity.location, Sound.ENTITY_WITHER_SPAWN, 1f, 2.0f)

        val slayerBossDeathEvent = SlayerBossDeathEvent(this)
        slayerBossDeathEvent.callEvent()
    }
}