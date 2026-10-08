package xyz.devvydont.smprpg.items.blueprints.tomes.spells

import io.papermc.paper.datacomponent.DataComponentTypes
import net.kyori.adventure.key.Key
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint
import xyz.devvydont.smprpg.items.interfaces.IAbilityCaster
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden
import xyz.devvydont.smprpg.services.ItemService

abstract class SpellBlueprint(itemService: ItemService, type: CustomItemType) : CustomItemBlueprint(itemService, type), IAbilityCaster, IModelOverridden {
    override val itemClassification: ItemClassification get() = ItemClassification.SPELL

    override fun updateItemData(itemStack: ItemStack) {
        itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, 1)
        super.updateItemData(itemStack)
    }

    override fun getDisplayKey(): Key? {
        return IModelOverridden.ofItemTypeInDirectory(type, "spells")
    }

}