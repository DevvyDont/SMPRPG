package xyz.devvydont.smprpg.reforge

import org.apache.commons.lang3.StringUtils
import org.bukkit.Material
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.reforge.definitions.*
import java.lang.reflect.InvocationTargetException
import java.util.*
import java.util.List

/**
 * Contains ALL the reforges that are applicable to items.
 * When creating a new reforge, MAKE SURE you check the following things:
 * - Make sure your reforge class handler (the first parameter) implements Listener and handles any special logic (if desired)!
 * - What items do you want the item to be applicable to? You are adding item types that show up on the bottom of item lore.
 * - If you DON'T WANT your reforge to be NPC rollable, make sure you flag it as so in the [ReforgeType.isRollable] method!
 * - Make sure you provide a material to make your reforge show up as in the /reforges menu! ([ReforgeType.getDisplayMaterial])
 * - When adding attributes to your reforges in its handler class, make sure to make item rarity affect how good it is! [xyz.devvydont.smprpg.items.ItemRarity.ordinal] is great for this.
 */
enum class ReforgeType(val handler: Class<out ReforgeBase>, vararg whitelist: ItemClassification) {
    ERROR(UnimplementedReforge::class.java, ItemClassification.ITEM),

    // HP oriented
    HEALTHY(
        HealthyReforge::class.java,
        ItemClassification.HELMET,
        ItemClassification.CHESTPLATE,
        ItemClassification.LEGGINGS,
        ItemClassification.BOOTS,
        ItemClassification.CHARM
    ),
    HEARTY(
        HeartyReforge::class.java,
        ItemClassification.HELMET,
        ItemClassification.CHESTPLATE,
        ItemClassification.LEGGINGS,
        ItemClassification.BOOTS,
        ItemClassification.CHARM
    ),

    // ARMOR oriented
    DURABLE(
        DurableReforge::class.java,
        ItemClassification.HELMET,
        ItemClassification.CHESTPLATE,
        ItemClassification.LEGGINGS,
        ItemClassification.BOOTS,
        ItemClassification.CHARM
    ),
    FORTIFIED(
        FortifiedReforge::class.java,
        ItemClassification.HELMET,
        ItemClassification.CHESTPLATE,
        ItemClassification.LEGGINGS,
        ItemClassification.BOOTS,
        ItemClassification.CHARM
    ),

    // KNOCKBACK oriented
    FIRM(
        FirmReforge::class.java,
        ItemClassification.HELMET,
        ItemClassification.CHESTPLATE,
        ItemClassification.LEGGINGS,
        ItemClassification.BOOTS,
        ItemClassification.CHARM
    ),
    HEAVY(
        HeavyReforge::class.java,
        ItemClassification.HELMET,
        ItemClassification.CHESTPLATE,
        ItemClassification.LEGGINGS,
        ItemClassification.BOOTS,
        ItemClassification.CHARM
    ),
    HEFTY(
        HeftyReforge::class.java,
        ItemClassification.HELMET,
        ItemClassification.CHESTPLATE,
        ItemClassification.LEGGINGS,
        ItemClassification.BOOTS,
        ItemClassification.CHARM
    ),

    // DPS for armor
    SAVAGE(
        SavageReforge::class.java,
        ItemClassification.HELMET,
        ItemClassification.CHESTPLATE,
        ItemClassification.LEGGINGS,
        ItemClassification.BOOTS,
        ItemClassification.CHARM
    ),
    STRONG(
        StrongReforge::class.java,
        ItemClassification.HELMET,
        ItemClassification.CHESTPLATE,
        ItemClassification.LEGGINGS,
        ItemClassification.BOOTS,
        ItemClassification.CHARM
    ),

    // ALL AROUND ARMOR
    POLISHED(
        PolishedReforge::class.java,
        ItemClassification.HELMET,
        ItemClassification.CHESTPLATE,
        ItemClassification.LEGGINGS,
        ItemClassification.BOOTS,
        ItemClassification.CHARM
    ),
    ANCIENT(
        AncientReforge::class.java,
        ItemClassification.HELMET,
        ItemClassification.CHESTPLATE,
        ItemClassification.LEGGINGS,
        ItemClassification.BOOTS,
        ItemClassification.CHARM
    ),

    // Movement Speed oriented
    LIGHT(
        LightReforge::class.java,
        ItemClassification.HELMET,
        ItemClassification.CHESTPLATE,
        ItemClassification.LEGGINGS,
        ItemClassification.BOOTS,
        ItemClassification.CHARM,
        ItemClassification.BOW,
        ItemClassification.SHORTBOW,
        ItemClassification.CROSSBOW
    ),
    SWIFT(
        SwiftReforge::class.java,
        ItemClassification.HELMET,
        ItemClassification.CHESTPLATE,
        ItemClassification.LEGGINGS,
        ItemClassification.BOOTS,
        ItemClassification.CHARM
    ),
    AGILE(
        AgileReforge::class.java,
        ItemClassification.HELMET,
        ItemClassification.CHESTPLATE,
        ItemClassification.LEGGINGS,
        ItemClassification.BOOTS,
        ItemClassification.CHARM
    ),

