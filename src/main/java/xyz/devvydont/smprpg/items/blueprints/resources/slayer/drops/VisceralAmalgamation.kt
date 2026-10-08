package xyz.devvydont.smprpg.items.blueprints.resources.slayer.drops

import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint
import xyz.devvydont.smprpg.items.interfaces.IHeaderDescribable
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden
import xyz.devvydont.smprpg.items.interfaces.ISellable
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class VisceralAmalgamation(itemService: ItemService, type: CustomItemType) : CustomItemBlueprint(itemService, type),
    ISellable, IHeaderDescribable, IModelOverridden {

    override val itemClassification: ItemClassification get() = ItemClassification.ITEM

    override fun getWorth(item: ItemStack): Int { return 138172 * item.amount }

    override fun getHeader(itemStack: ItemStack?): MutableList<Component?> {
        return mutableListOf(
            ComponentUtils.merge(
                ComponentUtils.create("A horrifying concoction consisting of "),
                ComponentUtils.create("absurd amounts", NamedTextColor.DARK_RED)
            ),
            ComponentUtils.merge(
                ComponentUtils.create("of "),
                ComponentUtils.create("flesh at various stages of decomposition, ", NamedTextColor.RED)
            ),
            ComponentUtils.create("chunks of viscera and gored remains, ", NamedTextColor.GOLD),
            ComponentUtils.create("and of course, to bind it all together..."),
            ComponentUtils.merge(
                ComponentUtils.create("slime", NamedTextColor.GREEN, TextDecoration.BOLD),
                ComponentUtils.create(".")
            )
        )
    }

    override fun getDisplayKey(): Key? { return IModelOverridden.ofItemTypeInDirectory(customItemType, "materials") }
}
