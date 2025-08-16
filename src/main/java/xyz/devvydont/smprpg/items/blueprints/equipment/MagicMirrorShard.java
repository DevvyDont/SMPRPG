package xyz.devvydont.smprpg.items.blueprints.equipment;

import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.items.interfaces.ICraftable;
import xyz.devvydont.smprpg.items.interfaces.ISellable;
import xyz.devvydont.smprpg.services.ItemService;

import java.util.Collection;
import java.util.List;

public class MagicMirrorShard extends CustomItemBlueprint implements ISellable, ICraftable, Listener {

    public MagicMirrorShard(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public void updateItemData(ItemStack itemStack) {
        super.updateItemData(itemStack);
        itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
    }

    /**
     * Determine what type of item this is.
     */
    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.ITEM;
    }

    @Override
    public NamespacedKey getRecipeKey() {
        return new NamespacedKey(SMPRPG.getPlugin(), this.getCustomItemType().getKey() + "_recipe");
    }

    public MagicMirror.MagicMirrorMode getMode() {
        return switch (this.getCustomItemType()) {
            case SLUMBER_SHARD -> MagicMirror.MagicMirrorMode.PLAYER_SPAWN;
            case CINDER_SHARD -> MagicMirror.MagicMirrorMode.NETHER_SPAWN;
            case VOID_SHARD -> MagicMirror.MagicMirrorMode.END_SPAWN;
            default -> throw new IllegalStateException("Unexpected value: " + this.getCustomItemType());
        };
    }


    public CustomItemType getMaterial() {
        return switch (this.getCustomItemType()) {
            case SLUMBER_SHARD -> CustomItemType.PLUTOS_ARTIFACT;
            case CINDER_SHARD -> CustomItemType.INFERNO_REMNANT;
            case VOID_SHARD -> CustomItemType.DRACONIC_CRYSTAL;
            default -> CustomItemType.ENCHANTED_DIAMOND;
        };
    }

    @Override
    public CraftingRecipe getCustomRecipe() {
        var recipe = new ShapedRecipe(this.getRecipeKey(), generate());
        recipe.shape(" g ", "gcg", " g ");
        recipe.setIngredient('g', ItemService.generate(CustomItemType.WARP_CATALYST));
        recipe.setIngredient('c', ItemService.generate(getMaterial()));
        recipe.setCategory(CraftingBookCategory.MISC);
        return recipe;
    }

    /**
     * A collection of items that will unlock the recipe for this item. Typically, will be one of the components
     * of the recipe itself, but can be set to whatever is desired
     *
     * @return
     */
    @Override
    public Collection<ItemStack> unlockedBy() {
        return List.of(ItemService.generate(CustomItemType.MAGIC_MIRROR));
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
        return 20_000;
    }

    /**
     * Listen for when we combine this item with a mirror. We need to apply the mode.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCombineInAnvil(PrepareAnvilEvent event) {

        var first = event.getInventory().getFirstItem();
        var second = event.getInventory().getSecondItem();

        // We only care if there are two items involved.
        if (first == null || second == null)
            return;

        if (first.getType().equals(Material.AIR) || second.getType().equals(Material.AIR))
            return;

        if (!isItemOfType(second))
            return;

        // We only care if we have a mirror and shard.
        var firstBlueprint = ItemService.blueprint(first);
        var secondBlueprint = ItemService.blueprint(second);
        if (!(firstBlueprint instanceof MagicMirror mirror))
            return;

        if (!(secondBlueprint instanceof MagicMirrorShard mirrorShard))
            return;

        // We only care if the mirror already doesn't have the mode.
        var mode = mirrorShard.getMode();
        if (mirror.hasModeUnlocked(first, mode))
            return;

        // Awesome! Continue.
        var result = first.clone();
        mirror.withModeUnlocked(result, mode);
        event.setResult(result);
        event.getView().setRepairCost(30);
    }
}
