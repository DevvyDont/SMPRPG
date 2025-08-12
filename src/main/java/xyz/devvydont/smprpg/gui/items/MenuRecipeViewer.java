package xyz.devvydont.smprpg.gui.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.block.Campfire;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.gui.InterfaceUtil;
import xyz.devvydont.smprpg.gui.base.MenuBase;
import xyz.devvydont.smprpg.items.base.SMPItemBlueprint;
import xyz.devvydont.smprpg.items.interfaces.ICraftable;
import xyz.devvydont.smprpg.items.interfaces.ISmeltable;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.services.RecipeService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;
import xyz.devvydont.smprpg.util.time.TickTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MenuRecipeViewer extends MenuBase {

    public static final int ROWS = 6;

    // Hard set positions
    public static final int CORNER = 10;
    public static final int RESULT = 23;
    public static final int REQUIREMENTS = 25;

    // The index of the recipe we want to show if there is more than one recipe.
    private int currentRecipe = 0;

    private final List<Recipe> recipes;
    private final ItemStack result;

    // Integer that will allow us to continuously flip through different options a recipe provides.
    private int recipeChoiceIndex = 0;

    /**
     * Default constructor initialized from within the MenuItemBrowser typically when a craftable item was clicked.
     *
     * @param player The player viewing the recipe.
     * @param parentMenu The menu calling this menu. Typically, the MenuItemBrowser.
     * @param result The result from crafting.
     */
    public MenuRecipeViewer(@NotNull Player player, MenuBase parentMenu, List<Recipe> recipes, ItemStack result) {
        super(player, ROWS, parentMenu);
        this.recipes = recipes;
        this.result = ItemService.clean(result);  // This may seem silly, but it's necessary to remove the "craftable" lore.
    }

    @Override
    protected void handleInventoryOpened(InventoryOpenEvent event) {
        super.handleInventoryOpened(event);
        event.titleOverride(ComponentUtils.merge(ComponentUtils.create("Recipes for: "), result.displayName()));
        this.render();
        Bukkit.getScheduler().runTaskTimer(SMPRPG.getInstance(), task -> {

            // If nobody is viewing us, we can stop the task.
            if (this.inventory.getViewers().isEmpty()) {
                task.cancel();
                return;
            }

            render();
            recipeChoiceIndex++;
        }, TickTime.HALF_SECOND, TickTime.HALF_SECOND);
    }

    @Override
    protected void handleInventoryClicked(InventoryClickEvent event) {
        super.handleInventoryClicked(event);
        event.setCancelled(true);
        this.playInvalidAnimation();
    }

    private ItemStack getItemFromRecipeChoice(RecipeChoice choice) {

        // Yes, this is possible... LMAO
        if (choice == null)
            return ItemStack.of(Material.AIR);

        ItemStack item = null;
        if (choice instanceof RecipeChoice.ExactChoice exact)
            item = ItemService.clean(exact.getChoices().get(recipeChoiceIndex % exact.getChoices().size()));

        if (choice instanceof RecipeChoice.MaterialChoice material)
            item = ItemService.generate(material.getChoices().get(recipeChoiceIndex % material.getChoices().size()));

        // This will only occur if Spigot/Paper ever add a new child of RecipeChoice in a future update.
        if (item == null) {
            SMPRPG.broadcastToOperatorsCausedBy(this.player, ComponentUtils.create("Unknown recipe choice candidate: " + choice.getClass().getSimpleName()));
            throw new IllegalStateException("Unknown choice type: " + choice.getClass().getSimpleName());
        }

        ItemService.blueprint(item).updateItemData(item);

        // If this item is deemed craftable by us, inject lore explaining that they can click it to go deeper.
        if (!RecipeService.getRecipesFor(item).isEmpty()) {
            var lore = new ArrayList<Component>(List.of(
                    ComponentUtils.EMPTY,
                    ComponentUtils.create("Click to view recipe!", NamedTextColor.YELLOW),
                    ComponentUtils.EMPTY
            ));
            if (item.lore() != null)
                lore.addAll(item.lore());
            item.lore(lore);
        }

        return item;
    }

    /**
     * When an ingredient is clicked, if the blueprint of that ingredient is craftable itself as well then open
     * another layer of this menu with its recipe.
     *
     * @param itemStack The ItemStack clicked as a crafting component
     */
    private void handleIngredientClick(ItemStack itemStack) {

        // Retrieve the blueprint of this item. If it is craftable, enter another recipe layer
        var recipesFor = RecipeService.getRecipesFor(ItemService.blueprint(itemStack).generate());
        if (recipesFor.isEmpty() || itemStack.getType().equals(Material.AIR)) {
            this.playInvalidAnimation();
            return;
        }

        this.openSubMenu(new MenuRecipeViewer(player, this, recipesFor, itemStack));
    }

    /**
     * Renders the recipe completely
     */
    public void renderRecipe() {

        if (recipes.isEmpty()) {
            SMPRPG.broadcastToOperatorsCausedBy(this.player, ComponentUtils.create("Achieved impossible recipe viewer state (no recipes to show): ", NamedTextColor.RED).append(result.displayName()));
            return;
        }

        if (currentRecipe >= recipes.size())
            currentRecipe = 0;
        if (currentRecipe < 0)
            currentRecipe = recipes.size() - 1;

        var recipe = recipes.get(currentRecipe);
        switch (recipe) {
            case CookingRecipe<?> cooking -> renderCookingRecipe(cooking);
            case ShapelessRecipe shapeless -> renderShapelessRecipe(shapeless);
            case ShapedRecipe shaped -> renderShapedRecipe(shaped);
            case TransmuteRecipe transmute -> renderTransmuteRecipe(transmute);
            case SmithingRecipe smithing -> renderSmithingTransformRecipe(smithing);
            case StonecuttingRecipe stonecutting -> renderStonecuttingRecipe(stonecutting);
            case ComplexRecipe complex -> renderComplexRecipe(complex);  // Not really necessary, this is more of a quirk if anything...
            default ->
                    SMPRPG.broadcastToOperatorsCausedBy(this.player, ComponentUtils.create("Unknown recipe handler: " + recipe.getClass().getSimpleName()));
        }
    }

    private void renderComplexRecipe(ComplexRecipe complex) {

        setSlot(CORNER + 10, InterfaceUtil.getNamedItemWithDescription(
                Material.BARRIER,
                ComponentUtils.create("Complex Recipe", NamedTextColor.RED),
                ComponentUtils.EMPTY,
                ComponentUtils.create("This recipe is considered 'complex',"),
                ComponentUtils.create("which means that we can't"),
                ComponentUtils.create("determine the process to craft the"),
                ComponentUtils.create("result. There's not much we can provide"),
                ComponentUtils.create("you in this interface about this recipe!"),
                ComponentUtils.EMPTY,
                ComponentUtils.merge(ComponentUtils.create("Recipe Key: "), ComponentUtils.create(complex.getKey().asString(), NamedTextColor.RED))
        ));

    }

    private void renderStonecuttingRecipe(StonecuttingRecipe stonecutting) {
        var input = getItemFromRecipeChoice(stonecutting.getInputChoice());
        setButton(CORNER + 10, input, event -> handleIngredientClick(input));
        setSlot(CORNER + 18 + 1, InterfaceUtil.getNamedItem(Material.STONECUTTER, ComponentUtils.create("Stonecutter Recipe", NamedTextColor.GOLD)));
    }

    private void renderSmithingTransformRecipe(SmithingRecipe smithing) {

        if (smithing instanceof SmithingTransformRecipe transform) {
            var input = getItemFromRecipeChoice(transform.getTemplate());
            if (input.getType().equals(Material.AIR))
                input = InterfaceUtil.getNamedItem(Material.BARRIER, ComponentUtils.create("No template needed!", NamedTextColor.GREEN));
            ItemStack finalInput = input;
            setButton(CORNER + 9, input, event -> handleIngredientClick(finalInput));
        }

        var base = getItemFromRecipeChoice(smithing.getBase());
        var addition = getItemFromRecipeChoice(smithing.getAddition());

        if (addition.getType().equals(Material.AIR))
            addition = InterfaceUtil.getNamedItem(Material.BARRIER, ComponentUtils.create("No additional item needed!", NamedTextColor.GREEN));

        if (base.getType().equals(Material.AIR))
            base = InterfaceUtil.getNamedItem(Material.BARRIER, ComponentUtils.create("No base item needed!", NamedTextColor.GREEN));

        ItemStack finalBase = base;
        ItemStack finalAddition = addition;
        setButton(CORNER + 10, base, event -> handleIngredientClick(finalBase));
        setButton(CORNER + 11, addition, event -> handleIngredientClick(finalAddition));

        setSlot(CORNER + 19, InterfaceUtil.getNamedItem(Material.SMITHING_TABLE, ComponentUtils.create("Smithing Recipe", NamedTextColor.GOLD)));
    }

    private void renderShapelessRecipe(ShapelessRecipe shapeless) {
        int x = 0;
        int y = 0;

        // Loop through all the choices. These are essentially just the ingredients.
        for (var choice : shapeless.getChoiceList()) {

            var item = getItemFromRecipeChoice(choice);
            this.setButton(y*9+x+CORNER, item, event -> handleIngredientClick(item));

            x += 1;
            // If we are out of bounds, go to the next row.
            if (x >= 3) {
                x = 0;
                y += 1;
            }

        }
        this.setSlot(CORNER + 27 + 1, InterfaceUtil.getNamedItem(Material.CRAFTING_TABLE, ComponentUtils.create("Shapeless Crafting Recipe", NamedTextColor.GOLD)));
    }

    private void renderShapedRecipe(ShapedRecipe shaped) {
        int x = 0;
        int y = 0;
        for (String row : shaped.getShape()) {
            for (char ingredient : row.toCharArray()) {
                var choice = shaped.getChoiceMap().get(ingredient);
                var item = getItemFromRecipeChoice(choice);
                this.setButton(y*9+x+CORNER, item, event -> handleIngredientClick(item));
                x+= 1;
            }
            y += 1;
            x = 0;
        }
        this.setSlot(CORNER + 27 + 1, InterfaceUtil.getNamedItem(Material.CRAFTING_TABLE, ComponentUtils.create("Shaped Crafting Recipe", NamedTextColor.GOLD)));
    }

    /**
     * Renders a transmute recipe on the interface.
     * Transmute recipes are pretty simple, it is just two items that turn an item into another, similar to shapeless.
     * @param transmute The transmute recipe.
     */
    private void renderTransmuteRecipe(TransmuteRecipe transmute) {
        var input = getItemFromRecipeChoice(transmute.getInput());
        var transmuter = getItemFromRecipeChoice(transmute.getMaterial());
        this.setButton(CORNER + 9 + 1, input, event -> handleIngredientClick(input));
        this.setButton(CORNER + 9 + 2, transmuter, event -> handleIngredientClick(transmuter));
        this.setSlot(CORNER + 27 + 1, InterfaceUtil.getNamedItem(Material.CRAFTING_TABLE, ComponentUtils.create("Transmute Crafting Recipe", NamedTextColor.GOLD)));
    }

    private void renderCookingRecipe(CookingRecipe<?> cooking) {

        var top = CORNER + 1;
        var middle = top + 9;
        var bottom = middle + 9;

        ItemStack smelter;

        if (cooking instanceof FurnaceRecipe)
            smelter = InterfaceUtil.getNamedItem(Material.FURNACE, ComponentUtils.create("Furnace Recipe", NamedTextColor.GOLD));
        else if (cooking instanceof BlastingRecipe)
            smelter = InterfaceUtil.getNamedItem(Material.BLAST_FURNACE, ComponentUtils.create("Blasting Recipe", NamedTextColor.GOLD));
        else if (cooking instanceof SmokingRecipe)
            smelter = InterfaceUtil.getNamedItem(Material.SMOKER, ComponentUtils.create("Smoker Recipe", NamedTextColor.GOLD));
        else if (cooking instanceof CampfireRecipe)
            smelter = InterfaceUtil.getNamedItem(Material.CAMPFIRE, ComponentUtils.create("Campfire Recipe", NamedTextColor.GOLD));
        else
            smelter = InterfaceUtil.getNamedItem(Material.BARRIER, ComponentUtils.create("Unknown Smelter: " + cooking.getClass().getSimpleName(), NamedTextColor.RED));

        smelter.lore(ComponentUtils.cleanItalics(List.of(
                ComponentUtils.EMPTY,
                ComponentUtils.merge(ComponentUtils.create("Cook in a "), Component.translatable(smelter.translationKey()).color(NamedTextColor.YELLOW), ComponentUtils.create(" block")),
                ComponentUtils.merge(ComponentUtils.create("for "), ComponentUtils.create(cooking.getCookingTime() / 20 + "s", NamedTextColor.GREEN))
        )));

        var ingredient = getItemFromRecipeChoice(cooking.getInputChoice());
        this.setButton(top, ingredient, event -> handleIngredientClick(ingredient));
        this.setSlot(middle, smelter);
        this.setSlot(bottom, InterfaceUtil.getNamedItem(Material.COAL, ComponentUtils.EMPTY));
    }

    /**
     * Generates an item stack that shows what "requirements" an item has in order to craft. This is subject to change,
     * but for now it simply displays what item "discovers" the recipe upon picking up. A common example is how the
     * recipe for a copper chestplate is discovered when we pick up a copper ingot.
     *
     * @param requirements A collection of item stacks that is "required" to discover this recipe.
     * @return an item stack to be used as a display in the interface
     */
    public ItemStack getRequirements(Collection<ItemStack> requirements) {

        ItemStack paper = createNamedItem(Material.PAPER, ComponentUtils.create("Crafting Requirements", NamedTextColor.RED));
        paper.editMeta(meta -> {
            List<Component> lore = new ArrayList<>();
            lore.add(ComponentUtils.EMPTY);
            lore.add(ComponentUtils.create("The following items must be"));
            lore.add(ComponentUtils.create("discovered to unlock this recipe"));
            lore.add(ComponentUtils.EMPTY);
            for (ItemStack item : requirements)
                lore.add(ComponentUtils.create("- ").append(item.displayName()));
            meta.lore(ComponentUtils.cleanItalics(lore));
            meta.setEnchantmentGlintOverride(true);
        });
        SMPRPG.getService(ItemService.class).setIgnoreMetaUpdate(paper);
        return paper;
    }

    private void changePage(int delta) {
        currentRecipe += delta;

        if (delta < 0)
            this.sounds.playPagePrevious();
        else
            this.sounds.playPageNext();

        render();
    }

    public void render() {

        this.clear();
        this.setBorderFull();

        this.renderRecipe();

        // Misc buttons
        this.setSlot(RESULT, result);

        var blueprint = ItemService.blueprint(result);
        if (blueprint instanceof ICraftable craftable)
            this.setSlot(REQUIREMENTS, getRequirements(craftable.unlockedBy()));

        if (recipes.size() > 1) {
            this.setButton(37, InterfaceUtil.getNamedItem(Material.ARROW, ComponentUtils.create("Previous Page " + (currentRecipe+1) + "/" + recipes.size(), NamedTextColor.GOLD)), event -> changePage(-1));
            this.setButton(39, InterfaceUtil.getNamedItem(Material.ARROW, ComponentUtils.create("Next Page " + (currentRecipe+1) + "/" + recipes.size(), NamedTextColor.GOLD)), event -> changePage(1));
        }

        if (this.player.isOp()) {
            String key = "?";
            if (recipes.get(currentRecipe) instanceof Keyed keyed)
                key = keyed.getKey().asString();
            this.setSlot(0, InterfaceUtil.getNamedItemWithDescription(
                    Material.OAK_SIGN,
                    ComponentUtils.create("Info", NamedTextColor.RED),
                    ComponentUtils.create("Key: " + key, NamedTextColor.YELLOW)
            ));
        }

        // Utility buttons
        this.setButton((ROWS-1)*9+4, BUTTON_BACK, (e) -> this.openParentMenu());
    }
}
