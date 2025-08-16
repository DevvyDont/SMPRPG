package xyz.devvydont.smprpg.reforge.definitions

import com.destroystokyo.paper.ParticleBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.EntityEquipment
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.entity.fishing.SeaCreature
import xyz.devvydont.smprpg.items.ItemRarity
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.reforge.ReforgeBase
import xyz.devvydont.smprpg.reforge.ReforgeType
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils


class SirenicReforge(type: ReforgeType) : ReforgeBase(type), Listener {
    override fun getAttributeModifiersWithRarity(rarity: ItemRarity): List<AttributeEntry> {
        return listOf<AttributeEntry>(
            AttributeEntry.additive(AttributeWrapper.FISHING_RATING, 30.0),
            AttributeEntry.additive(
                AttributeWrapper.FISHING_CREATURE_CHANCE,
                AlluringReforge.Companion.getChance(rarity) / 2
            ),
            AttributeEntry.scalar(AttributeWrapper.STRENGTH, .1),
            AttributeEntry.additive(AttributeWrapper.CRITICAL_DAMAGE, 15.0),
            AttributeEntry.additive(AttributeWrapper.FISHING_SPEED, 15.0)
        )
    }

    override val description: List<Component>
        /**
         * An item lore friendly list of components to display as a vague description of the item for what it does
         * @return
         */
        get() = listOf<Component>(
            ComponentUtils.create("Drastically increases chance"),
            ComponentUtils.merge(
                ComponentUtils.create("to fish up "),
                ComponentUtils.create("Sea Creatures", SeaCreature.NAME_COLOR)
            ),
            ComponentUtils.EMPTY,
            ComponentUtils.create("Siren's Aura Bonus:", NamedTextColor.BLUE),
            ComponentUtils.merge(
                ComponentUtils.create("When attacked by a "),
                ComponentUtils.create("Sea Creature", SeaCreature.NAME_COLOR),
                ComponentUtils.create(", you")
            ),
            ComponentUtils.merge(
                ComponentUtils.create("have a "),
                ComponentUtils.create(DODGE_CHANCE.toString() + "%", NamedTextColor.GREEN),
                ComponentUtils.create(" chance to dodge the attack!")
            ),
            ComponentUtils.create("Apply to multiple pieces", NamedTextColor.DARK_GRAY),
            ComponentUtils.create("to boost the effect!", NamedTextColor.DARK_GRAY)
        )

    /**
     * How much should we increase the power rating of an item if this container is present?
     *
     * @return
     */
    override fun getPowerRating(): Int {
        return 3
    }

    /**
     * When an entity receives damage, work out how many stacks of the reforge they have. If the attacker is a sea
     * creature, we have a chance to negate the damage!
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    @Suppress("unused")
    private fun onReceiveDamageFromSeaCreature(event: EntityDamageEvent) {
        val damager = event.damageSource.causingEntity
        if (damager == null) return

        if (damager !is LivingEntity) return

        val damagerWrapper = SMPRPG.getService(EntityService::class.java).getEntityInstance(damager)
        if (damagerWrapper !is SeaCreature<*>)
            return

        val entity = event.entity
        if (entity !is LivingEntity)
            return

        val equipment: EntityEquipment? = entity.equipment
        var sirenicStacks = 0

        if (equipment == null)
            return

        for (armor in equipment.armorContents)
            if (hasReforge(armor))
                sirenicStacks++

        if (hasReforge(equipment.itemInMainHand))
            sirenicStacks++
        if (hasReforge(equipment.itemInOffHand))
            sirenicStacks++

        val chance: Int = sirenicStacks * DODGE_CHANCE
        val roll = Math.random() * 100
        if (roll > chance)
            return

        event.isCancelled = true
        entity.noDamageTicks = 20
        event.getEntity().world.playSound(event.getEntity().location, Sound.ENTITY_BREEZE_DEATH, 1f, 1.5f)
        ParticleBuilder(Particle.FLASH)
            .location(event.getEntity().location.add(0.0, 1.0, 0.0))
            .spawn()
    }

    companion object {
        const val DODGE_CHANCE: Int = 15
    }
}
