package xyz.devvydont.smprpg.items.blueprints.sets.inferno;

import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimPattern;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.util.crafting.builders.LeggingsRecipe;

import java.util.Collection;
import java.util.List;

public class InfernoLeggings extends InfernoArmorSet {

    public InfernoLeggings(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiers(ItemStack item) {
        return List.of(
                new AdditiveAttributeEntry(AttributeWrapper.DEFENSE, getDefense()),
                new AdditiveAttributeEntry(AttributeWrapper.HEALTH, getHealth()),
                new ScalarAttributeEntry(AttributeWrapper.STRENGTH, getStrength()),
                new ScalarAttributeEntry(AttributeWrapper.MOVEMENT_SPEED, .15),
                new AdditiveAttributeEntry(AttributeWrapper.CRITICAL_DAMAGE, 25)
        );
    }

    @Override
    public int getDefense() {
        return InfernoChestplate.DEFENSE - 40;
    }

    @Override
    public int getHealth() {
        return InfernoChestplate.HEALTH - -10;
    }

    @Override
    public double getStrength() {
        return InfernoChestplate.STRENGTH -.05;
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.LEGGINGS;
    }

    @Override
    public CraftingRecipe getCustomRecipe() {
        return new LeggingsRecipe(this, ItemService.generate(CustomItemType.INFERNO_REMNANT), generate()).build();
    }
}
