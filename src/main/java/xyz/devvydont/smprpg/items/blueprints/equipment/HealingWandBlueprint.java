package xyz.devvydont.smprpg.items.blueprints.equipment;

import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.ability.Ability;
import xyz.devvydont.smprpg.ability.AbilityActivationMethod;
import xyz.devvydont.smprpg.ability.AbilityCost;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.items.interfaces.IAbilityCaster;
import xyz.devvydont.smprpg.items.interfaces.ICraftable;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.time.TickTime;

import java.util.Collection;
import java.util.List;

public class HealingWandBlueprint extends CustomItemBlueprint implements IAbilityCaster, ICraftable {

    public HealingWandBlueprint(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    protected int getCost() {
        return switch (this.getCustomItemType()) {
            case HEALING_WAND -> 50;
            case ALLEVIATION_WAND -> 75;
            case CURING_ROD -> 120;
            case STAFF_OF_REGENERATION -> 200;
            case STAFF_OF_REJUVENATION -> 500;
            default -> 999;
        };
    }

    protected Ability getAbility() {
        return switch (this.getCustomItemType()) {
            case HEALING_WAND -> Ability.MELON_MEND;
            case ALLEVIATION_WAND -> Ability.FRUITFUL_REMEDY;
            case CURING_ROD -> Ability.NATURES_RESPITE;
            case STAFF_OF_REGENERATION -> Ability.FULL_BLOOM;
            case STAFF_OF_REJUVENATION -> Ability.REJUVENATION_BURST;
            default -> Ability.MELON_MEND;
        };
    }

    protected CustomItemType getUpgradeMaterial() {
        return switch (this.getCustomItemType()) {
            case ALLEVIATION_WAND -> CustomItemType.PREMIUM_MELON;
            case CURING_ROD -> CustomItemType.ENCHANTED_MELON_SLICE;
            case STAFF_OF_REGENERATION -> CustomItemType.ENCHANTED_MELON;
            case STAFF_OF_REJUVENATION -> CustomItemType.MELON_SLICE_SINGULARITY;
            default -> CustomItemType.MELON_SLICE_SINGULARITY;
        };
    }

    protected CustomItemType getPredecessor() {
        return switch (this.getCustomItemType()) {
            case ALLEVIATION_WAND -> CustomItemType.HEALING_WAND;
            case CURING_ROD -> CustomItemType.ALLEVIATION_WAND;
            case STAFF_OF_REGENERATION -> CustomItemType.CURING_ROD;
            case STAFF_OF_REJUVENATION -> CustomItemType.STAFF_OF_REGENERATION;
            default -> CustomItemType.HEALING_WAND;
        };
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
        return ItemClassification.EQUIPMENT;
    }

    /**
     * Get the abilities this item has, and how they can be cast.
     *
     * @param item The item.
     * @return A list of abilities.
     */
    @Override
    public Collection<AbilityEntry> getAbilities(ItemStack item) {
        return List.of(
                new AbilityEntry(
                        getAbility(),
                        AbilityActivationMethod.RIGHT_CLICK,
                        AbilityCost.of(AbilityCost.Resource.MANA, getCost())
                )
        );
    }

    /**
     * Get the cooldown in between item uses.
     * Keep in mind this is more for preventing strange things from happening via casting on the same tick or teleporting,
     * so it needs to be per item since we use the default cooldown system.
     *
     * @param item The item.
     * @return The cooldown in ticks.
     */
    @Override
    public long getCooldown(ItemStack item) {
        return TickTime.seconds(3);
    }

    @Override
    public NamespacedKey getRecipeKey() {
        return new NamespacedKey(SMPRPG.getPlugin(), this.getCustomItemType().getKey() + "_recipe");
    }

    @Override
    public CraftingRecipe getCustomRecipe() {

        if (this.getCustomItemType() == CustomItemType.HEALING_WAND) {
            var recipe = new ShapedRecipe(getRecipeKey(), generate());
            recipe.setCategory(CraftingBookCategory.EQUIPMENT);
            recipe.shape(
                    " m ",
                    " d ",
                    " d "
            );
            recipe.setIngredient('d', ItemService.generate(Material.DIAMOND));
            recipe.setIngredient('m', ItemService.generate(Material.MELON));
            return recipe;
        }

        var recipe = new ShapedRecipe(getRecipeKey(), generate());
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        recipe.shape(
                "mmm",
                "mrm",
                "mmm"
        );
        recipe.setIngredient('m', ItemService.generate(getUpgradeMaterial()));
        recipe.setIngredient('r', ItemService.generate(getPredecessor()));
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
        return List.of(ItemService.generate(Material.MELON_SLICE));
    }
}
