package xyz.devvydont.smprpg.ability.handlers

import net.momirealms.craftengine.bukkit.api.BukkitAdaptor
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks
import net.momirealms.craftengine.bukkit.world.BukkitExistingBlock
import net.momirealms.craftengine.core.util.Key
import org.bukkit.Effect
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import xyz.devvydont.smprpg.ability.AbilityContext
import xyz.devvydont.smprpg.ability.AbilityHandler
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.time.TickTime

class ConjureWallAbilityHandler : AbilityHandler {

    override val cooldown: Long
        get() = COOLDOWN

    /**
     * Attempts to execute the ability.
     *
     * @param ctx The context of the ability.
     * @return True if the ability succeeded and should have cost reduced, false otherwise.
     */
    override fun execute(ctx: AbilityContext): Boolean {

        val player = ctx.caster as Player
        val lookMod = when (player.facing) {
            BlockFace.SOUTH -> Vector(0, 0, 2)
            BlockFace.EAST -> Vector(2, 0, 0)
            BlockFace.WEST -> Vector(-2, 0, 0)
            else -> Vector(0, 0, -2)
        }
        val locationInFront = player.world.getBlockAt(player.location).location.clone().add(lookMod)
        val blockInFront = player.world.getBlockAt(locationInFront)

        var numReplaced = 0
        var blockAt: BukkitExistingBlock
        for (h in -RADIUS..RADIUS) {
            for (v in 0..RADIUS * 2) {
                when (player.facing) {
                    BlockFace.EAST, BlockFace.WEST -> { blockAt = BukkitAdaptor.adapt(player.world.getBlockAt(blockInFront.x, blockInFront.y + v, blockInFront.z + h)) }
                    else -> { blockAt = BukkitAdaptor.adapt(player.world.getBlockAt(blockInFront.x + h, blockInFront.y + v, blockInFront.z)) }
                }

                if (blockAt.block().isReplaceable) {
                    val blockLoc = Location(player.world, blockAt.position().x, blockAt.position().y, blockAt.position().z)
                    CraftEngineBlocks.place(blockLoc, Key.of("smprpg:conjure_block"), false)
                    numReplaced++
                }
            }
        }

        if (numReplaced == 0) {
            player.sendMessage(ComponentUtils.error("You cannot place a wall here!"))
            player.playSound(player.location, Sound.ENTITY_VILLAGER_NO, 1f, 1f)
            return false
        }

        player.playSound(blockInFront.location, Sound.BLOCK_END_PORTAL_FRAME_FILL, 0.5f, 1f)
        player.playSound(blockInFront.location, Sound.BLOCK_END_PORTAL_FRAME_FILL, 0.5f, 1.25f)
        player.playSound(blockInFront.location, Sound.BLOCK_END_PORTAL_FRAME_FILL, 0.5f, 1.5f)
        player.playSound(blockInFront.location, Sound.BLOCK_END_PORTAL_FRAME_FILL, 0.5f, 1.75f)
        player.playSound(blockInFront.location, Sound.BLOCK_END_PORTAL_FRAME_FILL, 0.5f, 2.0f)
        return true
    }

    companion object {
        val COOLDOWN: Long = TickTime.seconds(20)
        const val RADIUS: Int = 2
    }
}
