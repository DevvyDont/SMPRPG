package xyz.devvydont.smprpg.items.blueprints.craftengine

import io.papermc.paper.datacomponent.item.Consumable
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.interfaces.IEdible
import xyz.devvydont.smprpg.services.ItemService

@Suppress("UnstableApiUsage")
open class CraftEngineFoodBlueprint(itemService: ItemService, type: CustomItemType) : CraftEngineBlueprint(itemService, type),
    IEdible {

    override val itemClassification: ItemClassification get() = ItemClassification.CONSUMABLE

    override fun getNutrition(item: ItemStack): Int { return 1 }

    override fun getSaturation(item: ItemStack): Float { return 0.0f }

    override fun canAlwaysEat(item: ItemStack): Boolean { return true }

    override fun getConsumableComponent(item: ItemStack): Consumable {
        return Consumable.consumable()
            .consumeSeconds(IEdible.DEFAULT_EAT_SPEED)
            .build()
    }

}