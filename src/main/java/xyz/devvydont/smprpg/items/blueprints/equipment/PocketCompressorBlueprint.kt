package xyz.devvydont.smprpg.items.blueprints.equipment

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.ItemContainerContents
import io.papermc.paper.datacomponent.item.TooltipDisplay
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint
import xyz.devvydont.smprpg.items.interfaces.ICosmeticDurability
import xyz.devvydont.smprpg.items.interfaces.IHeaderDescribable
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class PocketCompressorBlueprint(itemService: ItemService, type: CustomItemType) : CustomItemBlueprint(itemService, type), IModelOverridden,
    ICosmeticDurability, IHeaderDescribable {

    override val itemClassification: ItemClassification get() = ItemClassification.EQUIPMENT
    val compressorSlots: Int get() = when (type) {
        CustomItemType.SMALL_POCKET_COMPRESSOR    -> 1
        CustomItemType.MEDIUM_POCKET_COMPRESSOR   -> 3
        CustomItemType.LARGE_POCKET_COMPRESSOR    -> 5
        CustomItemType.GIGANTIC_POCKET_COMPRESSOR -> 7
        CustomItemType.COLOSSAL_POCKET_COMPRESSOR -> 9
        else -> 1
    }

    override fun getDisplayKey(): Key {
        // TODO: Give a custom model to these. It is hardcoded to use droppers right now.
        return IModelOverridden.ofMaterial(Material.DROPPER)
        //return IModelOverridden.ofItemTypeInDirectory(type, "equipment")
    }

    override fun updateItemData(itemStack: ItemStack) {
        if (itemStack.getDataOrDefault(DataComponentTypes.CONTAINER, null) == null) initialize(itemStack)
        super.updateItemData(itemStack)
    }

    fun initialize(itemStack: ItemStack) {
        // Create an empty container for compressibles.
        val contents = mutableListOf<ItemStack>()
        for (i in 0..<compressorSlots) contents.add(ItemStack.of(DUMMY_MATERIAL))
        itemStack.setData(DataComponentTypes.CONTAINER, ItemContainerContents.containerContents(contents))
        itemStack.setData(DataComponentTypes.TOOLTIP_DISPLAY,
            TooltipDisplay.tooltipDisplay().addHiddenComponents(DataComponentTypes.CONTAINER).build());
    }

    override fun getHeader(itemStack: ItemStack): List<Component> {
        return listOf(
            ComponentUtils.create("Automatically compresses items that you pick up!"),
            ComponentUtils.EMPTY,
            ComponentUtils.merge(
                ComponentUtils.create("This tier has "),
                ComponentUtils.create(compressorSlots, NamedTextColor.AQUA),
                ComponentUtils.create(" configuration slot${if (type == CustomItemType.SMALL_POCKET_COMPRESSOR) "" else "s"}")
            )
        )
    }

    companion object {
        val COMPRESSION_ITEM_KEY = NamespacedKey(SMPRPG.plugin, "compressible_items")
        val DUMMY_MATERIAL = Material.BARRIER
    }

}