package xyz.devvydont.smprpg.items.blueprints.vanilla

import org.bukkit.Material
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.base.VanillaAttributeItem
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.items.interfaces.IRepairable
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.items.ToolStats

class ItemBow(itemService: ItemService, material: Material) : VanillaAttributeItem(itemService, material),
    IBreakableEquipment, IRepairable {

    override val itemClassification: ItemClassification get() = ItemClassification.BOW
    override val repairMaterial: MutableCollection<ItemStack> = mutableListOf(itemService.getCustomItem(Material.OAK_PLANKS), itemService.getCustomItem(Material.BIRCH_PLANKS), itemService.getCustomItem(Material.SPRUCE_PLANKS),
        itemService.getCustomItem(Material.JUNGLE_PLANKS), itemService.getCustomItem(Material.DARK_OAK_PLANKS), itemService.getCustomItem(Material.ACACIA_PLANKS),
        itemService.getCustomItem(Material.CRIMSON_PLANKS), itemService.getCustomItem(Material.WARPED_PLANKS), itemService.getCustomItem(Material.MANGROVE_PLANKS),
        itemService.getCustomItem(Material.CHERRY_PLANKS), itemService.getCustomItem(Material.PALE_OAK_PLANKS), itemService.getCustomItem(Material.BAMBOO_PLANKS))

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.STRENGTH, ItemSword.getSwordDamage(Material.WOODEN_SWORD))
        )
    }

    override fun getPowerRating(): Int { return ToolStats.WOOD.power }

    override fun getActiveSlot(): EquipmentSlotGroup { return EquipmentSlotGroup.HAND }

    override fun getMaxDurability(): Int { return ToolStats.WOOD.durability }
}
