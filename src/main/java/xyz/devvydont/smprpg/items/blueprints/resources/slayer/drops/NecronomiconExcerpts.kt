package xyz.devvydont.smprpg.items.blueprints.resources.slayer.drops

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.ItemContainerContents
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.gui.items.MenuTomeModification
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint
import xyz.devvydont.smprpg.items.interfaces.IHeaderDescribable
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden
import xyz.devvydont.smprpg.items.interfaces.ISellable
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import java.util.function.Consumer

class NecronomiconExcerpts(itemService: ItemService, type: CustomItemType) : CustomItemBlueprint(itemService, type),
    ISellable, IHeaderDescribable, IModelOverridden {
    /**
     * Determine what type of item this is.
     */
    override val itemClassification: ItemClassification get() = ItemClassification.AUGMENT_STONE

    /**
     * Given this item stack, how much should it be able to sell for?
     * Keep in mind that the size of the stack needs to considered as well!
     *
     * @param item The item that can be sold.
     * @return The worth of the item.
     */
    override fun getWorth(item: ItemStack): Int {
        return 500000 * item.getAmount()
    }

    override fun updateItemData(itemStack: ItemStack) {
        super.updateItemData(itemStack)
        itemStack.editMeta(Consumer { meta: ItemMeta? -> meta!!.setMaxStackSize(4) })
    }

    override fun getHeader(itemStack: ItemStack): List<Component> {
        return listOf(
            ComponentUtils.create("Combine with a Tome in an anvil up to 3"),
            ComponentUtils.merge(
                ComponentUtils.create("times to add extra "),
                ComponentUtils.create("spell slots", NamedTextColor.LIGHT_PURPLE),
                ComponentUtils.create(".")
            )
        )
    }

    override fun getDisplayKey(): Key? {
        return IModelOverridden.ofItemTypeInDirectory(type, "augment_stones")
    }

    companion object {
        val TOME_SPELL_COUNT_MODIFIER = NamespacedKey(SMPRPG.plugin, "necronomicon_excerpts_modifier")
        val MAX_EXCERPTS = 3

        fun addExcerptsToTome(tome: ItemStack) {
            val numExcerpts = tome.persistentDataContainer.getOrDefault(TOME_SPELL_COUNT_MODIFIER, PersistentDataType.INTEGER, 0)
            tome.editPersistentDataContainer { pdc -> pdc.set(TOME_SPELL_COUNT_MODIFIER, PersistentDataType.INTEGER, numExcerpts + 1) }
            val containerComp = tome.getData(DataComponentTypes.CONTAINER)
            val newContents = mutableListOf<ItemStack>()
            for (item in containerComp!!.contents())
                newContents.add(item)
            newContents.add(ItemStack(MenuTomeModification.DUMMY_MATERIAL))
            tome.setData(DataComponentTypes.CONTAINER, ItemContainerContents.containerContents(newContents))
        }
    }
}
