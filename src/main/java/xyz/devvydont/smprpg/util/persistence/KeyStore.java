package xyz.devvydont.smprpg.util.persistence;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import org.bukkit.generator.structure.Structure;
import org.bukkit.inventory.ItemType;
import xyz.devvydont.smprpg.SMPRPG;

/**
 * Stores various common {@link org.bukkit.NamespacedKey} globals to make code less spaghetti.
 * There is no general pattern to what keys can be stored here, if it feels right to be global then put it here.
 * Keep in mind that changing key namespaces and values will break some things that used the prior key.
 */
public class KeyStore {

    /**
     * The namespace to use when generating keys. You should never change this or else you will break the plugin.
     */
    private static final String NAMESPACE = "smprpg";

    /**
     * Creates a new namespaced key using the given value. This key will ensure that data accessed/stored will be
     * consistent across plugin restarts, assuming the namespace (or key values) never change.
     * @param value The value of the key.
     * @return A {@link NamespacedKey} instance.
     */
    private static NamespacedKey key(String value) {
        return new NamespacedKey(NAMESPACE, value);
    }

    /**
     * Used for storing a "fishing tracker" PDC. Tracks the amount of times certain things were caught.
     */
    public static final NamespacedKey FISHING_GALLERY = key("fishing_gallery");

    /**
     * Used for storing armor sets and quick swapping between them.
     */
    public static final NamespacedKey PLAYER_WARDROBE = key("wardrobe");

    /**
     * Used for tracking which wardrobe upgrade slots a player has unlocked.
     */
    public static final NamespacedKey WARDROBE_UPGRADES = key("wardrobe_upgrades");

    /**
     * Used for storing a player's personal HUD/gameplay preferences.
     */
    public static final NamespacedKey PLAYER_SETTINGS = key("player_settings");

    /**
     * Used for item rarity adjustments in item blueprints.
     */
    public static final NamespacedKey ITEM_RARITY_OVERRIDE = key("item_rarity_override");

    /**
     * Used for nerfing fishing attributes when dual wielding fishing rods.
     */
    public static final NamespacedKey FISHING_ATTRIBUTE_DUAL_WIELD_NERF = key("rod_dual_wield_nerf");

    /**
     * Used to grant bonus fishing speed while a player is standing in the rain.
     */
    public static final NamespacedKey FISHING_RAIN_BONUS = key("rain_bonus");

    /**
     * Used to grant bonus fishing speed while a player is standing in a thunderstorm.
     */
    public static final NamespacedKey FISHING_THUNDERSTORM_BONUS = key("thunderstorm_bonus");

    /**
     * Used on slayer spawn entity PDCs to flag them for slayer quests
     */
    public static final NamespacedKey SLAYER_SPAWN_TYPE = key("slayer_spawn_type");

    /**
     * Used on a name tag item to store the name a player has typed in (the pending name to apply), and on an
     * entity to store the name a player has assigned to it with that name tag.
     */
    public static final NamespacedKey ASSIGNED_NAME = key("assigned_name");

    // Easy access use to RegistryAccess, for code readability
    private static final RegistryAccess access = RegistryAccess.registryAccess();

    // Tag Keys
    public static final TagKey<ItemType> ENCHANTABLE_TOME = TagKey.create(RegistryKey.ITEM, Key.key(NAMESPACE, "enchantable/tome"));
    public static final TagKey<ItemType> ENCHANTABLE_APTITUDE = TagKey.create(RegistryKey.ITEM, Key.key(NAMESPACE, "enchantable/aptitude"));

    // Dim Keys
    public static final NamespacedKey DIM_OVERWORLD = new NamespacedKey("minecraft", "overworld");
    public static final NamespacedKey DIM_NETHER = new NamespacedKey("minecraft", "the_nether");
    public static final NamespacedKey DIM_AETHER = new NamespacedKey(NAMESPACE, "the_aether");
    public static final NamespacedKey DIM_END = new NamespacedKey("minecraft", "the_end");

    // Structure Keys
    public static final Structure CASTLE_DWELLING = access.getRegistry(RegistryKey.STRUCTURE).get(Key.key(NAMESPACE, "castle_dwelling"));

    // Sound Keys
    public static final NamespacedKey AUDIO_BREADBOARD_EAT = new NamespacedKey(NAMESPACE, "food.breadboard.eat");

