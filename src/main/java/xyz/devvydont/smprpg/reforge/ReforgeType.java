package xyz.devvydont.smprpg.reforge;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.reforge.definitions.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Contains ALL reforges that are possible to get
 * When reforges are applied, we can apply the following stats:
 *
 * HP
 * DEFENSE
 * ARMOR TOUGHNESS
 * KNOCKBACK RESIST
 *
 * MOVEMENT SPEED
 * MINING SPEED
 *
 * DAMAGE
 * ATTACK SPEED
 *
 * BLOCK REACH
 * COMBAT REACH
 *
 * SAFE FALL DISTANCE
 * FALL DAMAGE MULTIPLIER
 *
 * LUCK
 * JUMP/GRAVITY
 */
public enum ReforgeType {

    ERROR(UnimplementedReforge.class, ItemClassification.ITEM),

    // HP oriented
    HEALTHY(HealthyReforge.class, ItemClassification.HELMET, ItemClassification.CHESTPLATE, ItemClassification.LEGGINGS, ItemClassification.BOOTS, ItemClassification.CHARM),
    HEARTY(HeartyReforge.class, ItemClassification.HELMET, ItemClassification.CHESTPLATE, ItemClassification.LEGGINGS, ItemClassification.BOOTS, ItemClassification.CHARM),

    // ARMOR oriented
    DURABLE(DurableReforge.class, ItemClassification.HELMET, ItemClassification.CHESTPLATE, ItemClassification.LEGGINGS, ItemClassification.BOOTS, ItemClassification.CHARM),
    FORTIFIED(FortifiedReforge.class, ItemClassification.HELMET, ItemClassification.CHESTPLATE, ItemClassification.LEGGINGS, ItemClassification.BOOTS, ItemClassification.CHARM),

    // KNOCKBACK oriented
    FIRM(FirmReforge.class, ItemClassification.HELMET, ItemClassification.CHESTPLATE, ItemClassification.LEGGINGS, ItemClassification.BOOTS, ItemClassification.CHARM),
    HEAVY(HeavyReforge.class, ItemClassification.HELMET, ItemClassification.CHESTPLATE, ItemClassification.LEGGINGS, ItemClassification.BOOTS, ItemClassification.CHARM),
    HEFTY(HeftyReforge.class, ItemClassification.HELMET, ItemClassification.CHESTPLATE, ItemClassification.LEGGINGS, ItemClassification.BOOTS, ItemClassification.CHARM),

    // DPS for armor
    SAVAGE(SavageReforge.class, ItemClassification.HELMET, ItemClassification.CHESTPLATE, ItemClassification.LEGGINGS, ItemClassification.BOOTS, ItemClassification.CHARM),
    STRONG(StrongReforge.class, ItemClassification.HELMET, ItemClassification.CHESTPLATE, ItemClassification.LEGGINGS, ItemClassification.BOOTS, ItemClassification.CHARM),

    // ALL AROUND ARMOR
    POLISHED(PolishedReforge.class, ItemClassification.HELMET, ItemClassification.CHESTPLATE, ItemClassification.LEGGINGS, ItemClassification.BOOTS, ItemClassification.CHARM),
    ANCIENT(AncientReforge.class, ItemClassification.HELMET, ItemClassification.CHESTPLATE, ItemClassification.LEGGINGS, ItemClassification.BOOTS, ItemClassification.CHARM),

    // Movement Speed oriented
    LIGHT(LightReforge.class, ItemClassification.HELMET, ItemClassification.CHESTPLATE, ItemClassification.LEGGINGS, ItemClassification.BOOTS, ItemClassification.CHARM, ItemClassification.BOW, ItemClassification.SHORTBOW, ItemClassification.CROSSBOW),
    SWIFT(SwiftReforge.class, ItemClassification.HELMET, ItemClassification.CHESTPLATE, ItemClassification.LEGGINGS, ItemClassification.BOOTS, ItemClassification.CHARM),
    AGILE(AgileReforge.class, ItemClassification.HELMET, ItemClassification.CHESTPLATE, ItemClassification.LEGGINGS, ItemClassification.BOOTS, ItemClassification.CHARM),

    // Mining/Dig speed oriented
    QUICK(QuickReforge.class, ItemClassification.TOOL, ItemClassification.CHARM, ItemClassification.AXE, ItemClassification.PICKAXE, ItemClassification.HOE),
    HASTY(HastyReforge.class, ItemClassification.TOOL, ItemClassification.CHARM, ItemClassification.AXE, ItemClassification.PICKAXE, ItemClassification.HOE),

