package xyz.devvydont.smprpg.items.blueprints.sets.bedrock

import org.bukkit.Color
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.interfaces.IDyeable
import xyz.devvydont.smprpg.services.ItemService

class BedrockBoots(itemService: ItemService, type: CustomItemType) : BedrockArmorSet(itemService, type), IDyeable {

    override val itemClassification: ItemClassification get() = ItemClassification.BOOTS
    override val defense: Int get() = 225

    override fun getColor(): Color { return Color.fromRGB(0x1d1d21) }

    override fun getMaxDurability(): Int { return ARMOR_DURABILITY_UNIT * 4 }
}
