package xyz.devvydont.smprpg.block.behaviors

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.momirealms.craftengine.bukkit.api.BukkitAdaptor
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior
import net.momirealms.craftengine.bukkit.block.entity.SimpleStorageBlockEntityController
import net.momirealms.craftengine.bukkit.plugin.gui.BukkitInventory
import net.momirealms.craftengine.bukkit.util.LocationUtils
import net.momirealms.craftengine.bukkit.world.BukkitWorldManager
import net.momirealms.craftengine.core.block.BlockDefinition
import net.momirealms.craftengine.core.block.ImmutableBlockState
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory
import net.momirealms.craftengine.core.block.behavior.EntityBlock
import net.momirealms.craftengine.core.block.behavior.WorldlyContainerHolder
import net.momirealms.craftengine.core.block.entity.BlockEntity
import net.momirealms.craftengine.core.block.entity.BlockEntityController
import net.momirealms.craftengine.core.entity.player.InteractionHand
import net.momirealms.craftengine.core.entity.player.InteractionResult
import net.momirealms.craftengine.core.plugin.config.ConfigSection
import net.momirealms.craftengine.core.plugin.context.PlayerOptionalContext
import net.momirealms.craftengine.core.util.AdventureHelper
import net.momirealms.craftengine.core.world.BlockPos
import net.momirealms.craftengine.core.world.CEWorld
import net.momirealms.craftengine.core.world.context.UseOnContext
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.craftbukkit.block.CraftBlock
import xyz.devvydont.smprpg.block.entity.CookingPotBlockEntityController
import xyz.devvydont.smprpg.util.formatting.Symbols
import java.util.function.Consumer

class CookingPotBlockBehavior(
    blockDefinition: BlockDefinition
) : BukkitBlockBehavior(blockDefinition), EntityBlock, WorldlyContainerHolder {

    private var controllerId: Int = -1
    val customDataKey = "smprpg:cooking_pot"
    val containerTitle = "<white>${Symbols.OFFSET_NEG_1 + Symbols.COOKING_POT_MENU}</white>${Symbols.OVERLAY_BG_OFFSET_STANDARD + Symbols.OFFSET_NEG_5 + Symbols.OFFSET_NEG_3}<#3f3f3f>               Cooking Pot</#3f3f3f>"

    override fun createBlockEntityController(blockEntity: BlockEntity): BlockEntityController? {
        return CookingPotBlockEntityController(blockEntity, this)
    }

    override fun initControllerId(id: Int) {
        controllerId = id
    }

    override fun useWithoutItem(context: UseOnContext, state: ImmutableBlockState?): InteractionResult {
        val world = context.getLevel().storageWorld()
        val player = context.getPlayer()
        if (player == null) {
            return InteractionResult.SUCCESS_AND_CANCEL
        }
        val blockPos = context.getClickedPos()
        val bukkitWorld = context.getLevel().platformWorld() as World?
        val location = Location(bukkitWorld, blockPos.x().toDouble(), blockPos.y().toDouble(), blockPos.z().toDouble())
        val bukkitPlayer = player.platformPlayer() as org.bukkit.entity.Player?
        //if (!BukkitCraftEngine.instance().antiGriefProvider().test(bukkitPlayer, Flag.OPEN_CONTAINER, location)) {
        //    return InteractionResult.SUCCESS_AND_CANCEL
        //}
        val blockEntity = world.getBlockEntityAtIfLoaded(blockPos)
        if (blockEntity == null) return InteractionResult.FAIL
        blockEntity.controller.let<CookingPotBlockEntityController?>(
            CookingPotBlockEntityController::class.java,
            this.controllerId,
            { c: CookingPotBlockEntityController ->
                c.onPlayerOpen(player)
                BukkitInventory(c.inventory()).open(
                    player,
                    AdventureHelper.miniMessage().deserialize(this.containerTitle, *PlayerOptionalContext.of(player).tagResolvers())
                )
            })
        player.swingHand(InteractionHand.MAIN_HAND)
        return InteractionResult.SUCCESS_AND_CANCEL
    }

    override fun tick(thisBlock: Any?, args: Array<Any?>) {
        val world = args[1] as ServerLevel
        val blockPos = args[2]
        val pos = LocationUtils.fromBlockPos(blockPos)
        val bukkitWorld: World = world.world
        val ceWorld = BukkitWorldManager.instance().getWorld(bukkitWorld.uid)
        val blockEntity = ceWorld.getBlockEntityAtIfLoaded(pos)
        if (blockEntity == null) return
        blockEntity.controller.let<CookingPotBlockEntityController>(
            CookingPotBlockEntityController::class.java,
            this.controllerId,
            { c: CookingPotBlockEntityController ->
                c.checkOpeners(world, blockPos, args[0])
            })
    }

    override fun getContainer(thisBlock: Any, args: Array<Any?>): Any? {
        val levelAccessor = args[1] as ServerLevelAccessor
        val ceWorld: CEWorld = BukkitWorldManager.instance().getWorld(levelAccessor.level.world)
        val blockPos = LocationUtils.fromBlockPos(args[2])
        val blockEntity = ceWorld.getBlockEntityAtIfLoaded(blockPos)
        if (blockEntity == null) return null
        return blockEntity.controller.let<CookingPotBlockEntityController, Any?>(
            CookingPotBlockEntityController::class.java,
            this.controllerId,
            { c: CookingPotBlockEntityController -> c.inventory() })
    }

    override fun neighborChanged(thisBlock: Any?, args: Array<out Any?>?) {
        super.neighborChanged(thisBlock, args)
        val level = args!![1] as Level
        val pos = args[2] as net.minecraft.core.BlockPos
        val belowPos = pos.below()
        val belowBlock = CraftBlock.at(level, belowPos)
        val ceBlock = BukkitAdaptor.adapt(belowBlock)
        var entityAt = ceBlock.world().storageWorld().getBlockEntityAtIfLoaded(BlockPos(pos.x, pos.y, pos.z))
        if (entityAt != null) {
            (entityAt.controller as CookingPotBlockEntityController).updateHeated()
        }
    }

    companion object {
        val FACTORY = Factory()

        class Factory : BlockBehaviorFactory<CookingPotBlockBehavior?> {
            override fun create(block: BlockDefinition, section: ConfigSection): CookingPotBlockBehavior {
                return CookingPotBlockBehavior(
                    block
                )
            }
        }
    }
}