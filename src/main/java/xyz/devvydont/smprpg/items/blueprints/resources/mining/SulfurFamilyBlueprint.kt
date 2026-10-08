package xyz.devvydont.smprpg.items.blueprints.resources.mining

import net.kyori.adventure.key.Key
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.blueprints.craftengine.CraftEngineCompressibleBlueprint
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden
import xyz.devvydont.smprpg.items.interfaces.ISellable
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.extensions.calculateCompressedWorth

class SulfurFamilyBlueprint(itemService: ItemService, type: CustomItemType) :
    CraftEngineCompressibleBlueprint(itemService, type), ISellable, IModelOverridden {

    override val itemClassification get() = ItemClassification.MATERIAL

    override fun getWorth(itemStack: ItemStack) = calculateCompressedWorth(itemStack)

    override fun getDisplayKey(): Key = when (customItemType) {
        CustomItemType.SULFUR, CustomItemType.ENCHANTED_SULFUR, CustomItemType.SULFUR_SINGULARITY ->
            IModelOverridden.ofItemTypeInDirectory(CustomItemType.SULFUR, "materials")
        else -> NamespacedKey(SMPRPG.plugin, "sulfur_block")
    }
}
