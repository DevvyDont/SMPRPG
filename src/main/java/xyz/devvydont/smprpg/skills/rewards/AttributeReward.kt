package xyz.devvydont.smprpg.skills.rewards

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.NamespacedKey
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlotGroup
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.entity.player.ProfileDifficulty
import xyz.devvydont.smprpg.services.AttributeService.Companion.instance
import xyz.devvydont.smprpg.services.DifficultyService
import xyz.devvydont.smprpg.skills.SkillType
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import java.util.*

class AttributeReward @JvmOverloads constructor(
    var attribute: AttributeWrapper,
    private var operation: AttributeModifier.Operation,
    private var amount: Double,
    private var previousAmount: Double = 0.0
) : ISkillReward {
    fun calculateRewardAmount(player: Player, amount: Double): Double {
        // No matter what, non combat stats are always full amounts.
        if (!this.attribute.isCombatAttribute)
            return amount

        // Hard mode players will have attributes nerfed.
        if (SMPRPG.getService(DifficultyService::class.java).getDifficulty(player) == ProfileDifficulty.HARD)
            return amount / 2

        // No special case.
        return amount
    }

    private fun formatNumber(number: Double): String {
        // If this number is already a whole number, use int representation.
        if (number - (number.toInt()) == 0.0)
            return number.toInt().toString()

        // Otherwise only use 2 decimals.
        return "%.2f".format(number)
    }

    override fun generateRewardComponent(player: Player): Component {
        val perc = if (operation == AttributeModifier.Operation.ADD_NUMBER) "" else "%"
        val rawNew = calculateRewardAmount(player, amount)
        val rawOld = calculateRewardAmount(player, previousAmount)
        val old = "+" + formatNumber(rawOld) + perc
        val _new = "+" + formatNumber(rawNew) + perc
        return ComponentUtils.merge(
            ComponentUtils.create(attribute.DisplayName + " ", NamedTextColor.AQUA),
            ComponentUtils.upgrade(old, _new, NamedTextColor.GREEN)
        ).hoverEvent(
            ComponentUtils.merge(
                ComponentUtils.create("Your "),
                ComponentUtils.create(attribute.DisplayName, NamedTextColor.AQUA),
                ComponentUtils.create(" attribute bonus from this skill is now "),
                ComponentUtils.create(_new, NamedTextColor.GREEN),
                ComponentUtils.create("!")
            )
        )
    }

    /**
     * This modifier can only be shared amongst one skill type and attribute
     * If one skill gives two bonuses of the same attribute, collision will occur
     *
     * @return
     */
    fun getModifierKey(skill: SkillType): NamespacedKey {
        return NamespacedKey(
            "smprpg",
            skill.identifier + "_" + attribute.name.lowercase(Locale.getDefault()) + "_bonus"
        )
    }

    override fun remove(player: Player, skill: SkillType) {
        val attributeInstance = instance.getAttribute(player, attribute)
        if (attributeInstance == null) return

        attributeInstance.removeModifier(getModifierKey(skill))
        attributeInstance.save(player, attribute)
    }

    /**
     * Applies this reward to the player.
     * Queries the attribute modifier of this specific skill and modifies it accordingly.
     * Adds it to the player if it hasn't been added yet
     *
     * @param player
     */
    override fun apply(player: Player, skill: SkillType) {
        var adjustedAmount = calculateRewardAmount(player, this.amount)

        // Depending on the operation, it may need to apply differently in order to work as a vanilla modifier.
        if (operation == AttributeModifier.Operation.ADD_SCALAR || operation == AttributeModifier.Operation.MULTIPLY_SCALAR_1)
            adjustedAmount /= 100.0

        remove(player, skill)

        val attributeInstance = instance.getOrCreateAttribute(player, attribute)
        val modifier = AttributeModifier(getModifierKey(skill), adjustedAmount, operation, EquipmentSlotGroup.ANY)
        attributeInstance.addModifier(modifier)
        attributeInstance.save(player, attribute)
    }
}