    // Fishing oriented
    TEMPTING(TemptingReforge.class, ItemClassification.CHARM, ItemClassification.ROD),
    ALLURING(AlluringReforge.class, ItemClassification.CHARM, ItemClassification.ROD),
    MAGNETIC(MagneticReforge.class, ItemClassification.CHARM, ItemClassification.ROD),
    PLUNDERING(PlunderingReforge.class, ItemClassification.CHARM, ItemClassification.ROD),
    SALTY(SaltyReforge.class, ItemClassification.CHARM, ItemClassification.ROD),
    PRISMATIC(PrismaticReforge.class, ItemClassification.CHARM, ItemClassification.ROD),

    // Luck oriented
    LUCKY(LuckyReforge.class, ItemClassification.HELMET, ItemClassification.CHESTPLATE, ItemClassification.LEGGINGS, ItemClassification.BOOTS, ItemClassification.ROD, ItemClassification.TOOL, ItemClassification.CHARM, ItemClassification.SWORD, ItemClassification.BOW, ItemClassification.SHORTBOW, ItemClassification.CROSSBOW, ItemClassification.AXE, ItemClassification.TRIDENT, ItemClassification.MACE, ItemClassification.PICKAXE, ItemClassification.HOE),
    COPIOUS(CopiousReforge.class, ItemClassification.HELMET, ItemClassification.CHESTPLATE, ItemClassification.LEGGINGS, ItemClassification.BOOTS, ItemClassification.ROD, ItemClassification.TOOL, ItemClassification.CHARM, ItemClassification.SWORD, ItemClassification.BOW, ItemClassification.SHORTBOW, ItemClassification.CROSSBOW, ItemClassification.AXE, ItemClassification.TRIDENT, ItemClassification.MACE, ItemClassification.PICKAXE, ItemClassification.HOE),

    // DAMAGE (melee)
    SPICY(SpicyReforge.class, ItemClassification.SWORD, ItemClassification.TRIDENT, ItemClassification.WEAPON, ItemClassification.AXE, ItemClassification.MACE, ItemClassification.BOW, ItemClassification.SHORTBOW, ItemClassification.CROSSBOW, ItemClassification.CHARM),
    SHARP(SharpReforge.class, ItemClassification.SWORD, ItemClassification.TRIDENT, ItemClassification.WEAPON, ItemClassification.AXE, ItemClassification.MACE, ItemClassification.TOOL, ItemClassification.CHARM, ItemClassification.PICKAXE, ItemClassification.HOE),
    POWERFUL(PowerfulReforge.class, ItemClassification.BOW, ItemClassification.SHORTBOW, ItemClassification.CROSSBOW, ItemClassification.TOOL, ItemClassification.CHARM, ItemClassification.PICKAXE, ItemClassification.HOE),
    DULL(DullReforge.class, ItemClassification.SWORD, ItemClassification.CHARM),
    SLUGGISH(SluggishReforge.class, ItemClassification.SWORD, ItemClassification.TRIDENT, ItemClassification.WEAPON, ItemClassification.AXE, ItemClassification.MACE, ItemClassification.CHARM, ItemClassification.SHORTBOW),
    STINGING(StingingReforge.class, ItemClassification.SWORD, ItemClassification.TRIDENT, ItemClassification.WEAPON, ItemClassification.AXE, ItemClassification.MACE, ItemClassification.BOW, ItemClassification.SHORTBOW, ItemClassification.CROSSBOW, ItemClassification.CHARM),

    RAPID(RapidReforge.class, ItemClassification.SWORD, ItemClassification.TRIDENT, ItemClassification.WEAPON, ItemClassification.AXE, ItemClassification.MACE, ItemClassification.SHORTBOW, ItemClassification.CHARM),

    // REACH
    REACHING(ReachingReforge.class, ItemClassification.TOOL, ItemClassification.SWORD, ItemClassification.TRIDENT, ItemClassification.WEAPON, ItemClassification.AXE, ItemClassification.MACE, ItemClassification.CHARM, ItemClassification.PICKAXE, ItemClassification.HOE),
    EXTENDED(ExtendedReforge.class, ItemClassification.TOOL, ItemClassification.SWORD, ItemClassification.TRIDENT, ItemClassification.WEAPON, ItemClassification.AXE, ItemClassification.MACE, ItemClassification.CHARM, ItemClassification.PICKAXE, ItemClassification.HOE),

