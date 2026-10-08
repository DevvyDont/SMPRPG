package xyz.devvydont.smprpg.items.blueprints.tomes

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.ItemContainerContents
import io.papermc.paper.datacomponent.item.TooltipDisplay
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.NamespacedKey
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.gui.items.MenuTomeModification
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.base.CustomAttributeItem
import xyz.devvydont.smprpg.items.blueprints.resources.slayer.drops.NecronomiconExcerpts
import xyz.devvydont.smprpg.items.blueprints.tomes.spells.SpellBlueprint
import xyz.devvydont.smprpg.items.interfaces.IAbilityCaster
import xyz.devvydont.smprpg.items.interfaces.IFooterDescribable
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

abstract class TomeBlueprint(itemService: ItemService, type: CustomItemType) : CustomAttributeItem(itemService, type), Listener, IAbilityCaster, IModelOverridden, IFooterDescribable {

    override val itemClassification: ItemClassification get() = ItemClassification.TOME
    abstract val maxSpellSlots: Int
    abstract val cooldownMult: Double

    fun initialize(tome: ItemStack) {
        // Create an empty container for spell storage.
        val contents = mutableListOf<ItemStack>()
        for (i in 0..<getModifiedMaxSlots(tome)) contents.add(ItemStack.of(MenuTomeModification.DUMMY_MATERIAL))
        tome.setData(DataComponentTypes.CONTAINER, ItemContainerContents.containerContents(contents))
        // Default to index 0 of our spell storage.
        tome.editPersistentDataContainer { pdc: PersistentDataContainer ->
            pdc.set(ACTIVE_SPELL_INDEX_KEY, PersistentDataType.INTEGER, 0)
        }
    }

    fun getModifiedMaxSlots(tome: ItemStack): Int {
        var maxSlots = maxSpellSlots
        maxSlots += tome.persistentDataContainer.getOrDefault(
            NecronomiconExcerpts.TOME_SPELL_COUNT_MODIFIER,
            PersistentDataType.INTEGER,
            0)
        return maxSlots
    }

    override fun updateItemData(itemStack: ItemStack) {
        if (itemStack.getDataOrDefault(DataComponentTypes.CONTAINER, null) == null) initialize(itemStack)
        itemStack.setData(DataComponentTypes.TOOLTIP_DISPLAY,
            TooltipDisplay.tooltipDisplay().addHiddenComponents(DataComponentTypes.CONTAINER).build());
        super.updateItemData(itemStack)
    }

    override fun getAbilities(item: ItemStack): Collection<IAbilityCaster.AbilityEntry> {
        // We only want to show the current active spell on the item, so index and return that spell.
        val spellStorage = item.getDataOrDefault(DataComponentTypes.CONTAINER, null)
        // No spells, no abilities
        if (spellStorage == null) return listOf()

        val chosenSpellItem = spellStorage.contents().getOrNull(item.persistentDataContainer.get(ACTIVE_SPELL_INDEX_KEY, PersistentDataType.INTEGER)!!) ?: return listOf()
        if (chosenSpellItem.isEmpty) return listOf()

        val spellBp = itemService.getBlueprint(chosenSpellItem)
        // This check is required in case we select a "null" spell (slot isn't filled)
        if (spellBp !is SpellBlueprint) return listOf()

        // All checks passed! Return the abilities of the selected spell.
        return spellBp.getAbilities(chosenSpellItem)
    }

    override fun getCooldown(item: ItemStack): Long {
        // We only want to show the current active spell on the item, so index and return that spell.
        val spellStorage = item.getDataOrDefault(DataComponentTypes.CONTAINER, null)
        // No spells, no abilities
        if (spellStorage == null) return 0

        val chosenSpellItem = spellStorage.contents()[item.persistentDataContainer.get(ACTIVE_SPELL_INDEX_KEY, PersistentDataType.INTEGER)!!]
        if (chosenSpellItem.isEmpty) return 0

        val spellBp = itemService.getBlueprint(chosenSpellItem)
        // This check is required in case we select a "null" spell (slot isn't filled)
        if (spellBp !is SpellBlueprint) return 0

        // All checks passed! Return the cooldown of the selected spell, and adjust according to our cooldown reduction.
        return (spellBp.getCooldown(chosenSpellItem) * cooldownMult).toLong()
    }

    override fun getDisplayKey(): Key {
        return IModelOverridden.ofItemTypeInDirectory(type, "tomes")
    }

    override fun getFooter(itemStack: ItemStack): List<Component> {
        return listOf(
            ComponentUtils.merge(
                ComponentUtils.create("Right click ", NamedTextColor.GOLD, TextDecoration.BOLD),
                ComponentUtils.create("this tome while in your inventory", NamedTextColor.GRAY),
                ),
            ComponentUtils.create("to open the spell inventory.", NamedTextColor.GRAY),
            ComponentUtils.merge(
                ComponentUtils.create("Press ", NamedTextColor.GRAY),
                Component.keybind("key.swapOffhand").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD),
                ComponentUtils.create(" while holding to change the active spell.", NamedTextColor.GRAY)
            )
        )
    }

    companion object {
        val SPELLS_CONTAINER_KEY = NamespacedKey(SMPRPG.plugin, "equipped_spells")
        val ACTIVE_SPELL_INDEX_KEY = NamespacedKey(SMPRPG.plugin, "active_spell")
    }
}