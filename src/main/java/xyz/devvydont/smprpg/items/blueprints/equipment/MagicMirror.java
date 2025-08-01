package xyz.devvydont.smprpg.items.blueprints.equipment;

import com.destroystokyo.paper.ParticleBuilder;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import io.papermc.paper.registry.keys.SoundEventKeys;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
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
import xyz.devvydont.smprpg.util.formatting.Symbols;

import java.util.Collection;
import java.util.List;

public class MagicMirror extends CustomItemBlueprint implements IConsumable, Listener, ICraftable, ISellable, IHeaderDescribable {

    public MagicMirror(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public List<Component> getHeader(ItemStack itemStack) {
        return List.of(
                ComponentUtils.merge(ComponentUtils.create("At the expense of")),
                ComponentUtils.merge(ComponentUtils.create("all of your "), ComponentUtils.create(Symbols.MANA + "Mana", NamedTextColor.AQUA), ComponentUtils.create(",")),
                ComponentUtils.merge(ComponentUtils.create("teleport to "), ComponentUtils.create("Spawn", NamedTextColor.DARK_GREEN)),
                ComponentUtils.merge(ComponentUtils.create("after using for "), ComponentUtils.create("7s", NamedTextColor.GREEN))
        );
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
        return new NamespacedKey(SMPRPG.getInstance(), this.getCustomItemType().getKey() + "_recipe");
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

    /**
     * A collection of items that will unlock the recipe for this item. Typically will be one of the components
     * of the recipe itself, but can be set to whatever is desired
     *
     * @return
     */
    @Override
    public Collection<ItemStack> unlockedBy() {
        return List.of(ItemService.generate(Material.ENDER_PEARL));
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
     * Prevents interactions when the user is not at full mana.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {

        if (event.getItem() == null || event.getItem().getType().equals(Material.AIR))
            return;

        var blueprint = ItemService.blueprint(event.getItem());
        if (!(blueprint instanceof MagicMirror))
            return;

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
        if (!(blueprint instanceof MagicMirror))
            return;

        event.setCancelled(true);

        if (Bukkit.getWorlds().isEmpty())
            return;

        var player = SMPRPG.getService(EntityService.class).getPlayerInstance(event.getPlayer());
        if (player.getMana() < player.getMaxMana()-1) {
            event.getPlayer().sendMessage(ComponentUtils.error("You are not at full mana!"));
            return;
        }

        var overworld = Bukkit.getWorlds().getFirst();
        event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.ENTITY_SHULKER_TELEPORT, 1, 2f);
        new ParticleBuilder(Particle.DRAGON_BREATH)
                .location(event.getPlayer().getLocation().add(0, 1, 0))
                .offset(.75, .1, .75)
                .count(25)
                .extra(0)
                .spawn();
        event.getPlayer().teleport(overworld.getSpawnLocation());
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
