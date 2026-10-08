package xyz.devvydont.smprpg.items.blueprints.vanilla

import org.bukkit.Material
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry
import xyz.devvydont.smprpg.items.base.VanillaAttributeItem
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.items.interfaces.IRepairable
import xyz.devvydont.smprpg.items.interfaces.ISkillRequirement
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.skills.SkillType
import xyz.devvydont.smprpg.util.items.ToolGlobals
import xyz.devvydont.smprpg.util.items.ToolStats

class ItemShovel(itemService: ItemService, material: Material) : VanillaAttributeItem(itemService, material),
    IBreakableEquipment, IRepairable, ISkillRequirement {

    override val itemClassification: ItemClassification get() = ItemClassification.SHOVEL
    override val repairMaterial: MutableCollection<ItemStack>
        get() = when (material) {
            Material.NETHERITE_SHOVEL -> mutableListOf(itemService.getCustomItem(Material.NETHERITE_INGOT))
            // Material.DIAMOND_SHOVEL -> mutableListOf(itemService.getCustomItem(Material.DIAMOND))
            Material.GOLDEN_SHOVEL -> mutableListOf(itemService.getCustomItem(Material.GOLD_INGOT))
            Material.IRON_SHOVEL -> mutableListOf(itemService.getCustomItem(Material.IRON_INGOT))
            // Material.STONE_SHOVEL -> mutableListOf(itemService.getCustomItem(Material.COBBLESTONE))
            Material.COPPER_SHOVEL -> mutableListOf(itemService.getCustomItem(Material.COPPER_INGOT))
            Material.WOODEN_SHOVEL -> mutableListOf(itemService.getCustomItem(Material.OAK_PLANKS), itemService.getCustomItem(Material.BIRCH_PLANKS), itemService.getCustomItem(Material.SPRUCE_PLANKS),
                itemService.getCustomItem(Material.JUNGLE_PLANKS), itemService.getCustomItem(Material.DARK_OAK_PLANKS), itemService.getCustomItem(Material.ACACIA_PLANKS),
                itemService.getCustomItem(Material.CRIMSON_PLANKS), itemService.getCustomItem(Material.WARPED_PLANKS), itemService.getCustomItem(Material.MANGROVE_PLANKS),
                itemService.getCustomItem(Material.CHERRY_PLANKS), itemService.getCustomItem(Material.PALE_OAK_PLANKS), itemService.getCustomItem(Material.BAMBOO_PLANKS))
            else -> mutableListOf()
        }
    override val skillRequirements: MutableMap<SkillType, Int> get() = when (material) {
        Material.NETHERITE_SHOVEL -> mutableMapOf(Pair(SkillType.MINING, ToolStats.NETHERITE.skillReqLevel))
        Material.GOLDEN_SHOVEL -> mutableMapOf(Pair(SkillType.MINING, ToolStats.GOLD.skillReqLevel))
        Material.IRON_SHOVEL -> mutableMapOf(Pair(SkillType.MINING, ToolStats.IRON.skillReqLevel))
        Material.COPPER_SHOVEL -> mutableMapOf(Pair(SkillType.MINING, ToolStats.COPPER.skillReqLevel))
        else -> mutableMapOf()
    }

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.MINING_POWER, getShovelMiningPower(material).toDouble()),
            AdditiveAttributeEntry(AttributeWrapper.STRENGTH, getShovelDamage(material)),
            AdditiveAttributeEntry(AttributeWrapper.MINING_SPEED, getShovelSpeed(material).toDouble()),
            MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, SHOVEL_ATTACK_SPEED_DEBUFF)
        )
    }

    override fun getPowerRating(): Int { return getShovelRating(material) }

    override fun getActiveSlot(): EquipmentSlotGroup { return EquipmentSlotGroup.MAINHAND }

    override fun getMaxDurability(): Int {
        return when (material) {
            Material.NETHERITE_SHOVEL -> ToolStats.NETHERITE.durability
            // Material.DIAMOND_SHOVEL -> ToolStats.DIAMOND.durability
            Material.GOLDEN_SHOVEL -> ToolStats.GOLD.durability
            Material.IRON_SHOVEL -> ToolStats.IRON.durability
            // Material.STONE_SHOVEL -> ToolStats.STONE.durability
            Material.COPPER_SHOVEL -> ToolStats.COPPER.durability
            Material.WOODEN_SHOVEL -> ToolStats.WOOD.durability
            else -> 50_000
        }
    }

    companion object {
        fun getShovelDamage(material: Material): Double {
            return when (material) {
                Material.NETHERITE_SHOVEL -> 30
                Material.DIAMOND_SHOVEL -> 25
                Material.GOLDEN_SHOVEL -> 20
                Material.IRON_SHOVEL -> 15
                Material.STONE_SHOVEL -> 10
                Material.COPPER_SHOVEL -> 10
                Material.WOODEN_SHOVEL -> 5
                else -> 0
            }.toDouble()
        }

        fun getShovelDamage(itemType: CustomItemType): Double {
            return when (itemType) {
                CustomItemType.TIN_SHOVEL -> 5
                CustomItemType.SILVER_SHOVEL -> 15
                CustomItemType.STEEL_SHOVEL -> 20
                CustomItemType.ROSE_GOLD_SHOVEL, CustomItemType.MITHRIL_SHOVEL, CustomItemType.COBALT_SHOVEL -> 22
                CustomItemType.TITANIUM_SHOVEL, CustomItemType.TUNGSTEN_SHOVEL -> 25
                CustomItemType.PLATINUM_SHOVEL -> 26
                CustomItemType.ADAMANTIUM_SHOVEL, CustomItemType.PALLADIUM_SHOVEL -> 27
                CustomItemType.ORICHALCUM_SHOVEL, CustomItemType.AETHERIUM_SHOVEL -> 29
                else -> 0
            }.toDouble()
        }

        fun getShovelMiningPower(material: Material): Int {
            return when (material) {
                Material.NETHERITE_SHOVEL -> ToolStats.NETHERITE.miningPower
                // Material.DIAMOND_SHOVEL -> ToolStats.DIAMOND.miningPower
                Material.GOLDEN_SHOVEL -> ToolStats.GOLD.miningPower
                Material.IRON_SHOVEL -> ToolStats.IRON.miningPower
                // Material.STONE_SHOVEL -> ToolStats.STONE.miningPower
                Material.COPPER_SHOVEL -> ToolStats.COPPER.miningPower
                Material.WOODEN_SHOVEL -> ToolStats.WOOD.miningPower
                else -> 0
            }
        }

        fun getShovelRating(material: Material): Int {
            return when (material) {
                Material.NETHERITE_SHOVEL -> ToolStats.NETHERITE.power
                // Material.DIAMOND_SHOVEL -> ToolStats.DIAMOND.power
                Material.GOLDEN_SHOVEL -> ToolStats.GOLD.power
                Material.IRON_SHOVEL -> ToolStats.IRON.power
                // Material.STONE_SHOVEL -> ToolStats.STONE.power
                Material.COPPER_SHOVEL -> ToolStats.COPPER.power
                Material.WOODEN_SHOVEL -> ToolStats.WOOD.power
                else -> 1
            }
        }

        fun getShovelSpeed(material: Material): Int {
            return when (material) {
                Material.NETHERITE_SHOVEL -> ToolGlobals.NETHERITE_TOOL_SPEED
                Material.DIAMOND_SHOVEL -> ToolGlobals.DIAMOND_TOOL_SPEED
                Material.GOLDEN_SHOVEL -> ToolGlobals.GOLD_TOOL_SPEED
                Material.IRON_SHOVEL -> ToolGlobals.IRON_TOOL_SPEED
                Material.STONE_SHOVEL -> ToolGlobals.STONE_TOOL_SPEED
                Material.WOODEN_SHOVEL -> ToolGlobals.WOOD_TOOL_SPEED
                Material.COPPER_SHOVEL -> ToolGlobals.COPPER_TOOL_SPEED
                else -> 1
            }
        }

        var SHOVEL_ATTACK_SPEED_DEBUFF: Double = -0.75
    }
}
