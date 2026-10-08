package xyz.devvydont.smprpg.items.attribute

import org.bukkit.attribute.AttributeModifier
import xyz.devvydont.smprpg.attribute.AttributeWrapper

class MultiplicativeAttributeEntry : AttributeEntry {
    constructor(attribute: AttributeWrapper?, amount: Double) : super(
        attribute,
        amount,
        AttributeModifier.Operation.MULTIPLY_SCALAR_1
    )

    constructor(attribute: AttributeWrapper?, amount: Double, key: String?) : super(
        attribute,
        amount,
        AttributeModifier.Operation.MULTIPLY_SCALAR_1,
        key
    )
}
