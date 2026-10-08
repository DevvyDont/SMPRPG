package xyz.devvydont.smprpg.items.blueprints.sets.iron

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.blueprints.craftengine.CraftEngineAttributeItem
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemSword
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.items.interfaces.IKnife
import xyz.devvydont.smprpg.items.interfaces.IRepairable
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.items.ToolStats

class IronKnife(itemService: ItemService, type: CustomItemType) : CraftEngineAttributeItem(itemService, type), IBreakableEquipment,
    IKnife, IRepairable {

    override val itemClassification: ItemClassification
        get() = ItemClassification.KNIFE

    override fun getAttributeModifiers(item: ItemStack): MutableCollection<AttributeEntry> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.STRENGTH, ItemSword.getSwordDamage(Material.IRON_SWORD) / 2),
            AdditiveAttributeEntry(AttributeWrapper.ATTACK_SPEED, -0.3)
        )
    }

    override fun getMaxDurability(): Int {
        return ToolStats.IRON.durability
    }

    override val repairMaterial: MutableCollection<ItemStack>
        get() = mutableListOf(itemService.getCustomItem(Material.IRON_INGOT))
}