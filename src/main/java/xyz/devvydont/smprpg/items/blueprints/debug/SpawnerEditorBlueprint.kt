package xyz.devvydont.smprpg.items.blueprints.debug

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.entity.CustomEntityType
import xyz.devvydont.smprpg.entity.spawning.EntitySpawner
import xyz.devvydont.smprpg.gui.spawner.InterfaceSpawnerMainMenu
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint
import xyz.devvydont.smprpg.items.interfaces.IFooterDescribable
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class SpawnerEditorBlueprint(itemService: ItemService, type: CustomItemType) : CustomItemBlueprint(itemService, type),
    Listener, IFooterDescribable {

    override val itemClassification: ItemClassification get() = ItemClassification.ITEM

    override fun getFooter(itemStack: ItemStack?): MutableList<Component?> {
        return mutableListOf(
            ComponentUtils.create("Used to interact with"),
            ComponentUtils.create("and edit custom spawner"),
            ComponentUtils.create("entities in the world")
        )
    }

    private fun canUse(player: Player): Boolean {
        return player.isOp || player.permissionValue("smprpg.items.spawneditor.view").toBooleanOrElse(false)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onInteractWhileHoldingEditor(event: PlayerInteractEvent) {
        val item = event.item
        if (item == null) return
        if (!isItemOfType(item)) return

        event.setCancelled(true)
        val player = event.player

        if (!canUse(player)) {
            player.sendMessage(ComponentUtils.error("You lack permissions to use this item!"))
            return
        }

        val nearbyDisplays = player.world.getNearbyEntitiesByType(
            CustomEntityType.SPAWNER.Type.entityClass,
            player.eyeLocation,
            2.5
        )
        val nearbySpawners: MutableList<EntitySpawner> = ArrayList()
        for (display in nearbyDisplays) {
            if (SMPRPG.getService(EntityService::class.java).getEntityInstance(display) is EntitySpawner) {
                nearbySpawners.add(SMPRPG.getService(EntityService::class.java).getEntityInstance(display) as EntitySpawner)
            }
        }

        if (nearbySpawners.isEmpty()) {
            player.sendMessage(ComponentUtils.error("Did not detect any spawners near you! Get closer to one and try again :3"))
            return
        }

        if (nearbySpawners.size > 1) {
            player.sendMessage(ComponentUtils.error("Detected too many spawners near you! Try to limit the spawners you are close to!"))
            return
        }

        val spawner: EntitySpawner = nearbySpawners.first()

        InterfaceSpawnerMainMenu(event.getPlayer(), spawner).openMenu()
        event.getPlayer().sendMessage(ComponentUtils.success("Now editing the spawner you were looking at!"))
    }
}
