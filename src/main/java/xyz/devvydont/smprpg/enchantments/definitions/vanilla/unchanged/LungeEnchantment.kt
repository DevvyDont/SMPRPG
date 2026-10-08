package xyz.devvydont.smprpg.enchantments.definitions.vanilla.unchanged

import io.papermc.paper.registry.TypedKey
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import io.papermc.paper.registry.tag.TagKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Color
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemType
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity
import xyz.devvydont.smprpg.enchantments.definitions.vanilla.UnchangedEnchantment
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import java.text.DecimalFormat

class LungeEnchantment(key: TypedKey<Enchantment>) : UnchangedEnchantment(key) {
    private val df = DecimalFormat("#.##")

    override val displayName: Component get() = ComponentUtils.create("Lunge")
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Launches you forward "),
            ComponentUtils.create("${getBlocksPerSecondString(getBlocksPerLevel(level) * 20)} blocks per second", NamedTextColor.AQUA),
            ComponentUtils.create(" in the direction you are facing.")
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(237, 146, 240)

    override val itemTypeTag: TagKey<ItemType> get() = ItemTypeTagKeys.ENCHANTABLE_HEAD_ARMOR
    override val weight: Int get()                   = EnchantmentRarity.UNCOMMON.weight
    override val skillRequirement: Int get()         = 18

    /**
     * Returns the blocks this enchantment moves you in blocks/tick
     */
    fun getBlocksPerLevel(level : Int) : Double {
        return when (level) {
            1 -> 0.458
            2 -> 0.916
            3 -> 1.374
            else -> level * 0.5
        }
    }

    /**
     * Returns the formatted string for Blocks/second in enchantment description
     */
    fun getBlocksPerSecondString(bps : Double) : String {
        return df.format(bps)
    }
}
