package xyz.devvydont.smprpg.items.blueprints.equipment

import net.kyori.adventure.key.Key
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden
import xyz.devvydont.smprpg.items.interfaces.ISellable
import xyz.devvydont.smprpg.reforge.ReforgeType
import xyz.devvydont.smprpg.services.ItemService

class IridescentLens(itemService: ItemService?, type: CustomItemType?) : ReforgeStone(itemService, type),
    ISellable, IModelOverridden {

    override fun getWorth(item: ItemStack): Int { return 75000 * item.amount }

    override fun getReforgeType(): ReforgeType { return ReforgeType.PRISMATIC }

    override fun getExperienceCost(): Int { return 50 }

    override fun getDisplayKey(): Key { return IModelOverridden.ofItemTypeInDirectory(customItemType, "reforge_stones") }
}
