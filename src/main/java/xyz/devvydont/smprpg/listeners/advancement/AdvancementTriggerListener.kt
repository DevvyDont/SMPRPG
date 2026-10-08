package xyz.devvydont.smprpg.listeners.advancement

import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.block.CraftEngineBlockEnums
import xyz.devvydont.smprpg.events.ContainerLootGeneratedEvent
import xyz.devvydont.smprpg.util.listeners.ToggleableListener
import xyz.devvydont.smprpg.util.persistence.KeyStore

class AdvancementTriggerListener : ToggleableListener() {
    /**
     * Listens to plugin-driven events to grant advancement criterion
     * The vanilla system is robust, but does not have enough
     * capability to encompass what we need to do in the plugin
     * without scoreboard hacks.
     */
    @EventHandler
    private fun onBlockBreak(event: BlockBreakEvent) {
        val block = event.block
        val player = event.player
        if (CraftEngineBlocks.isCustomBlock(block)) {
            when (CraftEngineBlocks.getCustomBlockState(block)!!.customBlockState().ownerId()) {
                CraftEngineBlockEnums.GOLDEN_APPLE_CROP.key -> {
                    val advancement = Bukkit.getAdvancement(NamespacedKey(SMPRPG.plugin, "aether/harvest_golden_apple"))!!
                    val progress = player.getAdvancementProgress(advancement)
                    progress.awardCriteria("harvest_golden_apple")
                }
            }
        }
    }

    @EventHandler
    private fun handleAetherAdvancementSounds(event: PlayerAdvancementDoneEvent) {
        val advancement = event.advancement
        if (advancement.display != null) {
            if (!advancement.display!!.doesAnnounceToChat()) // Only play sounds for achievements that get announced
                return
        }
        else return  // Don't play sound if display is null (Not an acheivement we are tracking for sounds)

        val key = advancement.key
        val keyComps = key.key.split("/")
        val player = event.player
        if (keyComps[0].equals("aether")) {
            player.playSound(player.location, KeyStore.AUDIO_AETHER_ADVANCEMENT_GENERAL.toString(), 1f, 1f)
        }
    }

    /**
     * This is done to integrate advancements into our regenerating
     * loot system, as vanilla loot generation does not occur.
     */
    @EventHandler
    private fun lootGeneratedHack(event: ContainerLootGeneratedEvent) {
        val player = event.player
        when (event.lootTable.key.toString()) {
            "minecraft:chests/nether/bastion_bridge", "minecraft:chests/nether/bastion_hoglin_stable",
            "minecraft:chests/nether/bastion_other", "minecraft:chests/nether/bastion_treasure" -> grantSimpleAdvancement(player,
                NamespacedKey("minecraft", "nether/loot_bastion"),
                "loot_bastion_treasure")
            "smprpg:chests/aether/crystal_tree" -> grantSimpleAdvancement(player,
                NamespacedKey(SMPRPG.plugin, "aether/loot_crystal_island"),
                "loot_crystal_island")
        }
    }

    companion object {
        fun grantSimpleAdvancement(player: Player, advancementKey: NamespacedKey, criteria: String) {
            player.getAdvancementProgress(
                Bukkit.getAdvancement(advancementKey)!!)
                .awardCriteria(criteria)
        }
    }
}