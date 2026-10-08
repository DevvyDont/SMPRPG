package xyz.devvydont.smprpg.items.base

import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemRarity
import xyz.devvydont.smprpg.items.attribute.AttributeModifierType
import xyz.devvydont.smprpg.items.interfaces.IAttributeItem
import xyz.devvydont.smprpg.items.interfaces.ISellable
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.services.RecipeService
import xyz.devvydont.smprpg.util.attributes.AttributeUtil
import java.util.*

/**
 * Represents some sort of item that can modify an entity's attributes either when worn or held.
 * Contains an extra "lore" section in the item description to display these stats to players since
 * we are overriding the given minecraft one (it's ugly)
 *
 * Also, all children of this class can be "prefixed", where a player can reforge an item for small additional
 * stat bonuses. Only one may be applied
 */
abstract class CustomAttributeItem(itemService: ItemService, override val type: CustomItemType) :
    CustomItemBlueprint(itemService, type), IAttributeItem, ISellable {
    open fun wantNerfedSellPrice(): Boolean {
        return defaultRarity.ordinal < ItemRarity.EPIC.ordinal
    }

    override fun getWorth(item: ItemStack): Int {
        return AttributeUtil.calculateValue(
            getTotalPower(item.itemMeta),
            defaultRarity,
            RecipeService.isCraftable(item) && wantNerfedSellPrice()
        ) * item.amount
    }

    override fun getAttributeModifierType(): AttributeModifierType {
        return AttributeModifierType.BASE
    }

    /**
     * Sums the power rating of the item with any additional bonuses on it
     *
     * @param meta
     * @return
     */
    fun getTotalPower(meta: ItemMeta?): Int {
        return getPowerRating() + AttributeUtil.getPowerBonus(meta)
    }

    override fun getUniqueModifierKey(): String {
        return this.customItemType.name.lowercase(Locale.getDefault())
    }

    override fun getRarity(item: ItemStack): ItemRarity {
        return super.getRarity(item)
    }

    override fun getItemName(item: ItemStack?): String {
        return super.getItemName(item)
    }
}