    public static final NamespacedKey AUDIO_RARE_DROP = new NamespacedKey(NAMESPACE, "events.drops.rare");
    public static final NamespacedKey AUDIO_EPIC_DROP = new NamespacedKey(NAMESPACE, "events.drops.epic");
    public static final NamespacedKey AUDIO_LEGENDARY_DROP = new NamespacedKey(NAMESPACE, "events.drops.legendary");

    public static final NamespacedKey AUDIO_ILLAGER_WARLOCK_TOSS_CAST = new NamespacedKey(NAMESPACE, "entity.illager_warlock.cast_toss");
    public static final NamespacedKey AUDIO_ILLAGER_WARLOCK_TOSS_CAST_VO = new NamespacedKey(NAMESPACE, "entity.illager_warlock.cast_toss_vo");
    public static final NamespacedKey AUDIO_ILLAGER_WARLOCK_FIREBALL_CAST = new NamespacedKey(NAMESPACE, "entity.illager_warlock.cast_fireball");
    public static final NamespacedKey AUDIO_ILLAGER_WARLOCK_TELEPORT_CAST = new NamespacedKey(NAMESPACE, "entity.illager_warlock.cast_teleport");
    public static final NamespacedKey AUDIO_ILLAGER_WARLOCK_VEX_CAST = new NamespacedKey(NAMESPACE, "entity.illager_warlock.cast_vex");
    public static final NamespacedKey AUDIO_ILLAGER_WARLOCK_FANGS_CAST = new NamespacedKey(NAMESPACE, "entity.illager_warlock.cast_fangs");

    public static final NamespacedKey AUDIO_ZEPHYR_CALL = new NamespacedKey(NAMESPACE, "entity.zephyr.call");
    public static final NamespacedKey AUDIO_ZEPHYR_SHOOT = new NamespacedKey(NAMESPACE, "entity.zephyr.shoot");
    public static final NamespacedKey AUDIO_ZEPHYR_HURT = new NamespacedKey(NAMESPACE, "entity.zephyr.hurt");
    public static final NamespacedKey AUDIO_ZEPHYR_DEATH = new NamespacedKey(NAMESPACE, "entity.zephyr.death");

    public static final NamespacedKey AUDIO_BLUE_AERCOULD_BOUNCE = new NamespacedKey(NAMESPACE, "block.blue_aercloud.bounce");
    public static final NamespacedKey AUDIO_AETHER_ADVANCEMENT_GENERAL = new NamespacedKey(NAMESPACE, "events.advancement.aether");

    public static final NamespacedKey AUDIO_CUTTING_BOARD_CUT = new NamespacedKey(NAMESPACE, "block.cutting_board.cut");

    public static final NamespacedKey AUDIO_NOTE_SAWTOOTH = new NamespacedKey(NAMESPACE, "block.note_block.sawtooth");
    public static final NamespacedKey AUDIO_NOTE_SNES_PIANO = new NamespacedKey(NAMESPACE, "block.note_block.snes_piano");
    public static final NamespacedKey AUDIO_NOTE_STEEL_DRUM = new NamespacedKey(NAMESPACE, "block.note_block.steel_drum");
    public static final NamespacedKey AUDIO_NOTE_ORCHESTRA_HIT = new NamespacedKey(NAMESPACE, "block.note_block.orchestra_hit");
    public static final NamespacedKey AUDIO_NOTE_DOO_HIGH = new NamespacedKey(NAMESPACE, "block.note_block.doo_high");
    public static final NamespacedKey AUDIO_NOTE_DOO_LOW = new NamespacedKey(NAMESPACE, "block.note_block.doo_low");
    public static final NamespacedKey AUDIO_NOTE_DOO_BASS = new NamespacedKey(NAMESPACE, "block.note_block.doo_bass");
    public static final NamespacedKey AUDIO_NOTE_PAH = new NamespacedKey(NAMESPACE, "block.note_block.pah");
    public static final NamespacedKey AUDIO_NOTE_DRUM = new NamespacedKey(NAMESPACE, "block.note_block.drum");
    public static final NamespacedKey AUDIO_NOTE_OCARINA = new NamespacedKey(NAMESPACE, "block.note_block.ocarina");

}
