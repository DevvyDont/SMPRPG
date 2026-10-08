package xyz.devvydont.smprpg.items.blueprints.resources.slayer

import net.kyori.adventure.key.Key
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden
import xyz.devvydont.smprpg.items.interfaces.ISellable
import xyz.devvydont.smprpg.services.ItemService

class SpellPowder(itemService: ItemService, type: CustomItemType) :
    CustomItemBlueprint(itemService, type), ISellable, IModelOverridden {

    override val itemClassification: ItemClassification get() = ItemClassification.ITEM

    override fun getWorth(itemStack: ItemStack): Int = 75 * itemStack.amount

    override fun getDisplayKey(): Key {
        return IModelOverridden.ofItemTypeInDirectory(customItemType, "materials")
    }
}

