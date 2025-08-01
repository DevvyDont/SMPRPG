package xyz.devvydont.smprpg.items.blueprints.sets.araxys;

import org.bukkit.inventory.EquipmentSlotGroup;
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

public class AraxysBoots extends AraxysArmorPiece {

    public AraxysBoots(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public EquipmentSlotGroup getActiveSlot() {
        return EquipmentSlotGroup.FEET;
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiers(ItemStack item) {
        return List.of(
                new AdditiveAttributeEntry(AttributeWrapper.DEFENSE, (double) AraxysChestplate.DEFENSE / 2),
                new AdditiveAttributeEntry(AttributeWrapper.HEALTH, (double) AraxysChestplate.HEALTH / 2),
                new ScalarAttributeEntry(AttributeWrapper.STRENGTH, AraxysChestplate.STRENGTH),
                new AdditiveAttributeEntry(AttributeWrapper.CRITICAL_DAMAGE, AraxysChestplate.CRIT),
                new ScalarAttributeEntry(AttributeWrapper.MOVEMENT_SPEED, .15),
                new AdditiveAttributeEntry(AttributeWrapper.STEP, 1)
        );
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.BOOTS;
    }
}
