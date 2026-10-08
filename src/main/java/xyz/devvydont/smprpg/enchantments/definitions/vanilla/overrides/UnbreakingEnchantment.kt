package xyz.devvydont.smprpg.enchantments.definitions.vanilla.overrides

import io.papermc.paper.registry.TypedKey
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import io.papermc.paper.registry.tag.TagKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Color
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.Listener
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ItemType
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity
import xyz.devvydont.smprpg.enchantments.definitions.vanilla.VanillaEnchantment
import xyz.devvydont.smprpg.entity.player.LeveledPlayer
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import java.util.*

class UnbreakingEnchantment(key: TypedKey<Enchantment>) : VanillaEnchantment(key), Listener {
    private val random = Random()

    override val displayName: Component get() = ComponentUtils.create("Unbreaking")
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Increases max durability of this item by "),
            ComponentUtils.create(
                "${getDurabilityIncrease(level)}%",
                NamedTextColor.GREEN
            )
        )
    override val scrollBindingColor: Color get() = Color.fromRGB(89, 89, 89)

    override val itemTypeTag: TagKey<ItemType> get()           = ItemTypeTagKeys.ENCHANTABLE_DURABILITY
    override val maxLevel: Int get()                           = 10
    override val weight: Int get()                             = EnchantmentRarity.COMMON.weight
    override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.ANY
    override val skillRequirement: Int get()                   = 0

    override fun isEnchantmentActive(itemStack: ItemStack, player: LeveledPlayer): Boolean {return true }

    companion object {
        fun getDurabilityIncrease(level: Int): Int { return level * 20 }
    }
}
