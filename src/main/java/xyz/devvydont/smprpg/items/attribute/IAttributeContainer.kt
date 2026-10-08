package xyz.devvydont.smprpg.items.attribute

interface IAttributeContainer {
    /**
     * What kind of attribute container is this? Items can have multiple containers of stats that stack
     * to prevent collisions
     *
     * @return
     */
    val attributeModifierType: AttributeModifierType

    /**
     * What modifiers themselves will be contained on the item if there are no variables to affect them?
     *
     * @return
     */
    fun getHeldAttributes(): MutableCollection<AttributeEntry?>?

    /**
     * How much should we increase the power rating of an item if this container is present?
     *
     * @return
     */
    val powerRating: Int
}
