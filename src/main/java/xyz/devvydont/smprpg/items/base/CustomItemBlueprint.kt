package xyz.devvydont.smprpg.items.base

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemRarity
import xyz.devvydont.smprpg.items.blueprints.augment.Recombobulator
import xyz.devvydont.smprpg.services.ItemService

abstract class CustomItemBlueprint(
    itemService: ItemService,
    /**
     * Since this item is custom, return the CustomItem enum that this item is linked to.
     */
    open val type: CustomItemType) : SMPItemBlueprint(itemService) {

    val customItemType : CustomItemType get() = type

    override val customModelDataIdentifier: String get() = "smprpg:" + customItemType.key

    override fun getRarity(item: ItemStack): ItemRarity {
        var rarityMod = 0
        if (item.persistentDataContainer.getOrDefault(Recombobulator.RECOMBOBULATOR_KEY, PersistentDataType.BOOLEAN, false))
            rarityMod += 1
        return ItemRarity.values()[defaultRarity.ordinal + rarityMod]
    }

    override val defaultRarity: ItemRarity get() = this.customItemType.DefaultRarity

    override fun getItemName(item: ItemStack?): String { return this.customItemType.ItemName }

    override fun updateItemData(meta: ItemMeta) {
        // Apply the key to the item so the plugin knows this item is custom

        meta.persistentDataContainer.set(itemService.itemTypeKey, PersistentDataType.STRING, this.customItemType.key)
        super.updateItemData(meta)
    }

    override fun generate(): ItemStack {
        // Make the starting item.

        val itemStack = ItemStack.of(customItemType.DisplayMaterial)

        // Apply updates to this item according to our blueprint's spec.
        updateItemData(itemStack)
        return itemStack
    }

    /**
     * Determines if item given is an item belonging to this blueprint
     */
    override fun isItemOfType(itemStack: ItemStack): Boolean {
        val itemKey = itemService.getItemKey(itemStack)
        if (itemKey == null) return false

        return itemKey == this.customItemType.key
    }

    /**
     * All items that descend from this class are custom.
     */
    override val isCustom: Boolean get() = true


    override fun wantFakeEnchantGlow(): Boolean { return this.customItemType.WantGlow }
}
