package xyz.devvydont.smprpg.items.blueprints.sets.redstone;

import io.papermc.paper.datacomponent.item.Equippable;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.interfaces.IEquippableOverride;
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment;
import xyz.devvydont.smprpg.services.ItemService;

public class RedstoneHelmet extends RedstoneArmorSet implements IBreakableEquipment, IEquippableOverride {

    public RedstoneHelmet(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public Equippable getEquipmentOverride() {
        return IEquippableOverride.generateDefault(EquipmentSlot.HEAD);
    }

    @Override
    public int getDefense() {
        return 45;
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
    public int getMaxDurability() {
        return RedstoneArmorSet.DURABILITY;
    }
}
