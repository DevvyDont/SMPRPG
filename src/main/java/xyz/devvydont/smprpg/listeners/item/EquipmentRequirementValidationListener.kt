package xyz.devvydont.smprpg.listeners.item

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.event.entity.EntityEquipmentChangedEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.block.data.Ageable
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerHarvestBlockEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.items.interfaces.IDamageFromCrops
import xyz.devvydont.smprpg.items.interfaces.ISkillRequirement
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.listeners.ToggleableListener
import xyz.devvydont.smprpg.util.time.TickTime
import kotlin.collections.iterator

class EquipmentRequirementValidationListener : ToggleableListener() {
    /**
     * Listens for equipment changes, and checks skill requirements on items that are equipped.
     */

    // Handles left click for tools, right click for armor hotswaps
    @EventHandler
    private fun onItemInteract(event: PlayerInteractEvent) {
        val item = event.item
        if (event.item == null) return
        if (!ItemService.meetsRequirements(item!!, event.player)) {
            event.isCancelled = true
            event.player.sendMessage(
                ComponentUtils.merge(
                    ComponentUtils.error(ComponentUtils.create("You do not meet the skill requirements to use ", NamedTextColor.RED)),
                    item.displayName()
                ))
            Bukkit.getScheduler().runTaskLater(SMPRPG.plugin, Runnable { event.player.updateInventory() }, 1L)
            event.player.playSound(event.player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f)
        }
    }

    // Handles attempts to equip armor
    @EventHandler
    private fun onAttemptEquipArmor(event: InventoryClickEvent) {
        // This portion handles regular clicking
        if (event.slotType == InventoryType.SlotType.ARMOR) {
            val item = event.cursor
            val player = event.whoClicked as Player
            if (!ItemService.meetsRequirements(item, player)) {
                event.isCancelled = true
                Bukkit.getScheduler().runTaskLater(SMPRPG.plugin, Runnable { player.updateInventory() }, 1L)
                player.sendMessage(
                    ComponentUtils.merge(
                        ComponentUtils.error(ComponentUtils.create("You do not meet the skill requirements to equip ", NamedTextColor.RED)),
                        item.displayName()
                    ))
                player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f)
            }
        }

        // This portion handles shift clicking
        if (event.isShiftClick || event.hotbarButton != -1) {
            val player = event.whoClicked as Player
            val item: ItemStack
            if (event.isShiftClick) item = event.currentItem ?: return
            else if (event.slotType == InventoryType.SlotType.ARMOR) item = player.inventory.getItem(event.hotbarButton) ?: return
            else return
            if (item.getData(DataComponentTypes.EQUIPPABLE) != null) {

                if (!ItemService.meetsRequirements(item, player)) {
                    event.isCancelled = true
                    Bukkit.getScheduler().runTaskLater(SMPRPG.plugin, Runnable { player.updateInventory() }, 1L)
                    player.sendMessage(
                        ComponentUtils.merge(
                            ComponentUtils.error(ComponentUtils.create("You do not meet the skill requirements to equip ", NamedTextColor.RED)),
                            item.displayName()
                        ))
                    player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f)
                }
            }
        }
    }

}