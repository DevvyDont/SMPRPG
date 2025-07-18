package xyz.devvydont.smprpg.items.blueprints.sets.valiant;

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

public class ValiantLeggings extends ValiantArmorSet {

    public ValiantLeggings(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }


    @Override
    public Collection<AttributeEntry> getAttributeModifiers(ItemStack item) {
        return List.of(
                new AdditiveAttributeEntry(AttributeWrapper.DEFENSE, 580),
                new AdditiveAttributeEntry(AttributeWrapper.HEALTH, 360),
                new ScalarAttributeEntry(AttributeWrapper.STRENGTH, .5)
        );
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.LEGGINGS;
    }
}
