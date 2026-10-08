package xyz.devvydont.smprpg.util.items

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.Symbols

object AbilityUtil {
    @JvmStatic
    fun getAbilityComponent(ability: String, passive: Boolean = false): Component {
        var symbol : String
        var color = NamedTextColor.GOLD
        if (passive) {
            symbol = Symbols.PASSIVE
            color = NamedTextColor.AQUA
        }
        else symbol = Symbols.ABILITY

        return ComponentUtils.create("$symbol ").append(
            ComponentUtils.create(ability, color).decoration(
                TextDecoration.BOLD, false
            )
        )
    }

    @JvmStatic
    fun getAbilityComponent(ability: String): Component {
        return getAbilityComponent(ability, false)
    }

    @JvmStatic
    fun getCooldownComponent(cooldown: String): Component {
        return ComponentUtils.create("($cooldown cooldown)", NamedTextColor.DARK_GRAY)
    }

    fun getHealthCostComponent(hp: Int): Component {
        return ComponentUtils.create("Usage cost: ")
            .append(ComponentUtils.create(hp.toString() + Symbols.HEART, NamedTextColor.RED))
    }

    fun getManaCostComponent(mana: Int): Component {
        return ComponentUtils.create("Usage cost: ")
            .append(ComponentUtils.create(mana.toString() + Symbols.MANA, NamedTextColor.AQUA))
    }
}
