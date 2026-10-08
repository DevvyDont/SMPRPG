package xyz.devvydont.smprpg.block.entity

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.CustomModelData
import io.papermc.paper.datacomponent.item.TooltipDisplay
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.block.Block
import net.momirealms.craftengine.bukkit.api.BukkitAdaptor
import net.momirealms.craftengine.bukkit.item.BukkitItemManager
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
import net.momirealms.craftengine.core.world.Vec3d
import net.momirealms.craftengine.core.world.WorldPosition
import net.momirealms.craftengine.libraries.nbt.CompoundTag
import net.momirealms.craftengine.libraries.nbt.ListTag
import org.bukkit.*
import org.bukkit.block.data.type.Furnace
import org.bukkit.craftbukkit.CraftWorld
import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.block.behaviors.CookingPotBlockBehavior
import xyz.devvydont.smprpg.events.skills.SkillExperienceGainEvent
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.recipe.core.CookingPotRecipe
import xyz.devvydont.smprpg.recipe.core.RecipeStationType
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.services.RecipeService
import xyz.devvydont.smprpg.util.extensions.transfer
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.time.TickTime
import java.util.*
import java.util.function.Supplier
import kotlin.math.max
import kotlin.math.min
import net.momirealms.craftengine.core.util.Key as CEKey

