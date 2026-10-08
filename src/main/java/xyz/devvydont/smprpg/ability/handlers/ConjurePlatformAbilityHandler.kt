package xyz.devvydont.smprpg.ability.handlers

import net.momirealms.craftengine.bukkit.api.BukkitAdaptor
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks
import net.momirealms.craftengine.core.util.Key
import org.bukkit.Effect
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import xyz.devvydont.smprpg.ability.AbilityContext
import xyz.devvydont.smprpg.ability.AbilityHandler
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.time.TickTime

class ConjurePlatformAbilityHandler : AbilityHandler {

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
        val blockBelow = player.world.getBlockAt(player.location.x.toInt(), player.location.y.toInt() - 1, player.location.z.toInt())
        val ceBlock = BukkitAdaptor.adapt(blockBelow)
        if (!ceBlock.block().isReplaceable || ceBlock.block().isSolid) {
            player.sendMessage(ComponentUtils.error("You are already standing on a solid block!"))
            player.playSound(player.location, Sound.ENTITY_VILLAGER_NO, 1f, 1f)
            return false
        }

        for (x in -RADIUS..RADIUS) {
            for (z in -RADIUS..RADIUS) {
                val blockAt = BukkitAdaptor.adapt(player.world.getBlockAt(blockBelow.x + x, blockBelow.y, blockBelow.z + z))
                if (blockAt.block().isReplaceable) {
                    val blockLoc = Location(player.world, blockAt.position().x, blockAt.position().y, blockAt.position().z)
                    CraftEngineBlocks.place(blockLoc, Key.of("smprpg:conjure_block"), false)
                }
            }
        }
        player.velocity = Vector(0, 0, 0)
        val teleportLoc = blockBelow.location.clone()
        teleportLoc.y += 1
        teleportLoc.x += 0.5
        teleportLoc.z += 0.5
        teleportLoc.yaw = player.yaw
        teleportLoc.pitch = player.pitch
        if (player.isGliding) player.isGliding = false
        player.teleport(teleportLoc)
        player.playSound(blockBelow.location, Sound.BLOCK_END_PORTAL_FRAME_FILL, 0.5f, 1f)
        player.playSound(blockBelow.location, Sound.BLOCK_END_PORTAL_FRAME_FILL, 0.5f, 1.25f)
        player.playSound(blockBelow.location, Sound.BLOCK_END_PORTAL_FRAME_FILL, 0.5f, 1.5f)
        player.playSound(blockBelow.location, Sound.BLOCK_END_PORTAL_FRAME_FILL, 0.5f, 1.75f)
        player.playSound(blockBelow.location, Sound.BLOCK_END_PORTAL_FRAME_FILL, 0.5f, 2.0f)
        return true
    }

    companion object {
        val COOLDOWN: Long = TickTime.seconds(20)
        const val RADIUS: Int = 2
    }
}
