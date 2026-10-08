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

class ItemAxe(itemService: ItemService, material: Material) : VanillaAttributeItem(itemService, material),
    IBreakableEquipment, IRepairable, ISkillRequirement {
    override val itemClassification: ItemClassification get() = ItemClassification.AXE
    override val skillRequirements: MutableMap<SkillType, Int> get() = when (material) {
        Material.NETHERITE_AXE -> mutableMapOf(Pair(SkillType.WOODCUTTING, ToolStats.NETHERITE.skillReqLevel))
        Material.GOLDEN_AXE -> mutableMapOf(Pair(SkillType.WOODCUTTING, ToolStats.GOLD.skillReqLevel))
        Material.IRON_AXE -> mutableMapOf(Pair(SkillType.WOODCUTTING, ToolStats.IRON.skillReqLevel))
        Material.COPPER_AXE -> mutableMapOf(Pair(SkillType.WOODCUTTING, ToolStats.COPPER.skillReqLevel))
        else -> mutableMapOf()
    }

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        val lumbering: Double = getAxeLumbering(material)
        // Mild hack, Minecraft will display zeroed out stats if present, even if 0 value.
        if (lumbering > 0) {
            return mutableListOf(
                AdditiveAttributeEntry(AttributeWrapper.MINING_POWER, getAxePower(material)),
                AdditiveAttributeEntry(AttributeWrapper.STRENGTH, getAxeDamage(material)),
                MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, AXE_ATTACK_SPEED_DEBUFF),
                AdditiveAttributeEntry(AttributeWrapper.MINING_SPEED, getAxeSpeed(material)),
                AdditiveAttributeEntry(AttributeWrapper.WOODCUTTING_FORTUNE, getAxeFortune(material)),
                AdditiveAttributeEntry(AttributeWrapper.LUMBERING, getAxeLumbering(material))
            )
        } else {
            return mutableListOf(
                AdditiveAttributeEntry(AttributeWrapper.MINING_POWER, getAxePower(material)),
                AdditiveAttributeEntry(AttributeWrapper.STRENGTH, getAxeDamage(material)),
                MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, AXE_ATTACK_SPEED_DEBUFF),
                AdditiveAttributeEntry(AttributeWrapper.MINING_SPEED, getAxeSpeed(material)),
                AdditiveAttributeEntry(AttributeWrapper.WOODCUTTING_FORTUNE, getAxeFortune(material))
            )
        }
    }

    override fun getPowerRating(): Int {
        return getAxeRating(material)
    }

    override fun getActiveSlot(): EquipmentSlotGroup {
        return EquipmentSlotGroup.MAINHAND
    }

    override fun getMaxDurability(): Int {
        return when (material) {
            Material.NETHERITE_AXE -> ToolStats.NETHERITE.durability
            // Material.DIAMOND_AXE -> ToolStats.DIAMOND.durability
            Material.GOLDEN_AXE -> ToolStats.GOLD.durability
            Material.IRON_AXE -> ToolStats.IRON.durability
            // Material.STONE_AXE -> ToolStats.STONE.durability
            Material.WOODEN_AXE -> ToolStats.WOOD.durability
            Material.COPPER_AXE -> ToolStats.COPPER.durability
            else -> 50_000
        }
    }

    override val repairMaterial: MutableCollection<ItemStack>
        get() = when (material) {
            Material.NETHERITE_AXE -> mutableListOf(itemService.getCustomItem(Material.NETHERITE_INGOT))
            // Material.DIAMOND_AXE -> mutableListOf(itemService.getCustomItem(Material.DIAMOND))
            Material.GOLDEN_AXE -> mutableListOf(itemService.getCustomItem(Material.GOLD_INGOT))
            Material.IRON_AXE -> mutableListOf(itemService.getCustomItem(Material.IRON_INGOT))
            // Material.STONE_AXE -> mutableListOf(itemService.getCustomItem(Material.COBBLESTONE))
            Material.COPPER_AXE -> mutableListOf(itemService.getCustomItem(Material.COPPER_INGOT))
            Material.WOODEN_AXE -> mutableListOf(itemService.getCustomItem(Material.OAK_PLANKS), itemService.getCustomItem(Material.BIRCH_PLANKS), itemService.getCustomItem(Material.SPRUCE_PLANKS),
                itemService.getCustomItem(Material.JUNGLE_PLANKS), itemService.getCustomItem(Material.DARK_OAK_PLANKS), itemService.getCustomItem(Material.ACACIA_PLANKS),
                itemService.getCustomItem(Material.CRIMSON_PLANKS), itemService.getCustomItem(Material.WARPED_PLANKS), itemService.getCustomItem(Material.MANGROVE_PLANKS),
                itemService.getCustomItem(Material.CHERRY_PLANKS), itemService.getCustomItem(Material.PALE_OAK_PLANKS), itemService.getCustomItem(Material.BAMBOO_PLANKS))
            else -> null
        }!!

    companion object {
        fun getAxeFortune(material: Material): Double {
            return when (material) {
                Material.NETHERITE_AXE -> ToolStats.NETHERITE.fortune
                // Material.DIAMOND_AXE -> ToolStats.DIAMOND.fortune
                Material.GOLDEN_AXE -> ToolStats.GOLD.fortune
                Material.IRON_AXE -> ToolStats.IRON.fortune
                // Material.STONE_AXE -> ToolStats.STONE.fortune
                Material.COPPER_AXE -> ToolStats.COPPER.fortune
                Material.WOODEN_AXE -> ToolStats.WOOD.fortune
                else -> 0
            }.toDouble()
        }

        fun getAxeSpeed(material: Material): Double {
            return when (material) {
                Material.NETHERITE_AXE -> ToolStats.NETHERITE.speed
                // Material.DIAMOND_AXE -> ToolStats.DIAMOND.speed
                Material.GOLDEN_AXE -> ToolStats.GOLD.speed
                Material.IRON_AXE -> ToolStats.IRON.speed
                // Material.STONE_AXE -> ToolStats.STONE.speed
                Material.COPPER_AXE -> ToolStats.COPPER.speed
                Material.WOODEN_AXE -> ToolStats.WOOD.speed
                else -> 0
            }.toDouble()
        }

        fun getAxePower(material: Material): Double {
            return when (material) {
                Material.NETHERITE_AXE -> ToolStats.NETHERITE.miningPower
                // Material.DIAMOND_AXE -> ToolStats.DIAMOND.miningPower
                Material.GOLDEN_AXE -> ToolStats.GOLD.miningPower
                Material.IRON_AXE -> ToolStats.IRON.miningPower
                // Material.STONE_AXE -> ToolStats.STONE.miningPower
                Material.COPPER_AXE -> ToolStats.COPPER.miningPower
                Material.WOODEN_AXE -> ToolStats.WOOD.miningPower
                else -> 0
            }.toDouble()
        }

        @JvmStatic
        fun getAxeDamage(material: Material): Double {
            return when (material) {
                Material.NETHERITE_AXE -> 100
                Material.DIAMOND_AXE -> 65
                Material.GOLDEN_AXE -> 40
                Material.IRON_AXE -> 35
                Material.STONE_AXE -> 30
                Material.COPPER_AXE -> 30
                Material.WOODEN_AXE -> 20
                else -> 0
            }.toDouble()
        }

        fun getAxeDamage(itemType: CustomItemType): Double {
            return when (itemType) {
                CustomItemType.TIN_AXE -> 25
                CustomItemType.BRONZE_AXE, CustomItemType.SILVER_AXE -> 35
                CustomItemType.STEEL_AXE -> 50
                CustomItemType.ROSE_GOLD_AXE, CustomItemType.MITHRIL_AXE, CustomItemType.COBALT_AXE -> 55
                CustomItemType.PLATINUM_AXE -> 60
                CustomItemType.TITANIUM_AXE, CustomItemType.TUNGSTEN_AXE -> 65
                CustomItemType.ADAMANTIUM_AXE, CustomItemType.PALLADIUM_AXE -> 80
                CustomItemType.ORICHALCUM_AXE, CustomItemType.AETHERIUM_AXE -> 90
                else -> 0
            }.toDouble()
        }

        fun getAxeRating(material: Material): Int {
            return when (material) {
                Material.NETHERITE_AXE -> ToolStats.NETHERITE.power
                // Material.DIAMOND_AXE -> ToolStats.DIAMOND.power
                Material.GOLDEN_AXE -> ToolStats.GOLD.power
                Material.IRON_AXE -> ToolStats.IRON.power
                // Material.STONE_AXE -> ToolStats.STONE.power
                Material.COPPER_AXE -> ToolStats.COPPER.power
                Material.WOODEN_AXE -> ToolStats.WOOD.power
                else -> 1
            }
        }

        fun getAxeLumbering(material: Material): Double {
            return when (material) {
                Material.NETHERITE_AXE -> 2
                Material.DIAMOND_AXE -> 1
                else -> 0
            }.toDouble()
        }

        fun getAxeLumbering(itemType: CustomItemType): Double {
            return when (itemType) {
                CustomItemType.TITANIUM_AXE, CustomItemType.ADAMANTIUM_AXE, CustomItemType.COBALT_AXE, CustomItemType.ORICHALCUM_AXE, CustomItemType.PALLADIUM_AXE, CustomItemType.PLATINUM_AXE -> 1
                CustomItemType.AETHERIUM_AXE -> 2
                CustomItemType.DRAGONSTEEL_AXE -> 3
                else -> 0
            }.toDouble()
        }

        @JvmField
        var AXE_ATTACK_SPEED_DEBUFF: Double = -0.8
    }
}
