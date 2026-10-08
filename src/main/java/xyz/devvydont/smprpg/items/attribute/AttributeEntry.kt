package xyz.devvydont.smprpg.items.attribute

import org.bukkit.NamespacedKey
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.EquipmentSlotGroup
import xyz.devvydont.smprpg.attribute.AttributeWrapper

open class AttributeEntry {
    @JvmField
    var attribute: AttributeWrapper?
    var amount: Double
    var operation: AttributeModifier.Operation
    @JvmField
    var key: String?

    constructor(attribute: AttributeWrapper?, amount: Double, operation: AttributeModifier.Operation) {
        this.attribute = attribute
        this.amount = amount
        this.operation = operation
        this.key = null
    }

    constructor(attribute: AttributeWrapper?, amount: Double, operation: AttributeModifier.Operation, key: String?) {
        this.attribute = attribute
        this.amount = amount
        this.operation = operation
        this.key = key
    }

    fun asModifier(key: NamespacedKey, slot: EquipmentSlotGroup): AttributeModifier {
        return AttributeModifier(key, amount, operation, slot)
    }

    companion object {
        /**
         * Shortcut method to get an additive attribute modifier.
         * @param wrapper The attribute.
         * @param amount The amount to add.
         * @return The attribute entry.
         */
        @JvmStatic
        fun additive(wrapper: AttributeWrapper?, amount: Double): AttributeEntry {
            return AttributeEntry(wrapper, amount, AttributeModifier.Operation.ADD_NUMBER)
        }

        /**
         * Shortcut method to get a scalar attribute modifier.
         * @param wrapper The attribute.
         * @param amount The amount to scale by.
         * @return The attribute entry.
         */
        @JvmStatic
        fun scalar(wrapper: AttributeWrapper?, amount: Double): AttributeEntry {
            return AttributeEntry(wrapper, amount, AttributeModifier.Operation.ADD_SCALAR)
        }

        /**
         * Shortcut method to get a multiplicative attribute modifier.
         * @param wrapper The attribute.
         * @param amount The amount to multiply by.
         * @return The attribute entry.
         */
        @JvmStatic
        fun multiplicative(wrapper: AttributeWrapper?, amount: Double): AttributeEntry {
            return AttributeEntry(wrapper, amount, AttributeModifier.Operation.MULTIPLY_SCALAR_1)
        }
    }
}
