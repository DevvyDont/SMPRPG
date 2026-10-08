package xyz.devvydont.smprpg.attribute

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import xyz.devvydont.smprpg.entity.fishing.SeaCreature
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import java.util.*

/**
 * Contains all the possible attributes that can be associated with items, players, and entities.
 * This functions as a way to unify minecraft's vanilla attribute system and well as add our own
 * custom attributes that attempt to function extremely similar to how the vanilla attributes
 * work. The goal is to keep all the ugliness confined to the attribute package of the project
 * so that the rest of the project can simply call AttributeService.getAttribute(player, MANA).setBaseValue(100) etc.
 * This will also allow us to go more in depth with combat attributes, allowing things like crit damage and crit % (w/o jumping)
 */
enum class AttributeWrapper @JvmOverloads constructor(
    @JvmField val DisplayName: String,
    val Category: AttributeCategory,
    @JvmField val Type: AttributeType,
    val Description: Component? = null
) {
    // First, start with vanilla attributes. These attributes interact with the vanilla attribute system in our API.
    // Keep in mind, if an attribute has vanilla support, it should be used over a custom one.
    MINING_POWER(
        "Breaking Power",
        AttributeCategory.FORAGING,
        AttributeType.SPECIAL,
        ComponentUtils.merge(
            ComponentUtils.create("The "),
            ComponentUtils.create("tier ", NamedTextColor.AQUA),
            ComponentUtils.create("of blocks that can be currently broken.")
        )
    ),

    HEALTH(
        Attribute.MAX_HEALTH,
        "Health",
        AttributeCategory.SURVIVABILITY,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("The amount of "),
            ComponentUtils.create("health", NamedTextColor.GREEN),
            ComponentUtils.create(" you have in half hearts.")
        )
    ),

    REGENERATION(
        "Regeneration",
        AttributeCategory.SURVIVABILITY,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("The effectiveness of"),
            ComponentUtils.create(" passive health regeneration", NamedTextColor.GREEN),
            ComponentUtils.create(".")
        )
    ),

    DEFENSE(
        "Defense",
        AttributeCategory.SURVIVABILITY,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("Amount of general "),
            ComponentUtils.create("damage reduction", NamedTextColor.RED),
            ComponentUtils.create(" from most sources.")
        )
    ),

    STRENGTH(
        Attribute.ATTACK_DAMAGE,
        "Strength",
        AttributeCategory.COMBAT,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("How much "),
            ComponentUtils.create("base damage", NamedTextColor.RED),
            ComponentUtils.create(" is dealt when attacking.")
        )
    ),

    CRITICAL_DAMAGE(
        "Critical Rating",
        AttributeCategory.COMBAT,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("The "),
            ComponentUtils.create("critical damage", NamedTextColor.BLUE),
            ComponentUtils.create(" multiplier when performing "),
            ComponentUtils.create("critical", NamedTextColor.BLUE),
            ComponentUtils.create(" hits.")
        )
    ),

    CRITICAL_CHANCE(
        "Critical Chance",
        AttributeCategory.COMBAT,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("The percentage to "),
            ComponentUtils.create("automatically", NamedTextColor.LIGHT_PURPLE),
            ComponentUtils.create(" perform "),
            ComponentUtils.create("critical", NamedTextColor.BLUE),
            ComponentUtils.create(" hits. "),
            ComponentUtils.create("(You can still crit by jumping/fully charging bows!)", NamedTextColor.DARK_GRAY)
        )
    ),

    ATTACK_SPEED(
        Attribute.ATTACK_SPEED,
        "Attack Recovery",
        AttributeCategory.COMBAT,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("How fast you "),
            ComponentUtils.create("recover", NamedTextColor.YELLOW),
            ComponentUtils.create(" from the attack cooldown."),
            ComponentUtils.create(" (full attacks per second)", NamedTextColor.DARK_GRAY)
        )
    ),

    INTELLIGENCE(
        "Intelligence",
        AttributeCategory.COMBAT,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("The "),
            ComponentUtils.create("maximum mana", NamedTextColor.BLUE),
            ComponentUtils.create(" available and effectiveness of"),
            ComponentUtils.create(" magic", NamedTextColor.LIGHT_PURPLE),
            ComponentUtils.create("/"),
            ComponentUtils.create("abilities", NamedTextColor.GOLD),
            ComponentUtils.create(".")
        )
    ),

    ARCANE_RATING(
        "Arcane Rating",
        AttributeCategory.COMBAT,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("The "),
            ComponentUtils.create("damage", NamedTextColor.RED),
            ComponentUtils.create(" multiplier when performing "),
            ComponentUtils.create("magical", NamedTextColor.BLUE),
            ComponentUtils.create(" attacks.")
        )
    ),

    MOVEMENT_SPEED(
        Attribute.MOVEMENT_SPEED,
        "Speed",
        AttributeCategory.MOVEMENT,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("Generic "),
            ComponentUtils.create("movement speed ", NamedTextColor.WHITE),
            ComponentUtils.create("while on foot.")
        )
    ),

    ARMOR(
        Attribute.ARMOR,
        "Armor",
        AttributeCategory.SURVIVABILITY,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("The delay before "),
            ComponentUtils.create("damage can be received again", NamedTextColor.RED),
            ComponentUtils.create("."),
            ComponentUtils.create(" (aka 'invincibility frames')", NamedTextColor.DARK_GRAY)
        )
    ),

    ABSORPTION(
        Attribute.MAX_ABSORPTION,
        "Absorption Retention",
        AttributeCategory.SURVIVABILITY,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("Amount of "),
            ComponentUtils.create("overflow absorption health", NamedTextColor.YELLOW),
            ComponentUtils.create(" that is retained when the "),
            ComponentUtils.create("absorption", NamedTextColor.YELLOW),
            ComponentUtils.create(" potion effect runs out.")
        )
    ),

    KNOCKBACK_RESISTANCE(
        Attribute.KNOCKBACK_RESISTANCE,
        "Knockback Resistance",
        AttributeCategory.SURVIVABILITY,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("Reduces "),
            ComponentUtils.create("knockback", NamedTextColor.RED),
            ComponentUtils.create(" inflicted from taking damage from certain sources.")
        )
    ),

    EXPLOSION_KNOCKBACK_RESISTANCE(
        Attribute.EXPLOSION_KNOCKBACK_RESISTANCE,
        "Explosion Knockback Resistance",
        AttributeCategory.SURVIVABILITY,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("Reduces "),
            ComponentUtils.create("knockback", NamedTextColor.RED),
            ComponentUtils.create(" inflicted from explosions.")
        )
    ),

    BURNING_TIME(
        Attribute.BURNING_TIME,
        "Burn Time",
        AttributeCategory.SURVIVABILITY,
        AttributeType.PUNISHING,
        ComponentUtils.merge(
            ComponentUtils.create("Amount of time "),
            ComponentUtils.create("fire ticks", NamedTextColor.RED),
            ComponentUtils.create(" last when ignited.")
        )
    ),

    SWEEPING(
        Attribute.SWEEPING_DAMAGE_RATIO,
        "Sweeping Efficiency",
        AttributeCategory.COMBAT,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("The effectiveness of "),
            ComponentUtils.create("sweeping damage", NamedTextColor.RED),
            ComponentUtils.create(" relative to the base damage of "),
            ComponentUtils.create("sweeping edge", NamedTextColor.DARK_RED),
            ComponentUtils.create(" attacks.")
        )
    ),

    ATTACK_KNOCKBACK(
        Attribute.ATTACK_KNOCKBACK,
        "Knockback",
        AttributeCategory.COMBAT,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("The amount of "),
            ComponentUtils.create("extra outgoing knockback", NamedTextColor.YELLOW),
            ComponentUtils.create(" to apply when dealing damage.")
        )
    ),

    MINING_SPEED(
        "Harvest Speed",
        AttributeCategory.FORAGING,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("The amount of "),
            ComponentUtils.create("damage ", NamedTextColor.RED),
            ComponentUtils.create("that is dealt to blocks "),
            ComponentUtils.create("per game tick.")
        )
    ),

    UNDERWATER_MINING(
        Attribute.SUBMERGED_MINING_SPEED,
        "Underwater Harvest Rate",
        AttributeCategory.FORAGING,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("The speed at which "),
            ComponentUtils.create("any blocks", NamedTextColor.AQUA),
            ComponentUtils.create(" can be broken while"),
            ComponentUtils.create(" submerged", NamedTextColor.BLUE),
            ComponentUtils.create(".")
        )
    ),

    AIRBORNE_MINING(
        "Airborne Harvest Rate",
        AttributeCategory.FORAGING,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("The speed at which "),
            ComponentUtils.create("any blocks", NamedTextColor.AQUA),
            ComponentUtils.create(" can be broken while"),
            ComponentUtils.create(" airborne", NamedTextColor.GRAY),
            ComponentUtils.create(".")
        )
    ),

    MINING_FORTUNE(
        "Fortune",
        AttributeCategory.FORAGING,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("Affects the amount of drops received from "),
            ComponentUtils.create("mining ores", NamedTextColor.DARK_PURPLE),
            ComponentUtils.create(".")
        )
    ),

    FARMING_FORTUNE(
        "Yield",
        AttributeCategory.FORAGING,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("Affects the amount of drops received from "),
            ComponentUtils.create("farming crops", NamedTextColor.GOLD),
            ComponentUtils.create(".")
        )
    ),

    CRITTER_CHANCE(
        "Uprooting",
        AttributeCategory.FORAGING,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("Affects the chance of digging up "),
            ComponentUtils.create("crop critters", NamedTextColor.GOLD),
            ComponentUtils.create(".")
        )
    ),

    WHEAT_FORTUNE(
        "Wheat Yield",
        AttributeCategory.FORAGING,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("Affects the amount of drops received from "),
            ComponentUtils.create("farming wheat", NamedTextColor.GOLD),
            ComponentUtils.create(".")
        )
    ),

    POTATO_FORTUNE(
        "Potato Yield",
        AttributeCategory.FORAGING,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("Affects the amount of drops received from "),
            ComponentUtils.create("farming potatoes", NamedTextColor.GOLD),
            ComponentUtils.create(".")
        )
    ),

    ONION_FORTUNE(
        "Onion Yield",
        AttributeCategory.FORAGING,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("Affects the amount of drops received from "),
            ComponentUtils.create("farming onion", NamedTextColor.GOLD),
            ComponentUtils.create(".")
        )
    ),

    CARROT_FORTUNE(
        "Carrot Yield",
        AttributeCategory.FORAGING,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("Affects the amount of drops received from "),
            ComponentUtils.create("farming carrots", NamedTextColor.GOLD),
            ComponentUtils.create(".")
        )
    ),

    MELON_FORTUNE(
        "Melon Yield",
        AttributeCategory.FORAGING,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("Affects the amount of drops received from "),
            ComponentUtils.create("farming melons", NamedTextColor.GOLD),
            ComponentUtils.create(".")
        )
    ),

    LUMBERING(
        "Lumbering",
        AttributeCategory.FORAGING,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("Affects the number of "),
            ComponentUtils.create("extra connected logs", NamedTextColor.YELLOW),
            ComponentUtils.create(" you can break at a time.")
        )
    ),

    WOODCUTTING_FORTUNE(
        "Splintering",
        AttributeCategory.FORAGING,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("Affects the amount of drops received from "),
            ComponentUtils.create("cutting trees", NamedTextColor.YELLOW),
            ComponentUtils.create(".")
        )
    ),

    FISHING_SPEED(
        "Fishing Speed",
        AttributeCategory.FISHING,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("The speed of your "),
            ComponentUtils.create("fishing bobbers", NamedTextColor.BLUE),
            ComponentUtils.create(" while fishing.")
        )
    ),

    FISHING_RATING(
        "Reeling",
        AttributeCategory.FISHING,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("The rating of "),
            ComponentUtils.create("fishing", NamedTextColor.BLUE),
            ComponentUtils.create(" loot rewards able to be found.")
        )
    ),

    FISHING_CREATURE_CHANCE(
        "Angling",
        AttributeCategory.FISHING,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("The chance to find a "),
            ComponentUtils.create("sea creature", SeaCreature.NAME_COLOR),
            ComponentUtils.create(" when "),
            ComponentUtils.create("fishing", NamedTextColor.BLUE),
            ComponentUtils.create(".")
        )
    ),

    FISHING_TREASURE_CHANCE(
        "Piracy",
        AttributeCategory.FISHING,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("The chance to find a "),
            ComponentUtils.create("treasure", NamedTextColor.YELLOW),
            ComponentUtils.create(" when "),
            ComponentUtils.create("fishing", NamedTextColor.BLUE),
            ComponentUtils.create(".")
        )
    ),

    SNEAKING_SPEED(
        Attribute.SNEAKING_SPEED,
        "Sneak Speed",
        AttributeCategory.MOVEMENT,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("The effectiveness of "),
            ComponentUtils.create("sneaking movement speed", NamedTextColor.WHITE),
            ComponentUtils.create(".")
        )
    ),

    MOVEMENT_EFFICIENCY(
        Attribute.MOVEMENT_EFFICIENCY,
        "Movement Efficiency",
        AttributeCategory.MOVEMENT,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("The effectiveness of "),
            ComponentUtils.create("movement speed when penalized", NamedTextColor.RED),
            ComponentUtils.create(" by walking on certain surfaces.")
        )
    ),

    OXYGEN_BONUS(
        Attribute.OXYGEN_BONUS,
        "Lung Capacity",
        AttributeCategory.SURVIVABILITY,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("Grants "),
            ComponentUtils.create("extra oxygen time", NamedTextColor.AQUA),
            ComponentUtils.create(" while holding breath underwater.")
        )
    ),

    WATER_MOVEMENT(
        Attribute.WATER_MOVEMENT_EFFICIENCY,
        "Water Speed",
        AttributeCategory.MOVEMENT,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("Generic "),
            ComponentUtils.create("movement speed", NamedTextColor.WHITE),
            ComponentUtils.create(" while underwater.")
        )
    ),

    MINING_REACH(
        Attribute.BLOCK_INTERACTION_RANGE,
        "Mining Reach",
        AttributeCategory.FORAGING,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("The distance at which "),
            ComponentUtils.create("any blocks", NamedTextColor.AQUA),
            ComponentUtils.create(" can be mined/interacted with.")
        )
    ),

    COMBAT_REACH(
        Attribute.ENTITY_INTERACTION_RANGE,
        "Combat Reach",
        AttributeCategory.COMBAT,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("The distance at which "),
            ComponentUtils.create("any entities", NamedTextColor.AQUA),
            ComponentUtils.create(" can be attacked/interacted with.")
        )
    ),

    FOLLOW_RANGE(
        Attribute.FOLLOW_RANGE,
        "Follow Range",
        AttributeCategory.SPECIAL,
        AttributeType.HELPFUL
    ),

    FLYING_SPEED(
        Attribute.FLYING_SPEED,
        "Flying Speed",
        AttributeCategory.MOVEMENT,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("The "),
            ComponentUtils.create("speed", NamedTextColor.WHITE),
            ComponentUtils.create(" of flight.")

        )
    ),

    FALL_DAMAGE_MULTIPLIER(
        Attribute.FALL_DAMAGE_MULTIPLIER,
        "Fall Damage",
        AttributeCategory.MOVEMENT,
        AttributeType.PUNISHING,
        ComponentUtils.merge(
            ComponentUtils.create("The multiplier of incoming damage due to "),
            ComponentUtils.create("falling", NamedTextColor.RED),
            ComponentUtils.create(".")
        )
    ),

    SAFE_FALL(
        Attribute.SAFE_FALL_DISTANCE,
        "Safe Fall",
        AttributeCategory.MOVEMENT,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("The maximum height at which "),
            ComponentUtils.create("fall damage", NamedTextColor.RED),
            ComponentUtils.create(" is ignored.")
        )
    ),

    STEP(
        Attribute.STEP_HEIGHT,
        "Step",
        AttributeCategory.MOVEMENT,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("Affects the "),
            ComponentUtils.create("step height", NamedTextColor.LIGHT_PURPLE),
            ComponentUtils.create(" for climbing blocks without jumping.")
        )
    ),

    GRAVITY(
        Attribute.GRAVITY,
        "Gravity",
        AttributeCategory.MOVEMENT,
        AttributeType.SPECIAL,
        ComponentUtils.merge(
            ComponentUtils.create("The strength of "),
            ComponentUtils.create("gravity's influence", NamedTextColor.LIGHT_PURPLE),
            ComponentUtils.create(" when airborne.")
        )
    ),

    JUMP_HEIGHT(
        Attribute.JUMP_STRENGTH,
        "Jump Strength",
        AttributeCategory.MOVEMENT,
        AttributeType.SPECIAL,
        ComponentUtils.merge(
            ComponentUtils.create("Affects "),
            ComponentUtils.create("jump height", NamedTextColor.LIGHT_PURPLE),
            ComponentUtils.create(".")
        )
    ),

    LUCK(
        "Luckiness",
        AttributeCategory.SPECIAL,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("The effectiveness of "),
            ComponentUtils.create("rare items", NamedTextColor.LIGHT_PURPLE),
            ComponentUtils.create(" dropping."),
            ComponentUtils.create(" (in most contexts!)", NamedTextColor.DARK_GRAY)
        )
    ),

    SCALE(
        Attribute.SCALE,
        "Size",
        AttributeCategory.SPECIAL,
        AttributeType.SPECIAL,
        ComponentUtils.merge(
            ComponentUtils.create("How "),
            ComponentUtils.create("big or small", NamedTextColor.LIGHT_PURPLE),
            ComponentUtils.create(" you are.")
        )
    ),

    PROFICIENCY(
        "Proficiency",
        AttributeCategory.PROFICIENCY,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("How much extra "),
            ComponentUtils.create("skill experience", NamedTextColor.GREEN),
            ComponentUtils.create(" you earn.")
        )
    ),

    COMBAT_PROFICIENCY(
        "Combat Proficiency",
        AttributeCategory.PROFICIENCY,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("How much extra "),
            ComponentUtils.create("combat experience", NamedTextColor.GREEN),
            ComponentUtils.create(" you earn.")
        )
    ),

    MINING_PROFICIENCY(
        "Mining Proficiency",
        AttributeCategory.PROFICIENCY,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("How much extra "),
            ComponentUtils.create("mining experience", NamedTextColor.GREEN),
            ComponentUtils.create(" you earn.")
        )
    ),

    FISHING_PROFICIENCY(
        "Fishing Proficiency",
        AttributeCategory.PROFICIENCY,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("How much extra "),
            ComponentUtils.create("fishing experience", NamedTextColor.GREEN),
            ComponentUtils.create(" you earn.")
        )
    ),

    FARMING_PROFICIENCY(
        "Farming Proficiency",
        AttributeCategory.PROFICIENCY,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("How much extra "),
            ComponentUtils.create("farming experience", NamedTextColor.GREEN),
            ComponentUtils.create(" you earn.")
        )
    ),

    WOODCUTTING_PROFICIENCY(
        "Woodcutting Proficiency",
        AttributeCategory.PROFICIENCY,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("How much extra "),
            ComponentUtils.create("woodcutting experience", NamedTextColor.GREEN),
            ComponentUtils.create(" you earn.")
        )
    ),

    MAGIC_PROFICIENCY(
        "Magic Proficiency",
        AttributeCategory.PROFICIENCY,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("How much extra "),
            ComponentUtils.create("magic experience", NamedTextColor.GREEN),
            ComponentUtils.create(" you earn.")
        )
    ),

    SLAYER_PROFICIENCY(
        "Slayer Proficiency",
        AttributeCategory.PROFICIENCY,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("How much extra "),
            ComponentUtils.create("slayer experience", NamedTextColor.GREEN),
            ComponentUtils.create(" you earn.")
        )
    ),

    ZOMBIE_REINFORCEMENTS(
        Attribute.SPAWN_REINFORCEMENTS,
        "Zombie Reinforcements",
        AttributeCategory.SPECIAL,
        AttributeType.SPECIAL
    ),

    // Attributes that exist for backwards compatibility. These attributes do nothing, and exist so the plugin doesn't
    // spit tracebacks for old unconverted items.
    LEGACY_DEFENSE(
        Attribute.ARMOR_TOUGHNESS,
        "Defense (Legacy)",
        AttributeCategory.SURVIVABILITY,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("Vanilla Minecraft's 'armor toughness'. Completely ineffective in this plugin.")
        )
    ),

    LEGACY_LUCK(
        Attribute.LUCK,
        "Luck (Legacy)",
        AttributeCategory.SPECIAL,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("Vanilla Minecraft's 'luck'. Completely ineffective in this plugin.")
        )
    ),

    LEGACY_MINING_SPEED(
        Attribute.BLOCK_BREAK_SPEED,
        "Mining Speed (Legacy)",
        AttributeCategory.SPECIAL,
        AttributeType.HELPFUL,
        ComponentUtils.merge(
            ComponentUtils.create("Vanilla Minecraft's 'mining speed'. Completely ineffective in this plugin.")
        )
    ),
    ;

    /**
     * Gets the wrapped attribute this attribute refers to.
     * Keep in mind, this will only return non-null values when isVanilla() is true.
     * @return The vanilla attribute this instance is wrapping. Returns null if this is a custom attribute.
     */
    var wrappedAttribute: Attribute? = null
        private set

    private val _key: NamespacedKey

    /**
     * Builds an attribute. This is called for either custom or vanilla ones.
     * @param DisplayName The safe display name to render on components in game.
     * @param Category The category of the attribute for organization.
     * @param Type The type of attribute, (helpful, harmful?)
     * @param Description An explanation of what this attribute does.
     */
    /**
     * Builds an attribute without a description.
     * Some attributes don't exactly need a description, but every attribute probably should just to be safe.
     * @param DisplayName The safe display name to render on components in game.
     * @param Category The category of the attribute for organization.
     * @param Type The type of attribute, (helpful, harmful?)
     */
    init {
        _key = NamespacedKey("smprpg", this.name.lowercase(Locale.getDefault()))
    }

    /**
     * Builds an attribute that is considered vanilla, meaning we are just wrapping over an already existing attribute.
     * @param vanillaAttribute The vanilla attribute type.
     * @param DisplayName The safe display name to render on components in game.
     * @param Category The category of the attribute for organization.
     * @param Type The type of attribute, (helpful, harmful?)
     * @param Description An explanation of what this attribute does.
     */
    /**
     * Builds an attribute that is considered vanilla, but omits the description.
     * @param vanillaAttribute The vanilla attribute type.
     * @param displayName The safe display name to render on components in game.
     * @param category The category of the attribute for organization.
     * @param type The type of attribute, (helpful, harmful?)
     */
    @JvmOverloads
    constructor(
        vanillaAttribute: Attribute,
        displayName: String,
        category: AttributeCategory,
        type: AttributeType,
        description: Component? = ComponentUtils.create("This attribute does not have a description.")
    ) : this(displayName, category, type, description) {
        this.wrappedAttribute = vanillaAttribute
    }


    val isVanilla: Boolean
        /**
         * Checks if this is simply a wrapper for a vanilla attribute.
         * @return True if this is a vanilla attribute wrapper.
         */
        get() = this.wrappedAttribute != null

    val isCustom: Boolean
        /**
         * Checks if this is a custom and new attribute.
         * @return True if this is a custom attribute.
         */
        get() = !this.isVanilla

    /**
     * Retrieve the unique key for this attribute. Useful for persistent data containers.
     * @return A valid unique identifier for this attribute.
     */
    fun key(): NamespacedKey {
        return _key
    }

    val isCombatAttribute: Boolean
        /**
         * Checks if this attribute contributes to general combat effectiveness. Used for hard mode attribute nerfs.
         * @return True if this is a combat attribute, false if hard mode should be unaffected.
         */
        get() = when (this) {
            AttributeWrapper.CRITICAL_DAMAGE, AttributeWrapper.CRITICAL_CHANCE, AttributeWrapper.STRENGTH, AttributeWrapper.REGENERATION, AttributeWrapper.INTELLIGENCE, AttributeWrapper.ARCANE_RATING, AttributeWrapper.HEALTH, AttributeWrapper.DEFENSE -> true
            else -> false
        }

    companion object {
        @JvmStatic
        fun fromKey(attributeKey: NamespacedKey): AttributeWrapper? {
            if (attributeKey.namespace != "smprpg") return null
            try {
                return valueOf(attributeKey.key.uppercase(Locale.getDefault()))
            } catch (e: IllegalArgumentException) {
                return null
            }
        }

        fun fromAttribute(attribute: Attribute): AttributeWrapper? {
            for (attributeWrapper in entries) if (attribute == attributeWrapper.wrappedAttribute) return attributeWrapper
            return null
        }
    }
}
