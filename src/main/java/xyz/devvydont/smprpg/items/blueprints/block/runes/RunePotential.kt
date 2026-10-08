package xyz.devvydont.smprpg.items.blueprints.block.runes

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.blueprints.craftengine.CraftEngineBlueprint
import xyz.devvydont.smprpg.items.interfaces.IFooterDescribable
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class RunePotential(itemService: ItemService, type: CustomItemType) : CraftEngineBlueprint(itemService, type),
    IFooterDescribable {

    override fun getFooter(itemStack: ItemStack?): MutableList<Component?> {
        return mutableListOf(
            ComponentUtils.create("When placed under an Enchanting Table (3x3):", NamedTextColor.LIGHT_PURPLE),
            ComponentUtils.merge(
                ComponentUtils.create("Grants ", NamedTextColor.GRAY),
                ComponentUtils.create("+" + 2 + " Magic Proficiency", NamedTextColor.DARK_PURPLE),
                ComponentUtils.create(".", NamedTextColor.GRAY)
            ),
            ComponentUtils.EMPTY,
            ComponentUtils.create("When placed directly under an Enchanting Table:", NamedTextColor.LIGHT_PURPLE),
            ComponentUtils.merge(
                ComponentUtils.create("Grants ", NamedTextColor.GRAY),
                ComponentUtils.create("+" + 4 + " Magic Proficiency", NamedTextColor.DARK_PURPLE),
                ComponentUtils.create(".", NamedTextColor.GRAY)
            )
        )
    }
}
