package xyz.devvydont.smprpg.block.behaviors

import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior
import net.momirealms.craftengine.core.block.BlockDefinition
import net.momirealms.craftengine.core.block.behavior.BlockBehavior
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory
import net.momirealms.craftengine.core.plugin.config.ConfigSection
import net.momirealms.craftengine.core.world.BlockPos
import org.bukkit.*
import org.bukkit.block.data.type.BubbleColumn
import org.bukkit.craftbukkit.entity.CraftPlayer
import xyz.devvydont.smprpg.block.CraftEngineBlockEnums
import xyz.devvydont.smprpg.util.persistence.KeyStore
import kotlin.random.Random

class LaunchBlockBehavior(blockDefinition: BlockDefinition,
                          val launchPower : Float,
                          val bubbleBoosted : Boolean,
                          val wantParticles : Boolean) : BukkitBlockBehavior(blockDefinition) {

    override fun stepOn(thisBlock: Any?, args: Array<out Any?>?) {
        super.stepOn(thisBlock, args)

        val nmsBlockPos = args?.get(1)!! as net.minecraft.core.BlockPos
        val nmsEntity = args[3]!! as net.minecraft.world.entity.Entity

        val blockPos = BlockPos.of(nmsBlockPos.asLong())
        val entity = nmsEntity.bukkitEntity

        if (entity is CraftPlayer) {
            if (blockDefinition.id().equals(CraftEngineBlockEnums.BLUE_AERCLOUD.key)) {
                val advancement = Bukkit.getAdvancement(NamespacedKey("smprpg", "aether/bounce_blue_aercloud"))!!
                val progress = entity.getAdvancementProgress(advancement)
                progress.awardCriteria("stand_on_aercloud")
            }
        }

        val world = entity.world

        val blockLoc = Location(entity.world,
            blockPos.x.toDouble(),
            blockPos.y.toDouble(),
            blockPos.z.toDouble())

        val blockBelowLoc = blockLoc.clone()
        blockBelowLoc.y = blockBelowLoc.y - 1

        val newVel = entity.velocity.clone()
        var upwardsVel = launchPower
        newVel.y = upwardsVel.toDouble()

        var blockBelowCloud = world.getBlockAt(blockBelowLoc).state
        val blockData = blockBelowCloud.blockData
        if (bubbleBoosted) {
            if (blockBelowCloud.type == Material.BUBBLE_COLUMN) {
                val column = blockData as BubbleColumn
                if (column.isDrag)
                    upwardsVel = launchPower / 2
                else
                    upwardsVel = launchPower * 1.5f
                newVel.y = upwardsVel.toDouble()
            }
        }
        if (wantParticles) {
            for (count in 0..50) {
                val pos = blockLoc
                val xOffset = pos.x + Random.nextDouble()
                val yOffset = pos.y + Random.nextDouble()
                val zOffset = pos.z + Random.nextDouble()
                world.spawnParticle(Particle.SPLASH, xOffset, yOffset, zOffset, 50)
            }
        }
        world.playSound(
            blockLoc,
            KeyStore.AUDIO_BLUE_AERCOULD_BOUNCE.toString(),
            1f,
            (upwardsVel / 2.0).toFloat()
        )
        entity.velocity = newVel
    }

    override fun isPathFindable(thisBlock: Any?, args: Array<out Any?>?): Boolean {
        return false
    }

    companion object {
        val FACTORY = Factory()

        class Factory : BlockBehaviorFactory<BlockBehavior> {
            override fun create(block: BlockDefinition, section: ConfigSection): LaunchBlockBehavior {
                val launchPower : Float = section.getFloat("launch-power")
                val bubbleBoosted : Boolean = section.getBoolean("bubble-boosted", false)
                val wantParticles : Boolean = section.getBoolean("want-particles", false)
                return LaunchBlockBehavior(block, launchPower, bubbleBoosted, wantParticles)
            }
        }
    }
}