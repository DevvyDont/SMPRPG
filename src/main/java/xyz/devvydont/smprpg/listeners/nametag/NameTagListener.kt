package xyz.devvydont.smprpg.listeners.nametag

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.entity.base.BossInstance
import xyz.devvydont.smprpg.gui.nametag.DialogRenameNameTag
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.listeners.ToggleableListener
import xyz.devvydont.smprpg.util.persistence.KeyStore

/**
 * Restores name tag functionality now that the vanilla anvil GUI has been replaced. Naming happens in two steps:
 *
 *  1. Right-click air with a name tag to open a [DialogRenameNameTag] and type the name. The name is stored on the
 *     name tag's PDC under [KeyStore.ASSIGNED_NAME].
 *  2. Right-click a mob with that name tag to apply the name. The name is copied to the entity's PDC and the entity's
 *     nametag is rebuilt (see LeveledEntity#getNameComponent), which italicizes player-assigned names.
 *
 * Right-clicking a mob with a blank (unnamed) tag opens the dialog instead, so the whole flow is reachable from a mob.
 */
class NameTagListener : ToggleableListener() {

    /**
     * Right-clicking air with a name tag opens the rename dialog. We deliberately ignore block clicks so that holding
     * a name tag never blocks normal interactions like opening chests or doors.
     */
    @EventHandler(priority = EventPriority.LOW)
    @Suppress("unused")
    private fun onRightClickAir(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_AIR) return
        if (event.hand != EquipmentSlot.HAND) return

        val item = event.item ?: return
        if (item.type != Material.NAME_TAG) return

        event.isCancelled = true
        openRenameDialog(event.player, item)
    }

    /**
     * Right-clicking a mob with a name tag applies the tag's stored name to it, consuming one tag. A blank tag opens
     * the rename dialog instead.
     */
    @EventHandler(priority = EventPriority.LOW)
    @Suppress("unused")
    private fun onRightClickEntity(event: PlayerInteractEntityEvent) {
        if (event.hand != EquipmentSlot.HAND) return

        val player = event.player
        val item = player.inventory.getItem(event.hand) ?: return
        if (item.type != Material.NAME_TAG) return

        // We fully own name tag interactions, so vanilla never gets a chance to apply its own name.
        event.isCancelled = true

        val assignedName = item.persistentDataContainer.get(KeyStore.ASSIGNED_NAME, PersistentDataType.STRING)
        if (assignedName.isNullOrBlank()) {
            openRenameDialog(player, item)
            return
        }

        val target = event.rightClicked
        if (target !is LivingEntity || target is Player) {
            player.sendMessage(ComponentUtils.error("You can't name that!"))
            return
        }

        val leveled = SMPRPG.getService(EntityService::class.java).getEntityInstance(target)
        if (leveled is BossInstance<*>) {
            player.sendMessage(ComponentUtils.error("Bosses can't be renamed!"))
            return
        }

        target.persistentDataContainer.set(KeyStore.ASSIGNED_NAME, PersistentDataType.STRING, assignedName)
        leveled.updateNametag()
        consumeOne(player, item)
        playNamingEffects(target)
        player.sendMessage(ComponentUtils.success("Named the ${leveled.entityName} \"$assignedName\"!"))
    }

    /**
     * Plays a celebratory chime and green sparkles around a freshly named mob. Spawned at the world level so every
     * nearby player sees the feedback, not just the one holding the tag.
     */
    private fun playNamingEffects(target: LivingEntity) {
        val world = target.world
        val center = target.location.add(0.0, target.height * PARTICLE_HEIGHT_RATIO, 0.0)
        world.spawnParticle(
            Particle.HAPPY_VILLAGER, center, NAMING_PARTICLE_COUNT,
            PARTICLE_SPREAD, PARTICLE_SPREAD, PARTICLE_SPREAD, 0.0
        )
        world.playSound(target.location, Sound.BLOCK_AMETHYST_BLOCK_CHIME, NAMING_SOUND_VOLUME, NAMING_SOUND_PITCH)
    }

    private fun openRenameDialog(player: Player, item: ItemStack) {
        // Opening a dialog reacts to an interaction, so defer it a tick to stay clear of the event dispatch.
        Bukkit.getScheduler().runTaskLater(SMPRPG.plugin, Runnable {
            DialogRenameNameTag(player, item).openMenu()
        }, NEXT_TICK_DELAY)
    }

    /**
     * Consumes a single name tag from the stack, mirroring vanilla which spends the tag on use. Creative players keep
     * their tag, also matching vanilla.
     */
    private fun consumeOne(player: Player, item: ItemStack) {
        if (player.gameMode == GameMode.CREATIVE) return
        item.amount -= 1
    }

    companion object {
        private const val NEXT_TICK_DELAY = 1L
        private const val NAMING_PARTICLE_COUNT = 12
        private const val PARTICLE_SPREAD = 0.4
        private const val PARTICLE_HEIGHT_RATIO = 0.6
        private const val NAMING_SOUND_VOLUME = 1.0f
        private const val NAMING_SOUND_PITCH = 1.2f
    }
}
