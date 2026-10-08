package xyz.devvydont.smprpg.items.blueprints.vanilla

import org.bukkit.Material
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry.Companion.additive
import xyz.devvydont.smprpg.items.attribute.AttributeEntry.Companion.multiplicative
import xyz.devvydont.smprpg.items.base.VanillaAttributeItem
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.items.interfaces.IFishingRod
import xyz.devvydont.smprpg.items.interfaces.IFishingRod.FishingFlag
import xyz.devvydont.smprpg.items.interfaces.IRepairable
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.items.ToolStats

class ItemFishingRod(itemService: ItemService, material: Material) : VanillaAttributeItem(itemService, material),
    IBreakableEquipment, IFishingRod, IRepairable {

    override val itemClassification: ItemClassification get() = ItemClassification.ROD
    override val repairMaterial: MutableCollection<ItemStack> get() = mutableListOf(itemService.getCustomItem(Material.OAK_PLANKS), itemService.getCustomItem(Material.BIRCH_PLANKS), itemService.getCustomItem(Material.SPRUCE_PLANKS),
            itemService.getCustomItem(Material.JUNGLE_PLANKS), itemService.getCustomItem(Material.DARK_OAK_PLANKS), itemService.getCustomItem(Material.ACACIA_PLANKS),
            itemService.getCustomItem(Material.CRIMSON_PLANKS), itemService.getCustomItem(Material.WARPED_PLANKS), itemService.getCustomItem(Material.MANGROVE_PLANKS),
            itemService.getCustomItem(Material.CHERRY_PLANKS), itemService.getCustomItem(Material.PALE_OAK_PLANKS), itemService.getCustomItem(Material.BAMBOO_PLANKS))

    override fun getMaxDurability(): Int {
        return ToolStats.WOOD.durability
    }

    override fun getActiveSlot(): EquipmentSlotGroup {
        return EquipmentSlotGroup.HAND
    }

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            additive(AttributeWrapper.STRENGTH, 5.0),
            multiplicative(AttributeWrapper.ATTACK_SPEED, -.5),
            additive(AttributeWrapper.FISHING_RATING, 5.0),
            additive(AttributeWrapper.FISHING_TREASURE_CHANCE, .2),
            additive(AttributeWrapper.FISHING_TREASURE_CHANCE, .2)
        )
    }

    override fun getPowerRating(): Int {
        return ToolStats.WOOD.power
    }

    override fun getFishingFlags(): MutableSet<FishingFlag?> {
        return mutableSetOf(
            FishingFlag.NORMAL
        )
    }

}
