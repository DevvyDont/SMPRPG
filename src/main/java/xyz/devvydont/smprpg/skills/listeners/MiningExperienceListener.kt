package xyz.devvydont.smprpg.skills.listeners

import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks
import net.momirealms.craftengine.core.util.Key
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.block.CraftEngineBlockEnums
import xyz.devvydont.smprpg.events.skills.SkillExperienceGainEvent
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.world.ChunkUtil

class MiningExperienceListener() : Listener {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    /**
     * When a player places a block, we need to mark that block as unable to be farmed for experience
     * 
     * @param event
     */
    @EventHandler
    fun onPlaceBlock(event: BlockPlaceEvent) {
        if (event.isCancelled) return

        // When any block is placed, it is no longer able to earn skill experience
        ChunkUtil.markBlockSkillInvalid(event.getBlock())
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onGainGeneralMiningExperience(event: BlockBreakEvent) {
        val block = event.getBlock()

        if (event.isCancelled) return

        // If this block isn't allowed to retrieve experience
        if (ChunkUtil.isBlockSkillInvalid(block)) return

        val skill = SMPRPG.getService(EntityService::class.java).getPlayerInstance(event.player)
            .miningSkill

        var exp = 0
        exp += getBaseExperienceForDrop(block)
        if (exp <= 0) return

        // event.expToDrop = max(1, exp / 10)
        skill.addExperience(exp, SkillExperienceGainEvent.ExperienceSource.ORE)
    }

    companion object {
        fun getBaseExperienceForDrop(block: Block): Int {
            if (CraftEngineBlocks.isCustomBlock(block)) {
                val cb = Key.of(CraftEngineBlocks.getCustomBlockState(block)!!.customBlockState().asString) ?: return 0
                return when (cb) {
                    CraftEngineBlockEnums.RAW_SILVER_BLOCK.key -> 11
                    CraftEngineBlockEnums.RAW_TIN_BLOCK.key -> 8
                    CraftEngineBlockEnums.RAW_MITHRIL_BLOCK.key -> 20
                    CraftEngineBlockEnums.RAW_TITANIUM_BLOCK.key -> 125
                    CraftEngineBlockEnums.RAW_ADAMANTIUM_BLOCK.key -> 200
                    CraftEngineBlockEnums.SILVER_ORE.key -> 10
                    CraftEngineBlockEnums.DEEPSLATE_SILVER_ORE.key -> 30
                    CraftEngineBlockEnums.TIN_ORE.key -> 2
                    CraftEngineBlockEnums.DEEPSLATE_TIN_ORE.key -> 6
                    CraftEngineBlockEnums.SPARSE_MITHRIL_ORE.key -> 9
                    CraftEngineBlockEnums.MITHRIL_ORE.key -> 18
                    CraftEngineBlockEnums.DENSE_MITHRIL_ORE.key -> 36
                    CraftEngineBlockEnums.TITANIUM_ORE.key -> 75
                    CraftEngineBlockEnums.ADAMANTIUM_ORE.key -> 200
                    CraftEngineBlockEnums.GRIMSTONE.key -> 4
                    CraftEngineBlockEnums.COBBLED_GRIMSTONE.key -> 4
                    CraftEngineBlockEnums.GRIMSTONE_IRON_ORE.key -> 42
                    CraftEngineBlockEnums.GRIMSTONE_LAPIS_ORE.key -> 48
                    CraftEngineBlockEnums.GRIMSTONE_SILVER_ORE.key -> 60
                    CraftEngineBlockEnums.GRIMSTONE_GOLD_ORE.key -> 72
                    CraftEngineBlockEnums.GRIMSTONE_DIAMOND_ORE.key -> 150

                    CraftEngineBlockEnums.SULFUR_ORE.key -> 15
                    CraftEngineBlockEnums.TUNGSTEN_ORE.key -> 50
                    CraftEngineBlockEnums.COBALT_ORE.key -> 100
                    CraftEngineBlockEnums.ORICHALCUM_ORE.key -> 100

                    CraftEngineBlockEnums.AETHER_DIRT.key, CraftEngineBlockEnums.AETHER_GRASS_BLOCK.key, CraftEngineBlockEnums.ENCHANTED_AETHER_GRASS_BLOCK.key -> 1
                    CraftEngineBlockEnums.HOLYSTONE.key -> 2
                    CraftEngineBlockEnums.ICESTONE.key, CraftEngineBlockEnums.QUICKSOIL.key -> 4
                    CraftEngineBlockEnums.AETHER_SILVER_ORE.key -> 18
                    CraftEngineBlockEnums.AMBROSIUM_ORE.key -> 15
                    CraftEngineBlockEnums.ZANITE_ORE.key -> 50
                    CraftEngineBlockEnums.PLATINUM_ORE.key -> 100
                    CraftEngineBlockEnums.PALLADIUM_ORE.key -> 100
                    CraftEngineBlockEnums.GRAVITITE_ORE.key -> 200

                    CraftEngineBlockEnums.NULLYIUM.key -> 3
                    CraftEngineBlockEnums.SMOKY_QUARTZ_ORE.key -> 50
                    CraftEngineBlockEnums.POINTER_PRISM_BLOCK.key -> 35
                    else -> 0
                }
            }

            return when (block.type) {
                Material.STONE, Material.COBBLESTONE, Material.COBBLED_DEEPSLATE, Material.SAND, Material.RED_SAND, Material.SANDSTONE, Material.RED_SANDSTONE, Material.CLAY, Material.MYCELIUM, Material.GRASS_BLOCK, Material.DIRT, Material.GRAVEL, Material.DEEPSLATE, Material.TUFF, Material.FLINT -> 1
                Material.ANDESITE, Material.DIORITE, Material.GRANITE, Material.CALCITE, Material.BONE_BLOCK, Material.SOUL_SAND, Material.SOUL_SOIL, Material.ICE, Material.PACKED_ICE,
                Material.NETHERRACK, Material.WARPED_NYLIUM, Material.CRIMSON_NYLIUM, Material.BASALT, Material.SMOOTH_BASALT, Material.BLACKSTONE -> 2
                Material.END_STONE -> 3
                Material.SEA_LANTERN -> 10
                Material.PRISMARINE -> 2
                Material.PRISMARINE_BRICKS, Material.DARK_PRISMARINE -> 2
                Material.WET_SPONGE -> 25
                Material.BLUE_ICE -> 8
                Material.COAL_ORE, Material.COAL -> 5
                Material.DEEPSLATE_COAL_ORE -> 15
                Material.COPPER_ORE, Material.RAW_COPPER, Material.COPPER_INGOT -> 2
                Material.DEEPSLATE_COPPER_ORE -> 6
                Material.RAW_COPPER_BLOCK -> 8
                Material.IRON_ORE, Material.RAW_IRON, Material.IRON_INGOT -> 7
                Material.RAW_IRON_BLOCK -> 8
                Material.IRON_BLOCK -> 9
                Material.DEEPSLATE_IRON_ORE -> 21
                Material.GOLD_ORE, Material.RAW_GOLD, Material.GOLD_INGOT -> 12
                Material.RAW_GOLD_BLOCK -> 14
                Material.DEEPSLATE_GOLD_ORE -> 36
                Material.OBSIDIAN -> 20
                Material.CRYING_OBSIDIAN -> 24
                Material.AMETHYST_BLOCK -> 4
                Material.AMETHYST_SHARD -> 10
                Material.AMETHYST_CLUSTER -> 15
                Material.BUDDING_AMETHYST -> 10
                Material.LARGE_AMETHYST_BUD -> 15
                Material.MEDIUM_AMETHYST_BUD -> 20
                Material.SMALL_AMETHYST_BUD -> 25
                Material.LAPIS_LAZULI -> 3
                Material.REDSTONE -> 4
                Material.REDSTONE_BLOCK -> 15
                Material.REDSTONE_ORE -> 18
                Material.LAPIS_ORE -> 8
                Material.DEEPSLATE_REDSTONE_ORE -> 54
                Material.DEEPSLATE_LAPIS_ORE -> 24
                Material.DIAMOND_ORE, Material.DIAMOND -> 25
                Material.DEEPSLATE_DIAMOND_ORE -> 75
                Material.EMERALD_ORE, Material.EMERALD -> 100
                Material.DEEPSLATE_EMERALD_ORE -> 300
                Material.GOLD_BLOCK -> 45
                Material.EMERALD_BLOCK -> 200
                Material.MAGMA_BLOCK -> 2
                Material.GOLD_NUGGET -> 5
                Material.NETHER_GOLD_ORE -> 22
                Material.GLOWSTONE_DUST -> 5
                Material.GLOWSTONE -> 15
                Material.NETHER_QUARTZ_ORE -> 25
                Material.QUARTZ -> 10
                Material.QUARTZ_BLOCK -> 15
                Material.ANCIENT_DEBRIS -> 200
                else -> 0
            }
        }
    }
}
