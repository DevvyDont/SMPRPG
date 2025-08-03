package xyz.devvydont.smprpg.skills.rewards

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.services.EconomyService
import xyz.devvydont.smprpg.services.EconomyService.Companion.formatMoney
import xyz.devvydont.smprpg.skills.SkillType
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class CoinReward(private val amount: Int) : ISkillReward {
    override fun generateRewardComponent(player: Player): Component {
        return ComponentUtils.create("+" + formatMoney(this.amount), NamedTextColor.GOLD)
            .hoverEvent(
                ComponentUtils.merge(
                    ComponentUtils.create(formatMoney(this.amount), NamedTextColor.GOLD),
                    ComponentUtils.create(" coins were added directly to your balance!")
                )
            )
    }

    /**
     * Ideally we shouldn't 'remove' coin rewards from players. This makes no sense.
     * Either way, we will implement the logic. Just be careful if you are actually trying to use this.
     * @param player The player to remove the reward from.
     * @param skill The type of skill this reward is associated with. This is necessary for stackable skill rewards.
     */
    override fun remove(player: Player, skill: SkillType) {
        SMPRPG.getService(EconomyService::class.java).takeMoney(player, this.amount.toDouble())
    }

    override fun apply(player: Player, skill: SkillType) {
        SMPRPG.getService(EconomyService::class.java).addMoney(player, this.amount.toDouble())
    }
}
