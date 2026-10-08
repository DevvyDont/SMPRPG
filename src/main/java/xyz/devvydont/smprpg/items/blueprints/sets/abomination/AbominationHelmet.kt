package xyz.devvydont.smprpg.items.blueprints.sets.abomination

import io.papermc.paper.datacomponent.DataComponentTypes
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.entity.slayer.shambling.ShamblingAbominationParent
import xyz.devvydont.smprpg.events.CustomEntityDamageByEntityEvent
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.services.ItemService.Companion.blueprint
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.persistence.KeyStore

class AbominationHelmet(itemService: ItemService, type: CustomItemType) : AbominationArmorSet(itemService, type),
    IBreakableEquipment {

    override val itemClassification: ItemClassification get() = ItemClassification.HELMET

    override fun getAttributeModifiers(item: ItemStack): MutableCollection<AttributeEntry?> {
        val attributes: MutableList<AttributeEntry?> = ArrayList()
        attributes.add(AdditiveAttributeEntry(AttributeWrapper.DEFENSE, 90.0))
        attributes.add(AdditiveAttributeEntry(AttributeWrapper.INTELLIGENCE, 10.0))
        attributes.add(AdditiveAttributeEntry(AttributeWrapper.REGENERATION, 25.0))
        val strMult = getKillStrengthMultBoost(item)
        if (strMult > 0) {
            attributes.add(ScalarAttributeEntry(AttributeWrapper.STRENGTH, strMult))
        }
        return attributes
    }

    override fun getActiveSlot(): EquipmentSlotGroup {
        return EquipmentSlotGroup.HEAD
    }

    override fun getMaxDurability(): Int {
        return armorDurabilityUnit * 5
    }

    @EventHandler
    fun onValidEntityKill(event: EntityDeathEvent) {
        val entity = event.getEntity()
        val killer = entity.killer
        if (killer != null) {
            if (entity.persistentDataContainer.getOrDefault(
                    KeyStore.SLAYER_SPAWN_TYPE!!,
                    PersistentDataType.STRING!!,
                    ""
                ) == ShamblingAbominationParent.SPAWN_MOB_FLAG
            ) {
                val item = killer.inventory.getItem(EquipmentSlot.HEAD)
                if (blueprint(item) !is AbominationHelmet) return
                val nextMilestone = getNextKillMilestone(item)
                val kills: Int = item.persistentDataContainer.getOrDefault(
                    killstoreKey,
                    PersistentDataType.INTEGER,
                    0
                )!!
                item.editPersistentDataContainer { pdc: PersistentDataContainer? ->
                    pdc!!.set(
                        killstoreKey,
                        PersistentDataType.INTEGER,
                        kills + 1
                    )
                }
                if ((kills + 1) == nextMilestone) {
                    killer.sendMessage(
                        ComponentUtils.merge(
                            ComponentUtils.create("Your "),
                            item.getData(DataComponentTypes.ITEM_NAME),
                            ComponentUtils.create(" has leveled up to "),
                            ComponentUtils.create(
                                "+" + (getKillStrengthMultBoost(item) * 100.0).toInt() + "% Strength",
                                NamedTextColor.RED
                            ),
                            ComponentUtils.create("!")
                        )
                    )
                }
                blueprint(item).updateItemData(item)
            }
        }
    }

    @EventHandler
    fun onReceiveDamageFromBoss(event: CustomEntityDamageByEntityEvent) {
        // Is the attacker a shambling abomination?
        val isBoss = (SMPRPG.getService(EntityService::class.java)
            .getEntityInstance(event.dealer) is ShamblingAbominationParent)
        if (isBoss) {
            // Is the attacker holding the halberd?
            if (event.damaged !is LivingEntity) return
            val damaged = event.damaged

            if (damaged.equipment == null) return

            if (!isItemOfType(damaged.equipment!!.helmet)) return

            // Is this a direct event?
            if (event.isIndirect) return

            // Reduce damage
            event.multiplyDamage(BOSS_DAMAGE_REDUCTION)
        }
    }
}
