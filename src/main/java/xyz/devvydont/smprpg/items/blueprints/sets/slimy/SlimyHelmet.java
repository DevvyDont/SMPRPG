package xyz.devvydont.smprpg.items.blueprints.sets.slimy;

import io.papermc.paper.datacomponent.item.Equippable;
import org.bukkit.inventory.EquipmentSlot;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.interfaces.IEquippableOverride;
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment;
import xyz.devvydont.smprpg.services.ItemService;

public class SlimyHelmet extends SlimyArmorSet implements IBreakableEquipment, IEquippableOverride {


    public SlimyHelmet(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    public int getDefense() {
        return 55;
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
    public int getMaxDurability() {
        return 7500;
    }
}
