package xyz.devvydont.smprpg.block

import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks
import net.momirealms.craftengine.bukkit.api.event.CustomBlockBreakEvent
import org.bukkit.ExplosionResult
import org.bukkit.GameMode
import org.bukkit.block.data.Ageable
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.entity.EntityExplodeEvent
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.block.BlockLootRegistry.get
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.services.AttributeService.Companion.instance
import xyz.devvydont.smprpg.services.DropsService
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.skills.listeners.FarmingExperienceListener
import xyz.devvydont.smprpg.util.craftengine.CraftEngineHelpers
import xyz.devvydont.smprpg.util.listeners.ToggleableListener
import xyz.devvydont.smprpg.util.world.ChunkUtil
import java.util.function.Consumer
import kotlin.math.floor

class BlockLootOverrideListener : ToggleableListener() {
    /**
     * Using an item type, try to guess which fortune stat is desired to be use when an override isn't set.
     * For example, if we are using a pickaxe it's safe to assume we want to use mining fortune.
     * Keep in mind, this isn't reliable and [BlockLootEntry.fortuneOverride] is a desirable choice.
     * @param tool The tool to guess a fortune attribute for.
     * @return The attribute that we should use, null if inconclusive.
     */
    private fun predictDesiredFortuneAttribute(tool: ItemClassification): AttributeWrapper? {
        return when (tool) {
            ItemClassification.PICKAXE, ItemClassification.DRILL -> AttributeWrapper.MINING_FORTUNE
            ItemClassification.HOE -> AttributeWrapper.FARMING_FORTUNE
            ItemClassification.AXE -> AttributeWrapper.WOODCUTTING_FORTUNE
            else -> null
        }
    }

    /**
     * This method will handle exploded block loot from CraftEngine custom blocks.
     */
    @EventHandler(priority = EventPriority.LOW)
    private fun onCraftEngineBlockExplode(event: EntityExplodeEvent) {

        if (event.explosionResult == ExplosionResult.KEEP || event.explosionResult == ExplosionResult.TRIGGER_BLOCK) return

        for (block in event.blockList()) {
            if (CraftEngineBlocks.isCustomBlock(block)) {
                val entry = get(block.state) ?: continue
                val ctx = BlockLootContext.CORRECT_TOOL
                val loot: Collection<BlockLoot> = entry.getLootForContext(ctx)

                for (drop in loot) {
                    var amount = drop.chance * event.yield

                    val leftover = amount - floor(amount)
                    if (Math.random() < leftover) amount++

                    val item = drop.getLoot()
                    item.amount = amount.toInt()
                    event.entity.world.dropItemNaturally(block.location, item)
                }
            }
        }
    }

