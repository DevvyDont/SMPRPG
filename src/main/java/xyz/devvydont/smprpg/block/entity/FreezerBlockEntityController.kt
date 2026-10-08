package xyz.devvydont.smprpg.block.entity

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.CustomModelData
import io.papermc.paper.datacomponent.item.TooltipDisplay
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.block.Block
import net.momirealms.craftengine.bukkit.api.BukkitAdaptor
import net.momirealms.craftengine.bukkit.util.BlockStateUtils
import net.momirealms.craftengine.bukkit.util.ItemStackUtils
import net.momirealms.craftengine.bukkit.util.LocationUtils
import net.momirealms.craftengine.bukkit.world.WorldlyContainerHolder
import net.momirealms.craftengine.core.block.ImmutableBlockState
import net.momirealms.craftengine.core.block.entity.BlockEntity
import net.momirealms.craftengine.core.block.entity.BlockEntityController
import net.momirealms.craftengine.core.block.entity.tick.BlockEntityTicker
import net.momirealms.craftengine.core.entity.player.Player
import net.momirealms.craftengine.core.plugin.config.Config
import net.momirealms.craftengine.core.util.VersionHelper
import net.momirealms.craftengine.core.world.BlockPos
import net.momirealms.craftengine.core.world.CEWorld
import net.momirealms.craftengine.core.world.WorldPosition
import net.momirealms.craftengine.libraries.nbt.CompoundTag
import net.momirealms.craftengine.libraries.nbt.ListTag
import org.bukkit.*
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.Vector
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.block.behaviors.FreezerBlockBehavior
import xyz.devvydont.smprpg.recipe.core.FreezerRecipe
import xyz.devvydont.smprpg.recipe.core.RecipeStationType
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.services.RecipeService
import xyz.devvydont.smprpg.util.extensions.transfer
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.time.TickTime
import java.util.*
import java.util.function.Supplier
import kotlin.math.max

class FreezerBlockEntityController(val entity: BlockEntity, val behavior: FreezerBlockBehavior) : BlockEntityController(entity), Listener {

    private var fuelTime = 0    // How many ticks are left of fuel
    private var fuelSupply = 0  // How many ticks the most recent freezer fuel gave for burn
    private var freezeTime = 0  // How long the current item has been freezing for, in ticks
    private var recipeTime = 0  // How long the current item needs to freeze for before being finished.
    private var recipe: FreezerRecipe? = null  // The current working recipe
    private var bordersInitialized = false

    var BORDER: ItemStack? = null

    var maxInteractionDistance: Double = 0.0

    val inventory: Inventory
    val holder: WorldlyContainerHolder = WorldlyContainerHolder(
        { player: Player? -> this.onPlayerClose(player) },
        {
            WorldPosition(
                blockEntity.world.world,
                blockEntity.pos.x + 0.5,
                blockEntity.pos.y + 0.5,
                blockEntity.pos.z + 0.5
            )
        })

    init {
        inventory = Bukkit.createInventory(holder,
            27,
            MiniMessage.miniMessage().deserialize(behavior.containerTitle))

        plugin.server.pluginManager.registerEvents(this, plugin)

        val border = ItemStack(Material.BLACK_STAINED_GLASS_PANE)
        val meta = border.itemMeta
        meta.displayName(ComponentUtils.EMPTY.decoration(TextDecoration.ITALIC, false))
        border.setItemMeta(meta)
        border.setData(DataComponentTypes.ITEM_MODEL, Key.key("smprpg:empty"))
        border.setData(DataComponentTypes.TOOLTIP_DISPLAY,
            TooltipDisplay.tooltipDisplay().hideTooltip(true).build())

        BORDER = border
    }

    override fun <C : BlockEntityController?> createAsyncBlockEntityTicker(level: CEWorld, state: ImmutableBlockState): BlockEntityTicker<C> {
        return createTickerHelper<C, FreezerBlockEntityController>(BlockEntityTicker { ceWorld: CEWorld, blockPos: BlockPos, state: ImmutableBlockState, freezer: FreezerBlockEntityController ->
            tick(
                ceWorld,
                blockPos,
                state,
                freezer
            )
        })
    }

