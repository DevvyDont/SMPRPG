package xyz.devvydont.smprpg.block.behaviors

import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior
import net.momirealms.craftengine.bukkit.util.EventUtils
import net.momirealms.craftengine.bukkit.util.ItemTags
import net.momirealms.craftengine.bukkit.world.BukkitExistingBlock
import net.momirealms.craftengine.core.block.BlockDefinition
import net.momirealms.craftengine.core.block.ImmutableBlockState
import net.momirealms.craftengine.core.block.behavior.BlockBehavior
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory
import net.momirealms.craftengine.core.entity.EquipmentSlot
import net.momirealms.craftengine.core.entity.player.InteractionHand
import net.momirealms.craftengine.core.entity.player.InteractionResult
import net.momirealms.craftengine.core.plugin.config.ConfigSection
import net.momirealms.craftengine.core.util.Key
import net.momirealms.craftengine.core.world.Vec3d
import net.momirealms.craftengine.core.world.context.UseOnContext
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityChangeBlockEvent
import xyz.devvydont.smprpg.util.craftengine.CraftEngineHelpers

class TillableBlockBehavior(blockDefinition: BlockDefinition,
                            val tilledBlockKey: Key): BukkitBlockBehavior(blockDefinition) {

    override fun useOnBlock(context: UseOnContext, state: ImmutableBlockState): InteractionResult {
        val item = context.item
        if (item.isEmpty) return super.useOnBlock(context, state)
        if (item.hasVanillaTag(Key.of("minecraft:hoes"))) {
            // Till our block
            val clickedBlock = context.level.getBlock(context.clickedPos) as BukkitExistingBlock
            var bukkitPlayer: Player? = null
            val player: net.momirealms.craftengine.core.entity.player.Player? = context.player
            if (player != null) {
                bukkitPlayer = context.player!!.platformPlayer() as Player
                val event = EntityChangeBlockEvent(bukkitPlayer, clickedBlock.block(), clickedBlock.block().blockData)
                if (EventUtils.fireAndCheckCancel(event)) return InteractionResult.FAIL
            }

            // Damage our hoe
            val pos = context.clickedPos
            context.level.playBlockSound(Vec3d.atCenterOf(pos), TILL_BLOCK_SOUND, 1f, 1f)
            CraftEngineBlocks.place(CraftEngineHelpers.getLocationFromBlockPos(pos, (context.level.platformWorld() as World)),
                                    tilledBlockKey,
                                    false)
            if (bukkitPlayer != null) {
                player!!.swingHand(context.hand)
            }
            val slot = if (context.hand == InteractionHand.MAIN_HAND) EquipmentSlot.MAINHAND else EquipmentSlot.OFFHAND
            if (player != null)
                item.hurtAndBreak(1, player, slot)
            return InteractionResult.SUCCESS_AND_CANCEL
        }
        return super.useOnBlock(context, state)
    }

    companion object {
        val TILL_BLOCK_SOUND = Key.of("minecraft:item.hoe.till")
        val FACTORY = Factory()

        class Factory : BlockBehaviorFactory<BlockBehavior> {
            override fun create(block: BlockDefinition, section: ConfigSection): TillableBlockBehavior {
                val tilledBlock = Key.of(
                    section.getNonNullString("tilled-block")
                )
                return TillableBlockBehavior(block, tilledBlock)
            }
        }
    }
}