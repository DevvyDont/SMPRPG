package xyz.devvydont.smprpg.items.blueprints.tools.augments

import io.papermc.paper.datacomponent.DataComponentTypes
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.ItemRarity
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint
import xyz.devvydont.smprpg.items.interfaces.IHeaderDescribable
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

open class RepairCore(itemService: ItemService, type: CustomItemType) : CustomItemBlueprint(itemService, type), IHeaderDescribable, IModelOverridden {

    override val itemClassification: ItemClassification get() = ItemClassification.AUGMENT_STONE

    override fun updateItemData(itemStack: ItemStack) {
        super.updateItemData(itemStack)
        itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, MAX_STACK_SIZE)
    }

    override fun getHeader(itemStack: ItemStack?): List<Component?>? {
        val rarity = getRarity(itemStack!!)
        if (rarity == ItemRarity.COMMON) {
            return listOf(
                ComponentUtils.merge(
                    ComponentUtils.create("Repairs "),
                    ComponentUtils.create("50%", NamedTextColor.GREEN),
                    ComponentUtils.create(" of durability when combined"),
                ),
                ComponentUtils.merge(
                    ComponentUtils.create("with a repairable "),
                    ComponentUtils.create(rarity.name, rarity.color, TextDecoration.BOLD),
                    ComponentUtils.create(" item in an anvil."),
                ),
                ComponentUtils.EMPTY,
                ComponentUtils.merge(
                    ComponentUtils.create("This core can also be used on "),
                    ComponentUtils.create("${ItemRarity.UNCOMMON.name}+", ItemRarity.UNCOMMON.color, TextDecoration.BOLD),
                    ComponentUtils.create(" rarity")
                ),
                ComponentUtils.create("items with diminishing returns.")
            )
        }
        else if (rarity == ItemRarity.UNCOMMON) {
            return listOf(
                ComponentUtils.merge(
                    ComponentUtils.create("Repairs "),
                    ComponentUtils.create("50%", NamedTextColor.GREEN),
                    ComponentUtils.create(" of durability when combined"),
                ),
                ComponentUtils.merge(
                    ComponentUtils.create("with a repairable "),
                    ComponentUtils.create(rarity.name, rarity.color, TextDecoration.BOLD),
                    ComponentUtils.create(" item in an anvil."),
                ),
                ComponentUtils.EMPTY,
                ComponentUtils.merge(
                    ComponentUtils.create("This core can also be used on "),
                    ComponentUtils.create("${ItemRarity.RARE.name}+", ItemRarity.RARE.color, TextDecoration.BOLD),
                    ComponentUtils.create(" rarity")
                ),
                ComponentUtils.create("items with diminishing returns, and"),
                ComponentUtils.merge(
                    ComponentUtils.create(ItemRarity.COMMON.name, ItemRarity.COMMON.color, TextDecoration.BOLD),
                    ComponentUtils.create(" rarity items for extra durability.")
                ),
            )
        }
        else {
            val nextRarity = ItemRarity.entries.get(rarity.ordinal + 1)
            val previousRarity = ItemRarity.entries.get(rarity.ordinal - 1)
            return listOf(
                ComponentUtils.merge(
                    ComponentUtils.create("Repairs "),
                    ComponentUtils.create("50%", NamedTextColor.GREEN),
                    ComponentUtils.create(" of durability when combined"),
                ),
                ComponentUtils.merge(
                    ComponentUtils.create("with a repairable "),
                    ComponentUtils.create(rarity.name, rarity.color, TextDecoration.BOLD),
                    ComponentUtils.create(" item in an anvil."),
                ),
                ComponentUtils.EMPTY,
                ComponentUtils.merge(
                    ComponentUtils.create("This core can also be used on "),
                    ComponentUtils.create("${nextRarity.name}+", nextRarity.color, TextDecoration.BOLD),
                    ComponentUtils.create(" rarity")
                ),
                ComponentUtils.create("items with diminishing returns, and"),
                ComponentUtils.merge(
                    ComponentUtils.create(previousRarity.name, previousRarity.color, TextDecoration.BOLD),
                    ComponentUtils.create(" and lower rarity items for extra durability.")
                ),
            )
        }
    }

    override fun getDisplayKey(): Key? { return IModelOverridden.ofItemTypeInDirectory(customItemType, "augment_stones") }

    companion object {
        fun getRepairValue(coreItem : ItemStack, repairItem : ItemStack) : Double {
            val coreBp = ItemService.blueprint(coreItem)
            val repairBp = ItemService.blueprint(repairItem)
            var repairValue = 0.5

            // Add/Subtract a bonus depending on the item we are using it on
            // Ex: Using a common repair core on an uncommon item subtracts 10% from the repair value,
            // but using an uncommon repair core on a common item adds 10% to the repair value

            repairValue += 0.1 * (coreBp.getRarity(coreItem).ordinal - repairBp.getRarity(repairItem).ordinal)
            return repairValue
        }

        const val MAX_STACK_SIZE = 4
    }
}