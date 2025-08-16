package xyz.devvydont.smprpg.items.blueprints.equipment;

import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.*;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.ability.Ability;
import xyz.devvydont.smprpg.ability.AbilityActivationMethod;
import xyz.devvydont.smprpg.ability.AbilityCost;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.base.CustomAttributeItem;
import xyz.devvydont.smprpg.items.interfaces.IAbilityCaster;
import xyz.devvydont.smprpg.items.interfaces.ICraftable;
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden;
import xyz.devvydont.smprpg.services.ItemService;

import java.util.Collection;
import java.util.List;

public class TransmissionWand extends CustomAttributeItem implements ICraftable, IModelOverridden, IAbilityCaster {

    // The mana cost to use this item.
    public static final int COST = 50;

    public TransmissionWand(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public void updateItemData(ItemStack itemStack) {
        super.updateItemData(itemStack);
        itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.WEAPON;
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiers(ItemStack item) {
        return List.of(
                new AdditiveAttributeEntry(AttributeWrapper.STRENGTH, 100)
        );
    }

    @Override
    public int getPowerRating() {
        return 50;
    }

    @Override
    public EquipmentSlotGroup getActiveSlot() {
        return EquipmentSlotGroup.MAINHAND;
    }

    @Override
    public NamespacedKey getRecipeKey() {
        return new NamespacedKey(SMPRPG.getPlugin(), "transmission_wand_recipe");
    }

    @Override
    public CraftingRecipe getCustomRecipe() {
        var recipe = new ShapedRecipe(this.getRecipeKey(), generate());
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        recipe.shape(" m ", " r ", " r ");
        recipe.setIngredient('m', ItemService.generate(CustomItemType.WARP_CATALYST));
        recipe.setIngredient('r', ItemService.generate(CustomItemType.DRACONIC_CRYSTAL));
        return recipe;
    }

    @Override
    public Collection<ItemStack> unlockedBy() {
        return List.of(ItemService.generate(CustomItemType.DRACONIC_CRYSTAL));
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
                        Ability.INSTANT_TRANSMISSION,
                        AbilityActivationMethod.RIGHT_CLICK,
                        AbilityCost.of(AbilityCost.Resource.MANA, COST)
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
        return 1;
    }

    @Override
    public Material getDisplayMaterial() {
        return Material.DIAMOND_SHOVEL;
    }

}