    fun inventory(): Inventory? {
        if (!super.blockEntity.isValid) return null
        return this.inventory
    }

    override fun saveCustomData(tag: CompoundTag) {
        // Before saving, close all player interfaces currently accessing this container.
        this.inventory.close()
        if (!Bukkit.getPluginManager().isPluginEnabled("CraftEngine")) return
        val data = CompoundTag()
        data.putInt("data_version", VersionHelper.WORLD_VERSION)
        data.put("items", ItemStackUtils.saveBukkitItemsAsListTag(this.inventory.storageContents))
    }

    override fun loadCustomData(tag: CompoundTag) {
        // Newer data should be prioritized for reading
        val dataTag = tag.getCompound(behavior.customDataKey)
        if (dataTag != null) {
            val dataVersion = dataTag.getInt("data_version", Config.itemDataFixerUpperFallbackVersion())
            val itemsTag: ListTag = Optional.ofNullable(dataTag.getList("items")).orElseGet(Supplier { ListTag() })
            this.inventory.setStorageContents(
                ItemStackUtils.parseBukkitItems(
                    itemsTag,
                    27,
                    dataVersion
                )
            )
        } else {
            val oldItemsTag: ListTag? = tag.getList("items")
            if (oldItemsTag == null) return
            val itemsTag: ListTag = Optional.ofNullable(tag.getList("items")).orElseGet(Supplier { ListTag() })
            val dataVersion = tag.getInt("data_version", Config.itemDataFixerUpperFallbackVersion())
            this.inventory.setStorageContents(
                ItemStackUtils.parseBukkitItems(
                    itemsTag,
                    27,
                    dataVersion
                )
            )
        }
    }

    fun onPlayerOpen(player: Player) {
        if (!isValidContainer()) return
        if (!player.isSpectatorMode()) {
            if (!hasNoViewer(this.inventory.getViewers())) return
            this.maxInteractionDistance = max(player.getCachedInteractionRange(), this.maxInteractionDistance)
            this.setOpen(player)

            val level = blockEntity.world.world.minecraftWorld() as ServerLevelAccessor
            level.scheduleTick(LocationUtils.toBlockPos(super.blockEntity.pos) as net.minecraft.core.BlockPos,
                BlockStateUtils.getBlockOwner(super.blockEntity.blockState.customBlockState().minecraftState()) as Block,
                5
            )
        }
    }

    fun onPlayerClose(player: Player?) {
        if (player == null || !isValidContainer()) return
        if (!player.isSpectatorMode()) {
            for (viewer in this.inventory.getViewers()) {
                if (viewer.getGameMode() == GameMode.SPECTATOR || viewer === player.platformPlayer()) {
                    continue
                }
                return
            }
            this.maxInteractionDistance = 0.0
            this.setClose(player)
        }
    }

    private fun setOpen(player: Player?) {
        val bukkitWorld = super.blockEntity.world.world().platformWorld() as World
        if (player != null) {
            bukkitWorld.sendGameEvent(
                player.platformPlayer() as org.bukkit.entity.Player?,
                GameEvent.CONTAINER_OPEN,
                Vector(super.blockEntity.pos.x(), super.blockEntity.pos.y(), super.blockEntity.pos.z())
            )
        } else {
            bukkitWorld.sendGameEvent(
                null,
                GameEvent.CONTAINER_OPEN,
                Vector(super.blockEntity.pos.x(), super.blockEntity.pos.y(), super.blockEntity.pos.z())
            )
        }
    }

    private fun setClose(player: Player?) {
        val bukkitWorld = super.blockEntity.world.world().platformWorld() as World
        if (player != null) {
            bukkitWorld.sendGameEvent(
                player.platformPlayer() as org.bukkit.entity.Player?,
                GameEvent.CONTAINER_CLOSE,
                Vector(super.blockEntity.pos.x(), super.blockEntity.pos.y(), super.blockEntity.pos.z())
            )
        } else {
            bukkitWorld.sendGameEvent(
                null,
                GameEvent.CONTAINER_CLOSE,
                Vector(super.blockEntity.pos.x(), super.blockEntity.pos.y(), super.blockEntity.pos.z())
            )
        }
    }

