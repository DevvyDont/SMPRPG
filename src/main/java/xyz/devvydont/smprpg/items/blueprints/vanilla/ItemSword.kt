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

class ItemSword(itemService: ItemService, material: Material) : VanillaAttributeItem(itemService, material),
    IBreakableEquipment, IRepairable, ISkillRequirement {
    override val itemClassification: ItemClassification
        get() {
            if (material == Material.TRIDENT) return ItemClassification.TRIDENT
            return ItemClassification.SWORD
        }
    override val repairMaterial: MutableCollection<ItemStack>
        get() = when (material) {
            Material.NETHERITE_SWORD -> mutableListOf(itemService.getCustomItem(Material.NETHERITE_INGOT))
            // Material.DIAMOND_SWORD -> mutableListOf(itemService.getCustomItem(Material.DIAMOND))
            Material.GOLDEN_SWORD -> mutableListOf(itemService.getCustomItem(Material.GOLD_INGOT))
            Material.IRON_SWORD -> mutableListOf(itemService.getCustomItem(Material.IRON_INGOT))
            Material.TRIDENT -> mutableListOf(itemService.getCustomItem(CustomItemType.TRIDENTITE))
            // Material.STONE_SWORD -> mutableListOf(itemService.getCustomItem(Material.COBBLESTONE))
            Material.COPPER_SWORD -> mutableListOf(itemService.getCustomItem(Material.COPPER_INGOT))
            Material.WOODEN_SWORD -> mutableListOf(itemService.getCustomItem(Material.OAK_PLANKS), itemService.getCustomItem(Material.BIRCH_PLANKS), itemService.getCustomItem(Material.SPRUCE_PLANKS),
                itemService.getCustomItem(Material.JUNGLE_PLANKS), itemService.getCustomItem(Material.DARK_OAK_PLANKS), itemService.getCustomItem(Material.ACACIA_PLANKS),
                itemService.getCustomItem(Material.CRIMSON_PLANKS), itemService.getCustomItem(Material.WARPED_PLANKS), itemService.getCustomItem(Material.MANGROVE_PLANKS),
                itemService.getCustomItem(Material.CHERRY_PLANKS), itemService.getCustomItem(Material.PALE_OAK_PLANKS), itemService.getCustomItem(Material.BAMBOO_PLANKS))
            else -> mutableListOf()
        }
    override val skillRequirements: MutableMap<SkillType, Int> get() = when (material) {
        Material.NETHERITE_SWORD -> mutableMapOf(Pair(SkillType.COMBAT, ToolStats.NETHERITE.skillReqLevel))
        Material.GOLDEN_SWORD -> mutableMapOf(Pair(SkillType.COMBAT, ToolStats.GOLD.skillReqLevel))
        Material.IRON_SWORD -> mutableMapOf(Pair(SkillType.COMBAT, ToolStats.IRON.skillReqLevel))
        Material.COPPER_SWORD -> mutableMapOf(Pair(SkillType.COMBAT, ToolStats.COPPER.skillReqLevel))
        else -> mutableMapOf()
    }

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.STRENGTH, getSwordDamage(material)),
            MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, SWORD_ATTACK_SPEED_DEBUFF)
        )
    }

    override fun getPowerRating(): Int { return getSwordRating(material) }

    override fun getActiveSlot(): EquipmentSlotGroup { return EquipmentSlotGroup.MAINHAND }

    override fun getMaxDurability(): Int {
        return when (material) {
            Material.NETHERITE_SWORD -> ToolStats.NETHERITE.durability
            // Material.DIAMOND_SWORD -> ToolStats.DIAMOND.durability
            Material.GOLDEN_SWORD -> ToolStats.GOLD.durability
            Material.IRON_SWORD -> ToolStats.IRON.durability
            // Material.STONE_SWORD -> ToolStats.STONE.durability
            Material.COPPER_SWORD -> ToolStats.COPPER.durability
            Material.WOODEN_SWORD -> ToolStats.WOOD.durability
            else -> 50000
        }
    }

    companion object {
        @JvmStatic
        fun getSwordDamage(material: Material): Double {
            return when (material) {
                Material.NETHERITE_SWORD -> 80
                Material.DIAMOND_SWORD -> 50
                Material.GOLDEN_SWORD -> 35
                Material.TRIDENT, Material.IRON_SWORD -> 30
                Material.STONE_SWORD -> 20
                Material.WOODEN_SWORD -> 15
                Material.COPPER_SWORD -> 20
                else -> 0
            }.toDouble()
        }

        @JvmStatic
        fun getSwordDamage(itemType: CustomItemType): Double {
            return when (itemType) {
                CustomItemType.SILVER_SWORD -> 25
                CustomItemType.TIN_SWORD -> 17
                CustomItemType.BRONZE_SWORD -> 30
                CustomItemType.STEEL_SWORD -> 35
                CustomItemType.ROSE_GOLD_SWORD, CustomItemType.MITHRIL_SWORD, CustomItemType.COBALT_SWORD -> 40
                CustomItemType.PLATINUM_SWORD -> 45
                CustomItemType.TITANIUM_SWORD, CustomItemType.TUNGSTEN_SWORD -> 50
                CustomItemType.ADAMANTIUM_SWORD, CustomItemType.PALLADIUM_SWORD -> 65
                CustomItemType.ORICHALCUM_SWORD, CustomItemType.AETHERIUM_SWORD -> 70
                else -> 0
            }.toDouble()
        }

        @JvmStatic
        fun getSwordRating(material: Material): Int {
            return when (material) {
                Material.NETHERITE_SWORD -> ToolStats.NETHERITE.power
                // Material.DIAMOND_SWORD -> ToolStats.DIAMOND.power
                Material.GOLDEN_SWORD -> ToolStats.GOLD.power
                Material.TRIDENT, Material.IRON_SWORD -> ToolStats.IRON.power
                // Material.STONE_SWORD -> ToolStats.STONE.power
                Material.COPPER_SWORD -> ToolStats.COPPER.power
                Material.WOODEN_SWORD -> ToolStats.WOOD.power
                else -> 1
            }
        }

        @JvmField
        var SWORD_ATTACK_SPEED_DEBUFF: Double = -0.6
    }
}
