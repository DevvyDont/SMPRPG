package xyz.devvydont.smprpg.items.blueprints.sets.emberclad;

import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.crafting.builders.ChestplateRecipe;

import java.util.Collection;
import java.util.List;

public class CryaxChestplate extends CryaxArmorSet {

    public CryaxChestplate(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiers(ItemStack item) {
        return List.of(
                new AdditiveAttributeEntry(AttributeWrapper.DEFENSE, 175),
                new AdditiveAttributeEntry(AttributeWrapper.HEALTH, 30),
                new ScalarAttributeEntry(AttributeWrapper.STRENGTH, .35),
                new ScalarAttributeEntry(AttributeWrapper.BURNING_TIME, -.25)
        );
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.CHESTPLATE;
    }

    @Override
    public CraftingRecipe getCustomRecipe() {
        return new ChestplateRecipe(this, itemService.getCustomItem(CryaxArmorSet.INGREDIENT), generate()).build();
    }

}
