package xyz.devvydont.smprpg.block.behaviors

import net.minecraft.world.entity.Entity
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior
import net.momirealms.craftengine.core.block.BlockDefinition
import net.momirealms.craftengine.core.block.behavior.BlockBehavior
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory
import net.momirealms.craftengine.core.plugin.config.ConfigSection
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.FishHook
import org.bukkit.entity.Item
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.util.Vector
import xyz.devvydont.smprpg.listeners.entity.PlayerInputListener

class ZeroGravityBlockBehavior(blockDefinition: BlockDefinition): BukkitBlockBehavior(blockDefinition) {

    override fun entityInside(thisBlock: Any, args: Array<out Any>) {
        val entity = (args[3] as Entity).bukkitEntity
        val newVelocity = Vector().zero()
        if (entity is LivingEntity) {
            entity.fallDistance = 0.0f
            if (entity is Player) {
                val input = PlayerInputListener.getPlayerInput(entity)
                if (input != null) {
                    val lookDir = entity.location.direction
                    if (input.isForward) newVelocity.add(lookDir.multiply(0.21585)) // Player move speed is 4.317 blocks/sec, divide by 20 for blocks/t
                    else if (input.isBackward) newVelocity.add(lookDir.multiply(-0.21585))

                    if (input.isLeft) newVelocity.add(lookDir.crossProduct(Vector(0, 1, 0)).normalize().multiply(-0.21585))
                    else if (input.isRight) newVelocity.add(lookDir.crossProduct(Vector(0, 1, 0)).normalize().multiply(0.21585))


                    if (input.isJump) newVelocity.y = 0.2
                    else if (input.isSneak) newVelocity.y = -0.2
                    else newVelocity.y = -0.005
                }
                else newVelocity.y = -0.005
            }
        }
        val canMove = !(entity is ArmorStand || entity is Projectile || entity is Item)
        if (canMove)
            entity.velocity = newVelocity
        super.entityInside(thisBlock, args)
    }

    companion object {
        val FACTORY = Factory()

        class Factory : BlockBehaviorFactory<BlockBehavior> {
            override fun create(block: BlockDefinition, section: ConfigSection): ZeroGravityBlockBehavior {
                return ZeroGravityBlockBehavior(block)
            }
        }
    }
}