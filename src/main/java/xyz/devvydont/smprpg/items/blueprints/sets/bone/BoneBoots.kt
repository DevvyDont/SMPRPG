package xyz.devvydont.smprpg.items.blueprints.sets.bone

import org.bukkit.Color
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.interfaces.IDyeable
import xyz.devvydont.smprpg.services.ItemService

class BoneBoots(itemService: ItemService, type: CustomItemType) : BoneArmorSet(itemService, type), IDyeable {

    override val itemClassification: ItemClassification get() = ItemClassification.BOOTS
    override val defense: Int get() = 25

    override fun getColor(): Color { return Color.fromRGB(0x9d9d97) }

    override fun getMaxDurability(): Int { return ARMOR_DURABILITY_UNIT * 4 }
}
