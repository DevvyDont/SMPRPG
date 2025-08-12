package xyz.devvydont.smprpg.reforge.definitions

import com.destroystokyo.paper.ParticleBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.damage.DamageSource
import org.bukkit.damage.DamageType
import org.bukkit.entity.Enemy
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.scheduler.BukkitTask
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.ItemRarity
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry
import xyz.devvydont.smprpg.reforge.ReforgeBase
import xyz.devvydont.smprpg.reforge.ReforgeType
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.time.TickTime
import java.util.function.Consumer

class CrystallizedReforge(type: ReforgeType) : ReforgeBase(type), Listener {
    override val description: List<Component>
        get() = listOf<Component>(
            ComponentUtils.create("Provides a").append(ComponentUtils.create(" SIGNIFICANT", NamedTextColor.GOLD))
                .append(ComponentUtils.create(" boost")),
            ComponentUtils.create("in combat capabilities"),
            ComponentUtils.EMPTY,
            ComponentUtils.create("Shatter Bonus", NamedTextColor.BLUE),
            ComponentUtils.merge(
                ComponentUtils.create("When defeating an "),
                ComponentUtils.create("enemy", NamedTextColor.RED),
                ComponentUtils.create(", damage is")
            ),
            ComponentUtils.merge(
                ComponentUtils.create("reflected", NamedTextColor.AQUA),
                ComponentUtils.create(" to any "),
                ComponentUtils.create("enemies", NamedTextColor.RED)
            ),
            ComponentUtils.merge(
                ComponentUtils.create("within a "),
                ComponentUtils.create(
                    "$ABILITY_RADIUS block",
                    NamedTextColor.GREEN
                ),
                ComponentUtils.create(" radius")
            )
        )

    override fun getAttributeModifiersWithRarity(rarity: ItemRarity): List<AttributeEntry> {
        return listOf<AttributeEntry>(
            ScalarAttributeEntry(AttributeWrapper.STRENGTH, SpicyReforge.Companion.getDamageBonus(rarity) + .2),
            AttributeEntry.additive(
                AttributeWrapper.CRITICAL_DAMAGE,
                SpicyReforge.Companion.getCriticalBonus(rarity) * 1.5
            ),
            AttributeEntry.additive(AttributeWrapper.CRITICAL_CHANCE, (20 + rarity.ordinal * 10).toDouble()),
            MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, .05 * rarity.ordinal)
        )
    }

    override fun getPowerRating(): Int {
        return 5
    }

    @EventHandler
    @Suppress("unused")
    fun onEntityDeath(event: EntityDeathEvent) {
        // Only listen to hostile creatures.
        if (event.getEntity() !is Enemy)
            return

        // Only listen if there was a player killer that is holding something with this reforge.
        val killer = event.getEntity().killer
        if (killer == null)
            return

        val mainHandItem = killer.inventory.itemInMainHand
        val mainHandReforge = SMPRPG.getService(ItemService::class.java).getReforge(mainHandItem)
        if (mainHandReforge == null) return

        if (mainHandReforge.type != this.type) return

        // Get nearby enemies.
        val nearby = event.getEntity().location.getNearbyLivingEntities(ABILITY_RADIUS.toDouble(), 1.0)
        val targets = ArrayList<LivingEntity>()
        for (entity in nearby) if (entity is Enemy) targets.add(entity)
        targets.remove(event.getEntity())

        if (targets.isEmpty()) return

        // Work out how much damage we are dealing. All we do is distribute the dead entity's max HP amongst nearby enemies.
        var damage = 1000.0
        val hp = event.getEntity().getAttribute(Attribute.MAX_HEALTH)
        if (hp != null) damage = hp.value
        damage /= targets.size.toDouble()

        // Damage the targets on the next tick if they are not dead.
        val finalDamage = damage
        Bukkit.getScheduler().runTaskLater(plugin, Consumer { task: BukkitTask ->
            for (target in targets) {
                if (target.isDead) continue
                target.damage(
                    finalDamage,
                    DamageSource.builder(DamageType.MAGIC).withDirectEntity(killer).withCausingEntity(killer).build()
                )
                target.world.playSound(target.location, Sound.ENTITY_TURTLE_EGG_BREAK, 1f, 2f)
                ParticleBuilder(Particle.END_ROD).location(target.eyeLocation).count(3).offset(.25, .1, .25)
                    .spawn()
            }
        }, TickTime.TICK)
    }

    companion object {
        const val ABILITY_RADIUS: Int = 5
    }
}
