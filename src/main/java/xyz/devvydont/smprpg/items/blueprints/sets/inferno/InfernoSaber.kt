package xyz.devvydont.smprpg.items.blueprints.sets.inferno

import net.kyori.adventure.key.Key
import org.bukkit.Material
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.ability.Ability
import xyz.devvydont.smprpg.ability.AbilityActivationMethod
import xyz.devvydont.smprpg.ability.AbilityCost
import xyz.devvydont.smprpg.ability.AbilityCost.Companion.of
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry
import xyz.devvydont.smprpg.items.base.CustomAttributeItem
import xyz.devvydont.smprpg.items.interfaces.*
import xyz.devvydont.smprpg.items.interfaces.IAbilityCaster.AbilityEntry
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.skills.SkillType
import xyz.devvydont.smprpg.util.time.TickTime

class InfernoSaber(itemService: ItemService, type: CustomItemType) : CustomAttributeItem(itemService, type),
    IModelOverridden, IBreakableEquipment, IRepairable, ISkillRequirement {

    override val itemClassification: ItemClassification get() = ItemClassification.SWORD
    override val repairMaterial: MutableCollection<ItemStack> get() = mutableListOf(itemService.getCustomItem(CustomItemType.INFERNO_REMNANT))
    override val skillRequirements: MutableMap<SkillType, Int> = mutableMapOf(Pair(SkillType.COMBAT, 35))

    override fun getDisplayKey(): Key {
        return IModelOverridden.ofMaterial(Material.BLAZE_ROD)
    }

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.STRENGTH, 150.0),
            MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, -.6),
            AdditiveAttributeEntry(AttributeWrapper.CRITICAL_DAMAGE, 50.0),
            ScalarAttributeEntry(AttributeWrapper.MOVEMENT_SPEED, .25)
        )
    }

    override fun getPowerRating(): Int {
        return InfernoArmorSet.POWER
    }

    override fun getActiveSlot(): EquipmentSlotGroup {
        return EquipmentSlotGroup.MAINHAND
    }

    override fun getMaxDurability(): Int {
        return 50000
    }
    
}

