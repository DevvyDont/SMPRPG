package xyz.devvydont.smprpg.items.blueprints.farming

import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.momirealms.craftengine.bukkit.api.BukkitAdaptor
import net.momirealms.craftengine.core.block.property.IntegerProperty
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.block.data.Ageable
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.block.CraftEngineBlockEnums
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.base.CustomAttributeItem
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.items.interfaces.IDamageFromCrops
import xyz.devvydont.smprpg.items.interfaces.IFooterDescribable
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden
import xyz.devvydont.smprpg.items.interfaces.ISkillRequirement
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.skills.SkillType
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import net.momirealms.craftengine.core.util.Key as CEKey

class ProgressiveHoeBlueprint(itemService: ItemService, type: CustomItemType) : CustomAttributeItem(itemService, type),
    IModelOverridden, ISkillRequirement, IFooterDescribable, IBreakableEquipment, IDamageFromCrops, Listener {

    override val itemClassification: ItemClassification get() = ItemClassification.HOE
    override val skillRequirements: MutableMap<SkillType, Int> get() = mutableMapOf(Pair(SkillType.FARMING, 25))

    override fun getAttributeModifiers(item: ItemStack): Collection<AttributeEntry> {
        val retList = mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.MINING_SPEED, 1_000.0),
            AdditiveAttributeEntry(AttributeWrapper.FARMING_FORTUNE, 100.0),
        )
        val level = item.persistentDataContainer.getOrDefault(HOE_LEVEL_KEY, PersistentDataType.INTEGER, 0)
        if (level > 0) {
            when (type) {
                CustomItemType.WHEAT_HOE -> {
                    retList.add(AdditiveAttributeEntry(AttributeWrapper.WHEAT_FORTUNE, level * YIELD_PER_LEVEL))
                    retList.add(AdditiveAttributeEntry(AttributeWrapper.FARMING_PROFICIENCY, level * PROFICIENCY_PER_LEVEL))
                    retList.add(AdditiveAttributeEntry(AttributeWrapper.CRITTER_CHANCE, level * UPROOTING_PER_LEVEL))
                }
                CustomItemType.POTATO_HOE -> {
                    retList.add(AdditiveAttributeEntry(AttributeWrapper.POTATO_FORTUNE, level * YIELD_PER_LEVEL))
                    retList.add(AdditiveAttributeEntry(AttributeWrapper.FARMING_PROFICIENCY, level * PROFICIENCY_PER_LEVEL))
                    retList.add(AdditiveAttributeEntry(AttributeWrapper.CRITTER_CHANCE, level * UPROOTING_PER_LEVEL))
                }
                CustomItemType.ONION_HOE -> {
                    retList.add(AdditiveAttributeEntry(AttributeWrapper.ONION_FORTUNE, level * YIELD_PER_LEVEL))
                    retList.add(AdditiveAttributeEntry(AttributeWrapper.FARMING_PROFICIENCY, level * PROFICIENCY_PER_LEVEL))
                    retList.add(AdditiveAttributeEntry(AttributeWrapper.CRITTER_CHANCE, level * UPROOTING_PER_LEVEL))
                }
                CustomItemType.CARROT_HOE -> {
                    retList.add(AdditiveAttributeEntry(AttributeWrapper.CARROT_FORTUNE, level * YIELD_PER_LEVEL))
                    retList.add(AdditiveAttributeEntry(AttributeWrapper.FARMING_PROFICIENCY, level * PROFICIENCY_PER_LEVEL))
                    retList.add(AdditiveAttributeEntry(AttributeWrapper.CRITTER_CHANCE, level * UPROOTING_PER_LEVEL))
                }
                CustomItemType.MELON_HOE -> {
                    retList.add(AdditiveAttributeEntry(AttributeWrapper.MELON_FORTUNE, level * YIELD_PER_LEVEL))
                    retList.add(AdditiveAttributeEntry(AttributeWrapper.FARMING_PROFICIENCY, level * PROFICIENCY_PER_LEVEL))
                    retList.add(AdditiveAttributeEntry(AttributeWrapper.CRITTER_CHANCE, level * UPROOTING_PER_LEVEL))
                }
                else -> {}
            }
        }
        return retList
    }

    override fun getPowerRating(): Int {
        return 25
    }

    override fun getActiveSlot(): EquipmentSlotGroup {
        return EquipmentSlotGroup.MAINHAND
    }

    override fun getDisplayKey(): Key {
        return IModelOverridden.ofItemTypeInDirectory(type, "hoes")
    }

    override fun updateItemData(itemStack: ItemStack) {
        if (itemStack.persistentDataContainer.getOrDefault(HOE_LEVEL_KEY, PersistentDataType.INTEGER, -1) == -1)
            itemStack.editPersistentDataContainer { pdc -> pdc.set(HOE_LEVEL_KEY, PersistentDataType.INTEGER, 1) }
        super.updateItemData(itemStack)
    }

    override fun getFooter(itemStack: ItemStack): List<Component> {
        val crop = when(type) {
            CustomItemType.WHEAT_HOE -> "Wheat"
            CustomItemType.POTATO_HOE -> "Potatoes"
            CustomItemType.ONION_HOE -> "Onions"
            CustomItemType.CARROT_HOE -> "Carrots"
            CustomItemType.MELON_HOE -> "Melons"
            else -> "Unknown Crop"
        }
        var xpBar = ComponentUtils.EMPTY
        val cropsHarvested = itemStack.persistentDataContainer.getOrDefault(CROP_COUNT_KEY, PersistentDataType.INTEGER, 0)
        var progress = cropsHarvested / CROPS_PER_LEVEL.toDouble()
        val hoeLevel = itemStack.persistentDataContainer.getOrDefault(HOE_LEVEL_KEY, PersistentDataType.INTEGER, 1)
        for (i in 0..19) {
            if (progress > 0.05)
                xpBar = xpBar.append(ComponentUtils.create("=", NamedTextColor.DARK_GREEN))
            else
                xpBar = xpBar.append(ComponentUtils.create("=", NamedTextColor.DARK_GRAY))
            progress -= 0.05
        }
        return listOf(
            ComponentUtils.merge(
                ComponentUtils.create("Gains XP from harvesting "),
                ComponentUtils.create(crop, NamedTextColor.GOLD)
            ),
            ComponentUtils.EMPTY,
            ComponentUtils.create("Level $hoeLevel", NamedTextColor.AQUA, TextDecoration.BOLD),
            xpBar,
            ComponentUtils.merge(
                ComponentUtils.create("$cropsHarvested / $CROPS_PER_LEVEL "),
                ComponentUtils.create(crop, NamedTextColor.GRAY),
                ComponentUtils.create(" to next level!", NamedTextColor.DARK_GRAY)
            )
        )
    }

    override fun getMaxDurability(): Int {
        return 2_048
    }

    companion object {
        const val CROPS_PER_LEVEL = 500
        const val YIELD_PER_LEVEL = 2.0
        const val UPROOTING_PER_LEVEL = 1.0
        const val PROFICIENCY_PER_LEVEL = 1.0
        const val MAX_LEVEL = 500

        val CROP_COUNT_KEY = NamespacedKey(SMPRPG.plugin, "crop_count")
        val HOE_LEVEL_KEY = NamespacedKey(SMPRPG.plugin, "hoe_level")

        fun incrementCropProgress(item: ItemStack, player: Player?) {
            if (player != null) {
                if (!ItemService.meetsRequirements(item, player)) return
            }
            val blueprint = ItemService.blueprint(item)
            val cropCount = item.persistentDataContainer.getOrDefault(CROP_COUNT_KEY, PersistentDataType.INTEGER, 0)
            val newCount = cropCount + 1
            if (newCount >= CROPS_PER_LEVEL) {
                val currLevel = item.persistentDataContainer.getOrDefault(HOE_LEVEL_KEY, PersistentDataType.INTEGER, 0)
                if (currLevel == MAX_LEVEL) return
                item.editPersistentDataContainer { pdc -> run {
                        pdc.set(HOE_LEVEL_KEY, PersistentDataType.INTEGER, currLevel + 1)
                        pdc.set(CROP_COUNT_KEY, PersistentDataType.INTEGER, 0)
                    }
                }
                player?.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1f, 2f)
            }
            else {
                item.editPersistentDataContainer { pdc -> pdc.set(CROP_COUNT_KEY, PersistentDataType.INTEGER, newCount) }
            }
            blueprint.updateItemData(item)
        }
    }
}