    // META reforges, only dropped from rare drops
    ACCELERATED(AcceleratedReforge.class, ItemClassification.HELMET, ItemClassification.CHESTPLATE, ItemClassification.LEGGINGS, ItemClassification.BOOTS, ItemClassification.CHARM),  // MAX Movement Speed
    WITHERED(WitheredReforge.class, ItemClassification.SWORD, ItemClassification.AXE, ItemClassification.BOW, ItemClassification.CROSSBOW, ItemClassification.SHORTBOW, ItemClassification.TRIDENT, ItemClassification.MACE, ItemClassification.TOOL, ItemClassification.CHARM),  // MAX Movement Speed
    OVERHEATING(OverheatingReforge.class, ItemClassification.HELMET, ItemClassification.CHESTPLATE, ItemClassification.LEGGINGS, ItemClassification.BOOTS, ItemClassification.CHARM),
//    OVERCLOCKED(UnimplementedReforge.class, ItemClassification.TOOL, ItemClassification.PICKAXE, ItemClassification.HOE),  // MAX Dig speed
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

    ;

    private final Class<? extends ReforgeBase> handler;
    private final Collection<ItemClassification> allowedItems;

    ReforgeType(Class<? extends ReforgeBase> handler, ItemClassification...whitelist) {
        this.handler = handler;
        this.allowedItems = new HashSet<>();
        this.allowedItems.addAll(List.of(whitelist));

        if (whitelist.length <= 0)
            throw new IllegalStateException("Reforge must contain allowed item types!");
    }

    public String key() {
        return this.name().toLowerCase();
    }

    public static ReforgeType fromKey(String key) {
        return ReforgeType.valueOf(key.toUpperCase());
    }

    public String display() {
        return StringUtils.capitalize(key());
    }

    public Class<? extends ReforgeBase> getHandler() {
        return handler;
    }

    public Collection<ItemClassification> getAllowedItems() {
        return allowedItems;
    }

    public boolean isAllowed(ItemClassification classification) {
        return allowedItems.contains(classification);
    }

    /**
     * Checks if this reforge is allowed to be randomly rolled in a reforge anvil. Rare reforges that require
     * forged crystals will not allow this
     *
     * @return
     */
    public boolean isRollable() {

        return switch (this) {
            case ERROR, ACCELERATED, WITHERED, OVERHEATING, ALLURING, PRISMATIC, PLUNDERING -> false;
            default -> true;
        };
    }

    public Material getDisplayMaterial() {
        return switch (this) {
            case OVERHEATING -> Material.BLAZE_POWDER;
            case DULL -> Material.STONE_SWORD;
            case FIRM -> Material.IRON_SWORD;
            case SLUGGISH -> Material.STONE_AXE;
            case STINGING -> Material.DIAMOND_AXE;
            case HASTY -> Material.GOLDEN_PICKAXE;
            case HEAVY -> Material.IRON_CHESTPLATE;
            case HEFTY -> Material.DIAMOND_CHESTPLATE;
            case LIGHT -> Material.SUGAR;
            case LUCKY -> Material.EMERALD;
            case RAPID -> Material.RABBIT_FOOT;
            case SPICY -> Material.GOLDEN_SWORD;
            case QUICK -> Material.IRON_BOOTS;
            case SWIFT -> Material.CHAINMAIL_BOOTS;
            case SHARP -> Material.DIAMOND_AXE;
            case STRONG -> Material.CHAINMAIL_CHESTPLATE;
            case COPIOUS -> Material.DIAMOND;
            case HEALTHY -> Material.APPLE;
            case HEARTY -> Material.GOLDEN_APPLE;
            case DURABLE -> Material.IRON_INGOT;
            case POLISHED -> Material.DIAMOND_BLOCK;
            case EXTENDED -> Material.SPYGLASS;
            case REACHING -> Material.SPYGLASS;
            case SAVAGE -> Material.FIRE_CHARGE;
            case ACCELERATED -> Material.DIAMOND_PICKAXE;
            case POWERFUL -> Material.STONE_AXE;
            case WITHERED -> Material.WITHER_ROSE;
            case FORTIFIED -> Material.NETHERITE_CHESTPLATE;
            case AGILE -> Material.LEATHER_BOOTS;
            case ANCIENT -> Material.LEATHER_CHESTPLATE;
            case PLUNDERING -> Material.GOLD_BLOCK;
            case PRISMATIC -> Material.PRISMARINE_CRYSTALS;
            case SALTY -> Material.SUGAR;
            case TEMPTING -> Material.ROTTEN_FLESH;
            case ALLURING -> Material.PORKCHOP;
            case MAGNETIC -> Material.IRON_BLOCK;
            default -> Material.BARRIER;
        };
    }

    /**
     * To be called once during manager instantiation. Used as a "helper" to create the singleton instance responsible
     * for a reforge.
     *
     * @return
     */
    public ReforgeBase createHandler() {
        try {
            return this.handler.getConstructor(this.getClass()).newInstance(this);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            SMPRPG.getInstance().getLogger().severe("Failed to instantiate handler for " + this.name() + "! Does the constructor match ReforgeBase?");
            throw new RuntimeException(e);
        }
    }
}
