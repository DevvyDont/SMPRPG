package xyz.devvydont.smprpg.items.blueprints.equipment;

import com.destroystokyo.paper.ParticleBuilder;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import io.papermc.paper.registry.keys.SoundEventKeys;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.bukkit.persistence.PersistentDataType;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.items.interfaces.IConsumable;
import xyz.devvydont.smprpg.items.interfaces.ICraftable;
import xyz.devvydont.smprpg.items.interfaces.IHeaderDescribable;
import xyz.devvydont.smprpg.items.interfaces.ISellable;
import xyz.devvydont.smprpg.services.EntityService;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;
import xyz.devvydont.smprpg.util.formatting.MinecraftStringUtils;
import xyz.devvydont.smprpg.util.formatting.Symbols;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MagicMirror extends CustomItemBlueprint implements IConsumable, Listener, ICraftable, ISellable, IHeaderDescribable {

    public enum MagicMirrorMode {
        PLAYER_SPAWN("Your spawn", NamedTextColor.AQUA),
        OVERWORLD_SPAWN("Overworld spawn", NamedTextColor.DARK_GREEN),
        NETHER_SPAWN("Nether spawn", NamedTextColor.RED),
        END_SPAWN("End spawn", NamedTextColor.LIGHT_PURPLE);

        private final NamespacedKey key = new NamespacedKey(SMPRPG.getPlugin(), this.name().toLowerCase());
        private final String locationName;
        private final NamedTextColor color;

        MagicMirrorMode(String locationName, NamedTextColor color) {
            this.locationName = locationName;
            this.color = color;
        }

        public NamespacedKey getKey() {
            return key;
        }

        public String getLocationName() {
            return locationName;
        }

        public NamedTextColor getColor() {
            return color;
        }

        public String getDisplay() {
            return MinecraftStringUtils.getTitledString(name());
        }
    }

    private final NamespacedKey mode = new NamespacedKey("smprpg", "current_mode");

    public MagicMirror(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public List<Component> getHeader(ItemStack itemStack) {
        var lore = new ArrayList<Component>();
        lore.add(ComponentUtils.merge(ComponentUtils.create("At the expense of all of")));
        lore.add(ComponentUtils.merge(ComponentUtils.create("your "), ComponentUtils.create(Symbols.MANA + "Mana", NamedTextColor.AQUA), ComponentUtils.create(", "), ComponentUtils.create("teleport to ")));
        lore.add(ComponentUtils.merge(ComponentUtils.create(getMode(itemStack).getLocationName(), getMode(itemStack).getColor()), ComponentUtils.create(" after using for "), ComponentUtils.create("7s", NamedTextColor.GREEN)));
        lore.add(ComponentUtils.EMPTY);
        lore.add(ComponentUtils.merge(ComponentUtils.create("Left click to switch modes!")));
        lore.add(ComponentUtils.merge(ComponentUtils.create("Current Mode: ", NamedTextColor.GRAY), ComponentUtils.create(getMode(itemStack).getLocationName(), getMode(itemStack).getColor())));
        lore.add(ComponentUtils.EMPTY);
        lore.add(ComponentUtils.create("Available modes: ", NamedTextColor.DARK_GRAY));
        for (var mode : getUnlockedModes(itemStack))
            lore.add(ComponentUtils.create("* " + mode.getLocationName(), NamedTextColor.DARK_GRAY));
        return lore;
    }

    /**
     * Determine what type of item this is.
     */
    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.EQUIPMENT;
    }

    @Override
    public Consumable getConsumableComponent(ItemStack item) {
        return Consumable.consumable()
                .consumeSeconds(7)
                .animation(ItemUseAnimation.BLOCK)
                .hasConsumeParticles(false)
                .sound(SoundEventKeys.BLOCK_AMETHYST_BLOCK_CHIME)
                .build();
    }

    @Override
    public NamespacedKey getRecipeKey() {
        return new NamespacedKey(SMPRPG.getPlugin(), this.getCustomItemType().getKey() + "_recipe");
    }

    @Override
    public CraftingRecipe getCustomRecipe() {
        var recipe = new ShapedRecipe(getRecipeKey(), generate());
        recipe.shape("pip", "ici", "pip");
        recipe.setIngredient('p', ItemService.generate(CustomItemType.ENCHANTED_AMETHYST_BLOCK));
        recipe.setIngredient('i', ItemService.generate(CustomItemType.ENCHANTED_IRON_BLOCK));
        recipe.setIngredient('c', ItemService.generate(CustomItemType.WARP_CATALYST));
        recipe.setCategory(CraftingBookCategory.MISC);
        return recipe;
    }

    @Override
    public void updateItemData(ItemStack itemStack) {
        super.updateItemData(itemStack);
        if (!hasModeUnlocked(itemStack, MagicMirrorMode.OVERWORLD_SPAWN))
            withModeUnlocked(itemStack, MagicMirrorMode.OVERWORLD_SPAWN);
    }

    /**
     * A collection of items that will unlock the recipe for this item. Typically, will be one of the components
     * of the recipe itself, but can be set to whatever is desired
     *
     * @return
     */
    @Override
    public Collection<ItemStack> unlockedBy() {
        return List.of(ItemService.generate(Material.ENDER_PEARL));
    }

    /**
     * Returns the same item back, but with the necessary flag applied to allow teleportation to a certain location.*
     * @param itemStack The item to modify.
     * @param mode      The mode to allow access for.
     */
    public void withModeUnlocked(ItemStack itemStack, MagicMirrorMode mode) {
        itemStack.editPersistentDataContainer(pdc -> pdc.set(mode.getKey(), PersistentDataType.BOOLEAN, true));
        updateItemData(itemStack);
    }

    /**
     * Checks if the given item has the proper flags set to allow teleportation using a certain mode.
     * @param itemStack The item to check.
     * @param mode The mode to check.
     * @return True if the mode is allowed.
     */
    public boolean hasModeUnlocked(ItemStack itemStack, MagicMirrorMode mode) {
        return itemStack.getPersistentDataContainer().getOrDefault(mode.key, PersistentDataType.BOOLEAN, false);
    }

    /**
     * Gets the unlocked modes this item has.
     * @param itemStack The item to query.
     * @return The list of modes that it has unlocked.
     */
    public List<MagicMirrorMode> getUnlockedModes(ItemStack itemStack) {
        var modes = new ArrayList<MagicMirrorMode>();
        for (var mode : MagicMirrorMode.values())
            if (hasModeUnlocked(itemStack, mode))
                modes.add(mode);
        return modes;
    }

    /**
     * Simply cycles through the mode to use. Will go to the next available one. If nothing is available, nothing will
     * change.
     * @param itemStack The item to use.
     * @return The mode that was changed to.
     */
    public MagicMirrorMode toggleMode(ItemStack itemStack) {
        var unlockedModes = getUnlockedModes(itemStack);
        var indexOfCurrent = unlockedModes.indexOf(getMode(itemStack));
        var indexOfNew = indexOfCurrent + 1;
        if (indexOfNew >= unlockedModes.size())
            indexOfNew = 0;
        var newMode = unlockedModes.get(indexOfNew);
        this.setMode(itemStack, newMode);
        return newMode;
    }

    /**
     * Sets the mode of this magic mirror.
     * @param itemStack The magic mirror item.
     * @param mode The mode to set to.
     */
    private void setMode(ItemStack itemStack, MagicMirrorMode mode) {
        itemStack.editPersistentDataContainer(pdc -> pdc.set(this.mode, PersistentDataType.STRING, mode.name().toLowerCase()));
        updateItemData(itemStack);
    }

    /**
     * Gets the current mode this mirror is set to. Defaults to {@link MagicMirrorMode#OVERWORLD_SPAWN} if something
     * goes wrong.
     * @param itemStack The item to check.
     * @return The mode the item is set to.
     */
    public MagicMirrorMode getMode(ItemStack itemStack) {
        var stringVal = itemStack.getPersistentDataContainer().get(mode, PersistentDataType.STRING);

        // Try to query the value if it was present.
        if (stringVal != null)
            try {
                return MagicMirrorMode.valueOf(stringVal.toUpperCase());
            } catch (IllegalArgumentException ignored) {}

        return MagicMirrorMode.OVERWORLD_SPAWN;
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
        return item.getAmount() * 150_000;
    }

    /**
     * Gets the desired location to teleport to for a player give a mode.
     * @param player The player.
     * @param mode The mode.
     * @return The location to teleport to.
     */
    private Location getLocation(Player player, MagicMirrorMode mode) {

        var overworld = Bukkit.getWorlds().getFirst();
        World nether = null;
        World end = null;
        for (var world : Bukkit.getWorlds()) {
            if (world.getEnvironment().equals(World.Environment.NETHER))
                nether = world;
            if (world.getEnvironment().equals(World.Environment.THE_END))
                end = world;
        }

        if (nether == null)
            nether = overworld;
        if (end == null)
            end = overworld;

        var respawn = player.getRespawnLocation();
        if (respawn == null)
            respawn = overworld.getSpawnLocation();

        return switch (mode) {
            case PLAYER_SPAWN -> respawn;
            case NETHER_SPAWN -> nether.getSpawnLocation();
            case END_SPAWN -> end.getSpawnLocation();
            default -> overworld.getSpawnLocation();
        };
    }

    /**
     * Prevents interactions when the user is not at full mana.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {

        if (event.getItem() == null || event.getItem().getType().equals(Material.AIR))
            return;

        var blueprint = ItemService.blueprint(event.getItem());
        if (!(blueprint instanceof MagicMirror))
            return;

        // If this was a right click, simply toggle the mode.
        if (event.getAction().isLeftClick()) {
            var oldMode = getMode(event.getItem());
            var mode = toggleMode(event.getItem());
            if (oldMode.equals(mode)) {
                event.getPlayer().sendMessage(ComponentUtils.error("You don't have any other destinations! Try collecting some mirror shards and combining it with your mirror in an anvil!"));
                event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.BLOCK_GLASS_BREAK, .5f, 1.5f);
                return;
            }
            event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, .5f, 1.5f);
            event.getPlayer().sendMessage(ComponentUtils.success("Set to the " + mode.getDisplay() + " mode!"));
            return;
        }

        if (!event.getAction().isRightClick())
            return;

        var player = SMPRPG.getService(EntityService.class).getPlayerInstance(event.getPlayer());
        if (player.getMana() < player.getMaxMana()-1) {
            event.getPlayer().sendMessage(ComponentUtils.error("You are not at full mana!"));
            event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.ENTITY_SHULKER_TELEPORT, 1, .5f);
            event.setCancelled(true);
            return;
        }

        new ParticleBuilder(Particle.END_ROD)
                .location(event.getPlayer().getLocation().add(0, 1, 0))
                .offset(.75, .1, .75)
                .count(50)
                .extra(0)
                .spawn();
    }

    /**
     * Teleports the player to spawn when they are at full mana.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onConsume(PlayerItemConsumeEvent event) {

        var blueprint = ItemService.blueprint(event.getItem());
        if (!(blueprint instanceof MagicMirror mirror))
            return;

        event.setCancelled(true);

        if (Bukkit.getWorlds().isEmpty())
            return;

        var player = SMPRPG.getService(EntityService.class).getPlayerInstance(event.getPlayer());
        if (player.getMana() < player.getMaxMana()-1) {
            event.getPlayer().sendMessage(ComponentUtils.error("You are not at full mana!"));
            return;
        }

        event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.ENTITY_SHULKER_TELEPORT, 1, 2f);
        new ParticleBuilder(Particle.DRAGON_BREATH)
                .location(event.getPlayer().getLocation().add(0, 1, 0))
                .offset(.75, .1, .75)
                .count(25)
                .extra(0)
                .spawn();
        event.getPlayer().teleport(getLocation(event.getPlayer(), mirror.getMode(event.getItem())));
        event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 1, 2f);
        new ParticleBuilder(Particle.DRAGON_BREATH)
                .location(event.getPlayer().getLocation().add(0, 1, 0))
                .offset(.75, .1, .75)
                .count(25)
                .extra(0)
                .spawn();
        player = SMPRPG.getService(EntityService.class).getPlayerInstance(event.getPlayer());
        player.useMana((int) player.getMana());
    }
}
