package xyz.devvydont.smprpg.items.blueprints.sets.bedrock

import io.papermc.paper.datacomponent.item.Equippable
import org.bukkit.inventory.EquipmentSlot
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.items.interfaces.IEquippableOverride
import xyz.devvydont.smprpg.services.ItemService

class BedrockHelmet(itemService: ItemService, type: CustomItemType) : BedrockArmorSet(itemService, type),
    IBreakableEquipment, IEquippableOverride {

    override val itemClassification: ItemClassification get() = ItemClassification.HELMET
    override val defense: Int get() = 250

    override fun getEquipmentOverride(): Equippable { return IEquippableOverride.generateDefault(EquipmentSlot.HEAD) }

    override fun getMaxDurability(): Int { return ARMOR_DURABILITY_UNIT * 5 }
}
