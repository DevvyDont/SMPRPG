package xyz.devvydont.smprpg.listeners.entity

import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.generator.structure.GeneratedStructure
import org.bukkit.generator.structure.Structure
import org.bukkit.scheduler.BukkitRunnable
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.events.LeveledEntitySpawnEvent
import xyz.devvydont.smprpg.services.ActionBarService
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.MinecraftStringUtils
import xyz.devvydont.smprpg.util.listeners.ToggleableListener
import kotlin.math.max

/**
 * When entities are spawned inside of structures, we want to make them certain levels based on the structure.
 */
class StructureEntitySpawnListener : ToggleableListener() {
    override fun start() {
        super.start()

        // Create a task that checks if any players are in a structure. If they are, alert them.
        val plugin = plugin
        object : BukkitRunnable() {
            override fun run() {
                for (player in plugin.server.onlinePlayers)
                    doPlayerLocationCheck(player)
            }
        }.runTaskTimerAsynchronously(plugin, 1, (2 * 50).toLong())
    }

    private fun getStructureComponent(player: Player, structure: GeneratedStructure, power: Int): Component {

        val key: NamespacedKey? = RegistryAccess.registryAccess().getRegistry(RegistryKey.STRUCTURE).getKey(structure.structure)
        var name: String? = "???"
        if (key != null)
            name = MinecraftStringUtils.getTitledString(key.asMinimalString())

        // Create the base message.
        var send = ComponentUtils.merge(
            ComponentUtils.create("Currently in "),
            ComponentUtils.create("$name ", NamedTextColor.AQUA),
            ComponentUtils.powerLevelPrefix(power)
        )

        // If the player is underleveled, add a warning label.
        val entityService: EntityService = SMPRPG.getService(EntityService::class.java)
        if (!entityService.isTracking(player))
            return send

        if (power > entityService.getPlayerInstance(player).getLevel()) send =
            ComponentUtils.create("WARNING! ", NamedTextColor.RED).append(send)

        return send
    }


    private fun doPlayerLocationCheck(player: Player) {
        val location = player.location
        val chunk = location.chunk

        // Determine highest level structure we are in. If -1, that means we are not in one
        var mostDangerousStructure: GeneratedStructure? = null
        var highestLevel = -1
        for (structure in chunk.structures) {
            // Skip structures we aren't actually in

            if (!structure.boundingBox.overlaps(player.boundingBox)) continue

            if (highestLevel < getMinimumEntityLevel(structure)) {
                mostDangerousStructure = structure
                highestLevel = getMinimumEntityLevel(structure)
            }
        }

        // Don't do anything if we aren't in a structure
        if (mostDangerousStructure == null || highestLevel < 1) return

        SMPRPG.getService(ActionBarService::class.java)
            .addActionBarComponent(
                player,
                ActionBarService.ActionBarSource.STRUCTURE,
                getStructureComponent(player, mostDangerousStructure, highestLevel),
                5
            )
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    @Suppress("unused")
    fun onLeveledEntitySpawn(event: LeveledEntitySpawnEvent) {
        val location = event.entity.getEntity().location
        val originalLevel = event.entity.level
        var structureLevel = originalLevel

        // Loop through all the structures in this chunk. If this entity spawned inside a structure, make sure
        // they are at the minimum level at least
        for (structure in location.chunk
            .structures)  // Is the entity's bounding box in the structures bounding box?
            if (structure.boundingBox
                    .overlaps(event.entity.getEntity().boundingBox)
            )  // Does this structure have a minimum level definition?
                if (getMinimumEntityLevel(structure) >= 1) structureLevel =
                    max(structureLevel, getMinimumEntityLevel(structure))

        // If there is a difference in structure level, use that level instead
        if (structureLevel == originalLevel)
            return

        // After resolving the highest level based on current entity level and structures the entity was in, set it.
        event.entity.setLevel(structureLevel)
    }


    companion object {
        var minimumStructureLevels: MutableMap<Structure?, Int?> = HashMap<Structure?, Int?>()

        init {
            minimumStructureLevels.put(
                Structure.ANCIENT_CITY,
                100
            ) // Ancient cities are the hardest content in the game
            minimumStructureLevels.put(Structure.END_CITY, 55) // The end
            minimumStructureLevels.put(Structure.STRONGHOLD, 45) // The end
            minimumStructureLevels.put(
                Structure.BASTION_REMNANT,
                40
            ) // Piglins in bastions are meant to be terrifying to make netherite scary to obtain
            minimumStructureLevels.put(Structure.FORTRESS, 25) // If the nether is 25, these should be ~10ish above
            minimumStructureLevels.put(Structure.TRIAL_CHAMBERS, 30) // Trial chambers are endgame-ish
            minimumStructureLevels.put(Structure.MANSION, 25) // Pillagers are 15-25
            minimumStructureLevels.put(Structure.PILLAGER_OUTPOST, 20) // Pillagers are 15-25
            minimumStructureLevels.put(Structure.MONUMENT, 18) // Midgame boss
            minimumStructureLevels.put(Structure.MINESHAFT, 10) // Early game structure
            minimumStructureLevels.put(Structure.MINESHAFT_MESA, 10) // Early game structure
            minimumStructureLevels.put(Structure.VILLAGE_DESERT, 15) // Early game structure
            minimumStructureLevels.put(Structure.VILLAGE_PLAINS, 15) // Early game structure
            minimumStructureLevels.put(Structure.VILLAGE_SAVANNA, 15) // Early game structure
            minimumStructureLevels.put(Structure.VILLAGE_SNOWY, 15) // Early game structure
            minimumStructureLevels.put(Structure.VILLAGE_TAIGA, 15) // Early game structure
            minimumStructureLevels.put(Structure.DESERT_PYRAMID, 10) // Early game structure
            minimumStructureLevels.put(Structure.IGLOO, 10) // Early game structure
            minimumStructureLevels.put(Structure.JUNGLE_PYRAMID, 10) // Early game structure
            minimumStructureLevels.put(Structure.SWAMP_HUT, 10) // Early game structure
        }

        /**
         * Given a generated structure, determine the minimum level of entities within
         *
         * @param structure A generated structure from World#getStructures()
         * @return a number from 1-100 representing minimum level for entities. If -1 (or lower) this entity doesn't affect
         * entity levels
         */
        fun getMinimumEntityLevel(structure: GeneratedStructure): Int {
            return minimumStructureLevels.getOrDefault(structure.structure, -1)!!
        }
    }
}
