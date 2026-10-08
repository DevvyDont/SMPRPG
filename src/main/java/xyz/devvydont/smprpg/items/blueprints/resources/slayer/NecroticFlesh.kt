package xyz.devvydont.smprpg.items.blueprints.resources.slayer

import io.papermc.paper.datacomponent.item.Consumable
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect
import net.kyori.adventure.key.Key
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

class NecroticFlesh(itemService: ItemService, type: CustomItemType) : CustomItemBlueprint(itemService, type),
    ISellable, IEdible, IModelOverridden {
    /**
     * Determine what type of item this is.
     */
    override val itemClassification: ItemClassification get() = ItemClassification.ITEM

    /**
     * Given this item stack, how much should it be able to sell for?
     * Keep in mind that the size of the stack needs to considered as well!
     *
     * @param item The item that can be sold.
     * @return The worth of the item.
     */
    override fun getWorth(item: ItemStack): Int { return 30 * item.amount }

    override fun getConsumableComponent(item: ItemStack?): Consumable {
        val effects = ArrayList<ConsumeEffect>()

        effects.add(
            ConsumeEffect.applyStatusEffects(
                listOf(
                    PotionEffect(PotionEffectType.WITHER, TickTime.minutes(1).toInt(), 0)
                ), 1f
            )
        )

        return Consumable.consumable()
            .consumeSeconds(10f)
            .addEffects(effects)
            .build()
    }

    override fun getNutrition(item: ItemStack?): Int { return 0 }

    override fun getSaturation(item: ItemStack?): Float { return 0.0f }

    override fun canAlwaysEat(item: ItemStack?): Boolean { return true }

    override fun getDisplayKey(): Key? { return IModelOverridden.ofItemTypeInDirectory(customItemType, "materials") }
}
