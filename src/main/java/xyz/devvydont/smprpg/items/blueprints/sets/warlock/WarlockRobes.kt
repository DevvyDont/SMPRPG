package xyz.devvydont.smprpg.items.blueprints.sets.warlock

import io.papermc.paper.datacomponent.DataComponentTypes
import net.kyori.adventure.key.Key
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
import xyz.devvydont.smprpg.entity.slayer.illager.IllagerWarlockParent
import xyz.devvydont.smprpg.events.CustomEntityDamageByEntityEvent
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.services.ItemService.Companion.blueprint
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.persistence.KeyStore

class WarlockRobes(itemService: ItemService, type: CustomItemType) : WarlockArmorSet(itemService, type) {
    override fun getAttributeModifiers(item: ItemStack): MutableCollection<AttributeEntry?> {
        val attributes: MutableList<AttributeEntry?> = ArrayList()
        attributes.add(AdditiveAttributeEntry(AttributeWrapper.DEFENSE, 320.0))
        attributes.add(AdditiveAttributeEntry(AttributeWrapper.INTELLIGENCE, 200.0))
        val arcaneBoost = getKillArcaneRatingBoost(item)
        if (arcaneBoost > 0.0) {
            attributes.add(AdditiveAttributeEntry(AttributeWrapper.ARCANE_RATING, arcaneBoost))
        }
        return attributes
    }

    override fun getActiveSlot(): EquipmentSlotGroup {
        return EquipmentSlotGroup.CHEST
    }

    override fun getMaxDurability(): Int {
        return 1500
    }

    override val itemClassification: ItemClassification get() = ItemClassification.CHESTPLATE

    @EventHandler
    fun onValidEntityKill(event: EntityDeathEvent) {
        val entity = event.entity
        val killer = entity.killer
        if (killer != null) {
            if (entity.persistentDataContainer.getOrDefault(
                    KeyStore.SLAYER_SPAWN_TYPE!!,
                    PersistentDataType.STRING!!,
                    ""
                ) == IllagerWarlockParent.SPAWN_MOB_FLAG
            ) {
                val item = killer.inventory.getItem(EquipmentSlot.CHEST)
                if (blueprint(item) !is WarlockRobes) return
                val nextMilestone = getNextKillMilestone(item)
                val kills: Int = item.persistentDataContainer
                    .getOrDefault(
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
                                "+" + (getKillArcaneRatingBoost(item)).toInt() + " Arcane Rating",
                                NamedTextColor.DARK_AQUA
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
        // Is the attacker an illager warlock?
        val isBoss = (SMPRPG.getService(EntityService::class.java).getEntityInstance(event.dealer) is IllagerWarlockParent)
        if (isBoss) {
            // Is the attacker wearing the armor piece?
            val damaged = event.damaged
            if (event.damaged !is LivingEntity) return

            if (damaged.equipment == null) return

            if (!isItemOfType(damaged.equipment!!.boots)) return

            // Is this a direct event?
            if (event.isIndirect) return

            // Reduce damage
            event.multiplyDamage(BOSS_DAMAGE_REDUCTION)
        }
    }

    override fun getDisplayKey(): Key? {
        return IModelOverridden.ofItemType(customItemType)
    }
}
