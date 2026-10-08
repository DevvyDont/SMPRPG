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
import xyz.devvydont.smprpg.items.interfaces.IDamageFromCrops
import xyz.devvydont.smprpg.items.interfaces.IRepairable
import xyz.devvydont.smprpg.items.interfaces.ISkillRequirement
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.skills.SkillType
import xyz.devvydont.smprpg.util.items.ToolStats

class ItemHoe(itemService: ItemService, material: Material) : VanillaAttributeItem(itemService, material),
    IBreakableEquipment, IRepairable, ISkillRequirement, IDamageFromCrops {

    override val itemClassification: ItemClassification get() = ItemClassification.HOE
    override val repairMaterial: MutableCollection<ItemStack>
        get() = when (material) {
            Material.NETHERITE_HOE -> mutableListOf(itemService.getCustomItem(Material.NETHERITE_INGOT))
            // Material.DIAMOND_HOE -> mutableListOf(itemService.getCustomItem(Material.DIAMOND))
            Material.GOLDEN_HOE -> mutableListOf(itemService.getCustomItem(Material.GOLD_INGOT))
            Material.IRON_HOE -> mutableListOf(itemService.getCustomItem(Material.IRON_INGOT))
            // Material.STONE_HOE -> mutableListOf(itemService.getCustomItem(Material.COBBLESTONE))
            Material.COPPER_HOE -> mutableListOf(itemService.getCustomItem(Material.COPPER_INGOT))
            Material.WOODEN_HOE -> mutableListOf(itemService.getCustomItem(Material.OAK_PLANKS), itemService.getCustomItem(Material.BIRCH_PLANKS), itemService.getCustomItem(Material.SPRUCE_PLANKS),
                itemService.getCustomItem(Material.JUNGLE_PLANKS), itemService.getCustomItem(Material.DARK_OAK_PLANKS), itemService.getCustomItem(Material.ACACIA_PLANKS),
                itemService.getCustomItem(Material.CRIMSON_PLANKS), itemService.getCustomItem(Material.WARPED_PLANKS), itemService.getCustomItem(Material.MANGROVE_PLANKS),
                itemService.getCustomItem(Material.CHERRY_PLANKS), itemService.getCustomItem(Material.PALE_OAK_PLANKS), itemService.getCustomItem(Material.BAMBOO_PLANKS))
            else -> mutableListOf()
        }
    override val skillRequirements: MutableMap<SkillType, Int> get() = when (material) {
        Material.NETHERITE_HOE -> mutableMapOf(Pair(SkillType.FARMING, ToolStats.NETHERITE.skillReqLevel))
        Material.GOLDEN_HOE -> mutableMapOf(Pair(SkillType.FARMING, ToolStats.GOLD.skillReqLevel))
        Material.IRON_HOE -> mutableMapOf(Pair(SkillType.FARMING, ToolStats.IRON.skillReqLevel))
        Material.COPPER_HOE -> mutableMapOf(Pair(SkillType.FARMING, ToolStats.COPPER.skillReqLevel))
        else -> mutableMapOf()
    }

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.STRENGTH, getHoeDamage(material)),
            MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, getHoeAttackSpeedDebuff(material)),
            AdditiveAttributeEntry(AttributeWrapper.MINING_SPEED, getHoeSpeed(material)),
            AdditiveAttributeEntry(AttributeWrapper.FARMING_FORTUNE, getHoeFortune(material))
        )
    }

    override fun getPowerRating(): Int { return getHoeRating(material) }

    override fun getActiveSlot(): EquipmentSlotGroup { return EquipmentSlotGroup.MAINHAND }


    override fun getMaxDurability(): Int {
        return when (material) {
            Material.NETHERITE_HOE -> ToolStats.NETHERITE.durability
            // Material.DIAMOND_HOE -> ToolStats.DIAMOND.durability
            Material.GOLDEN_HOE -> ToolStats.GOLD.durability
            Material.IRON_HOE -> ToolStats.IRON.durability
            // Material.STONE_HOE -> ToolStats.STONE.durability
            Material.COPPER_HOE -> ToolStats.COPPER.durability
            Material.WOODEN_HOE -> ToolStats.WOOD.durability
            else -> 50_000
        }
    }

    companion object {
        fun getHoeFortune(material: Material): Double {
            return when (material) {
                Material.NETHERITE_HOE -> ToolStats.NETHERITE.fortune
                // Material.DIAMOND_HOE -> ToolStats.DIAMOND.fortune
                Material.GOLDEN_HOE -> ToolStats.GOLD.fortune
                Material.IRON_HOE -> ToolStats.IRON.fortune
                // Material.STONE_HOE -> ToolStats.STONE.fortune
                Material.COPPER_HOE -> ToolStats.COPPER.fortune
                Material.WOODEN_HOE -> ToolStats.WOOD.fortune
                else -> 0
            }.toDouble()
        }

        fun getHoeSpeed(material: Material): Double {
            return when (material) {
                Material.NETHERITE_HOE -> ToolStats.NETHERITE.speed
                // Material.DIAMOND_HOE -> ToolStats.DIAMOND.speed
                Material.GOLDEN_HOE -> ToolStats.GOLD.speed
                Material.IRON_HOE -> ToolStats.IRON.speed
                // Material.STONE_HOE -> ToolStats.STONE.speed
                Material.COPPER_HOE -> ToolStats.COPPER.speed
                Material.WOODEN_HOE -> ToolStats.WOOD.speed
                else -> 0
            }.toDouble()
        }

        fun getHoeDamage(material: Material): Double {
            return when (material) {
                Material.NETHERITE_HOE -> 20
                Material.DIAMOND_HOE -> 16
                Material.GOLDEN_HOE -> 12
                Material.IRON_HOE -> 10
                Material.STONE_HOE -> 5
                Material.COPPER_HOE -> 5
                Material.WOODEN_HOE -> 3
                else -> 0
            }.toDouble()
        }

        fun getHoeDamage(itemType: CustomItemType): Double {
            return when (itemType) {
                CustomItemType.TIN_HOE -> 4
                CustomItemType.SILVER_HOE -> 7
                CustomItemType.STEEL_HOE -> 13
                CustomItemType.ROSE_GOLD_HOE, CustomItemType.MITHRIL_HOE, CustomItemType.COBALT_HOE -> 14
                CustomItemType.TITANIUM_HOE, CustomItemType.TUNGSTEN_HOE, CustomItemType.PALLADIUM_HOE -> 16
                CustomItemType.PLATINUM_HOE -> 18
                CustomItemType.ADAMANTIUM_HOE, CustomItemType.ORICHALCUM_HOE, CustomItemType.AETHERIUM_HOE -> 20
                else -> 0
            }.toDouble()
        }

        fun getHoeAttackSpeedDebuff(material: Material): Double {
            return when (material) {
                Material.NETHERITE_HOE -> -0.05
                Material.DIAMOND_HOE -> -0.15
                Material.IRON_HOE -> -0.20
                Material.STONE_HOE, Material.COPPER_HOE -> -0.25
                Material.WOODEN_HOE, Material.GOLDEN_HOE -> -0.35
                else -> 0
            }.toDouble()
        }

        fun getHoeAttackSpeedDebuff(itemType: CustomItemType): Double {
            return when (itemType) {
                CustomItemType.SILVER_HOE, CustomItemType.TIN_HOE, CustomItemType.ROSE_GOLD_HOE, CustomItemType.MITHRIL_HOE -> -0.25
                CustomItemType.BRONZE_HOE, CustomItemType.STEEL_HOE -> -0.2
                CustomItemType.TITANIUM_HOE, CustomItemType.ADAMANTIUM_HOE -> -0.15
                CustomItemType.COBALT_HOE -> -0.075
                CustomItemType.DRAGONSTEEL_HOE -> -0.025
                else -> 0
            }.toDouble()
        }

        fun getHoeRating(material: Material): Int {
            return when (material) {
                Material.NETHERITE_HOE -> ToolStats.NETHERITE.power
                // Material.DIAMOND_HOE -> ToolStats.DIAMOND.power
                Material.GOLDEN_HOE -> ToolStats.GOLD.power
                Material.IRON_HOE -> ToolStats.IRON.power
                // Material.STONE_HOE -> ToolStats.STONE.power
                Material.COPPER_HOE -> ToolStats.COPPER.power
                Material.WOODEN_HOE -> ToolStats.WOOD.power
                else -> 0
            }
        }
    }
}
