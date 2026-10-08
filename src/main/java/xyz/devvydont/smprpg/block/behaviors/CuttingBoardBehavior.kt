package xyz.devvydont.smprpg.block.behaviors

import net.momirealms.craftengine.bukkit.api.BukkitAdaptor
import net.momirealms.craftengine.bukkit.block.behavior.BukkitBlockBehavior
import net.momirealms.craftengine.bukkit.item.BukkitItemManager
import net.momirealms.craftengine.bukkit.util.ItemStackUtils
import net.momirealms.craftengine.core.block.BlockDefinition
import net.momirealms.craftengine.core.block.ImmutableBlockState
import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory
import net.momirealms.craftengine.core.block.behavior.EntityBlock
import net.momirealms.craftengine.core.block.entity.BlockEntity
import net.momirealms.craftengine.core.block.entity.BlockEntityController
import net.momirealms.craftengine.core.entity.EquipmentSlot
import net.momirealms.craftengine.core.entity.player.GameMode
import net.momirealms.craftengine.core.entity.player.InteractionHand
import net.momirealms.craftengine.core.entity.player.InteractionResult
import net.momirealms.craftengine.core.entity.player.Player
import net.momirealms.craftengine.core.item.Item
import net.momirealms.craftengine.core.plugin.config.ConfigSection
import net.momirealms.craftengine.core.sound.SoundData
import net.momirealms.craftengine.core.util.Key
import net.momirealms.craftengine.core.world.Vec3d
import net.momirealms.craftengine.core.world.context.UseOnContext
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.World
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.block.entity.CuttingBoardBlockEntityController
import xyz.devvydont.smprpg.items.interfaces.IKnife
import xyz.devvydont.smprpg.recipe.core.CuttingBoardRecipe
import xyz.devvydont.smprpg.recipe.core.RecipeStationType
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.services.RecipeService
import xyz.devvydont.smprpg.util.persistence.KeyStore
import kotlin.random.Random

class CuttingBoardBehavior(blockDefinition: BlockDefinition) : BukkitBlockBehavior(blockDefinition), EntityBlock {

    private var controllerId: Int = -1

    override fun useOnBlock(context: UseOnContext, state: ImmutableBlockState): InteractionResult {
        var blockEntity = context.world.storageWorld().getBlockEntityAtIfLoaded(context.clickedPos)
        if (blockEntity == null) return super.useOnBlock(context, state)
        val boardEntity = blockEntity.controller as CuttingBoardBlockEntityController

        val item = context.item
        val itemStack = ItemStackUtils.getBukkitStack(item)
        val isCreative = context.player?.gameMode() != GameMode.CREATIVE

        // Only allow one item to be sent over to the cutting board.

        // Nothing on the board currently, we can try to place an item
        val hand = context.hand
        val offhandItem = context.player?.getItemInHand(InteractionHand.OFF_HAND)
        // Try to place items in offhand on the board first, then we can place main hand items.
        if (hand == InteractionHand.OFF_HAND) {
            if (boardEntity.item.isEmpty) {
                placeItemOnBoard(itemStack, boardEntity, isCreative)
                return InteractionResult.SUCCESS
            }
        }
        // There is no offhand item, so let's place our main hand item instead
        if (hand == InteractionHand.MAIN_HAND) {
            var noItem = offhandItem == null
            if (!noItem)
                noItem = offhandItem!!.isEmpty
            if (noItem) {
                if (boardEntity.item.isEmpty) {
                    placeItemOnBoard(itemStack, boardEntity, isCreative)
                    return InteractionResult.SUCCESS
                } else if (item.isEmpty)  // Take back our item if we have an empty main hand
                {
                    val dropPos = Vec3d.atCenterOf(blockEntity.pos)
                    blockEntity.world.world().dropItemNaturally(dropPos, BukkitAdaptor.adapt(boardEntity.item))
                    boardEntity.setItemStack(ItemStack.empty())
                    context.player?.swingHand(InteractionHand.MAIN_HAND)
                    val bukkitWorld = blockEntity.world.world.platformWorld() as World
                    bukkitWorld.playSound(
                        Location(
                            bukkitWorld,
                            blockEntity.pos.x.toDouble(),
                            blockEntity.pos.y.toDouble(),
                            blockEntity.pos.z.toDouble()
                        ),
                        Sound.ENTITY_ITEM_FRAME_PLACE,
                        1.0f,
                        1.5f
                    )
                }
            }

            // There's an item on the board, so let's try to process it with our mainhand item
            processItem(item, boardEntity, context.player!!)
        }

        return super.useOnBlock(context, state)
    }

