package xyz.devvydont.smprpg.items.blueprints.crops

import io.papermc.paper.datacomponent.item.Consumable
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.blueprints.craftengine.CraftEngineBlueprint
import xyz.devvydont.smprpg.items.interfaces.IEdible
import xyz.devvydont.smprpg.items.interfaces.ISellable
import xyz.devvydont.smprpg.services.ItemService

class Heartbeet(itemService: ItemService, type: CustomItemType) : CraftEngineBlueprint(itemService, type), IEdible,
    ISellable {
    override val itemClassification: ItemClassification get() = ItemClassification.CONSUMABLE

    override fun getWorth(itemStack: ItemStack): Int { return 5 * itemStack.amount }

    override fun getNutrition(item: ItemStack?): Int { return 35 }

    override fun getSaturation(item: ItemStack?): Float { return 0f }

    override fun canAlwaysEat(item: ItemStack?): Boolean { return true }

    override fun getConsumableComponent(item: ItemStack?): Consumable {
        return Consumable.consumable()
            .consumeSeconds(3.2f)
            .addEffect(ConsumeEffect.applyStatusEffects(listOf(
                PotionEffect(
                    PotionEffectType.REGENERATION,
                    20 * 20,
                    1,
                    true,
                    true
                )
            ), 1.0f))
            .build()
    }
}
