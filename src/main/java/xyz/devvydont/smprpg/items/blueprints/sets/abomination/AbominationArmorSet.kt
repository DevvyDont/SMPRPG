package xyz.devvydont.smprpg.items.blueprints.sets.abomination

import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.NamespacedKey
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.base.CustomAttributeItem
import xyz.devvydont.smprpg.items.interfaces.IEquippableAssetOverride
import xyz.devvydont.smprpg.items.interfaces.IFooterDescribable
import xyz.devvydont.smprpg.items.interfaces.IRepairable
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

abstract class AbominationArmorSet(itemService: ItemService, type: CustomItemType) :
    CustomAttributeItem(itemService, type), IEquippableAssetOverride, Listener, IFooterDescribable, IRepairable {

    override val repairMaterial = mutableListOf(itemService.getCustomItem(CustomItemType.REVILED_VISCERA))

    override fun getAssetId(): Key {
        return key
    }

    fun getKillStrengthMultBoost(item: ItemStack): Double {
        val numKills: Int = item.persistentDataContainer.getOrDefault(
            killstoreKey,
            PersistentDataType.INTEGER,
            0
        )!!
        if (numKills >= 25000) {
            return 0.50
        } else if (numKills >= 15000) {
            return 0.45
        } else if (numKills >= 10000) {
            return 0.40
        } else if (numKills >= 5000) {
            return 0.35
        } else if (numKills >= 2500) {
            return 0.30
        } else if (numKills >= 1500) {
            return 0.25
        } else if (numKills >= 1000) {
            return 0.20
        } else if (numKills >= 500) {
            return 0.15
        } else if (numKills >= 250) {
            return 0.1
        } else if (numKills >= 100) {
            return 0.05
        } else {
            return 0.0
        }
    }

    fun getNextKillMilestone(item: ItemStack): Int {
        val numKills: Int = item.persistentDataContainer.getOrDefault(
            killstoreKey,
            PersistentDataType.INTEGER,
            0
        )!!
        if (numKills < 100) {
            return 100
        } else if (numKills < 250) {
            return 250
        } else if (numKills < 500) {
            return 500
        } else if (numKills < 1000) {
            return 1000
        } else if (numKills < 2500) {
            return 2500
        } else if (numKills < 5000) {
            return 5000
        } else if (numKills < 10000) {
            return 10000
        } else if (numKills < 15000) {
            return 15000
        } else {
            return 25000
        }
    }

    override fun getPowerRating(): Int {
        return 25
    }

    val armorDurabilityUnit: Int get() = 96

    override fun getFooter(itemStack: ItemStack): MutableList<Component?> {
        val kills: Int = itemStack.persistentDataContainer.getOrDefault(
            killstoreKey,
            PersistentDataType.INTEGER,
            0
        )!!
        val killComp: Component
        if (kills >= 25000) {
            killComp = ComponentUtils.merge(
                ComponentUtils.create("Kills: "),
                ComponentUtils.create("MAXED!", NamedTextColor.AQUA, TextDecoration.BOLD)
            )
        } else {
            killComp = ComponentUtils.merge(
                ComponentUtils.create("Kills: "),
                ComponentUtils.create(kills, NamedTextColor.DARK_AQUA),
                ComponentUtils.create("/" + getNextKillMilestone(itemStack), NamedTextColor.DARK_GRAY)
            )
        }
        return mutableListOf(
            ComponentUtils.merge(
                ComponentUtils.create("All incoming damage from "),
                ComponentUtils.create("Shambling Abominations", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD),
                ComponentUtils.create(" is multiplied by 0.95x per piece.")
            ),
            ComponentUtils.merge(
                ComponentUtils.create("Gains "),
                ComponentUtils.create("+5% Strength", NamedTextColor.RED),
                ComponentUtils.create(" for every kill milestone reached.")
            ),
            killComp
        )
    }

    companion object {
        private val key = Key.key("abomination")
        @JvmField
        val killstoreKey: NamespacedKey = NamespacedKey(plugin, "abomination_kill_count")
        const val BOSS_DAMAGE_REDUCTION: Double = 0.95
    }
}
