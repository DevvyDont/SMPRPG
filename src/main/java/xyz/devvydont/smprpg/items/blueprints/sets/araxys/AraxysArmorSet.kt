package xyz.devvydont.smprpg.items.blueprints.sets.araxys

import org.bukkit.Color
import org.bukkit.inventory.meta.trim.TrimMaterial
import org.bukkit.inventory.meta.trim.TrimPattern
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.base.CustomAttributeItem
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.items.interfaces.IDyeable
import xyz.devvydont.smprpg.items.interfaces.ITrimmable
import xyz.devvydont.smprpg.services.ItemService

abstract class AraxysArmorSet(itemService: ItemService, type: CustomItemType) : CustomAttributeItem(itemService, type),
    IBreakableEquipment, IDyeable, ITrimmable {
    override fun getPowerRating(): Int { return POWER }

    override fun getColor(): Color { return Color.fromRGB(0x474f52) }

    override fun getTrimMaterial(): TrimMaterial? { return TrimMaterial.NETHERITE }

    override fun getTrimPattern(): TrimPattern? { return TrimPattern.DUNE }

    companion object {
        const val POWER: Int = 55
        const val ARMOR_DURABILITY_UNIT: Int = 192
    }
}