    fun checkOpeners(level: Any?, pos: Any?, blockState: Any?) {
        if (!this.isValidContainer()) return
        var maxInteractionDistance = 0.0
        val viewers = this.inventory.getViewers()
        var validViewers = 0
        for (viewer in viewers) {
            if (viewer is org.bukkit.entity.Player) {
                val serverPlayer = BukkitAdaptor.adapt(viewer)
                if (serverPlayer == null) continue
                maxInteractionDistance = max(serverPlayer.cachedInteractionRange, maxInteractionDistance)
                if (viewer.gameMode != GameMode.SPECTATOR) {
                    validViewers++
                }
            }
        }
        val shouldOpen = validViewers != 0
        if (shouldOpen) {
            this.setOpen(null)
        } else {
            this.setClose(null)
        }

        this.maxInteractionDistance = maxInteractionDistance
        if (!viewers.isEmpty()) {
            (level as ServerLevel).scheduleTick(pos as net.minecraft.core.BlockPos, BlockStateUtils.getBlockOwner(blockState) as Block, 5)
        }
    }

    private fun hasNoViewer(viewers: MutableList<HumanEntity>): Boolean {
        for (viewer in viewers) {
            if (viewer.getGameMode() != GameMode.SPECTATOR) {
                return false
            }
        }
        return true
    }

    private fun isValidContainer(): Boolean {
        return super.blockEntity.isValid
    }

    fun isLit() : Boolean {
        return fuelTime > 0
    }

