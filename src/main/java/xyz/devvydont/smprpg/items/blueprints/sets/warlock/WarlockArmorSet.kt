package xyz.devvydont.smprpg.items.blueprints.sets.warlock

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
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.items.interfaces.IEquippableAssetOverride
import xyz.devvydont.smprpg.items.interfaces.IFooterDescribable
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden
import xyz.devvydont.smprpg.items.interfaces.IRepairable
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

abstract class WarlockArmorSet(itemService: ItemService, type: CustomItemType) : CustomAttributeItem(itemService, type),
    IEquippableAssetOverride, IRepairable, IFooterDescribable, IBreakableEquipment, IModelOverridden, Listener {

    override val repairMaterial: MutableCollection<ItemStack> get() = mutableListOf(itemService.getCustomItem(CustomItemType.SPELLBOUND_CLOTH))

    override fun getAssetId(): Key { return key }

    fun getKillArcaneRatingBoost(item: ItemStack): Double {
        val numKills: Int = item.persistentDataContainer.getOrDefault(killstoreKey, PersistentDataType.INTEGER, 0)!!
        if (numKills >= 25000) {
            return 10.0
        } else if (numKills >= 15000) {
            return 9.0
        } else if (numKills >= 10000) {
            return 8.0
        } else if (numKills >= 5000) {
            return 7.0
        } else if (numKills >= 2500) {
            return 6.0
        } else if (numKills >= 1500) {
            return 5.0
        } else if (numKills >= 1000) {
            return 4.0
        } else if (numKills >= 500) {
            return 3.0
        } else if (numKills >= 250) {
            return 2.0
        } else if (numKills >= 100) {
            return 1.0
        } else {
            return 0.0
        }
    }

    fun getNextKillMilestone(item: ItemStack): Int {
        val numKills: Int = item.persistentDataContainer
            .getOrDefault(
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
        return 40
    }

    override fun getFooter(itemStack: ItemStack): MutableList<Component?> {
        val kills: Int = itemStack.persistentDataContainer
            .getOrDefault(
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
                ComponentUtils.create("Illager Warlocks", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD),
                ComponentUtils.create(" is multiplied by 0.95x per piece.")
            ),
            ComponentUtils.merge(
                ComponentUtils.create("Gains "),
                ComponentUtils.create("+1 Arcane Rating", NamedTextColor.DARK_AQUA),
                ComponentUtils.create(" for every kill milestone reached.")
            ),
            killComp
        )
    }

    companion object {
        private val key = Key.key(plugin, "warlock")
        val killstoreKey: NamespacedKey = NamespacedKey(plugin, "warlock_kill_count")
        const val BOSS_DAMAGE_REDUCTION: Double = 0.95
    }
}
