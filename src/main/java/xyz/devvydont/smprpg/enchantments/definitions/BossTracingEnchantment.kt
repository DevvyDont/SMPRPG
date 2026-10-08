package xyz.devvydont.smprpg.enchantments.definitions

import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import io.papermc.paper.registry.tag.TagKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Color
import org.bukkit.Effect
import org.bukkit.Sound
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemType
import org.bukkit.scheduler.BukkitRunnable
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.enchantments.CustomEnchantment
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity
import xyz.devvydont.smprpg.enchantments.EnchantmentUtil
import xyz.devvydont.smprpg.entity.base.BossInstance
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class BossTracingEnchantment(id: String) : CustomEnchantment(id), Listener {
    override val displayName: Component get() = ComponentUtils.create("Tracing")
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Arrows fired will home onto bosses "),
            ComponentUtils.create(
                getActivationDistance(level).toString() + "m",
                NamedTextColor.GREEN
            ),
            ComponentUtils.create(" away for "),
            ComponentUtils.create(
                getTimeout(level).toString() + "s",
                NamedTextColor.GREEN
            )
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(255, 38, 38)

    override val itemTypeTag: TagKey<ItemType> get()           = ItemTypeTagKeys.ENCHANTABLE_BOW
    override val maxLevel: Int get()                           = 4
    override val weight: Int get()                             = EnchantmentRarity.RARE.weight
    override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.HAND
    override val skillRequirement: Int get()                   = 35

    inner class HomingArrowTask(private val projectile: Entity, private val aggression: Int) : BukkitRunnable() {
        private var target: LivingEntity? = null

        override fun run() {
            // If the arrow doesn't exist anymore or is 10 sec old cancel the task

            if (!projectile.isValid || projectile.isOnGround || projectile.ticksLived > 20 * getTimeout(
                    aggression
                )
            ) {
                this.cancel()
                return
            }

            // If the target died somehow, give up
            if (target != null && !target!!.isValid) {
                this.cancel()
                return
            }

            // If we have a target, adjust direction
            if (target != null) {
                val oldSpeed = projectile.velocity.length()
                projectile.velocity = target!!.location.toVector().subtract(projectile.location.toVector())
                // Here we need to make the speed match the old speed
                var velocityMultiplier = oldSpeed / projectile.velocity.length()
                velocityMultiplier *= getVelocityDecay(aggression) / 100.0
                projectile.velocity = projectile.velocity.multiply(velocityMultiplier)

                projectile.world.playEffect(projectile.location, Effect.ENDER_SIGNAL, 0)
                projectile.world.playSound(projectile.location, Sound.ENTITY_ENDER_EYE_DEATH, .4f, 1f)
                return
            }

            // We need to find a target
            // Loop through nearby entities
            for (entity in projectile.world
                .getNearbyLivingEntities(projectile.location, getActivationDistance(aggression).toDouble())) {
                // If this isn't a boss then skip

                if (SMPRPG.getService(EntityService::class.java).getEntityInstance(entity) !is BossInstance<*>) continue

                // If we don't have line of sight we can't use this entity
                if (!entity.hasLineOfSight(projectile)) continue

                // Valid entity!
                target = entity
                break
            }
        }
    }

    @EventHandler
    fun onArrowFire(event: EntityShootBowEvent) {
        val dealer = event.entity
        if (event.entity is Player) {
            val leveledPlayer = SMPRPG.getService(EntityService::class.java).getPlayerInstance(dealer as Player)
            if (!isEnchantmentActive(event.bow!!, leveledPlayer)) return
        }

        val tracing = EnchantmentUtil.getEnchantLevel(enchantment, event.bow)
        if (tracing <= 0) return

        HomingArrowTask(event.projectile, tracing).runTaskTimer(plugin, 10, 1)
    }

    companion object {
        fun getActivationDistance(level: Int): Int { return (level - 1) * 15 + 5 }
        fun getVelocityDecay(level: Int): Int { return level * 3 + 92 }
        fun getTimeout(level: Int): Int { return level * 5 + 5 }
    }
}