    // Mining/Dig speed oriented
    QUICK(
        QuickReforge::class.java,
        ItemClassification.TOOL,
        ItemClassification.CHARM,
        ItemClassification.AXE,
        ItemClassification.PICKAXE,
        ItemClassification.HOE,
        ItemClassification.HATCHET
    ),
    HASTY(
        HastyReforge::class.java,
        ItemClassification.TOOL,
        ItemClassification.CHARM,
        ItemClassification.AXE,
        ItemClassification.PICKAXE,
        ItemClassification.HOE,
        ItemClassification.HATCHET
    ),

    // Fishing oriented (RODS)
    TEMPTING(TemptingReforge::class.java, ItemClassification.CHARM, ItemClassification.ROD),
    ALLURING(AlluringReforge::class.java, ItemClassification.CHARM, ItemClassification.ROD),
    MAGNETIC(MagneticReforge::class.java, ItemClassification.CHARM, ItemClassification.ROD),
    PLUNDERING(PlunderingReforge::class.java, ItemClassification.CHARM, ItemClassification.ROD),
    SALTY(SaltyReforge::class.java, ItemClassification.CHARM, ItemClassification.ROD),
    PRISMATIC(PrismaticReforge::class.java, ItemClassification.CHARM, ItemClassification.ROD),

    // Fishing oriented (ARMOR)
    SIRENIC(
        SirenicReforge::class.java,
        ItemClassification.HELMET,
        ItemClassification.CHESTPLATE,
        ItemClassification.LEGGINGS,
        ItemClassification.BOOTS,
        ItemClassification.CHARM
    ),

    // Luck oriented
    LUCKY(
        LuckyReforge::class.java,
        ItemClassification.HELMET,
        ItemClassification.CHESTPLATE,
        ItemClassification.LEGGINGS,
        ItemClassification.BOOTS,
        ItemClassification.ROD,
        ItemClassification.TOOL,
        ItemClassification.CHARM,
        ItemClassification.SWORD,
        ItemClassification.BOW,
        ItemClassification.SHORTBOW,
        ItemClassification.CROSSBOW,
        ItemClassification.AXE,
        ItemClassification.TRIDENT,
        ItemClassification.MACE,
        ItemClassification.PICKAXE,
        ItemClassification.HOE,
        ItemClassification.HATCHET
    ),
    COPIOUS(
        CopiousReforge::class.java,
        ItemClassification.HELMET,
        ItemClassification.CHESTPLATE,
        ItemClassification.LEGGINGS,
        ItemClassification.BOOTS,
        ItemClassification.ROD,
        ItemClassification.TOOL,
        ItemClassification.CHARM,
        ItemClassification.SWORD,
        ItemClassification.BOW,
        ItemClassification.SHORTBOW,
        ItemClassification.CROSSBOW,
        ItemClassification.AXE,
        ItemClassification.TRIDENT,
        ItemClassification.MACE,
        ItemClassification.PICKAXE,
        ItemClassification.HOE,
        ItemClassification.HATCHET
    ),

    // DAMAGE (melee)
    SPICY(
        SpicyReforge::class.java,
        ItemClassification.SWORD,
        ItemClassification.TRIDENT,
        ItemClassification.WEAPON,
        ItemClassification.AXE,
        ItemClassification.MACE,
        ItemClassification.BOW,
        ItemClassification.SHORTBOW,
        ItemClassification.CROSSBOW,
        ItemClassification.CHARM
    ),
    SHARP(
        SharpReforge::class.java,
        ItemClassification.SWORD,
        ItemClassification.TRIDENT,
        ItemClassification.WEAPON,
        ItemClassification.AXE,
        ItemClassification.MACE,
        ItemClassification.TOOL,
        ItemClassification.CHARM,
        ItemClassification.PICKAXE,
        ItemClassification.HOE
    ),
    POWERFUL(
        PowerfulReforge::class.java,
        ItemClassification.BOW,
        ItemClassification.SHORTBOW,
        ItemClassification.CROSSBOW,
        ItemClassification.TOOL,
        ItemClassification.CHARM,
        ItemClassification.PICKAXE,
        ItemClassification.HOE
    ),
    DULL(DullReforge::class.java, ItemClassification.SWORD, ItemClassification.CHARM),
    SLUGGISH(
        SluggishReforge::class.java,
        ItemClassification.SWORD,
        ItemClassification.TRIDENT,
        ItemClassification.WEAPON,
        ItemClassification.AXE,
        ItemClassification.MACE,
        ItemClassification.CHARM,
        ItemClassification.SHORTBOW
    ),
    STINGING(
        StingingReforge::class.java,
        ItemClassification.SWORD,
        ItemClassification.TRIDENT,
        ItemClassification.WEAPON,
        ItemClassification.AXE,
        ItemClassification.MACE,
        ItemClassification.BOW,
        ItemClassification.SHORTBOW,
        ItemClassification.CROSSBOW,
        ItemClassification.CHARM
    ),

