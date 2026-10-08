package xyz.devvydont.smprpg.items.attribute

import org.bukkit.attribute.AttributeModifier
import xyz.devvydont.smprpg.attribute.AttributeWrapper

class AdditiveAttributeEntry : AttributeEntry {
    constructor(attribute: AttributeWrapper?, amount: Double) : super(
        attribute,
        amount,
        AttributeModifier.Operation.ADD_NUMBER
    )

    constructor(attribute: AttributeWrapper?, amount: Double, key: String?) : super(
        attribute,
        amount,
        AttributeModifier.Operation.ADD_NUMBER,
        key
    )
}