    fun placeItemOnBoard(item: ItemStack, board: CuttingBoardBlockEntityController, takeItem: Boolean) {
        val placementStack = item.clone()
        placementStack.amount = 1
        if (takeItem)
            item.amount--
        board.setItemStack(placementStack)
        if (!placementStack.isEmpty) {
            val bukkitWorld = board.blockEntity.world.world().platformWorld() as World
            bukkitWorld.playSound(
                Location(
                    bukkitWorld,
                    board.blockEntity.pos.x.toDouble(),
                    board.blockEntity.pos.y.toDouble(),
                    board.blockEntity.pos.z.toDouble()
                ),
                Sound.BLOCK_WOOD_PLACE,
                1.0f,
                1.0f
            )
        }
    }

    fun placeItemOnBoard(item: ItemStack, board: CuttingBoardBlockEntityController) {
        placeItemOnBoard(item, board, false)
    }

    fun processItem(toolToProcess: Item, board: CuttingBoardBlockEntityController, player: Player) : Boolean {
        val damage = toolToProcess.damage()
        val maxDamage = toolToProcess.maxDamage() - 1

        if (!damage.isEmpty) {
            if (damage.get() >= maxDamage)
                return false
        }
        val dropPos = Vec3d.atCenterOf(board.blockEntity.pos)
        for (recipe in cuttingBoardRecipes()) {
            if (recipe.input.matchesType(board.item)) {
                // TODO: tool tags are not wired up yet, so every cutting board recipe currently requires a
                // knife (an IKnife blueprint). Once CraftEngine tag checks work, honor recipe.tool instead.
                val bukkitItem = ItemStackUtils.getBukkitStack(toolToProcess)
                if (ItemService.blueprint(bukkitItem) is IKnife) {
                    for (output in recipe.results) {
                        if (Random.nextDouble() <= output.chance) {
                            val itemStack = output.generate() ?: continue
                            board.blockEntity.world.world
                                .dropItemNaturally(
                                    dropPos,
                                    BukkitItemManager.instance().wrap(itemStack)
                                )
                        }
                    }
                    if (!damage.isEmpty) {
                        toolToProcess.hurtAndBreak(1, player, EquipmentSlot.MAINHAND)
                        player.swingHand(InteractionHand.MAIN_HAND)
                    }
                    board.setItemStack(ItemStack.empty())
                    board.blockEntity.world.world.playBlockSound(dropPos,
                        SoundData(Key.of(KeyStore.AUDIO_CUTTING_BOARD_CUT.toString()),
                            SoundData.SoundValue.FIXED_1,
                            SoundData.SoundValue.FIXED_1))
                    return true
                }
            }
        }
        return false
    }

    /** All cutting board recipes currently in the data-driven registry. */
    private fun cuttingBoardRecipes(): List<CuttingBoardRecipe> =
        SMPRPG.getService(RecipeService::class.java).getRegistry()
            .byStation(RecipeStationType.CUTTING_BOARD)
            .filterIsInstance<CuttingBoardRecipe>()

    override fun createBlockEntityController(blockEntity: BlockEntity): BlockEntityController? {
        return CuttingBoardBlockEntityController(blockEntity, this)
    }

    override fun initControllerId(id: Int) {
        controllerId = id
    }

    companion object {
        val FACTORY = Factory()

        class Factory : BlockBehaviorFactory<CuttingBoardBehavior> {
            override fun create(block: BlockDefinition, section: ConfigSection): CuttingBoardBehavior {
                return CuttingBoardBehavior(block)
            }
        }
    }
}