    RAPID(
        RapidReforge::class.java,
        ItemClassification.SWORD,
        ItemClassification.TRIDENT,
        ItemClassification.WEAPON,
        ItemClassification.AXE,
        ItemClassification.MACE,
        ItemClassification.SHORTBOW,
        ItemClassification.CHARM
    ),

    // REACH
    REACHING(
        ReachingReforge::class.java,
        ItemClassification.TOOL,
        ItemClassification.SWORD,
        ItemClassification.TRIDENT,
        ItemClassification.WEAPON,
        ItemClassification.AXE,
        ItemClassification.MACE,
        ItemClassification.CHARM,
        ItemClassification.PICKAXE,
        ItemClassification.HOE,
        ItemClassification.HATCHET
    ),
    EXTENDED(
        ExtendedReforge::class.java,
        ItemClassification.TOOL,
        ItemClassification.SWORD,
        ItemClassification.TRIDENT,
        ItemClassification.WEAPON,
        ItemClassification.AXE,
        ItemClassification.MACE,
        ItemClassification.CHARM,
        ItemClassification.PICKAXE,
        ItemClassification.HOE,
        ItemClassification.HATCHET
    ),

    // META reforges, only dropped from rare drops
    ACCELERATED(
        AcceleratedReforge::class.java,
        ItemClassification.HELMET,
        ItemClassification.CHESTPLATE,
        ItemClassification.LEGGINGS,
        ItemClassification.BOOTS,
        ItemClassification.CHARM
    ),  // MAX Movement Speed
    WITHERED(
        WitheredReforge::class.java,
        ItemClassification.SWORD,
        ItemClassification.AXE,
        ItemClassification.BOW,
        ItemClassification.CROSSBOW,
        ItemClassification.SHORTBOW,
        ItemClassification.TRIDENT,
        ItemClassification.MACE,
        ItemClassification.TOOL,
        ItemClassification.CHARM
    ),
    OVERHEATING(
        OverheatingReforge::class.java,
        ItemClassification.HELMET,
        ItemClassification.CHESTPLATE,
        ItemClassification.LEGGINGS,
        ItemClassification.BOOTS,
        ItemClassification.CHARM
    ),
    CRYSTALLIZED(
        CrystallizedReforge::class.java,
        ItemClassification.SWORD,
        ItemClassification.AXE,
        ItemClassification.BOW,
        ItemClassification.CROSSBOW,
        ItemClassification.SHORTBOW,
        ItemClassification.TRIDENT,
        ItemClassification.MACE,
        ItemClassification.TOOL,
        ItemClassification.CHARM
    ),
    ; //    OVERCLOCKED(UnimplementedReforge.class, ItemClassification.TOOL, ItemClassification.PICKAXE, ItemClassification.HOE),  // MAX Dig speed
    //    EPHEMERAL(UnimplementedReforge.class, ItemClassification.SWORD, ItemClassification.TRIDENT, ItemClassification.WEAPON, ItemClassification.AXE, ItemClassification.MACE),    // MAX Attack Speed
    //    IMMORTAL(UnimplementedReforge.class, ItemClassification.HELMET, ItemClassification.CHESTPLATE, ItemClassification.LEGGINGS, ItemClassification.BOOTS),     // MAX DEF armor
    //    TITANIC(UnimplementedReforge.class, ItemClassification.HELMET, ItemClassification.CHESTPLATE, ItemClassification.LEGGINGS, ItemClassification.BOOTS),      // MAX Knockback/Toughness
    //    VIGOROUS(UnimplementedReforge.class, ItemClassification.HELMET, ItemClassification.CHESTPLATE, ItemClassification.LEGGINGS, ItemClassification.BOOTS),     // MAX HP armor
    //    FORTUITOUS(UnimplementedReforge.class, ItemClassification.TOOL, ItemClassification.PICKAXE, ItemClassification.HOE),   // MAX Luck
    //    PROTRACTED(UnimplementedReforge.class, ItemClassification.TOOL, ItemClassification.SWORD, ItemClassification.TRIDENT, ItemClassification.WEAPON, ItemClassification.AXE, ItemClassification.MACE, ItemClassification.PICKAXE, ItemClassification.HOE),   // MAX reach
    //
    //    RENOWNED(UnimplementedReforge.class, ItemClassification.HELMET, ItemClassification.CHESTPLATE, ItemClassification.LEGGINGS, ItemClassification.BOOTS),     // All around armor
    //    FABLED(UnimplementedReforge.class, ItemClassification.SWORD, ItemClassification.TRIDENT, ItemClassification.WEAPON, ItemClassification.AXE, ItemClassification.MACE),       // All around melee
    //    DEMONIC(UnimplementedReforge.class, ItemClassification.BOW, ItemClassification.SHORTBOW, ItemClassification.CROSSBOW),      // All around ranged

