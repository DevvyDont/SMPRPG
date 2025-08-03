package xyz.devvydont.smprpg.skills.rewards

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player
import xyz.devvydont.smprpg.enchantments.CustomEnchantment
import xyz.devvydont.smprpg.skills.SkillType
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.MinecraftStringUtils
import xyz.devvydont.smprpg.util.formatting.Symbols

class EnchantmentSkillReward(private val enchantment: CustomEnchantment) : ISkillReward {
    private val hoverComponent: Component
        get() {
            val clone = enchantment.build(1)
            val max = enchantment.build(clone.getMaxLevel())
            var ret = clone.getDisplayName().color(NamedTextColor.LIGHT_PURPLE)
                .append(
                    ComponentUtils.create("\n")
                        .append(clone.getDescription())
                        .append(ComponentUtils.create(" (Lv. 1)", NamedTextColor.DARK_GRAY))
                )
            if (clone.getMaxLevel() > 1) ret = ret.append(
                ComponentUtils.create("\n").append(max.getDescription())
                    .append(ComponentUtils.create(" (Lv. " + clone.getMaxLevel() + ")", NamedTextColor.DARK_GRAY))
            )

            ret = ret.append(ComponentUtils.create("\n\n"))
            ret = ret.append(
                ComponentUtils.create("Item Type: ").append(
                    ComponentUtils.create(
                        MinecraftStringUtils.getTitledString(
                            clone.getItemTypeTag().key().asMinimalString().replace("/", " ")
                        ), NamedTextColor.GOLD
                    )
                )
            )
            return ret
        }

    override fun remove(player: Player, skill: SkillType) {
    }

    override fun apply(player: Player, skill: SkillType) {
    }

    override fun generateRewardComponent(player: Player): Component {
        return ComponentUtils.merge(
            ComponentUtils.create("Unlocked ").decoration(TextDecoration.BOLD, false),
            ComponentUtils.create(Symbols.SPARKLES, NamedTextColor.LIGHT_PURPLE),
            enchantment.getDisplayName().color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.BOLD, true)
                .hoverEvent(
                    this.hoverComponent
                ),
            ComponentUtils.create(" enchantment").decoration(TextDecoration.BOLD, false)
        )
    }
}