    companion object {
        const val INGREDIENT_SLOT = 3
        const val FUEL_SLOT = 21
        const val OUTPUT_SLOT = 15
        const val FLAME_SLOT = 12
        const val ARROW_SLOT = 13
        val ALLOWED_SLOTS = listOf(INGREDIENT_SLOT, FUEL_SLOT, OUTPUT_SLOT)

        /** All freezer recipes currently in the data-driven registry. */
        private fun freezerRecipes(): List<FreezerRecipe> =
            SMPRPG.getService(RecipeService::class.java).getRegistry()
                .byStation(RecipeStationType.FREEZER)
                .filterIsInstance<FreezerRecipe>()

        fun tick(ceWorld: CEWorld, blockPos: BlockPos, state: ImmutableBlockState, freezer: FreezerBlockEntityController) {
            val inv = freezer.inventory()!!
            if (!freezer.bordersInitialized) {
                for (i in 0..26) {
                    if (i !in ALLOWED_SLOTS) {
                        inv.setItem(i, freezer.BORDER)
                        if (i == FLAME_SLOT)
                            inv.getItem(i)!!.setData(DataComponentTypes.ITEM_MODEL, NamespacedKey(plugin, "ui/freezer_burn_flame"))
                        if (i == ARROW_SLOT)
                            inv.getItem(i)!!.setData(DataComponentTypes.ITEM_MODEL, NamespacedKey(plugin, "ui/progress_arrow_freezer"))
                    }
                }
                freezer.bordersInitialized = true
            }

            val bukkitWorld = ceWorld.world.platformWorld() as World
            if (freezer.isLit()) {
                freezer.fuelTime--
                bukkitWorld.spawnParticle(
                    Particle.SNOWFLAKE,
                    Location(bukkitWorld, blockPos.x + 0.5, blockPos.y + 1.0, blockPos.z + 0.5),
                    3,
                    0.15,
                    0.0,
                    0.15,
                    0.01)
                if (freezer.fuelTime % 40 == 0) {
                    Bukkit.getScheduler().runTask(plugin, Runnable {
                        bukkitWorld.playSound(Location(bukkitWorld, blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble()),
                            Sound.ENTITY_BREEZE_IDLE_GROUND,
                            0.5f,
                            0.5f)
                    })
                }
            }

            val hasIngredient = inv.getItem(INGREDIENT_SLOT) != null
            val fuel = inv.getItem(FUEL_SLOT)
            var fuelTime = 0
            val hasFuel = fuel != null
            if (hasFuel)
                fuelTime = fuel.persistentDataContainer.getOrDefault(ItemService.FREEZER_FUEL_KEY, PersistentDataType.INTEGER, 0)

            if (!hasIngredient) {
                freezer.recipeTime = 0
                freezer.recipe = null
            }

            if (freezer.isLit() || hasFuel && hasIngredient) {
                val ingredient = inv.getItem(INGREDIENT_SLOT)
                if (freezer.recipeTime == 0 && ingredient != null) {
                    for (entry in freezerRecipes()) {
                        if (entry.input.matchesType(ingredient)) {
                            freezer.recipe = entry
                            val result = inv.getItem(OUTPUT_SLOT)
                            val resultPreview = entry.result.generate()
                            if (result == null || (resultPreview != null && result.isSimilar(resultPreview)))
                                freezer.recipeTime = entry.time
                            break
                        }
                    }
                }

                val fuel = inv.getItem(FUEL_SLOT)
                if (freezer.recipeTime != 0) {
                    if (!freezer.isLit() && fuelTime > 0) {
                        freezer.fuelTime += fuelTime
                        freezer.fuelSupply = fuelTime
                        fuel!!.amount--
                    }
                    freezer.freezeTime++
                }
                else {
                    freezer.freezeTime = max(freezer.freezeTime--, 0)
                }

                if ((freezer.freezeTime > freezer.recipeTime) && (freezer.recipe != null)) {
                    ingredient?.amount--  // Remove from our input
                    freezer.freezeTime = 0
                    val result = inv.getItem(OUTPUT_SLOT)
                    // Add to our output
                    if (result != null)
                        result.add()
                    else
                        freezer.recipe!!.result.generate()?.let { inv.setItem(OUTPUT_SLOT, it) }
                    val newOutput = inv.getItem(OUTPUT_SLOT)!!
                    if (newOutput.amount >= newOutput.maxStackSize) {
                        freezer.recipeTime = 0
                        freezer.recipe = null
                    }
                }
            }
            else {
                freezer.freezeTime = max(freezer.freezeTime--, 0)
            }
            var fuelRatio = 0.0f
            if (freezer.fuelSupply != 0)
                fuelRatio = (freezer.fuelTime / freezer.fuelSupply.toFloat())
            inv.getItem(FLAME_SLOT)!!.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addFloat(fuelRatio).build())
            var progressRatio = 0.0f
            if (freezer.recipeTime != 0)
                progressRatio = (freezer.freezeTime / freezer.recipeTime.toFloat())
            inv.getItem(ARROW_SLOT)!!.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addFloat(progressRatio).build())
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (event.isShiftClick) {
            val inv = event.inventory
            // We only care about custom filtering for shift clicking INTO the freezer
            if (inv == inventory()) {
                if (event.clickedInventory != null) {
                    if (event.clickedInventory!!.type != InventoryType.PLAYER) {
                        if (event.slot !in ALLOWED_SLOTS)
                            event.isCancelled = true
                        return
                    }
                }
                event.isCancelled = true  // We aren't trusting vanilla for this one
                val clickedItem = event.currentItem
                if (clickedItem == null) return

                // First, try to fill fuel items into the fuel slot
                if (clickedItem.persistentDataContainer.getOrDefault(ItemService.FREEZER_FUEL_KEY, PersistentDataType.INTEGER, 0) != 0) {
                    val fuelItem = inv.getItem(FUEL_SLOT)

                    // No item here, move the entire stack over
                    if (fuelItem == null) {
                        inv.setItem(FUEL_SLOT, clickedItem.clone())
                        clickedItem.amount = 0
                    }
                    else if (fuelItem.isSimilar(clickedItem)) {
                        val amtToTake = fuelItem.maxStackSize - fuelItem.amount
                        clickedItem.transfer(amtToTake, fuelItem)
                    }
                    return  // Return here so we don't let overflow spill into ingredient slot
                }

                // Next, try to move items to the input slot
                val ingItem = inv.getItem(INGREDIENT_SLOT)

                // No item here, move the entire stack over
                if (ingItem == null) {
                    inv.setItem(INGREDIENT_SLOT, clickedItem.clone())
                    clickedItem.amount = 0
                }
                else if (ingItem.isSimilar(clickedItem)) {
                    val amtToTake = ingItem.maxStackSize - ingItem.amount
                    clickedItem.transfer(amtToTake, ingItem)
                }
            }
        }
        else {
            val inv = event.clickedInventory ?: return
            if (event.inventory == inventory() && event.clickedInventory == inventory()) {
                if (event.slot !in ALLOWED_SLOTS) {
                    event.isCancelled = true
                    return
                }

                val clickedItem = event.currentItem
                val cursorItem = event.cursor

                when (event.slot) {
                    FUEL_SLOT -> {
                        if (!cursorItem.isEmpty) {
                            if (cursorItem.persistentDataContainer.getOrDefault(ItemService.FREEZER_FUEL_KEY, PersistentDataType.INTEGER, 0) == 0) {
                                event.isCancelled = true
                            }
                        }
                    }

                    OUTPUT_SLOT -> {
                        val output = inv.getItem(OUTPUT_SLOT)
                        event.isCancelled = true
                        if (cursorItem.isEmpty || cursorItem.isSimilar(output)) {
                            // If cursor is empty, use vanilla logic and just take the item
                            if (cursorItem.isEmpty) {
                                event.isCancelled = false
                            } else {
                                val amtToTake = cursorItem.maxStackSize - cursorItem.amount
                                output!!.transfer(amtToTake, cursorItem)
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    fun onHopperTransfer(event: InventoryMoveItemEvent) {
        val inv = inventory()
        if (event.source == inv || event.destination == inv) {
            event.isCancelled = true  // We are just gonna do our own hopper logic so that this won't be absolute hell.
            val hopperPos = LocationUtils.toBlockPos(event.initiator.location)

            // Extracting output
            if (blockEntity.pos.below() == hopperPos) {
                if (event.initiator.firstEmpty() == -1) return  // No-op when the hopper is full
                val outputItem = inv.getItem(OUTPUT_SLOT) ?: ItemStack.empty()
                val transferItem = outputItem.clone()
                if (!outputItem.isEmpty) {
                    transferItem.amount = 1
                    outputItem.amount--
                }
                val itemsDidntFit = event.destination.addItem(transferItem)
                for (item in itemsDidntFit) {
                    event.initiator.addItem(item.value)
                }
                return
            }

            Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                // Input
                if (blockEntity.pos.above() == hopperPos) {
                    var itemToMove = ItemStack.empty()
                    for (stack in event.source) {
                        if (stack != null) {
                            itemToMove = stack
                            break
                        }
                    }
                    if (itemToMove.isEmpty) return@Runnable

                    val ingItem = inv.getItem(INGREDIENT_SLOT) ?: ItemStack.empty()
                    if (ingItem.isSimilar(itemToMove) || ingItem.isEmpty) {
                        if (ingItem.isEmpty) {
                            val initItem = itemToMove.clone()
                            initItem.amount = 1
                            inv.setItem(INGREDIENT_SLOT, initItem)
                            itemToMove.amount--
                        } else if (ingItem.amount < ingItem.maxStackSize) {
                            ingItem.amount++
                            itemToMove.amount--
                        }
                    }
                    return@Runnable
                }

                // Fuel
                val sides = listOf(blockEntity.pos.west(), blockEntity.pos.east(), blockEntity.pos.north(), blockEntity.pos.south())
                if (hopperPos in sides) {
                    var itemToMove = ItemStack.empty()
                    for (stack in event.source) {
                        if (stack != null) {
                            if (stack.persistentDataContainer.getOrDefault(ItemService.FREEZER_FUEL_KEY, PersistentDataType.INTEGER, 0) != 0) {
                                itemToMove = stack
                                break
                            }
                        }
                    }
                    if (itemToMove.isEmpty) return@Runnable

                    val fuelItem = inv.getItem(FUEL_SLOT) ?: ItemStack.empty()
                    if (fuelItem.isSimilar(itemToMove) || fuelItem.isEmpty) {
                        if (fuelItem.isEmpty) {
                            val initItem = itemToMove.clone()
                            initItem.amount = 1
                            inv.setItem(FUEL_SLOT, initItem)
                            itemToMove.amount--
                        } else if (fuelItem.amount < fuelItem.maxStackSize) {
                            fuelItem.amount++
                            itemToMove.amount--
                        }
                    }
                }
            }, TickTime.TICK)
        }
    }
}