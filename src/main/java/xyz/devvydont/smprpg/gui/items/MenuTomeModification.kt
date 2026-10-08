package xyz.devvydont.smprpg.gui.items

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.ItemContainerContents
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.items.blueprints.tomes.TomeBlueprint
import xyz.devvydont.smprpg.items.blueprints.tomes.spells.SpellBlueprint
import xyz.devvydont.smprpg.services.ItemService.Companion.blueprint
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.Symbols

class MenuTomeModification(player: Player, val blueprint: TomeBlueprint, val tome: ItemStack) :
    MenuBase(player, 1) {
        private var startSlot = 0

    init {
        this.sounds.setMenuOpen(Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, .5f)
        this.sounds.setMenuClose(Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 2.0f)
        this.sounds.setPageNext(Sound.ITEM_ARMOR_EQUIP_LEATHER, 1f, .8f)
        this.sounds.setPageNext(Sound.ITEM_ARMOR_EQUIP_LEATHER, 1f, 1.3f)
        render()
    }

    fun render() {
        startSlot = determineStartSlot()
        for (i in 0..8) {
            this.setSlot(i, BORDER_VOID_TOME)
        }
        val data = tome.getData(DataComponentTypes.CONTAINER)
        val spells: MutableList<ItemStack> = data!!.contents()
        var j = 0
        for (i in startSlot..<startSlot + blueprint.getModifiedMaxSlots(tome)) {
            this.clearSlot(i)
            if (spells[j].type == DUMMY_MATERIAL)
                this.setSlot(i, ItemStack.empty())
            else
                this.setSlot(i, spells[j])
            j++
        }
    }

    fun saveData() {
        val newSpells = ArrayList<ItemStack>()
        for (i in 0..<blueprint.getModifiedMaxSlots(tome)) {
            var item = inventory.getItem(startSlot + i)
            if (item == null) item = ItemStack.of(DUMMY_MATERIAL)
            newSpells.add(item)
        }
        tome.setData(
            DataComponentTypes.CONTAINER,
            ItemContainerContents.containerContents(newSpells)
        )

        blueprint.updateItemData(tome)
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        // Under any circumstances, NEVER let any other backpacks (or gui elements) be clicked or modified.
        val clicked = event.currentItem
        if (clicked == null) return

        if (blueprint(clicked) !is SpellBlueprint) {
            event.isCancelled = true
        }
    }

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        event.titleOverride(
            ComponentUtils.merge(
                ComponentUtils.create(Symbols.OFFSET_NEG_8 + Symbols.TOME_MENU, NamedTextColor.WHITE),
                ComponentUtils.create(
                    Symbols.OVERLAY_BG_OFFSET_STANDARD + Symbols.OFFSET_NEG_7 + "Tome Modification",
                    TextColor.color(61, 31, 0)
                )
            )
        )
        event.getInventory().setMaxStackSize(1)
    }

    override fun handleInventoryClosed(event: InventoryCloseEvent) {
        // When this inventory closes, our inventory is the source of truth, so we should copy everything we have over.
        this.saveData()
    }

    fun determineStartSlot(): Int {
        val maxSlots = blueprint.getModifiedMaxSlots(tome)
        return when (maxSlots) {
            1    -> 4
            2, 3 -> 3
            4, 5 -> 2
            6, 7 -> 1
            else -> 0
        }
    }

    companion object {
        val DUMMY_MATERIAL = Material.DANDELION
        val BORDER_VOID_TOME = BORDER_VOID.clone()

        init {
            BORDER_VOID_TOME.setData(DataComponentTypes.ITEM_MODEL, Key.key("smprpg:ui/void_slot_tome"))
        }
    }
}
