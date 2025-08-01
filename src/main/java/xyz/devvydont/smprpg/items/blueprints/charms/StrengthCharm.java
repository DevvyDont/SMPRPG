package xyz.devvydont.smprpg.items.blueprints.charms;

import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry;
import xyz.devvydont.smprpg.items.base.CustomAttributeItem;
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;

import java.util.Collection;
import java.util.List;

public class StrengthCharm extends CustomAttributeItem implements IModelOverridden {

    @Override
    public void updateItemData(ItemStack itemStack) {
        super.updateItemData(itemStack);
        itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
    }

    public StrengthCharm(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public Material getDisplayMaterial() {
        return Material.TOTEM_OF_UNDYING;
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiers(ItemStack item) {
        return List.of(
                new ScalarAttributeEntry(AttributeWrapper.STRENGTH, .3)
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
