package xyz.devvydont.smprpg.items.blueprints.sets.elderflame

import net.kyori.adventure.key.Key
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.base.CustomAttributeItem
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.items.interfaces.IEquippableAssetOverride
import xyz.devvydont.smprpg.items.interfaces.IRepairable
import xyz.devvydont.smprpg.items.interfaces.ISkillRequirement
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.skills.SkillType

abstract class ElderflameArmorSet(itemService: ItemService, type: CustomItemType) :
    CustomAttributeItem(itemService, type), IBreakableEquipment, IEquippableAssetOverride, IRepairable, ISkillRequirement {

    override val repairMaterial: MutableCollection<ItemStack> get() = mutableListOf(itemService.getCustomItem(CustomItemType.DRACONIC_CRYSTAL))
    override val skillRequirements: MutableMap<SkillType, Int> = mutableMapOf(Pair(SkillType.COMBAT, 45))

    override fun getActiveSlot(): EquipmentSlotGroup { return EquipmentSlotGroup.ARMOR }

    override fun getPowerRating(): Int { return 50 }

    override fun getAssetId(): Key { return key }

    companion object {
        const val ARMOR_DURABILITY_UNIT : Int = 192
        private val key = Key.key("elderflame")
    }
}