    /**
     * This specific method handles block loot for CraftEngine blocks. It filters out if we
     * have already defined a loot override, then sends a new event over to handle the block break.
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    @Suppress("UnstableApiUsage")
    private fun onCraftEngineBlockBreak(event: CustomBlockBreakEvent) {
        val entry = get(event.bukkitBlock().state)
        if (entry == null) return

        // We are overriding drops at this point.
        event.setDropItems(false)

        BlockDropItemEvent(event.bukkitBlock(), event.bukkitBlock().state, event.player, mutableListOf()).callEvent()
    }

    /**
     * The backbone of how all block drop loot functions in the plugin.
     * The idea is to check if our [BlockLootRegistry] has an override for the block broken.
     * If it does, we need to determine what fortune attribute to use based on the context (the tool!)
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private fun onBlockBreak(event: BlockDropItemEvent) {
        // If we are in creative, we aren't dropping anything.
        if (event.player.gameMode == GameMode.CREATIVE)
            return

        // If this block is ageable, then it needs to be at its max age before we consider custom logic.

        val blockData = event.blockState.blockData

        // Check if this block is flagged. If it isn't, let vanilla handle the logic.
        val entry = get(event.blockState) ?: return

        if (CraftEngineBlocks.isCustomBlock(event.block)) {
            val age = CraftEngineBlocks.getCustomBlockState(event.block)!!.customBlockState().getProperty<Int>("age")
            if (age != null) {
                val blockKey = CraftEngineHelpers.getBlockKey(event.block)
                val maxAge = FarmingExperienceListener.getCustomCropMaxAge(blockKey)
                if (age != maxAge) {
                    val loot: Collection<BlockLoot> = entry.getLootForContext(BlockLootContext.IMMATURE_AGEABLE)
                    for (drop in loot) {
                        event.items.clear()
                        val item = drop.getLoot()
                        item.amount = drop.chance.toInt()

                        val ent = event.getBlock().world
                            .dropItem(event.getBlock().location.toCenterLocation(), item, Consumer { entity: Item? ->
                                SMPRPG.getService(DropsService::class.java)
                                    .addDefaultLootFlags(item, event.player)
                                entity!!.itemStack = item
                                SMPRPG.getService(DropsService::class.java).transferLootFlags(entity)
                            })

                        event.items.add(ent)
                    }
                    return
                }
                else ChunkUtil.markBlockSkillValid(event.block)
            }
        }
        else {
            if (blockData is Ageable)
                if (blockData.age != blockData.maximumAge)
                    return
        }

        // If this block was placed by a player invalidate their fortune.
        var fortuneActive = true
        if (ChunkUtil.isBlockSkillInvalid(event.blockState)) fortuneActive = false

        // Check if this block is flagged to never trigger fortune.
        if (entry.dontUseFortune)
            fortuneActive = false

        // The block is flagged. We need context. Essentially, we have manual checks that determine this.
        var ctx = BlockLootContext.INCORRECT_TOOL
        val toolPreferences: MutableSet<ItemClassification> = entry.preferredTools
        val itemUsedToBreak = event.player.inventory.itemInMainHand
        val itemUsedToBreakBlueprint = ItemService.blueprint(itemUsedToBreak)
        val usedTool = itemUsedToBreakBlueprint.itemClassification

        // If there's no tool preference for the block or there's a tool preference match, then we are using correct tool.
        if (toolPreferences.isEmpty()) ctx = BlockLootContext.CORRECT_TOOL
        else if (toolPreferences.contains(usedTool)) ctx = BlockLootContext.CORRECT_TOOL

        // If we are using silk touch on our tool because of the enchantment...
        if (itemUsedToBreak.containsEnchantment(Enchantment.SILK_TOUCH)) ctx = BlockLootContext.SILK_TOUCH

        // If we are using fire aspect... (auto smelt)
        if (itemUsedToBreak.containsEnchantment(Enchantment.FIRE_ASPECT)) ctx = BlockLootContext.AUTO_SMELT

        // We now have context. If the block doesn't define the given context, we can let vanilla take over.
        val loot: Collection<BlockLoot> = entry.getLootForContext(ctx)

        // Given the preferred tool, we can actually work out which fortune stat is ideal. No preferred tool
        // means that no fortune is applicable to this drop.
        var fortune = 0.0
        if (fortuneActive) {
            var attribute = this.predictDesiredFortuneAttribute(usedTool)
            if (entry.fortuneOverride != null) attribute = entry.fortuneOverride

            // If we found one, increment the fortune to use in drop calculation.
            // The idea here is that base fortune is 0, and 100 fortune would yield 2x drops (scalarly scales).
            if (attribute != null) {
                val attrInstance = instance.getAttribute(event.player, attribute)
                if (attrInstance != null) {
                    fortune += attrInstance.value / 100
                }
            }

            // Add any special fortune if we have it.
            val specialFortuneWrapper =  entry.specialFortune
            if (specialFortuneWrapper != null) {
                val specialFortuneInstance = instance.getAttribute(event.player, specialFortuneWrapper)
                if (specialFortuneInstance != null) {
                    fortune += specialFortuneInstance.value / 100
                }
            }
        }

        // Set the drops manually! Take into account the base drop chance, and the fortune chance.
        event.items.clear()
        for (drop in loot) {
            // The amount of drops is equal to the chance. Drops with chances of 1 or higher are additive, otherwise multiplicative.

            var amount = drop.chance
            if (drop.chance < 1.0) amount *= (1 + fortune)
            else amount += fortune

            val leftover = amount - floor(amount)
            if (Math.random() < leftover) amount++

            val item = drop.getLoot()
            item.amount = amount.toInt()

            // This might seem scuffed, but there's a reason.
            // In order for loot tagging to be detected properly by things like telekinesis, we have to first add the
            // loot flags to the item, then manually and immediately transfer it to the item entity itself
            // BEFORE it spawns in. When we do it this way, succeeding BlockDropItemEvent calls will be able to detect
            // this. If we spawn it here and now, the ItemSpawnEvent will hijack us and mess everything up.
            val ent = event.getBlock().world
                .dropItem(event.getBlock().location.toCenterLocation(), item, Consumer { entity: Item? ->
                    SMPRPG.getService(DropsService::class.java)
                        .addDefaultLootFlags(item, event.player)
                    entity!!.itemStack = item
                    SMPRPG.getService(DropsService::class.java).transferLootFlags(entity)
                })

            event.items.add(ent)
        }
    }
}
