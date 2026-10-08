package xyz.devvydont.smprpg.items.blueprints.sets.neptune;

import io.papermc.paper.datacomponent.item.Equippable;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry;
import xyz.devvydont.smprpg.items.interfaces.IEquippableOverride;
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;

import java.util.Collection;
import java.util.List;

public class NeptuneHelmet extends NeptuneArmorSet implements IBreakableEquipment, IEquippableOverride {

    public NeptuneHelmet(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public Equippable getEquipmentOverride() {
        return IEquippableOverride.generateDefault(EquipmentSlot.HEAD);
    }

    @Override
    public boolean wantNerfedSellPrice() {
        return false;
    }

    @Override
    public int getDefense() {
        return 80;
    }

    public int getStrength() {
        return 25;
    }


    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.HELMET;
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiers(ItemStack item) {
        return List.of(
                new AdditiveAttributeEntry(AttributeWrapper.DEFENSE, getDefense()),
                new ScalarAttributeEntry(AttributeWrapper.STRENGTH, getStrength()/100.0),
                new AdditiveAttributeEntry(AttributeWrapper.OXYGEN_BONUS, NeptuneArmorSet.OXYGEN_BONUS),
                new ScalarAttributeEntry(AttributeWrapper.BURNING_TIME, -.1)
        );
    }

    @Override
    public int getPowerRating() {
        return NeptuneArmorSet.POWER_LEVEL;
    }

    @Override
    public int getMaxDurability() {
        return NeptuneArmorSet.DURABILITY;
    }

}
