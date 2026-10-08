package xyz.devvydont.smprpg.enchantments

import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import io.papermc.paper.registry.tag.TagKey
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.inventory.ItemType
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.MinecraftStringUtils
import xyz.devvydont.smprpg.util.persistence.KeyStore

/**
 * Single source of truth for rendering which items an enchantment can be applied to.
 *
 * Both the [xyz.devvydont.smprpg.gui.enchantments.EnchantmentMenu] and the
 * [xyz.devvydont.smprpg.items.blueprints.resources.scrolls.DynamicEnchantingScroll] lore use this so that the two
 * never disagree. Every item type tag an enchantment can target should map to a sprite (or descriptive text) here;
 * any tag that is not explicitly handled falls back to a readable name derived from the tag key, so an unmapped tag
 * reports its real target instead of silently claiming "Anything enchantable".
 */
object EnchantmentTargetDisplay {

    private val ITEMS_ATLAS: Key = Key.key("minecraft:items")

    private const val NAMESPACE_PREFIX = "smprpg:"
    private const val PATH_SEPARATOR = "/"
    private const val WORD_SEPARATOR = " "

    private fun sprite(spritePath: String): Component =
        ComponentUtils.atlasSprite(ITEMS_ATLAS, Key.key(spritePath))

    private val IRON_SWORD = sprite("minecraft:item/iron_sword")
    private val IRON_AXE = sprite("minecraft:item/iron_axe")
    private val IRON_HATCHET = sprite("tools:item/iron_hatchet")
    private val IRON_SPEAR = sprite("minecraft:item/iron_spear")
    private val IRON_KNIFE = sprite("smprpg:item/tools/iron_knife")
    private val AMETHYST_STAFF = sprite("staffs:item/amethyst_staff")
    private val BOW = sprite("minecraft:item/bow")
    private val CROSSBOW = sprite("minecraft:item/crossbow_standby")
    private val TRIDENT = sprite("minecraft:item/trident")
    private val MACE = sprite("minecraft:item/mace")
    private val IRON_PICKAXE = sprite("minecraft:item/iron_pickaxe")
    private val GOLDEN_PICKAXE = sprite("minecraft:item/golden_pickaxe")
    private val IRON_HOE = sprite("minecraft:item/iron_hoe")
    private val IRON_SHOVEL = sprite("minecraft:item/iron_shovel")
    private val SHEARS = sprite("minecraft:item/shears")
    private val FISHING_ROD = sprite("minecraft:item/fishing_rod")
    private val IRON_HELMET = sprite("minecraft:item/iron_helmet")
    private val IRON_CHESTPLATE = sprite("minecraft:item/iron_chestplate")
    private val IRON_LEGGINGS = sprite("minecraft:item/iron_leggings")
    private val IRON_BOOTS = sprite("minecraft:item/iron_boots")
    private val SWEEPING = sprite("smprpg:item/enchantable_icons/sweeping")
    private val TOME = sprite("tools:item/simple_tome_ui")

    /**
     * Builds the component describing what items the given enchantment item type tag applies to, rendered as item
     * sprites wherever possible.
     *
     * @param tag The enchantment's [CustomEnchantment.itemTypeTag].
     * @return A component containing the representative item sprites (or descriptive text) for the tag.
     */
    fun getApplicableItemsComponent(tag: TagKey<ItemType>): Component = when (tag) {
        ItemTypeTagKeys.ENCHANTABLE_WEAPON -> ComponentUtils.merge(
            IRON_SWORD, IRON_AXE, IRON_SPEAR, BOW, CROSSBOW, TRIDENT, MACE, AMETHYST_STAFF, IRON_KNIFE
        )
        ItemTypeTagKeys.ENCHANTABLE_SHARP_WEAPON -> ComponentUtils.merge(
            IRON_SWORD, IRON_AXE, IRON_SPEAR, TRIDENT, AMETHYST_STAFF, IRON_KNIFE
        )
        ItemTypeTagKeys.ENCHANTABLE_MELEE_WEAPON -> IRON_SWORD
        ItemTypeTagKeys.ENCHANTABLE_FIRE_ASPECT -> ComponentUtils.merge(
            IRON_SWORD, IRON_AXE, IRON_SPEAR, TRIDENT, MACE, AMETHYST_STAFF, IRON_KNIFE,
            IRON_PICKAXE, IRON_HOE, IRON_SHOVEL, SHEARS, IRON_HATCHET
        )
        ItemTypeTagKeys.ENCHANTABLE_BOW -> BOW
        ItemTypeTagKeys.ENCHANTABLE_CROSSBOW -> CROSSBOW
        ItemTypeTagKeys.ENCHANTABLE_TRIDENT -> TRIDENT
        ItemTypeTagKeys.ENCHANTABLE_MACE -> MACE
        ItemTypeTagKeys.ENCHANTABLE_SWEEPING -> SWEEPING
        ItemTypeTagKeys.ENCHANTABLE_LUNGE -> IRON_SPEAR
        ItemTypeTagKeys.AXES -> ComponentUtils.merge(IRON_AXE, IRON_HATCHET)
        ItemTypeTagKeys.PICKAXES -> IRON_PICKAXE
        ItemTypeTagKeys.HOES -> IRON_HOE
        ItemTypeTagKeys.ENCHANTABLE_MINING -> IRON_PICKAXE
        ItemTypeTagKeys.ENCHANTABLE_MINING_LOOT -> GOLDEN_PICKAXE
        ItemTypeTagKeys.ENCHANTABLE_FISHING -> FISHING_ROD
        ItemTypeTagKeys.ENCHANTABLE_ARMOR -> ComponentUtils.merge(
            IRON_HELMET, IRON_CHESTPLATE, IRON_LEGGINGS, IRON_BOOTS
        )
        ItemTypeTagKeys.ENCHANTABLE_HEAD_ARMOR -> IRON_HELMET
        ItemTypeTagKeys.ENCHANTABLE_CHEST_ARMOR -> IRON_CHESTPLATE
        ItemTypeTagKeys.ENCHANTABLE_LEG_ARMOR -> IRON_LEGGINGS
        ItemTypeTagKeys.ENCHANTABLE_FOOT_ARMOR -> IRON_BOOTS
        ItemTypeTagKeys.ENCHANTABLE_DURABILITY ->
            ItemService.COMMON_REPAIR_CORE_ATLAS_ICON.append(ComponentUtils.create(" Any durable item"))
        // Vanishing applies to virtually anything that can be held or worn, so describe it rather than list sprites.
        ItemTypeTagKeys.ENCHANTABLE_VANISHING -> ComponentUtils.create("Anything enchantable", NamedTextColor.LIGHT_PURPLE)
        KeyStore.ENCHANTABLE_TOME -> TOME
        KeyStore.ENCHANTABLE_APTITUDE -> ComponentUtils.merge(
            IRON_HELMET, IRON_CHESTPLATE, IRON_LEGGINGS, IRON_BOOTS, TOME
        )
        else -> ComponentUtils.create(getReadableTagName(tag), NamedTextColor.LIGHT_PURPLE)
    }

    /**
     * Derives a human readable name from a tag key for tags that have no explicit sprite mapping. This guarantees an
     * unmapped tag still reports its real target instead of a misleading catch-all.
     */
    private fun getReadableTagName(tag: TagKey<ItemType>): String =
        MinecraftStringUtils.getTitledString(
            tag.key().asMinimalString()
                .replace(PATH_SEPARATOR, WORD_SEPARATOR)
                .replace(NAMESPACE_PREFIX, "")
        )
}
