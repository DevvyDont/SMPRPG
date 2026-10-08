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
import xyz.devvydont.smprpg.util.items.ToolStats

class ItemPickaxe(itemService: ItemService, material: Material) : VanillaAttributeItem(itemService, material),
    IBreakableEquipment, IRepairable, ISkillRequirement {

    override val itemClassification: ItemClassification get() = ItemClassification.PICKAXE
    override val repairMaterial: MutableCollection<ItemStack>
        get() = when (material) {
            Material.NETHERITE_PICKAXE -> mutableListOf(itemService.getCustomItem(Material.NETHERITE_INGOT))
            // Material.DIAMOND_PICKAXE -> mutableListOf(itemService.getCustomItem(Material.DIAMOND))
            Material.GOLDEN_PICKAXE -> mutableListOf(itemService.getCustomItem(Material.GOLD_INGOT))
            Material.IRON_PICKAXE -> mutableListOf(itemService.getCustomItem(Material.IRON_INGOT))
            // Material.STONE_PICKAXE -> mutableListOf(itemService.getCustomItem(Material.COBBLESTONE))
            Material.COPPER_PICKAXE -> mutableListOf(itemService.getCustomItem(Material.COPPER_INGOT))
            Material.WOODEN_PICKAXE -> mutableListOf(itemService.getCustomItem(Material.OAK_PLANKS), itemService.getCustomItem(Material.BIRCH_PLANKS), itemService.getCustomItem(Material.SPRUCE_PLANKS),
                itemService.getCustomItem(Material.JUNGLE_PLANKS), itemService.getCustomItem(Material.DARK_OAK_PLANKS), itemService.getCustomItem(Material.ACACIA_PLANKS),
                itemService.getCustomItem(Material.CRIMSON_PLANKS), itemService.getCustomItem(Material.WARPED_PLANKS), itemService.getCustomItem(Material.MANGROVE_PLANKS),
                itemService.getCustomItem(Material.CHERRY_PLANKS), itemService.getCustomItem(Material.PALE_OAK_PLANKS), itemService.getCustomItem(Material.BAMBOO_PLANKS))
            else -> mutableListOf()
        }
    override val skillRequirements: MutableMap<SkillType, Int> get() = when (material) {
        Material.NETHERITE_PICKAXE -> mutableMapOf(Pair(SkillType.MINING, ToolStats.NETHERITE.skillReqLevel))
        Material.GOLDEN_PICKAXE -> mutableMapOf(Pair(SkillType.MINING, ToolStats.GOLD.skillReqLevel))
        Material.IRON_PICKAXE -> mutableMapOf(Pair(SkillType.MINING, ToolStats.IRON.skillReqLevel))
        Material.COPPER_PICKAXE -> mutableMapOf(Pair(SkillType.MINING, ToolStats.COPPER.skillReqLevel))
        else -> mutableMapOf()
    }

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.MINING_POWER, getPickaxePower(material)),
            AdditiveAttributeEntry(AttributeWrapper.STRENGTH, getPickaxeDamage(material)),
            MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, PICKAXE_ATTACK_SPEED_DEBUFF),
            AdditiveAttributeEntry(AttributeWrapper.MINING_SPEED, getPickaxeSpeed(material)),
            AdditiveAttributeEntry(AttributeWrapper.MINING_FORTUNE, getPickaxeFortune(material))
        )
    }

    override fun getPowerRating(): Int { return getPickaxeRating(material) }

    override fun getActiveSlot(): EquipmentSlotGroup { return EquipmentSlotGroup.MAINHAND }

    override fun getMaxDurability(): Int {
        return when (material) {
            Material.NETHERITE_PICKAXE -> ToolStats.NETHERITE.durability
            // Material.DIAMOND_PICKAXE -> ToolStats.DIAMOND.durability
            Material.GOLDEN_PICKAXE -> ToolStats.GOLD.durability
            Material.IRON_PICKAXE -> ToolStats.IRON.durability
            // Material.STONE_PICKAXE -> ToolStats.STONE.durability
            Material.COPPER_PICKAXE -> ToolStats.COPPER.durability
            Material.WOODEN_PICKAXE -> ToolStats.WOOD.durability
            else -> 50_000
        }
    }

    companion object {
        @JvmStatic
        fun getPickaxeFortune(material: Material): Double {
            return when (material) {
                Material.NETHERITE_PICKAXE -> ToolStats.NETHERITE.fortune
                // Material.DIAMOND_PICKAXE -> ToolStats.DIAMOND.fortune
                Material.GOLDEN_PICKAXE -> ToolStats.GOLD.fortune
                Material.IRON_PICKAXE -> ToolStats.IRON.fortune
                // Material.STONE_PICKAXE -> ToolStats.STONE.fortune
                Material.COPPER_PICKAXE -> ToolStats.COPPER.fortune
                Material.WOODEN_PICKAXE -> ToolStats.WOOD.fortune
                else -> 0
            }.toDouble()
        }

        @JvmStatic
        fun getPickaxeSpeed(material: Material): Double {
            return when (material) {
                Material.NETHERITE_PICKAXE -> ToolStats.NETHERITE.speed
                // Material.DIAMOND_PICKAXE -> ToolStats.DIAMOND.speed
                Material.GOLDEN_PICKAXE -> ToolStats.GOLD.speed
                Material.IRON_PICKAXE -> ToolStats.IRON.speed
                // Material.STONE_PICKAXE -> ToolStats.STONE.speed
                Material.COPPER_PICKAXE -> ToolStats.COPPER.speed
                Material.WOODEN_PICKAXE -> ToolStats.WOOD.speed
                else -> 0
            }.toDouble()
        }

        fun getPickaxePower(material: Material): Double {
            return when (material) {
                Material.NETHERITE_PICKAXE -> ToolStats.NETHERITE.miningPower
                // Material.DIAMOND_PICKAXE -> ToolStats.DIAMOND.miningPower
                Material.GOLDEN_PICKAXE -> ToolStats.GOLD.miningPower
                Material.IRON_PICKAXE -> ToolStats.IRON.miningPower
                // Material.STONE_PICKAXE -> ToolStats.STONE.miningPower
                Material.COPPER_PICKAXE -> ToolStats.COPPER.miningPower
                Material.WOODEN_PICKAXE -> ToolStats.WOOD.miningPower
                else -> 0
            }.toDouble()
        }

        fun getPickaxeDamage(material: Material): Double {
            return when (material) {
                Material.NETHERITE_PICKAXE -> 30
                Material.DIAMOND_PICKAXE -> 20
                Material.GOLDEN_PICKAXE -> 10
                Material.IRON_PICKAXE -> 7
                Material.STONE_PICKAXE -> 5
                Material.WOODEN_PICKAXE -> 4
                Material.COPPER_PICKAXE -> 5
                else -> 0
            }.toDouble()
        }

        fun getPickaxeDamage(itemType: CustomItemType): Double {
            return when (itemType) {
                CustomItemType.TIN_PICKAXE -> 4
                CustomItemType.SILVER_PICKAXE -> 6
                CustomItemType.BRONZE_PICKAXE -> 7
                CustomItemType.STEEL_PICKAXE -> 10
                CustomItemType.ROSE_GOLD_PICKAXE, CustomItemType.MITHRIL_PICKAXE, CustomItemType.COBALT_PICKAXE -> 12
                CustomItemType.PLATINUM_PICKAXE -> 14
                CustomItemType.TITANIUM_PICKAXE, CustomItemType.TUNGSTEN_PICKAXE -> 15
                CustomItemType.ADAMANTIUM_PICKAXE, CustomItemType.PALLADIUM_PICKAXE -> 18
                CustomItemType.ORICHALCUM_PICKAXE, CustomItemType.AETHERIUM_PICKAXE -> 24
                else -> 0
            }.toDouble()
        }

        fun getPickaxeRating(material: Material): Int {
            return when (material) {
                Material.NETHERITE_PICKAXE -> ToolStats.NETHERITE.power
                // Material.DIAMOND_PICKAXE -> ToolStats.DIAMOND.power
                Material.GOLDEN_PICKAXE -> ToolStats.GOLD.power
                Material.IRON_PICKAXE -> ToolStats.IRON.power
                // Material.STONE_PICKAXE -> ToolStats.STONE.power
                Material.COPPER_PICKAXE -> ToolStats.COPPER.power
                Material.WOODEN_PICKAXE -> ToolStats.WOOD.power
                else -> 0
            }
        }

        @JvmField
        var PICKAXE_ATTACK_SPEED_DEBUFF: Double = -0.7
    }
}
