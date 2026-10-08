package xyz.devvydont.smprpg.items.listeners

import io.papermc.paper.datacomponent.DataComponentTypes
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.gui.items.MenuTomeModification
import xyz.devvydont.smprpg.items.blueprints.tomes.TomeBlueprint
import xyz.devvydont.smprpg.items.blueprints.tomes.TomeBlueprint.Companion.ACTIVE_SPELL_INDEX_KEY
import xyz.devvydont.smprpg.services.ActionBarService
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.listeners.ToggleableListener

class TomeInteractionListener: ToggleableListener() {
    @EventHandler
    fun swapSpell(event: PlayerSwapHandItemsEvent) {
        val itemService = SMPRPG.getService(ItemService::class.java)
        // We check the opposite hands for this, since the switch is queued at this point
        val mainhandIsTome = itemService.getBlueprint(event.offHandItem) is TomeBlueprint
        val offhandIsTome = itemService.getBlueprint(event.mainHandItem) is TomeBlueprint
        if (mainhandIsTome || offhandIsTome) {
            var tomeToUse: ItemStack
            if (mainhandIsTome) tomeToUse = event.offHandItem
            else tomeToUse = event.mainHandItem

            event.isCancelled = true

            val player = event.player
            val tomeBp = itemService.getBlueprint(tomeToUse)

            val maxSlots = (tomeBp as TomeBlueprint).getModifiedMaxSlots(tomeToUse)
            val currSlot = tomeToUse.persistentDataContainer.getOrDefault(ACTIVE_SPELL_INDEX_KEY, PersistentDataType.INTEGER, 0)
            val newSlot = if (currSlot + 1 >= maxSlots) 0 else currSlot + 1
            tomeToUse.editPersistentDataContainer { pdc: PersistentDataContainer ->
                pdc.set(ACTIVE_SPELL_INDEX_KEY, PersistentDataType.INTEGER, newSlot)
            }
            val spell = tomeToUse.getData(DataComponentTypes.CONTAINER)!!.contents().get(newSlot)
            var spellName = spell.displayName()
            if (spell.type.equals(MenuTomeModification.DUMMY_MATERIAL))
                spellName = ComponentUtils.create("[Nothing]", NamedTextColor.RED)
            player.playSound(player, Sound.UI_BUTTON_CLICK, 1f, 2f)
            SMPRPG.getService(ActionBarService::class.java).addActionBarComponent(player,
                ActionBarService.ActionBarSource.MISC,
                ComponentUtils.merge(
                    ComponentUtils.create("Equipped ", NamedTextColor.GOLD),
                    spellName,
                    ComponentUtils.create(" (${newSlot + 1}) ", NamedTextColor.AQUA)
                ),
                2)
            tomeBp.updateItemData(tomeToUse)

            if (mainhandIsTome) player.inventory.setItem(EquipmentSlot.HAND, tomeToUse)
            else player.inventory.setItem(EquipmentSlot.OFF_HAND, tomeToUse)
        }
    }

    @EventHandler
    fun onRightClickTome(event: InventoryClickEvent) {
        // Only allow opening of the tome inventory in player inventories.
        if (event.inventory.type == InventoryType.CRAFTING) {
            if (event.click.isRightClick && !event.click.isShiftClick) {
                val itemService = SMPRPG.getService(ItemService::class.java)
                val item = event.currentItem
                if (item == null) return
                val itemBp = itemService.getBlueprint(item)
                if (itemBp is TomeBlueprint) {
                    event.clickedInventory?.close()
                    event.isCancelled = true
                    val player = event.whoClicked as Player
                    Bukkit.getScheduler().runTaskLater(SMPRPG.plugin, Runnable { player.updateInventory() }, 1L)
                    MenuTomeModification(player, itemBp, item).openMenu()
                }
            }
        }
    }
}