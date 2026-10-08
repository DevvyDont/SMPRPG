package xyz.devvydont.smprpg.attribute

enum class AttributeCategory(displayName: String, description: String) {
    COMBAT("Combat", "Attributes related to damage output and miscellaneous combat aspects"),
    SURVIVABILITY("Survivability", "Attributes that make staying alive easier"),
    MOVEMENT("Movement", "Attributes affect various agility factors"),
    FORAGING("Foraging", "Attributes related to gathering resources and experience"),
    FISHING("Fishing", "Attributes related to fishing"),
    PROFICIENCY("Proficiency", "Attributes that increase skill experience gain"),
    SPECIAL("Special", "Attributes that have unique effects");

    val DisplayName: String?
    val Description: String?

    init {
        DisplayName = displayName
        Description = description
    }
}
