package xyz.devvydont.smprpg.items.blueprints.charms;

import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry;
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry;
import xyz.devvydont.smprpg.items.base.CustomAttributeItem;
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;

import java.util.Collection;
import java.util.List;

public class SpeedCharm extends CustomAttributeItem implements IModelOverridden {

    public SpeedCharm(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public void updateItemData(ItemStack itemStack) {
        super.updateItemData(itemStack);
        itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
    }

    @Override
    public Material getDisplayMaterial() {
        return Material.TOTEM_OF_UNDYING;
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiers(ItemStack item) {
        return List.of(
                new ScalarAttributeEntry(AttributeWrapper.MOVEMENT_SPEED, .3),
                new MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, .3),
                new ScalarAttributeEntry(AttributeWrapper.MINING_SPEED, .3)
        );
    }

    @Override
    public int getPowerRating() {
        return 25;
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.CHARM;
    }

    @Override
    public EquipmentSlotGroup getActiveSlot() {
        return EquipmentSlotGroup.HAND;
    }
}
