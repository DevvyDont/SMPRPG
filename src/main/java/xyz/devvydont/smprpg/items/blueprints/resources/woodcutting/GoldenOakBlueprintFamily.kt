package xyz.devvydont.smprpg.items.blueprints.resources.woodcutting

import net.kyori.adventure.key.Key
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden
import xyz.devvydont.smprpg.items.interfaces.ISellable
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.extensions.calculateCompressedWorth

class GoldenOakBlueprintFamily(itemService: ItemService, type: CustomItemType) : CustomItemBlueprint(itemService, type),
    ISellable, IModelOverridden {
    override val itemClassification: ItemClassification get() = ItemClassification.MATERIAL

    override fun getWorth(itemStack: ItemStack): Int {
        return this.calculateCompressedWorth(itemStack)
    }

    override fun getDisplayKey(): Key? {
        return NamespacedKey(SMPRPG.plugin, "golden_oak_log")
    }
}
