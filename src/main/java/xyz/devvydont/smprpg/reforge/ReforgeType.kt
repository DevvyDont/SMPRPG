package xyz.devvydont.smprpg.reforge

import org.apache.commons.lang3.StringUtils
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.reforge.definitions.*
import xyz.devvydont.smprpg.services.ItemService
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
        ItemClassification.TOME,
        ItemClassification.HELMET,
        ItemClassification.CHESTPLATE,
        ItemClassification.LEGGINGS,
        ItemClassification.BOOTS,
        ItemClassification.CHARM
    ),
    HEARTY(
        HeartyReforge::class.java,
        ItemClassification.TOME,
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
        ItemClassification.TOME,
        ItemClassification.HELMET,
        ItemClassification.CHESTPLATE,
        ItemClassification.LEGGINGS,
        ItemClassification.BOOTS,
        ItemClassification.CHARM
    ),
    HEAVY(
        HeavyReforge::class.java,
        ItemClassification.TOME,
        ItemClassification.HELMET,
        ItemClassification.CHESTPLATE,
        ItemClassification.LEGGINGS,
        ItemClassification.BOOTS,
        ItemClassification.CHARM
    ),
    HEFTY(
        HeftyReforge::class.java,
        ItemClassification.TOME,
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

    // Magic focused for armor
    SMART(
        SmartReforge::class.java,
        ItemClassification.HELMET,
        ItemClassification.CHESTPLATE,
        ItemClassification.LEGGINGS,
        ItemClassification.BOOTS,
        ItemClassification.CHARM,
        ItemClassification.TOME
    ),
    WISE(
        WiseReforge::class.java,
        ItemClassification.HELMET,
        ItemClassification.CHESTPLATE,
        ItemClassification.LEGGINGS,
        ItemClassification.BOOTS,
        ItemClassification.CHARM,
        ItemClassification.TOME
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
        ItemClassification.TOME,
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
        ItemClassification.TOME,
        ItemClassification.HELMET,
        ItemClassification.CHESTPLATE,
        ItemClassification.LEGGINGS,
        ItemClassification.BOOTS,
        ItemClassification.CHARM
    ),
    AGILE(
        AgileReforge::class.java,
        ItemClassification.TOME,
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
        ItemClassification.SHOVEL,
        ItemClassification.HOE,
        ItemClassification.HATCHET,
        ItemClassification.DRILL
    ),
    HASTY(
        HastyReforge::class.java,
        ItemClassification.TOOL,
        ItemClassification.CHARM,
        ItemClassification.AXE,
        ItemClassification.PICKAXE,
        ItemClassification.SHOVEL,
        ItemClassification.HOE,
        ItemClassification.HATCHET,
        ItemClassification.DRILL
    ),
    PROSPECTING(
        ProspectingReforge::class.java,
        ItemClassification.PICKAXE,
        ItemClassification.DRILL,
    ),
    SURVEYING(
        SurveyingReforge::class.java,
        ItemClassification.PICKAXE,
        ItemClassification.DRILL,
    ),

    // Farming oriented
    GLEANING(
        GleaningReforge::class.java,
        ItemClassification.HOE,
        ItemClassification.HATCHET
    ),
    CULTIVATING(
        CultivatingReforge::class.java,
        ItemClassification.HOE,
        ItemClassification.HATCHET
    ),

    // Wooductting oriented
    CANADIAN(
        CanadianReforge::class.java,
        ItemClassification.AXE,
        ItemClassification.HATCHET
    ),
    ARBOR(
        ArborReforge::class.java,
        ItemClassification.AXE,
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
        ItemClassification.STAFF,
        ItemClassification.TOME,
        ItemClassification.HELMET,
        ItemClassification.CHESTPLATE,
        ItemClassification.LEGGINGS,
        ItemClassification.BOOTS,
        ItemClassification.ROD,
        ItemClassification.TOOL,
        ItemClassification.CHARM,
        ItemClassification.SWORD,
        ItemClassification.SPEAR,
        ItemClassification.BOW,
        ItemClassification.SHORTBOW,
        ItemClassification.CROSSBOW,
        ItemClassification.AXE,
        ItemClassification.TRIDENT,
        ItemClassification.MACE,
        ItemClassification.PICKAXE,
        ItemClassification.HOE,
        ItemClassification.HATCHET,
        ItemClassification.DRILL
    ),
    COPIOUS(
        CopiousReforge::class.java,
        ItemClassification.STAFF,
        ItemClassification.TOME,
        ItemClassification.HELMET,
        ItemClassification.CHESTPLATE,
        ItemClassification.LEGGINGS,
        ItemClassification.BOOTS,
        ItemClassification.ROD,
        ItemClassification.TOOL,
        ItemClassification.CHARM,
        ItemClassification.SWORD,
        ItemClassification.SPEAR,
        ItemClassification.BOW,
        ItemClassification.SHORTBOW,
        ItemClassification.CROSSBOW,
        ItemClassification.AXE,
        ItemClassification.TRIDENT,
        ItemClassification.MACE,
        ItemClassification.PICKAXE,
        ItemClassification.HOE,
        ItemClassification.HATCHET,
        ItemClassification.DRILL
    ),

    // DAMAGE (melee)
    SPICY(
        SpicyReforge::class.java,
        ItemClassification.STAFF,
        ItemClassification.SWORD,
        ItemClassification.SPEAR,
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
        ItemClassification.SPEAR,
        ItemClassification.TRIDENT,
        ItemClassification.WEAPON,
        ItemClassification.AXE,
        ItemClassification.MACE,
        ItemClassification.TOOL,
        ItemClassification.CHARM,
        ItemClassification.PICKAXE,
        ItemClassification.HOE,
        ItemClassification.DRILL
    ),
    POWERFUL(
        PowerfulReforge::class.java,
        ItemClassification.STAFF,
        ItemClassification.BOW,
        ItemClassification.SHORTBOW,
        ItemClassification.CROSSBOW,
        ItemClassification.TOOL,
        ItemClassification.CHARM,
        ItemClassification.PICKAXE,
        ItemClassification.HOE,
        ItemClassification.DRILL
    ),
    DULL(DullReforge::class.java, ItemClassification.SWORD, ItemClassification.SPEAR, ItemClassification.CHARM),
    SLUGGISH(
        SluggishReforge::class.java,
        ItemClassification.STAFF,
        ItemClassification.SWORD,
        ItemClassification.SPEAR,
        ItemClassification.TRIDENT,
        ItemClassification.WEAPON,
        ItemClassification.AXE,
        ItemClassification.MACE,
        ItemClassification.CHARM,
        ItemClassification.SHORTBOW
    ),
    STINGING(
        StingingReforge::class.java,
        ItemClassification.STAFF,
        ItemClassification.SWORD,
        ItemClassification.SPEAR,
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
        ItemClassification.STAFF,
        ItemClassification.SWORD,
        ItemClassification.SPEAR,
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
        ItemClassification.STAFF,
        ItemClassification.TOOL,
        ItemClassification.SWORD,
        ItemClassification.SPEAR,
        ItemClassification.TRIDENT,
        ItemClassification.WEAPON,
        ItemClassification.AXE,
        ItemClassification.MACE,
        ItemClassification.CHARM,
        ItemClassification.PICKAXE,
        ItemClassification.HOE,
        ItemClassification.HATCHET,
        ItemClassification.DRILL
    ),
    EXTENDED(
        ExtendedReforge::class.java,
        ItemClassification.STAFF,
        ItemClassification.TOOL,
        ItemClassification.SWORD,
        ItemClassification.SPEAR,
        ItemClassification.TRIDENT,
        ItemClassification.WEAPON,
        ItemClassification.AXE,
        ItemClassification.MACE,
        ItemClassification.CHARM,
        ItemClassification.PICKAXE,
        ItemClassification.HOE,
        ItemClassification.HATCHET,
        ItemClassification.DRILL
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
        ItemClassification.STAFF,
        ItemClassification.SWORD,
        ItemClassification.SPEAR,
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
    CRYPTIC(
        CrypticReforge::class.java,
        ItemClassification.STAFF,
        ItemClassification.SWORD,
        ItemClassification.SPEAR,
        ItemClassification.AXE,
        ItemClassification.BOW,
        ItemClassification.CROSSBOW,
        ItemClassification.SHORTBOW,
        ItemClassification.TRIDENT,
        ItemClassification.MACE,
        ItemClassification.TOOL,
        ItemClassification.CHARM
    ),
    CRYSTALLIZED(
        CrystallizedReforge::class.java,
        ItemClassification.STAFF,
        ItemClassification.SWORD,
        ItemClassification.SPEAR,
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
            ERROR, ACCELERATED, WITHERED, OVERHEATING, ALLURING, PRISMATIC, PLUNDERING, CRYPTIC, CRYSTALLIZED, SIRENIC -> false
            else -> true
        }

    val displayItem: ItemStack
        get() = when (this) {
                OVERHEATING -> ItemService.generate(CustomItemType.SMOLDERING_CORE)
                DULL -> ItemService.generate(Material.STONE_SWORD)
                FIRM -> ItemService.generate(Material.IRON_SWORD)
                SLUGGISH -> ItemService.generate(Material.STONE_AXE)
                STINGING -> ItemService.generate(Material.DIAMOND_AXE)
                HASTY -> ItemService.generate(Material.GOLDEN_PICKAXE)
                HEAVY -> ItemService.generate(Material.IRON_CHESTPLATE)
                HEFTY -> ItemService.generate(Material.DIAMOND_CHESTPLATE)
                LIGHT -> ItemService.generate(Material.SUGAR)
                LUCKY -> ItemService.generate(Material.EMERALD)
                RAPID -> ItemService.generate(Material.RABBIT_FOOT)
                SPICY -> ItemService.generate(Material.GOLDEN_SWORD)
                QUICK -> ItemService.generate(Material.IRON_BOOTS)
                SWIFT -> ItemService.generate(Material.CHAINMAIL_BOOTS)
                SHARP -> ItemService.generate(Material.DIAMOND_AXE)
                STRONG -> ItemService.generate(Material.CHAINMAIL_CHESTPLATE)
                COPIOUS -> ItemService.generate(Material.DIAMOND)
                HEALTHY -> ItemService.generate(Material.APPLE)
                HEARTY -> ItemService.generate(Material.GOLDEN_APPLE)
                DURABLE -> ItemService.generate(Material.IRON_INGOT)
                POLISHED -> ItemService.generate(Material.DIAMOND_BLOCK)
                EXTENDED -> ItemService.generate(Material.SPYGLASS)
                REACHING -> ItemService.generate(Material.SPYGLASS)
                SAVAGE -> ItemService.generate(Material.FIRE_CHARGE)
                SMART -> ItemService.generate(Material.BOOK)
                WISE -> ItemService.generate(Material.ENCHANTED_BOOK)
                PROSPECTING -> ItemService.generate(Material.IRON_PICKAXE)
                SURVEYING -> ItemService.generate(Material.STONE_PICKAXE)
                GLEANING -> ItemService.generate(Material.GOLDEN_HOE)
                CULTIVATING -> ItemService.generate(Material.DIAMOND_HOE)
                CANADIAN -> ItemService.generate(Material.RED_DYE)
                ARBOR -> ItemService.generate(Material.OAK_LOG)
                ACCELERATED -> ItemService.generate(Material.DIAMOND_PICKAXE)
                POWERFUL -> ItemService.generate(Material.STONE_AXE)
                WITHERED -> ItemService.generate(CustomItemType.DESOLATED_STONE)
                FORTIFIED -> ItemService.generate(Material.NETHERITE_CHESTPLATE)
                AGILE -> ItemService.generate(Material.LEATHER_BOOTS)
                ANCIENT -> ItemService.generate(Material.LEATHER_CHESTPLATE)
                PLUNDERING -> ItemService.generate(Material.GOLD_BLOCK)
                PRISMATIC -> ItemService.generate(CustomItemType.IRIDESCENT_LENS)
                SALTY -> ItemService.generate(Material.SUGAR)
                TEMPTING -> ItemService.generate(Material.ROTTEN_FLESH)
                ALLURING -> ItemService.generate(CustomItemType.PREDATOR_TOOTH)
                SIRENIC -> ItemService.generate(CustomItemType.HYPNOTIC_EYE)
                MAGNETIC -> ItemService.generate(Material.IRON_BLOCK)
                CRYSTALLIZED -> ItemService.generate(CustomItemType.VOID_RELIC)
                CRYPTIC -> ItemService.generate(CustomItemType.CRYSTAL_BALL)
                else -> ItemService.generate(Material.BARRIER)
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
