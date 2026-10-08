package xyz.devvydont.smprpg.block.behaviors

import net.minecraft.server.level.ServerLevel
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior
import net.momirealms.craftengine.bukkit.util.LocationUtils
import net.momirealms.craftengine.bukkit.world.BukkitWorldManager
import net.momirealms.craftengine.core.block.BlockDefinition
import net.momirealms.craftengine.core.block.ImmutableBlockState
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory
import net.momirealms.craftengine.core.block.behavior.EntityBlock
import net.momirealms.craftengine.core.block.entity.BlockEntity
import net.momirealms.craftengine.core.block.entity.BlockEntityController
import net.momirealms.craftengine.core.entity.player.InteractionResult
import net.momirealms.craftengine.core.plugin.config.ConfigSection
import net.momirealms.craftengine.core.world.context.UseOnContext
import org.bukkit.Bukkit
import org.bukkit.World
import xyz.devvydont.smprpg.block.entity.renderers.ReforgeTableBlockEntityController
import xyz.devvydont.smprpg.gui.items.MenuReforge

class ReforgeTableBehavior(blockDefinition: BlockDefinition) : BukkitBlockBehavior(blockDefinition), EntityBlock {

    private var controllerId: Int = -1

    override fun useWithoutItem(context: UseOnContext, state: ImmutableBlockState): InteractionResult {
        if (context.player != null) {
            val player = Bukkit.getServer().getPlayer(context.player!!.uuid())
            if (player == null) return super.useWithoutItem(context, state)

            player.swingMainHand()
            MenuReforge(player).openMenu()
            return InteractionResult.SUCCESS
        }
        return super.useWithoutItem(context, state)
    }

    override fun createBlockEntityController(blockEntity: BlockEntity): BlockEntityController? {
        return ReforgeTableBlockEntityController(blockEntity)
    }

    override fun tick(thisBlock: Any?, args: Array<Any?>) {
        val world = args[1] as ServerLevel
        val blockPos = args[2]
        val pos = LocationUtils.fromBlockPos(blockPos)
        val bukkitWorld: World = world.world
        val ceWorld = BukkitWorldManager.instance().getWorld(bukkitWorld.uid)
        val blockEntity = ceWorld.getBlockEntityAtIfLoaded(pos)
        if (blockEntity == null) return
        blockEntity.controller.let<ReforgeTableBlockEntityController>(
            ReforgeTableBlockEntityController::class.java,
            this.controllerId,
            {})
    }

    override fun initControllerId(id: Int) {
        controllerId = id
    }

    companion object {
        val FACTORY = Factory()

        class Factory : BlockBehaviorFactory<ReforgeTableBehavior> {
            override fun create(block: BlockDefinition, section: ConfigSection): ReforgeTableBehavior {
                return ReforgeTableBehavior(block)
            }
        }
    }
}