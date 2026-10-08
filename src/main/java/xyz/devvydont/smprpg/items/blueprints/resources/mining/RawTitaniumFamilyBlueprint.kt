package xyz.devvydont.smprpg.items.blueprints.resources.mining

import net.kyori.adventure.key.Key
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden
import xyz.devvydont.smprpg.items.interfaces.ISellable
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.extensions.calculateCompressedWorth

class RawTitaniumFamilyBlueprint(itemService: ItemService, type: CustomItemType) :
    CustomItemBlueprint(itemService, type), ISellable, IModelOverridden {

    override val itemClassification get() = ItemClassification.MATERIAL

    override fun getWorth(itemStack: ItemStack) = calculateCompressedWorth(itemStack)

    override fun getDisplayKey(): Key? = when (customItemType) {
        CustomItemType.RAW_TITANIUM, CustomItemType.ENCHANTED_RAW_TITANIUM ->
            IModelOverridden.ofItemTypeInDirectory(CustomItemType.RAW_TITANIUM, "materials")
        CustomItemType.RAW_TITANIUM_BLOCK ->
            IModelOverridden.ofItemTypeInDirectory(CustomItemType.RAW_TITANIUM_BLOCK, "blocks")
        else -> null
    }
}