    @JvmField
    val allowedItems: MutableCollection<ItemClassification> = HashSet<ItemClassification>()

    init {
        this.allowedItems.addAll(listOf<ItemClassification>(*whitelist))
        check(whitelist.isNotEmpty()) { "Reforge must contain allowed item types!" }
    }

    fun key(): String {
        return this.name.lowercase(Locale.getDefault())
    }

    fun display(): String {
        return StringUtils.capitalize(key())
    }

    fun isAllowed(classification: ItemClassification): Boolean {
        return allowedItems.contains(classification)
    }

    val isRollable: Boolean
        /**
         * Checks if this reforge is allowed to be randomly rolled in a reforge anvil. Rare reforges that require
         * forged crystals will not allow this
         *
         * @return
         */
        get() = when (this) {
            ERROR, ACCELERATED, WITHERED, OVERHEATING, ALLURING, PRISMATIC, PLUNDERING, CRYSTALLIZED, SIRENIC -> false
            else -> true
        }

    val displayMaterial: Material
        get() = when (this) {
            OVERHEATING -> Material.BLAZE_POWDER
            DULL -> Material.STONE_SWORD
            FIRM -> Material.IRON_SWORD
            SLUGGISH -> Material.STONE_AXE
            STINGING -> Material.DIAMOND_AXE
            HASTY -> Material.GOLDEN_PICKAXE
            HEAVY -> Material.IRON_CHESTPLATE
            HEFTY -> Material.DIAMOND_CHESTPLATE
            LIGHT -> Material.SUGAR
            LUCKY -> Material.EMERALD
            RAPID -> Material.RABBIT_FOOT
            SPICY -> Material.GOLDEN_SWORD
            QUICK -> Material.IRON_BOOTS
            SWIFT -> Material.CHAINMAIL_BOOTS
            SHARP -> Material.DIAMOND_AXE
            STRONG -> Material.CHAINMAIL_CHESTPLATE
            COPIOUS -> Material.DIAMOND
            HEALTHY -> Material.APPLE
            HEARTY -> Material.GOLDEN_APPLE
            DURABLE -> Material.IRON_INGOT
            POLISHED -> Material.DIAMOND_BLOCK
            EXTENDED -> Material.SPYGLASS
            REACHING -> Material.SPYGLASS
            SAVAGE -> Material.FIRE_CHARGE
            ACCELERATED -> Material.DIAMOND_PICKAXE
            POWERFUL -> Material.STONE_AXE
            WITHERED -> Material.WITHER_ROSE
            FORTIFIED -> Material.NETHERITE_CHESTPLATE
            AGILE -> Material.LEATHER_BOOTS
            ANCIENT -> Material.LEATHER_CHESTPLATE
            PLUNDERING -> Material.GOLD_BLOCK
            PRISMATIC -> Material.PRISMARINE_CRYSTALS
            SALTY -> Material.SUGAR
            TEMPTING -> Material.ROTTEN_FLESH
            ALLURING -> Material.PORKCHOP
            SIRENIC -> Material.HEART_OF_THE_SEA
            MAGNETIC -> Material.IRON_BLOCK
            CRYSTALLIZED -> Material.END_CRYSTAL
            else -> Material.BARRIER
        }

    /**
     * To be called once during manager instantiation. Used as a "helper" to create the singleton instance responsible
     * for a reforge.
     *
     * @return
     */
    fun createHandler(): ReforgeBase {
        try {
            return this.handler.getConstructor(this.javaClass).newInstance(this)
        } catch (e: InvocationTargetException) {
            plugin.logger
                .severe("Failed to instantiate handler for " + this.name + "! Does the constructor match ReforgeBase?")
            throw RuntimeException(e)
        } catch (e: InstantiationException) {
            plugin.logger
                .severe("Failed to instantiate handler for " + this.name + "! Does the constructor match ReforgeBase?")
            throw RuntimeException(e)
        } catch (e: IllegalAccessException) {
            plugin.logger
                .severe("Failed to instantiate handler for " + this.name + "! Does the constructor match ReforgeBase?")
            throw RuntimeException(e)
        } catch (e: NoSuchMethodException) {
            plugin.logger
                .severe("Failed to instantiate handler for " + this.name + "! Does the constructor match ReforgeBase?")
            throw RuntimeException(e)
        }
    }
}
