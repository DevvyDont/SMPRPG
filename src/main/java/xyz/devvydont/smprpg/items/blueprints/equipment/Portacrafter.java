package xyz.devvydont.smprpg.items.blueprints.equipment;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.items.interfaces.ICraftable;
import xyz.devvydont.smprpg.items.interfaces.ICustomTextured;
import xyz.devvydont.smprpg.items.interfaces.IHeaderDescribable;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;
import xyz.devvydont.smprpg.util.items.AbilityUtil;

import java.util.Collection;
import java.util.List;

public class Portacrafter extends CustomItemBlueprint implements IHeaderDescribable, Listener, ICraftable, ICustomTextured {

    public Portacrafter(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public List<Component> getHeader(ItemStack itemStack) {
        return List.of(
                ComponentUtils.merge(AbilityUtil.getAbilityComponent("Craft!"), ComponentUtils.create(" (Right Click)", NamedTextColor.DARK_GRAY)),
                ComponentUtils.create("Crafting on the go!"),
                ComponentUtils.create("Right click while holding", NamedTextColor.GRAY),
                ComponentUtils.create("to open the crafting menu", NamedTextColor.GRAY)
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
    public NamespacedKey getRecipeKey() {
        return new NamespacedKey(SMPRPG.getPlugin(), this.getCustomItemType().getKey() + "_recipe");
    }

    @Override
    public CraftingRecipe getCustomRecipe() {
        var recipe = new ShapedRecipe(this.getRecipeKey(), generate());
        recipe.shape("ltl", " l ");
        recipe.setIngredient('l', ItemService.generate(CustomItemType.PREMIUM_LEATHER));
        recipe.setIngredient('t', ItemService.generate(Material.CRAFTING_TABLE));
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
        return List.of(
                ItemService.generate(Material.CRAFTING_TABLE)
        );
    }

    /**
     * Retrieve the URL to use for the custom head texture of this item.
     * The link that is set here should follow the following format:
     * Let's say you have the following link to a skin;
     * <a href="https://textures.minecraft.net/texture/18813764b2abc94ec3c3bc67b9147c21be850cdf996679703157f4555997ea63a">...</a>
     * You should only use the very last component of the URL, as the backend will fill in the rest.
     * Meaning we would end up using: "18813764b2abc94ec3c3bc67b9147c21be850cdf996679703157f4555997ea63a"
     *
     * @return The URL to the skin.
     */
    @Override
    public String getTextureUrl() {
        return "2cdc0feb7001e2c10fd5066e501b87e3d64793092b85a50c856d962f8be92c78";
    }

    @EventHandler
    public void __onInteract(PlayerInteractEvent event) {

        if (!event.getAction().isRightClick())
            return;

        var item = event.getItem();
        if (!isItemOfType(item))
            return;

        event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_WORK_TOOLSMITH, .5f, 1.5f);
        MenuType.CRAFTING.builder()
                .location(event.getPlayer().getLocation())
                .checkReachable(false)
                .title(Component.text("Port-a-crafter"))
                .build(event.getPlayer())
                .open();
    }
}
