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

class ItemSpear(itemService: ItemService, material: Material) : VanillaAttributeItem(itemService, material),
    IBreakableEquipment, IRepairable, ISkillRequirement {

    override val itemClassification: ItemClassification  get() = ItemClassification.SPEAR
    override val repairMaterial: MutableCollection<ItemStack>
        get() = when (material) {
            Material.NETHERITE_SPEAR -> mutableListOf(itemService.getCustomItem(Material.NETHERITE_INGOT))
            // Material.DIAMOND_SPEAR -> mutableListOf(itemService.getCustomItem(Material.DIAMOND))
            Material.GOLDEN_SPEAR -> mutableListOf(itemService.getCustomItem(Material.GOLD_INGOT))
            Material.IRON_SPEAR -> mutableListOf(itemService.getCustomItem(Material.IRON_INGOT))
            // Material.STONE_SPEAR -> mutableListOf(itemService.getCustomItem(Material.COBBLESTONE))
            Material.COPPER_SPEAR -> mutableListOf(itemService.getCustomItem(Material.COPPER_INGOT))
            Material.WOODEN_SPEAR -> mutableListOf(itemService.getCustomItem(Material.OAK_PLANKS), itemService.getCustomItem(Material.BIRCH_PLANKS), itemService.getCustomItem(Material.SPRUCE_PLANKS),
                itemService.getCustomItem(Material.JUNGLE_PLANKS), itemService.getCustomItem(Material.DARK_OAK_PLANKS), itemService.getCustomItem(Material.ACACIA_PLANKS),
                itemService.getCustomItem(Material.CRIMSON_PLANKS), itemService.getCustomItem(Material.WARPED_PLANKS), itemService.getCustomItem(Material.MANGROVE_PLANKS),
                itemService.getCustomItem(Material.CHERRY_PLANKS), itemService.getCustomItem(Material.PALE_OAK_PLANKS), itemService.getCustomItem(Material.BAMBOO_PLANKS))
            else -> mutableListOf()
        }
    override val skillRequirements: MutableMap<SkillType, Int> get() = when (material) {
        Material.NETHERITE_SPEAR -> mutableMapOf(Pair(SkillType.COMBAT, ToolStats.NETHERITE.skillReqLevel))
        Material.GOLDEN_SPEAR -> mutableMapOf(Pair(SkillType.COMBAT, ToolStats.GOLD.skillReqLevel))
        Material.IRON_SPEAR -> mutableMapOf(Pair(SkillType.COMBAT, ToolStats.IRON.skillReqLevel))
        Material.COPPER_SPEAR -> mutableMapOf(Pair(SkillType.COMBAT, ToolStats.COPPER.skillReqLevel))
        else -> mutableMapOf()
    }

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.STRENGTH, getSpearDamage(material)),
            MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, getSpearRecovery(material))
        )
    }

    override fun getPowerRating(): Int { return getSpearRating(material) }

    override fun getActiveSlot(): EquipmentSlotGroup { return EquipmentSlotGroup.MAINHAND }

    override fun getMaxDurability(): Int {
        return when (material) {
            Material.NETHERITE_SPEAR -> ToolStats.NETHERITE.durability
            // Material.DIAMOND_SPEAR -> ToolStats.DIAMOND.durability
            Material.GOLDEN_SPEAR -> ToolStats.GOLD.durability
            Material.IRON_SPEAR -> ToolStats.IRON.durability
            // Material.STONE_SPEAR -> ToolStats.STONE.durability
            Material.COPPER_SPEAR -> ToolStats.COPPER.durability
            Material.WOODEN_SPEAR -> ToolStats.WOOD.durability
            else -> 50000
        }
    }

    companion object {
        fun getSpearDamage(material: Material): Double {
            return when (material) {
                Material.NETHERITE_SPEAR -> 40
                Material.DIAMOND_SPEAR -> 25
                Material.GOLDEN_SPEAR -> 18
                Material.IRON_SPEAR -> 15
                Material.STONE_SPEAR, Material.COPPER_SPEAR -> 10
                Material.WOODEN_SPEAR -> 8
                else -> 0
            }.toDouble()
        }

        fun getSpearDamage(itemType: CustomItemType): Double {
            return when (itemType) {
                CustomItemType.TIN_SPEAR -> 9.0
                CustomItemType.SILVER_SPEAR -> 13.0
                CustomItemType.BRONZE_SPEAR -> 15.0
                CustomItemType.STEEL_SPEAR -> 18.0
                CustomItemType.ROSE_GOLD_SPEAR, CustomItemType.MITHRIL_SPEAR, CustomItemType.COBALT_SPEAR -> 20.0
                CustomItemType.TITANIUM_SPEAR, CustomItemType.TUNGSTEN_SPEAR -> 25.0
                CustomItemType.ADAMANTIUM_SPEAR, CustomItemType.PALLADIUM_SPEAR -> 33.0
                CustomItemType.PLATINUM_SPEAR -> 34.0
                CustomItemType.ORICHALCUM_SPEAR, CustomItemType.AETHERIUM_SPEAR -> 35.0
                CustomItemType.DRAGONSTEEL_SPEAR -> 60.0
                else -> 0
            }.toDouble()
        }

        fun getSpearRating(material: Material): Int {
            return when (material) {
                Material.NETHERITE_SPEAR -> ToolStats.NETHERITE.power
                // Material.DIAMOND_SPEAR -> ToolStats.DIAMOND.power
                Material.GOLDEN_SPEAR -> ToolStats.GOLD.power
                Material.IRON_SPEAR -> ToolStats.IRON.power
                // Material.STONE_SPEAR -> ToolStats.STONE.power
                Material.COPPER_SPEAR -> ToolStats.COPPER.power
                Material.WOODEN_SPEAR -> ToolStats.WOOD.power
                else -> 1
            }
        }

        fun getSpearRecovery(material: Material): Double {
            return when (material) {
                Material.NETHERITE_SPEAR -> -0.85
                Material.DIAMOND_SPEAR -> -0.8
                Material.IRON_SPEAR, Material.GOLDEN_SPEAR -> -0.75
                Material.COPPER_SPEAR -> -0.7
                Material.STONE_SPEAR -> -.65
                Material.WOODEN_SPEAR -> -.6
                else -> -1.0
            }
        }

        fun getSpearRecovery(itemType: CustomItemType): Double {
            return when (itemType) {
                CustomItemType.TIN_SPEAR -> -0.6
                CustomItemType.SILVER_SPEAR -> -0.65
                CustomItemType.BRONZE_SPEAR -> -0.7
                CustomItemType.STEEL_SPEAR, CustomItemType.MITHRIL_SPEAR, CustomItemType.TITANIUM_SPEAR, CustomItemType.ADAMANTIUM_SPEAR -> -0.75
                CustomItemType.COBALT_SPEAR -> -0.75
                CustomItemType.TUNGSTEN_SPEAR -> -0.8
                CustomItemType.ORICHALCUM_SPEAR -> -0.85
                CustomItemType.DRAGONSTEEL_SPEAR -> -0.9
                else -> -1.0
            }
        }
    }
}
