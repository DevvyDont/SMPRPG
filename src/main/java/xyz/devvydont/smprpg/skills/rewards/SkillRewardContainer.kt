package xyz.devvydont.smprpg.skills.rewards

import org.bukkit.attribute.AttributeModifier
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.skills.SkillGlobals.getCoinRewardForLevel
import xyz.devvydont.smprpg.skills.SkillGlobals.getScalingStatPerLevel
import xyz.devvydont.smprpg.skills.SkillGlobals.getScalingStatPerXLevel
import xyz.devvydont.smprpg.skills.SkillGlobals.maxSkillLevel

abstract class SkillRewardContainer {

    private val skillRewards: MutableMap<Int, MutableList<ISkillReward>> = HashMap()

    fun getRewardsForLevel(level: Int): MutableCollection<ISkillReward> {
        return skillRewards.getOrDefault(level, ArrayList())
    }

    fun getRewardsForLevels(start: Int, end: Int): MutableCollection<ISkillReward> {
        val result = ArrayList<ISkillReward>()
        for (i in start..end) result.addAll(getRewardsForLevel(i))
        return result
    }

    val rewards: MutableCollection<ISkillReward>
        get() = getRewardsForLevels(1, maxSkillLevel)

    protected fun addReward(level: Int, reward: ISkillReward) {
        val rewards = skillRewards.getOrDefault(level, ArrayList())
        rewards.add(reward)
        skillRewards.put(level, rewards)
    }

    /**
     * Shortcut method to add an attribute that linearly adds up depending on the level.
     * @param attribute The attribute to award.
     * @param operation The attribute operation to use.
     * @param amountPerLevel The linear amount of the attribute to add per level.
     */
    protected fun addAttributeRewardEveryLevel(
        attribute: AttributeWrapper,
        operation: AttributeModifier.Operation,
        amountPerLevel: Double
    ) {
        for (i in 1..maxSkillLevel)
            addReward(
            i, AttributeReward(
                attribute,
                operation,
                amountPerLevel * i,
                amountPerLevel * (i - 1)
            )
        )
    }

    /**
     * Shortcut method to add an attribute that linearly adds up depending on the level.
     * @param attribute The attribute to award.
     * @param operation The attribute operation to use.
     * @param amountPerLevel The linear amount of the attribute to add per level.
     * @param x The every xth level you want to add the attribute to.
     */
    protected fun addAttributeRewardEveryXLevels(
        attribute: AttributeWrapper,
        operation: AttributeModifier.Operation,
        amountPerLevel: Double,
        x: Int
    ) {
        var i = x
        while (i <= maxSkillLevel) {
            addReward(
                i, AttributeReward(
                    attribute,
                    operation,
                    amountPerLevel * (i.toDouble() / x),
                    amountPerLevel * ((i - x).toDouble() / x)
                )
            )
            i += x
        }
    }

    /**
     * Shortcut method to add a scaling attribute to every level of this container.
     * @param attribute The attribute you want to modify.
     * @param operation The operation you want to use.
     * @param amount The base stat amount. Keep in mind this scales as you get higher.
     */
    protected fun addScalingAttributeRewardEveryLevel(
        attribute: AttributeWrapper,
        operation: AttributeModifier.Operation,
        amount: Double
    ) {
        for (i in 1..maxSkillLevel) addReward(
            i, AttributeReward(
                attribute,
                operation,
                getScalingStatPerLevel(amount, i),
                getScalingStatPerLevel(amount, i - 1)
            )
        )
    }

    /**
     * Shortcut method to add a scaling attribute to every certain level of this container.
     * @param attribute The attribute you want to modify.
     * @param operation The operation you want to use.
     * @param amount The base stat amount. Keep in mind this scales as you get higher.
     * @param x How many levels to skip per entry. If x is 4, the attribute will be rewarded every 4 levels starting at 4.
     */
    protected fun addScalingAttributeRewardEveryXLevels(
        attribute: AttributeWrapper,
        operation: AttributeModifier.Operation,
        amount: Double,
        x: Int
    ) {
        var i = x
        while (i <= maxSkillLevel) {
            addReward(
                i, AttributeReward(
                    attribute,
                    operation,
                    getScalingStatPerXLevel(amount, x, i),
                    getScalingStatPerXLevel(amount, x, i - x)
                )
            )
            i += x
        }
    }

    /**
     * Shortcut method to add a coin reward for every level. Currently, this is the same for every skill, it just needs
     * to be called.
     */
    protected fun addCoinsEveryLevel() {
        for (i in 1..maxSkillLevel)
            addReward(i, CoinReward(getCoinRewardForLevel(i)))
    }
}
