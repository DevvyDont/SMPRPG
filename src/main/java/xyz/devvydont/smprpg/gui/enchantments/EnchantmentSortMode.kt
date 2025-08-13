package xyz.devvydont.smprpg.gui.enchantments

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.enchantments.CustomEnchantment
import xyz.devvydont.smprpg.services.EnchantmentService
import xyz.devvydont.smprpg.util.formatting.MinecraftStringUtils
import java.util.function.ToIntFunction
import kotlin.Comparator
import kotlin.IllegalStateException

/*
 * Helper enum for the EnchantmentMenu. Used to define different sorting modes for CustomEnchantment collections.
 */
enum class EnchantmentSortMode {
    DEFAULT,  // The order that enchantments render on items based on our curated order.
    REQUIREMENT,  // The magic skill requirement for the enchantment to be rolled.
    ALPHABETICAL; // Self-explanatory.

    /**
     * Gets a string to display to the user when this is the "selected mode".
     * @return A string representation of this enum.
     */
    fun display(): String {
        return MinecraftStringUtils.getTitledString(this.name)
    }

    /**
     * Performs an in place sort on a collection of enchantments depending on which mode this is if possible. (The DEFAULT sorting mode does NOT perform an in place sort.)
     * The sorted collection will also be returned, and is preferred to be used when calling this method.
     * @param enchantments A collection of enchantments to be sorted.
     * @return A sorted collection of enchantments.
     */
    fun sort(enchantments: MutableList<CustomEnchantment>): MutableList<CustomEnchantment> {
        when (this) {
            DEFAULT -> return ArrayList(SMPRPG.getService(EnchantmentService::class.java).orderedCustomEnchantments)
            REQUIREMENT -> {
                enchantments.sortWith(Comparator { e1: CustomEnchantment, e2: CustomEnchantment ->
                                    Comparator.comparingInt(
                                        ToIntFunction { obj: CustomEnchantment -> obj.getSkillRequirement() }).compare(e1, e2)
                })
                return enchantments
            }

            ALPHABETICAL -> {
                val comparator = Comparator { e1: CustomEnchantment, e2: CustomEnchantment ->
                    val str1 = PlainTextComponentSerializer.plainText().serialize(
                        e1.getDisplayName()
                    )
                    val str2 = PlainTextComponentSerializer.plainText().serialize(e2.getDisplayName())
                    var res = String.CASE_INSENSITIVE_ORDER.compare(str1, str2)
                    if (res == 0) res = str1.compareTo(str2)
                    res
                }
                enchantments.sortWith(comparator)
                return enchantments
            }
        }
    }

    /**
     * Retrieve the next sort mode after this one. If this is the last, use the first one.
     * @return A new enchantment sort mode enum
     */
    fun next(): EnchantmentSortMode {
        var desiredModeOrdinal = this.ordinal + 1
        if (desiredModeOrdinal >= entries.size) desiredModeOrdinal = 0
        return entries[desiredModeOrdinal]
    }
}