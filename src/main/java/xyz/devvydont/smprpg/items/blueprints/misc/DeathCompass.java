package xyz.devvydont.smprpg.items.blueprints.misc;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.LodestoneTracker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.entity.player.ProfileDifficulty;
import xyz.devvydont.smprpg.gui.misc.MenuDeathCompassConfirmDialog;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.items.interfaces.IHeaderDescribable;
import xyz.devvydont.smprpg.items.interfaces.ISellable;
import xyz.devvydont.smprpg.services.DifficultyService;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;
import xyz.devvydont.smprpg.util.formatting.EnvironmentDisplay;
import xyz.devvydont.smprpg.util.persistence.PDCAdapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The successor to the {@link DeathCertificate}. A Death Compass remembers a single death (who died, where, and when)
 * on its PDC and points its needle at that location via a {@link LodestoneTracker} component, so simply holding it
 * navigates the player back to the death site (working instantly client-side, in any dimension). A Death Certificate
 * can be converted into a Death Compass by right-clicking it and confirming a coin fee, carrying over the location,
 * dimension, deceased player, and timestamp it recorded.
 *
 * todo note: we should probably reconsider some functionality in here alongside {@link DeathCertificate}
 * todo: i wanted to maintain backwards compatibility and also players might prefer the death certificate
 * todo: as a keepsake over a compass, so this is moreso just an option for players who want to utilize it.
 */
public class DeathCompass extends CustomItemBlueprint implements Listener, ISellable, IHeaderDescribable {

    /**
     * The coin fee charged to convert a Death Certificate into a Death Compass.
     */
    public static final long CONVERSION_COST = 5_000L;

