package xyz.devvydont.smprpg.items.blueprints.food

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.Consumable
import io.papermc.paper.datacomponent.item.UseRemainder
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect
import net.kyori.adventure.key.Key
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint
import xyz.devvydont.smprpg.items.interfaces.IEdible
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden
import xyz.devvydont.smprpg.items.interfaces.ISellable
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.time.TickTime

class BeefStew(itemService: ItemService, type: CustomItemType) : CustomItemBlueprint(itemService, type), IEdible,
    ISellable, IModelOverridden {
    override val itemClassification: ItemClassification get() = ItemClassification.CONSUMABLE

    override fun getNutrition(item: ItemStack?): Int {
        return 40
    }

    override fun getSaturation(item: ItemStack?): Float {
        return 0f
    }

    override fun getWorth(itemStack: ItemStack): Int {
        return 45 * itemStack.amount
    }

    override fun getConsumableComponent(item: ItemStack): Consumable {
        return Consumable.consumable()
            .consumeSeconds(3.0f)
            .addEffect(
                ConsumeEffect.applyStatusEffects(
                    listOf(
                        PotionEffect(
                            PotionEffectType.REGENERATION,
                            TickTime.seconds(10).toInt(),
                            0,
                            true,
                            true
                        )
                    ), 1.0f
                )
            )
            .build()
    }

    override fun updateItemData(itemStack: ItemStack) {
        itemStack.setData(DataComponentTypes.USE_REMAINDER, UseRemainder.useRemainder(itemService.getCustomItem(Material.BOWL)))
        itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, 16)
        super.updateItemData(itemStack)
    }

    /**
     * Get the material that this item should display as, regardless of what it actually is internally.
     * This allows you to change how an item looks without affecting its behavior.
     *
     * @return The material this item should render as.
     */
    override fun getDisplayKey(): Key { return IModelOverridden.ofItemTypeInDirectory(customItemType, "consumables") }

    override fun canAlwaysEat(item: ItemStack?): Boolean {
        return true
    }
}
