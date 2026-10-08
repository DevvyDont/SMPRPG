package xyz.devvydont.smprpg.items.blueprints.craftengine

import net.momirealms.craftengine.bukkit.item.BukkitItemManager
import net.momirealms.craftengine.core.util.Key
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden
import xyz.devvydont.smprpg.items.interfaces.ISellable
import xyz.devvydont.smprpg.services.ItemService

open class CraftEngineBlueprint(itemService: ItemService, type: CustomItemType) : CustomItemBlueprint(itemService, type),
    IModelOverridden, ISellable {

    override val itemClassification: ItemClassification get() = ItemClassification.ITEM

    override fun generate(): ItemStack {
        // Make the starting item.

        var itemStack : ItemStack
        val craftEngineItem = BukkitItemManager.instance().createCustomWrappedItem(Key.of("smprpg:${customItemType.key}"), null)
        if (craftEngineItem != null)
            itemStack = craftEngineItem.bukkitItem
        else
            itemStack = ItemStack.of(customItemType.DisplayMaterial)


        // Apply updates to this item according to our blueprint's spec.
        updateItemData(itemStack)
        return itemStack
    }

    /**
     * Craft Engine items automatically create model entries in smprpg:items, so we just grab that model.
     */
    override fun getDisplayKey() : net.kyori.adventure.key.Key {
        return net.kyori.adventure.key.Key.key(SMPRPG.Companion.plugin, this.customItemType.name.lowercase())
    }

    override fun getWorth(item: ItemStack): Int {
        val individualWorth = item.persistentDataContainer.getOrDefault(ItemService.Companion.SELL_VALUE_KEY, PersistentDataType.INTEGER, 0)
        return individualWorth * item.amount
    }
}