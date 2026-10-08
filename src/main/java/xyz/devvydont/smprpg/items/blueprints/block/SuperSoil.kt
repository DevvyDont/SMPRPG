package xyz.devvydont.smprpg.items.blueprints.block

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.recipe.CraftingBookCategory
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.blueprints.craftengine.CraftEngineBlueprint
import xyz.devvydont.smprpg.items.interfaces.IHeaderDescribable
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class SuperSoil(itemService: ItemService, type: CustomItemType) : CraftEngineBlueprint(itemService, type),
    IHeaderDescribable {

    override val itemClassification: ItemClassification get() = ItemClassification.BLOCK

    override fun getHeader(itemStack: ItemStack): List<Component> {
        return listOf(
            ComponentUtils.create("Increases growth speed of crops"),
            ComponentUtils.merge(
                ComponentUtils.create("planted on this soil by "),
                ComponentUtils.create("50%", NamedTextColor.GREEN)
            ),
            ComponentUtils.merge(
                ComponentUtils.create("Crops grown on this block gain "),
                ComponentUtils.create("+${UPROOTING_BONUS.toInt()}", NamedTextColor.GREEN),
                ComponentUtils.create(" Uprooting", NamedTextColor.YELLOW),
                ComponentUtils.create("."),
            ),
            ComponentUtils.EMPTY,
            ComponentUtils.merge(
                ComponentUtils.create("Doesn't require "),
                ComponentUtils.create("water", NamedTextColor.BLUE),
                ComponentUtils.create(", and comes pre-tilled!")
            ),
        )
    }

    companion object {
        const val UPROOTING_BONUS = 200.0
    }
}