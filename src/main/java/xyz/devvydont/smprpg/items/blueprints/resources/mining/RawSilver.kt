package xyz.devvydont.smprpg.items.blueprints.resources.mining

import net.kyori.adventure.key.Key
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.blueprints.craftengine.CraftEngineBlueprint
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden
import xyz.devvydont.smprpg.items.interfaces.ISellable
import xyz.devvydont.smprpg.services.ItemService

private const val WORTH_PER_ITEM = 15

class RawSilver(itemService: ItemService, type: CustomItemType) :
    CraftEngineBlueprint(itemService, type), ISellable, IModelOverridden {

    override val itemClassification: ItemClassification get() = ItemClassification.MATERIAL

    override fun getWorth(itemStack: ItemStack): Int = WORTH_PER_ITEM * itemStack.amount

    override fun getDisplayKey(): Key =
        IModelOverridden.ofItemTypeInDirectory(CustomItemType.RAW_SILVER, "materials")
}

