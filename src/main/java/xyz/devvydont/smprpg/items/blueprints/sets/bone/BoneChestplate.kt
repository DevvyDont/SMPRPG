package xyz.devvydont.smprpg.items.blueprints.sets.bone

import org.bukkit.Color
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.interfaces.IDyeable
import xyz.devvydont.smprpg.services.ItemService

class BoneChestplate(itemService: ItemService, type: CustomItemType) : BoneArmorSet(itemService, type), IDyeable {

    override val itemClassification: ItemClassification get() = ItemClassification.CHESTPLATE
    override val defense: Int get() = 60

    override fun getColor(): Color { return Color.fromRGB(0x9d9d97) }

    override fun getMaxDurability(): Int { return ARMOR_DURABILITY_UNIT * 8 }
}
