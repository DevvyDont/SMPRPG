package xyz.devvydont.smprpg.block.behaviors

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ServerLevelAccessor
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior
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
import net.momirealms.craftengine.core.world.CEWorld
import net.momirealms.craftengine.core.world.context.UseOnContext
import org.bukkit.Location
import org.bukkit.World
import xyz.devvydont.smprpg.block.entity.FreezerBlockEntityController
import xyz.devvydont.smprpg.util.formatting.Symbols

class FreezerBlockBehavior(blockDefinition: BlockDefinition) : BukkitBlockBehavior(blockDefinition), EntityBlock, WorldlyContainerHolder {

    private var controllerId: Int = -1
    val customDataKey = "smprpg:freezer"
    val containerTitle = "<white>${Symbols.OFFSET_NEG_1 + Symbols.FREEZER_MENU}</white>${Symbols.OVERLAY_BG_OFFSET_STANDARD + Symbols.OFFSET_NEG_8 + Symbols.OFFSET_NEG_3}<#3f3f3f>                   Freezer</#3f3f3f>"

    override fun createBlockEntityController(blockEntity: BlockEntity): BlockEntityController? {
        return FreezerBlockEntityController(blockEntity, this)
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
        blockEntity.controller.let<FreezerBlockEntityController?>(
            FreezerBlockEntityController::class.java,
            this.controllerId,
            { c: FreezerBlockEntityController ->
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
        blockEntity.controller.let<FreezerBlockEntityController>(
            FreezerBlockEntityController::class.java,
            this.controllerId,
            { c: FreezerBlockEntityController ->
                c.checkOpeners(world, blockPos, args[0])
            })
    }

    override fun getContainer(thisBlock: Any, args: Array<Any?>): Any? {
        val levelAccessor = args[1] as ServerLevelAccessor
        val ceWorld: CEWorld = BukkitWorldManager.instance().getWorld(levelAccessor.level.world)
        val blockPos = LocationUtils.fromBlockPos(args[2])
        val blockEntity = ceWorld.getBlockEntityAtIfLoaded(blockPos)
        if (blockEntity == null) return null
        return blockEntity.controller.let<FreezerBlockEntityController, Any?>(
            FreezerBlockEntityController::class.java,
            this.controllerId,
            { c: FreezerBlockEntityController -> c.inventory() })
    }

    companion object {
        val FACTORY = Factory()

        class Factory : BlockBehaviorFactory<FreezerBlockBehavior?> {
            override fun create(block: BlockDefinition, section: ConfigSection): FreezerBlockBehavior {
                return FreezerBlockBehavior(block)
            }
        }
    }
}