@Suppress("UnstableApiUsage")
class CookingPotBlockEntityController(val entity: BlockEntity, val behavior: CookingPotBlockBehavior) : BlockEntityController(entity),
    Listener {

    private var ticker = 0    // Generic ticker used for visual effects on the pot when heated.
    private var cookTime = 0  // How long the current item has been cooking for, in ticks
    private var recipeTime = 0  // How long the current item needs to cook for before being finished.
    private var recipe: CookingPotRecipe? = null  // The current working recipe
    private var bordersInitialized = false
    private var speed = 1  // How many ticks should the recipe progress per tick?

    var BORDER: ItemStack? = null
    var heated = false
    var boundPlayer: UUID? = null
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
            36,
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

        // Check 1 tick after placement/reload if we are on an active heat source
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            updateHeated()
        }, TickTime.TICK)
    }

    override fun <C : BlockEntityController?> createAsyncBlockEntityTicker(level: CEWorld, state: ImmutableBlockState): BlockEntityTicker<C> {
        return createTickerHelper<C, CookingPotBlockEntityController>(BlockEntityTicker { ceWorld: CEWorld, blockPos: BlockPos, state: ImmutableBlockState, cookingPot: CookingPotBlockEntityController ->
            tick(
                ceWorld,
                blockPos,
                state,
                cookingPot
            )})
    }

    fun updateHeated() {
        val world = (blockEntity.world.world.platformWorld() as CraftWorld)
        val pos = blockEntity.pos
        val bukkitBlock = world.getBlockAt(pos.x, pos.y - 1, pos.z)

        // Lit furnaces will progress the pot speed at 4x speed
        if (bukkitBlock.blockData is Furnace) {
            if ((bukkitBlock.blockData as Furnace).isLit) {
                heated = true
                speed = 4
            }
            else {
                heated = false
                speed = 1
            }
            return
        }
        speed = 1
        val blockState = BukkitAdaptor.adapt(bukkitBlock).blockState()
        if (blockState.hasTag(HEAT_SOURCE_TAG))
        {
            heated = true
        }
        else heated = false
    }

    override fun onRemove() {
        val inv = inventory()!!
        inv.close()
        val pos = Vec3d.atCenterOf(blockEntity.pos)
        for (idx in ALLOWED_SLOTS) {
            val stack = inv.getItem(idx)
            if (stack != null) {
                blockEntity.world.world.dropItemNaturally(pos, BukkitItemManager.instance().wrap(stack))
            }
        }
        inv.clear()
        HandlerList.unregisterAll(this)
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
        if (boundPlayer != null)
            data.putUUID("bound_player", boundPlayer!!)
        tag.put(behavior.customDataKey, data)
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
                    36,
                    dataVersion
                )
            )
            boundPlayer = dataTag.getUUID("bound_player")
        } else {
            val oldItemsTag: ListTag? = tag.getList("items")
            if (oldItemsTag == null) return
            val itemsTag: ListTag = Optional.ofNullable(tag.getList("items")).orElseGet(Supplier { ListTag() })
            val dataVersion = tag.getInt("data_version", Config.itemDataFixerUpperFallbackVersion())
            this.inventory.setStorageContents(
                ItemStackUtils.parseBukkitItems(
                    itemsTag,
                    36,
                    dataVersion
                )
            )
            boundPlayer = tag.getUUID("bound_player")
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
        return super.blockEntity.isValid()
    }

    fun grantRewards(recipe: CookingPotRecipe) {
        if (recipe.rewards.isEmpty) return
        Bukkit.getScheduler().runTask(plugin, Runnable {
            val uuid = boundPlayer ?: return@Runnable
            val player = Bukkit.getPlayer(uuid) ?: return@Runnable
            recipe.rewards.grant(player, SkillExperienceGainEvent.ExperienceSource.COOK)
        })
    }

    companion object {
        val HEAT_SOURCE_TAG: CEKey = CEKey.of("smprpg:cooking_pot_heat_sources")
        //val ACTIVE_HEAT_SOURCES = listOf()

        val INGREDIENT_SLOTS = listOf(1, 2, 3, 10, 11, 12)
        const val OUTPUT_SLOT = 15
        const val FLAME_SLOT = 20
        const val ARROW_SLOT = 13
        const val PLATING_SLOT = 33
        val PLATING_ITEMS = listOf(  // Hacky as hell, TODO: Make this adapative
            ItemService.generate(Material.BOWL),
            ItemService.generate(Material.GLASS_BOTTLE),
            ItemService.generate(CustomItemType.CERAMIC_PLATE)
        )
        val ALLOWED_SLOTS = listOf(OUTPUT_SLOT, PLATING_SLOT) + INGREDIENT_SLOTS

        /** All cooking pot recipes currently in the data-driven registry. */
        private fun cookingPotRecipes(): List<CookingPotRecipe> =
            SMPRPG.getService(RecipeService::class.java).getRegistry()
                .byStation(RecipeStationType.COOKING_POT)
                .filterIsInstance<CookingPotRecipe>()

        /**
         * Find the first registry recipe whose required ingredient set the pot exactly satisfies: the pot's
         * ingredient slots must contain precisely the recipe's ingredient types (no extras), each present in
         * at least the required amount, and the plating slot must hold the required vessel (if any).
         */
        fun firstRecipeMatch(pot: CookingPotBlockEntityController): CookingPotRecipe? {
            val itemService = SMPRPG.getService(ItemService::class.java)
            val inv = pot.inventory() ?: return null

            // Total amount of each ingredient type currently in the pot, keyed by namespaced identifier.
            val potCounts = HashMap<String, Int>()
            for (slot in INGREDIENT_SLOTS) {
                val ing = inv.getItem(slot) ?: continue
                if (ing.isEmpty) continue
                val id = itemService.getIdentifier(ing)
                potCounts[id] = (potCounts[id] ?: 0) + ing.amount
            }
            if (potCounts.isEmpty()) return null

            for (recipe in cookingPotRecipes()) {
                val needed = HashMap<String, Int>()
                for (ingredient in recipe.ingredients) {
                    val id = ingredient.identifier.asString()
                    needed[id] = (needed[id] ?: 0) + ingredient.amount
                }
                // The pot must contain exactly the recipe's ingredient types...
                if (needed.keys != potCounts.keys) continue
                // ...each in sufficient quantity.
                if (needed.any { (id, amount) -> (potCounts[id] ?: 0) < amount }) continue
                // Plating, if required, must match.
                if (recipe.plating != null) {
                    val platingItem = inv.getItem(PLATING_SLOT) ?: continue
                    if (!recipe.plating.matches(platingItem)) continue
                }
                return recipe
            }
            return null
        }

        /** Consume the recipe's ingredient amounts (and one plating item, if any) from the pot. */
        fun takeIngredients(pot: CookingPotBlockEntityController, recipe: CookingPotRecipe) {
            val inv = pot.inventory() ?: return
            for (ingredient in recipe.ingredients) {
                var toRemove = ingredient.amount
                for (slot in INGREDIENT_SLOTS) {
                    if (toRemove <= 0) break
                    val item = inv.getItem(slot) ?: continue
                    if (item.isEmpty || !ingredient.matchesType(item)) continue
                    val take = min(toRemove, item.amount)
                    item.amount -= take
                    toRemove -= take
                }
            }
            if (recipe.plating != null)
                inv.getItem(PLATING_SLOT)?.let { it.amount-- }
        }

        fun tick(ceWorld: CEWorld, blockPos: BlockPos, state: ImmutableBlockState, pot: CookingPotBlockEntityController) {
            val inv = pot.inventory()!!
            if (!pot.bordersInitialized) {
                for (i in 0..35) {
                    if (i !in ALLOWED_SLOTS) {
                        inv.setItem(i, pot.BORDER)
                        if (i == FLAME_SLOT)
                            inv.getItem(i)!!.setData(DataComponentTypes.ITEM_MODEL, NamespacedKey(plugin, "ui/pot_flame"))
                        if (i == ARROW_SLOT)
                            inv.getItem(i)!!.setData(DataComponentTypes.ITEM_MODEL, NamespacedKey(plugin, "ui/progress_arrow"))
                    }
                }
                pot.bordersInitialized = true
            }

            val bukkitWorld = ceWorld.world.platformWorld() as World
            if (pot.heated) {
                pot.ticker++
                if (pot.ticker % 4 == 0) {
                    bukkitWorld.spawnParticle(
                        Particle.BUBBLE,
                        Location(bukkitWorld, blockPos.x + 0.5, blockPos.y + 0.625, blockPos.z + 0.5),
                        3,
                        0.0625,
                        0.0,
                        0.0625,
                        0.5
                    )
                    bukkitWorld.spawnParticle(
                        Particle.BUBBLE_POP,
                        Location(bukkitWorld, blockPos.x + 0.5, blockPos.y + 0.625, blockPos.z + 0.5),
                        1,
                        0.0625,
                        0.0,
                        0.0625,
                        0.05
                    )
                }
                if (pot.ticker % 40 == 0) {
                    pot.ticker = 0
                    Bukkit.getScheduler().runTask(plugin, Runnable {
                        bukkitWorld.playSound(Location(bukkitWorld, blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble()),
                            Sound.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT,
                            0.5f,
                            2.0f)
                    })
                }
            }

            var hasIngredient = false
            for (slot in INGREDIENT_SLOTS) {
                val ing = inv.getItem(slot) ?: continue
                if (!ing.isEmpty) {
                    hasIngredient = true
                    break
                }
            }

            val currResult = inv.getItem(OUTPUT_SLOT)
            if (!hasIngredient || ((currResult != null) && currResult.amount >= currResult.maxStackSize)) {
                pot.recipeTime = 0
                pot.recipe = null
            }
            else if (pot.heated) {
                pot.recipe = firstRecipeMatch(pot)
                if (pot.recipe != null) {
                    pot.recipeTime = pot.recipe!!.time
                    pot.cookTime += pot.speed
                }
                else {
                    pot.cookTime = max(pot.cookTime - 2, 0)
                }


                if ((pot.cookTime > pot.recipeTime) && (pot.recipe != null)) {
                    pot.cookTime = 0
                    val result = inv.getItem(OUTPUT_SLOT)
                    val resultStack = pot.recipe!!.result.generate()
                    // Add to our output
                    if (result == null) {
                        if (resultStack != null) {
                            inv.setItem(OUTPUT_SLOT, resultStack)
                            takeIngredients(pot, pot.recipe!!)
                            pot.grantRewards(pot.recipe!!)
                        }
                    }
                    else if (resultStack != null && result.isSimilar(resultStack) && result.amount < result.maxStackSize) {
                        result.add()
                        takeIngredients(pot, pot.recipe!!)
                        pot.grantRewards(pot.recipe!!)
                    }

                    val newOutput = inv.getItem(OUTPUT_SLOT)!!
                    if (newOutput.amount >= newOutput.maxStackSize) {
                        pot.recipeTime = 0
                        pot.recipe = null
                    }
                }
            }
            else {
                pot.cookTime = max(pot.cookTime - 2, 0)
            }
            if (pot.heated) inv.getItem(FLAME_SLOT)!!.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString("on").build())
            else inv.getItem(FLAME_SLOT)!!.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString("off").build())
            var progressRatio = 0.0f
            if (pot.recipeTime != 0)
                progressRatio = (pot.cookTime / pot.recipeTime.toFloat())
            inv.getItem(ARROW_SLOT)!!.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addFloat(progressRatio).build())
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (event.isShiftClick) {
            val inv = event.inventory
            // We only care about custom filtering for shift clicking INTO the pot
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

                // First, try to fill plating items into the plating slot
                for (usablePlate in PLATING_ITEMS) {
                    if (clickedItem.isSimilar(usablePlate)) {
                        val platingItem = inv.getItem(PLATING_SLOT)

                        // No item here, move the entire stack over
                        if (platingItem == null) {
                            inv.setItem(PLATING_SLOT, clickedItem.clone())
                            clickedItem.amount = 0
                        } else if (platingItem.isSimilar(clickedItem)) {
                            val amtToTake = platingItem.maxStackSize - platingItem.amount
                            clickedItem.transfer(amtToTake, platingItem)
                        }
                        return  // Return here so we don't let overflow spill into ingredient slot
                    }
                }

                // Next, try to move items to the input slots
                for (slot in INGREDIENT_SLOTS) {
                    val ingItem = inv.getItem(slot)

                    // No item here, move the entire stack over
                    if (ingItem == null) {
                        inv.setItem(slot, clickedItem.clone())
                        clickedItem.amount = 0
                    } else if (ingItem.isSimilar(clickedItem)) {
                        val amtToTake = ingItem.maxStackSize - ingItem.amount
                        clickedItem.transfer(amtToTake, ingItem)
                    }
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
                    PLATING_SLOT -> {
                        if (!cursorItem.isEmpty) {
                            for (plate in PLATING_ITEMS) {
                                if (!cursorItem.isSimilar(plate)) {
                                    event.isCancelled = true
                                }
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

    // Bind a player to the inventory when opened.
    @EventHandler
    fun onInventoryOpen(event: InventoryOpenEvent) {
        if (event.inventory == inventory()) {
            if (boundPlayer == null) {
                boundPlayer = event.player.uniqueId
                event.player.sendMessage(ComponentUtils.success(ComponentUtils.create("This cooking pot has been bound to you for skill XP gain.", NamedTextColor.GRAY)))
            }
        }
    }
}