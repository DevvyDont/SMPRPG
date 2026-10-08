package xyz.devvydont.smprpg.items.blueprints.sets.neptune

import net.kyori.adventure.key.Key
import org.bukkit.Material
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.ability.Passive
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry
import xyz.devvydont.smprpg.items.blueprints.craftengine.CraftEngineAttributeItem
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemSword
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemSword.Companion.getSwordDamage
import xyz.devvydont.smprpg.items.interfaces.*
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.skills.SkillType

class NeptuneTrident(itemService: ItemService, type: CustomItemType) : CraftEngineAttributeItem(itemService, type),
    IBreakableEquipment, IPassiveProvider, IRepairable, IModelOverridden, ISkillRequirement {
    override val itemClassification: ItemClassification get() = ItemClassification.TRIDENT
    override val repairMaterial: MutableCollection<ItemStack> get() = mutableListOf(itemService.getCustomItem(CustomItemType.PLUTOS_ARTIFACT))
    override val skillRequirements: MutableMap<SkillType, Int> = mutableMapOf(Pair(SkillType.COMBAT, 15))

    override fun wantNerfedSellPrice(): Boolean {
        return false
    }

    override fun getAttributeModifiers(item: ItemStack): Collection<AttributeEntry> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.STRENGTH, getSwordDamage(Material.NETHERITE_SWORD) - 10),
            MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, ItemSword.SWORD_ATTACK_SPEED_DEBUFF),
            AdditiveAttributeEntry(AttributeWrapper.CRITICAL_DAMAGE, 20.0)
        )
    }

    override fun getPowerRating(): Int { return NeptuneArmorSet.POWER_LEVEL }
    override fun getActiveSlot(): EquipmentSlotGroup { return EquipmentSlotGroup.MAINHAND }
    override fun getMaxDurability(): Int { return NeptuneArmorSet.DURABILITY }

    /**
     * Retrieve the passives this item has.
     *
     * @return A set of passives.
     */
    override fun getPassives(): MutableSet<Passive?> {
        return mutableSetOf(Passive.ABYSSAL_ANNIHILATION)
    }

    override fun getDisplayKey(): Key { return IModelOverridden.ofItemType(type) }
}
