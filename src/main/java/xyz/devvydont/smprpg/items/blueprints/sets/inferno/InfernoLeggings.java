package xyz.devvydont.smprpg.items.blueprints.sets.inferno;

import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;

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
        return 220;
    }

    @Override
    public int getHealth() {
        return 20;
    }

    @Override
    public double getStrength() {
        return InfernoChestplate.STRENGTH -.05;
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.LEGGINGS;
    }
}
