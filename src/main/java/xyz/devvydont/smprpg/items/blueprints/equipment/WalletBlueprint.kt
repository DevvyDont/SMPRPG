package xyz.devvydont.smprpg.items.blueprints.equipment

import io.papermc.paper.datacomponent.DataComponentTypes
import net.kyori.adventure.key.Key
import org.bukkit.inventory.ItemStack
import org.checkerframework.checker.index.qual.NonNegative
import org.checkerframework.checker.index.qual.Positive
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint
import xyz.devvydont.smprpg.items.interfaces.ICosmeticDurability
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden
import xyz.devvydont.smprpg.services.ItemService

class WalletBlueprint(itemService: ItemService, type: CustomItemType) : CustomItemBlueprint(itemService, type), IModelOverridden,
    ICosmeticDurability {

    override val itemClassification: ItemClassification get() = ItemClassification.EQUIPMENT
    val maxCoins: Int get() = when (type) {
        CustomItemType.SMALL_COIN_PURSE    -> 10_000
        CustomItemType.MEDIUM_COIN_PURSE   -> 50_000
        CustomItemType.LARGE_COIN_PURSE    -> 100_000
        CustomItemType.GIGANTIC_COIN_PURSE -> 250_000
        CustomItemType.COLOSSAL_COIN_PURSE -> 500_000
        else -> 1
    }

    override fun updateItemData(itemStack: ItemStack) {
        if (itemStack.getDataOrDefault(DataComponentTypes.DAMAGE, -1) == -1) itemStack.setData(DataComponentTypes.DAMAGE, maxCoins)
        itemStack.setData(DataComponentTypes.MAX_DAMAGE, maxCoins + 1)
        itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, 1)
        super.updateItemData(itemStack)
    }

    override fun getDisplayKey(): Key? {
        return IModelOverridden.ofItemTypeInDirectory(type, "equipment")
    }

    fun getBalance(item: ItemStack): Int {
        val durabilityComponent = item.getData<@Positive Int?>(DataComponentTypes.MAX_DAMAGE)
        val durabilityUsed = item.getData<@NonNegative Int?>(DataComponentTypes.DAMAGE)

        // If either component is null, these item is broken and the balance has to be 0.
        if (durabilityComponent == null || durabilityUsed == null)
            return 0

        return durabilityComponent - durabilityUsed - 1
    }

    fun setBalance(item: ItemStack, balance: Int) {
        val clamped = balance.coerceIn(0, maxCoins)
        item.setData(DataComponentTypes.DAMAGE, maxCoins - clamped)
    }

}