    private static final NamespacedKey BOUND_LOCATION_KEY = new NamespacedKey("smprpg", "bound_location");
    private static final NamespacedKey DECEASED_KEY = new NamespacedKey("smprpg", "deceased");
    private static final NamespacedKey TIMESTAMP_KEY = new NamespacedKey("smprpg", "timestamp");

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    public DeathCompass(@NotNull ItemService itemService, @NotNull CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public void updateItemData(@NotNull ItemStack itemStack) {
        super.updateItemData(itemStack);
        // Point the compass needle at the bound location (no lodestone block required). Untracked so it never clears
        // itself, and it updates on the client the instant the item is held.
        Location location = getBoundLocation(itemStack);
        if (location != null)
            itemStack.setData(DataComponentTypes.LODESTONE_TRACKER, LodestoneTracker.lodestoneTracker(location, false));
        else
            itemStack.unsetData(DataComponentTypes.LODESTONE_TRACKER);

        // Apply a custom name component to this item since lodestone compass overrides the item name client side
        itemStack.setData(DataComponentTypes.CUSTOM_NAME, getRarity(itemStack).applyDecoration(ComponentUtils.create("Death Compass").decoration(TextDecoration.ITALIC, false)));
    }

    /**
     * Records a death onto the compass so it can later be bound to a player's last death location.
     *
     * @param itemStack The compass to write to.
     * @param location  The death location (its world carries the dimension).
     * @param deceased  The name of the player who died.
     * @param timestamp When the death occurred, in epoch milliseconds.
     */
    public void bind(@NotNull ItemStack itemStack, @NotNull Location location, @NotNull String deceased, long timestamp) {
        itemStack.editPersistentDataContainer(pdc -> {
            pdc.set(BOUND_LOCATION_KEY, PDCAdapters.LOCATION, location);
            pdc.set(DECEASED_KEY, PersistentDataType.STRING, deceased);
            pdc.set(TIMESTAMP_KEY, PersistentDataType.LONG, timestamp);
        });
        updateItemData(itemStack);
    }

    /**
     * Retrieves the location this compass is bound to, if any.
     *
     * @param itemStack The compass to read from.
     * @return The bound location, or null if none is stored or its world is no longer loaded.
     */
    public @Nullable Location getBoundLocation(@NotNull ItemStack itemStack) {
        Location location = itemStack.getPersistentDataContainer().get(BOUND_LOCATION_KEY, PDCAdapters.LOCATION);
        if (location == null || location.getWorld() == null)
            return null;
        return location;
    }

    /**
     * @param itemStack The compass to check.
     * @return True if this compass has a usable bound location.
     */
    public boolean hasBoundLocation(@NotNull ItemStack itemStack) {
        return getBoundLocation(itemStack) != null;
    }

    private @Nullable String getDeceased(@NotNull ItemStack itemStack) {
        return itemStack.getPersistentDataContainer().get(DECEASED_KEY, PersistentDataType.STRING);
    }

    private long getTimestamp(@NotNull ItemStack itemStack) {
        return itemStack.getPersistentDataContainer().getOrDefault(TIMESTAMP_KEY, PersistentDataType.LONG, 0L);
    }

    @Override
    public @NotNull ItemClassification getItemClassification() {
        return ItemClassification.ITEM;
    }

    @Override
    public List<Component> getHeader(ItemStack itemStack) {

        Location location = getBoundLocation(itemStack);
        if (location == null)
            return List.of(
                    ComponentUtils.create("This compass isn't bound to a death yet.", NamedTextColor.DARK_GRAY)
            );

        String deceased = getDeceased(itemStack);
        Component subject = deceased == null
                ? ComponentUtils.create("Someone", NamedTextColor.AQUA)
                : ComponentUtils.create(deceased, NamedTextColor.AQUA);

        var header = new ArrayList<Component>();
        header.add(ComponentUtils.merge(
                subject,
                ComponentUtils.create(" died in "),
                EnvironmentDisplay.name(location.getWorld().getEnvironment())
        ));
        header.add(ComponentUtils.merge(
                ComponentUtils.create("Coordinates: "),
                getCoordinatesComponent(location)
        ));

        long timestamp = getTimestamp(itemStack);
        if (timestamp > 0)
            header.add(ComponentUtils.create("Death occurred at: " + DATE_FORMAT.format(new Date(timestamp)) + " EST", NamedTextColor.DARK_GRAY));

        header.add(ComponentUtils.EMPTY);
        header.add(ComponentUtils.create("Hold this compass to navigate back to this spot.", NamedTextColor.GRAY));
        return header;
    }

    private Component getCoordinatesComponent(Location location) {
        String coordinates = String.format("%d %d %d", location.getBlockX(), location.getBlockY(), location.getBlockZ());
        return ComponentUtils.create(coordinates, NamedTextColor.BLUE);
    }

    /**
     * Handles right-clicking a Death Certificate to start the conversion into a Death Compass.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {

        // Only react to a main-hand right click so we never double-fire.
        if (!event.getAction().isRightClick() || event.getHand() != EquipmentSlot.HAND)
            return;

        ItemStack item = event.getItem();
        if (item == null)
            return;

        if (ItemService.blueprint(item) instanceof DeathCertificate certificate)
            promptConversion(event, item, certificate);
    }

    /**
     * Validates that a Death Certificate is convertible and opens a confirmation dialog before charging the player.
     */
    private void promptConversion(PlayerInteractEvent event, ItemStack certificateItem, DeathCertificate certificate) {

        event.setCancelled(true);
        Player player = event.getPlayer();

        // Ascendants are denied the convenience of a guiding needle; they must navigate by raw coordinates alone.
        if (SMPRPG.getService(DifficultyService.class).getDifficulty(player) == ProfileDifficulty.HARD) {
            player.sendMessage(ComponentUtils.merge(
                    ComponentUtils.create("The path of the ", NamedTextColor.RED),
                    ComponentUtils.create(ProfileDifficulty.HARD.Display, ProfileDifficulty.HARD.Color),
                    ComponentUtils.create(" grants no guiding needle. Read the coordinates and brave the way back to your grave yourself.", NamedTextColor.RED)
            ));
            player.playSound(player, Sound.BLOCK_GLASS_BREAK, 0.5f, 1.5f);
            return;
        }

        var meta = certificateItem.getItemMeta();
        if (!certificate.hasData(meta)) {
            player.sendMessage(ComponentUtils.error("This Death Certificate doesn't record a death location."));
            player.playSound(player, Sound.BLOCK_GLASS_BREAK, 0.5f, 1.5f);
            return;
        }

        Location location = resolveCertificateLocation(certificate.getPrimitiveLocation(meta));
        if (location == null) {
            player.sendMessage(ComponentUtils.error("The world this death occurred in is no longer available."));
            player.playSound(player, Sound.BLOCK_GLASS_BREAK, 0.5f, 1.5f);
            return;
        }

        new MenuDeathCompassConfirmDialog(
                player,
                certificateItem,
                location,
                certificate.getWhoDied(meta),
                certificate.getTimestamp(meta)
        ).openMenu();
    }

    /**
     * Rebuilds a usable location from the primitive {x, y, z, environmentOrdinal} array stored by a Death Certificate.
     * The environment is resolved to the first loaded world that matches it.
     *
     * @return The resolved location, or null if no matching world is loaded.
     */
    private @Nullable Location resolveCertificateLocation(int[] primitiveLocation) {

        World.Environment environment = World.Environment.values()[primitiveLocation[3]];
        World world = null;
        for (World candidate : Bukkit.getWorlds())
            if (candidate.getEnvironment() == environment) {
                world = candidate;
                break;
            }

        if (world == null)
            return null;

        return new Location(world, primitiveLocation[0], primitiveLocation[1], primitiveLocation[2]);
    }

    /**
     * Given this item stack, how much should it be able to sell for?
     * Keep in mind that the size of the stack needs to considered as well!
     *
     * @param item The item that can be sold.
     * @return The worth of the item.
     */
    @Override
    public int getWorth(ItemStack item) {
        return item.getAmount();
    }
}
