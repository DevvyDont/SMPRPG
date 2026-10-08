package xyz.devvydont.smprpg.items.blueprints.sets.abomination

import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.entity.slayer.shambling.ShamblingAbominationParent
import xyz.devvydont.smprpg.events.CustomEntityDamageByEntityEvent
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry
import xyz.devvydont.smprpg.items.base.CustomAttributeItem
import xyz.devvydont.smprpg.items.interfaces.*
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.slayer.quest.SlayerType
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.items.AbilityUtil
import xyz.devvydont.smprpg.util.persistence.KeyStore

class AbominableCleaver(itemService: ItemService, type: CustomItemType) : CustomAttributeItem(itemService, type),
    Listener, IHeaderDescribable, IBreakableEquipment, IRepairable, ISlayerProficiencyBoost, IModelOverridden {

    override val itemClassification: ItemClassification get() = ItemClassification.SWORD
    override val repairMaterial: MutableCollection<ItemStack> get() = mutableListOf(itemService.getCustomItem(CustomItemType.PREMIUM_NECROTIC_FLESH))

    override val slayerToBoost: SlayerType get() = SlayerType.SHAMBLING_ABOMINATION
    override val slayerProficiencyBoost: Int get() = 25

    override fun getHeader(itemStack: ItemStack?): MutableList<Component?> {
        val components: MutableList<Component?> = ArrayList()
        components.add(AbilityUtil.getAbilityComponent("Hack Away", passive = true))
        components.add(
            ComponentUtils.create("Attacks deal ")
                .append(ComponentUtils.create(DAMAGE_MULT.toInt().toString() + "x", NamedTextColor.GREEN))
                .append(ComponentUtils.create(" damage against"))
        )
        components.add(
            ComponentUtils.merge(
                ComponentUtils.create("Shambling Abominations", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD),
                ComponentUtils.create(" and associated mobs.")
            )
        )
        components.add(
            ComponentUtils.create("Attacks deal ").append(ComponentUtils.create("10%", NamedTextColor.RED))
                .append(ComponentUtils.create(" damage to any other mobs."))
        )

        return components
    }

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.STRENGTH, 150.0),
            MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, -.6)
        )
    }

    override fun getPowerRating(): Int {
        return 10
    }

    override fun getActiveSlot(): EquipmentSlotGroup {
        return EquipmentSlotGroup.MAINHAND
    }


    override fun getMaxDurability(): Int { return 333 }

    override fun getDisplayKey(): Key {
        return IModelOverridden.ofItemType(type)
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onAttackWithCleaver(event: CustomEntityDamageByEntityEvent) {
        // Did the attacker use the cleaver?
        if (event.dealer !is LivingEntity) return
        val dealer = event.dealer

        if (dealer.equipment == null) return

        if (!isItemOfType(dealer.equipment!!.itemInMainHand)) return

        // Is this a direct event?
        if (event.isIndirect) return

        // Is the attacked mob a shambling abomination?
        val entity = SMPRPG.getService(EntityService::class.java).getEntityInstance(event.damaged)
        val isBoss = (entity.entity.persistentDataContainer
            .getOrDefault(
                KeyStore.SLAYER_SPAWN_TYPE,
                PersistentDataType.STRING,
                ""
            ) == ShamblingAbominationParent.SPAWN_MOB_FLAG)
        if (!isBoss) {
            // If it isn't, we reduce our damage instead of quadruple it.
            // This makes it not viable outside of slayer usage.
            event.multiplyDamage(0.1)
            return
        }

        event.multiplyDamage(DAMAGE_MULT)
    }

    companion object {
        const val DAMAGE_MULT: Double = 4.0
    }
}
