package xyz.devvydont.smprpg.items.blueprints.sets.special;

import io.papermc.paper.datacomponent.item.Equippable;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.base.CustomAttributeItem;
import xyz.devvydont.smprpg.items.interfaces.IEquippableOverride;
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment;
import xyz.devvydont.smprpg.items.interfaces.ICustomTextured;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;

import java.util.Collection;
import java.util.List;

public class SquidHelmet extends CustomAttributeItem implements IBreakableEquipment, ICustomTextured, IEquippableOverride {

    public SquidHelmet(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public String getTextureUrl() {
        return "464bdc6f600656511bef596c1a16aab1d3f5dbaae8bee19d5c04de0db21ce92c";
    }

    @Override
    public Equippable getEquipmentOverride() {
        return IEquippableOverride.generateDefault(EquipmentSlot.HEAD);
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.HELMET;
    }

    @Override
    public EquipmentSlotGroup getActiveSlot() {
        return EquipmentSlotGroup.HEAD;
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiers(ItemStack item) {
        return List.of(
                new AdditiveAttributeEntry(AttributeWrapper.OXYGEN_BONUS, 180),
                new AdditiveAttributeEntry(AttributeWrapper.HEALTH, 10)
        );
    }

    @Override
    public int getPowerRating() {
        return 5;
    }

    @Override
    public int getMaxDurability() {
        return 2500;
    }
}
