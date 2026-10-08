package xyz.devvydont.smprpg.skills.listeners

import org.bukkit.damage.DamageType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.entity.interfaces.IDamageTrackable
import xyz.devvydont.smprpg.events.skills.SkillExperienceGainEvent
import xyz.devvydont.smprpg.events.slayer.SlayerQuestEarnExperienceEvent
import xyz.devvydont.smprpg.items.interfaces.IMageBeam
import xyz.devvydont.smprpg.items.interfaces.IMagicWeapon
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.skills.SkillType
import kotlin.math.min
import kotlin.math.roundToInt

class CombatExperienceListener() : Listener {

    init {
        SMPRPG.plugin.server.pluginManager.registerEvents(this, SMPRPG.plugin)
    }

    @EventHandler
    @Suppress("unused")
    private fun onEntityDeath(event: EntityDeathEvent) {
        if (event.isCancelled)
            return

        // This entity did not have a player killer
        if (event.getEntity().killer == null)
            return

        val dead = SMPRPG.getService(EntityService::class.java).getEntityInstance(event.getEntity())
        if (dead !is IDamageTrackable)
            return

        // Calculate how much base experience to drop, if there is none don't do anything
        val experience = dead.generateSkillExperienceReward()
        if (experience.isEmpty)
            return

        // Grant some Magic XP if you killed via magic source (equal to half of the combat xp reward)
        if (SMPRPG.getService(ItemService::class.java).getBlueprint(event.entity.killer!!.inventory.itemInMainHand) is IMagicWeapon ||
            event.damageSource.damageType == DamageType.MAGIC) {
            experience.add(SkillType.MAGIC, (experience.value(SkillType.COMBAT) / 2.0).roundToInt())
        }

        // Loop through everyone who helped kill this entity
        for (entry in dead.getDamageTracker().getPlayerDamageTracker().entries) {
            // Calculate a percentage of how much damage the player did to the entity

            val percentage = min(1.0, entry.value / dead.getMaxHp()).toFloat()
            // Take that percentage and multiply it by 3 for generosity
            val multiplier = min(1.0, (percentage * 3).toDouble())
            experience.multiply(multiplier)

            val player = SMPRPG.getService(EntityService::class.java).getPlayerInstance(entry.key)
            experience.apply(player, SkillExperienceGainEvent.ExperienceSource.KILL)
            val slayerEvent = SlayerQuestEarnExperienceEvent(player, dead, experience.value(SkillType.COMBAT))
            slayerEvent.callEvent()
        }
    }
}
