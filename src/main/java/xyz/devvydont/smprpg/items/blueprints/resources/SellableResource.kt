package xyz.devvydont.smprpg.items.blueprints.resources

import io.papermc.paper.datacomponent.DataComponentTypes
import net.kyori.adventure.key.Key
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden
import xyz.devvydont.smprpg.items.interfaces.ISellable
import xyz.devvydont.smprpg.services.ItemService

class SellableResource(itemService: ItemService, type: CustomItemType) : CustomItemBlueprint(itemService, type),
    ISellable, IModelOverridden {
    override val itemClassification: ItemClassification get() = ItemClassification.MATERIAL

    override fun getWorth(itemStack: ItemStack): Int { return customItemType.Worth * itemStack.amount }

    override fun getDisplayKey(): Key {
        if (customItemType.ModelDir != null) {
            return IModelOverridden.ofItemTypeInDirectory(customItemType, customItemType.ModelDir)
        }
        return IModelOverridden.ofMaterial(customItemType.DisplayMaterial)
    }
}
