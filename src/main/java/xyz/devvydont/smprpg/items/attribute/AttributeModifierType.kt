package xyz.devvydont.smprpg.items.attribute

import org.bukkit.NamespacedKey
import java.util.*

enum class AttributeModifierType {
    BASE,
    ENCHANTMENT,
    REFORGE,
    AUGMENT
    ;

    val Key: NamespacedKey = NamespacedKey("smprpg", "modifier-" + name.lowercase(Locale.getDefault()))

    fun keyForItem(item: String?): NamespacedKey {
        return NamespacedKey(Key.getNamespace(), item + "_" + name.lowercase(Locale.getDefault()))
    }
}
