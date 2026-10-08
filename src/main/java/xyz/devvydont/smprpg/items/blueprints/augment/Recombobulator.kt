package xyz.devvydont.smprpg.items.blueprints.augment

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint
import xyz.devvydont.smprpg.items.interfaces.ICustomTextured
import xyz.devvydont.smprpg.items.interfaces.IHeaderDescribable
import xyz.devvydont.smprpg.items.interfaces.ISellable
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import java.util.function.Consumer

class Recombobulator(itemService: ItemService, type: CustomItemType) : CustomItemBlueprint(itemService, type),
    ISellable, IHeaderDescribable, ICustomTextured {
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
        return 250_000 * item.amount
    }

    override fun updateItemData(itemStack: ItemStack) {
        super.updateItemData(itemStack)
        itemStack.editMeta(Consumer { meta: ItemMeta? -> meta!!.setMaxStackSize(1) })
    }

    override fun getHeader(itemStack: ItemStack): List<Component> {
        return listOf(
            ComponentUtils.create("Combine with an item in an anvil to"),
            ComponentUtils.merge(
                ComponentUtils.create("increase the "),
                ComponentUtils.create("rarity", NamedTextColor.LIGHT_PURPLE),
                ComponentUtils.create(" once.")
            )
        )
    }

    override fun getTextureUrl(): String {
        return "57ccd36dc8f72adcb1f8c8e61ee82cd96ead140cf2a16a1366be9b5a8e3cc3fc"
    }

    companion object {
        val RECOMBOBULATOR_KEY = NamespacedKey(SMPRPG.plugin, "recombobulated")

        fun addRecombToITem(item: ItemStack) {
            item.editPersistentDataContainer { pdc -> pdc.set(RECOMBOBULATOR_KEY, PersistentDataType.BOOLEAN, true) }
        }
    }
}
