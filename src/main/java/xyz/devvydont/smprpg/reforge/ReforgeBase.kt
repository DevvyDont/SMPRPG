package xyz.devvydont.smprpg.reforge

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Keyed
import org.bukkit.NamespacedKey
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.attribute.AttributeType
import xyz.devvydont.smprpg.items.ItemRarity
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeModifierType
import xyz.devvydont.smprpg.items.attribute.IAttributeContainer
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.attributes.AttributeUtil
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import java.util.function.Consumer

abstract class ReforgeBase(@JvmField val type: ReforgeType) : Keyed, IAttributeContainer {

    protected val itemService: ItemService
        get() = SMPRPG.getService(ItemService::class.java)

    override fun getAttributeModifierType(): AttributeModifierType {
        return AttributeModifierType.REFORGE
    }

    override fun getKey(): NamespacedKey {
        return NamespacedKey(plugin, this.type.key())
    }

    /**
     * Returns attribute modifiers using default rarity. Might be worth looking into figuring out a better way
     * around this because we have to consider item rarity
     *
     * @return
     */
    override fun getHeldAttributes(): List<AttributeEntry> {
        return getAttributeModifiersWithRarity(ItemRarity.COMMON)
    }

    abstract fun getAttributeModifiersWithRarity(rarity: ItemRarity): List<AttributeEntry>

    fun formatAttributeModifiersWithRarity(rarity: ItemRarity): List<Component> {
        val lines: MutableList<Component> = ArrayList()
        for (entry in this.getAttributeModifiersWithRarity(rarity)) {
            val sign = if (entry.getAmount() > 0) "+" else ""
            // The number is unchanged if this is an additive operation. If it isn't, x100 to make it show as a percentage.
            val option = AttributeUtil.getAttributeFormat(entry.getAttribute())
            val number =
                if (entry.getOperation() == AttributeModifier.Operation.ADD_NUMBER) option.format(entry.getAmount()) else option.format(
                    entry.getAmount() * 100
                )
            // If this is a multiplicative operation, or we need to force the attribute to show as a percent, use percents.
            val numberSection = String.format("%s%s", sign, number)
            val wrapper = entry.getAttribute()
            val numberColor =
                if (wrapper.Type == AttributeType.SPECIAL)
                    NamedTextColor.LIGHT_PURPLE
                else if (wrapper.Type == AttributeType.HELPFUL && entry.getAmount() > 0)
                    NamedTextColor.GREEN else NamedTextColor.RED

            val numberComponent: Component = ComponentUtils.create(numberSection, numberColor)
            lines.add(ComponentUtils.create(wrapper.DisplayName + ": ").append(numberComponent))
        }
        return lines
    }

    /**
     * Sets the persistent key on this item to this reforge
     *
     * @param itemStack
     */
    fun applyItemPersistence(itemStack: ItemStack) {
        itemStack.editMeta(Consumer { meta: ItemMeta ->
            meta.persistentDataContainer.set(
                this.itemService.reforgeTypeKey, PersistentDataType.STRING, key.value()
            )
        })
    }

    /**
     * Removes the key from this item that says this item is of a certain reforge
     *
     * @param itemStack
     */
    fun removeItemPersistence(itemStack: ItemStack) {
        itemStack.editMeta(Consumer { meta: ItemMeta ->
            meta.persistentDataContainer.remove(this.itemService.reforgeTypeKey)
        })
    }

    /**
     * An item lore friendly list of components to display as a vague description of the item for what it does
     *
     * @return
     */
    abstract val description: List<Component>

    /**
     * Attempts to apply this reforge to the item
     *
     * @param item
     * @return
     */
    fun apply(item: ItemStack) {
        // Attempt to remove this reforge no matter what
        remove(item)

        // Apply this reforges tag
        applyItemPersistence(item)

        // Update the item (Attributes will be updated from doing this)
        val blueprint = this.itemService.getBlueprint(item)
        blueprint.updateItemData(item)
    }

    /**
     * Removes this reforge from an item
     *
     * @param item
     */
    fun remove(item: ItemStack?) {
        if (item == null || item.itemMeta == null)
            return

        val blueprint = this.itemService.getBlueprint(item)

        // Remove this item's attributes that make it reforged under this handler
        removeItemPersistence(item)

        // Now that it is removed, update the item
        blueprint.updateItemData(item)
    }

    /**
     * Checks if an item has this reforge currently equipped
     *
     * @param item an ItemStack to check a reforge against
     * @return true if the item has this reforge, false otherwise
     */
    fun hasReforge(item: ItemStack?): Boolean {
        val applied = SMPRPG.getService(ItemService::class.java).getReforge(item)
        if (applied == null)
            return false

        return applied.type == this.type
    }
}
