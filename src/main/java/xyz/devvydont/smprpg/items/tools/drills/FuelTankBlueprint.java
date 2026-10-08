package xyz.devvydont.smprpg.items.tools.drills;

import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment;
import xyz.devvydont.smprpg.items.interfaces.IFueledEquipment;
import xyz.devvydont.smprpg.items.interfaces.IModularToolComponent;
import xyz.devvydont.smprpg.services.ItemService;

import java.util.Collection;
import java.util.List;

public class FuelTankBlueprint extends CustomItemBlueprint implements IModularToolComponent, IBreakableEquipment {

    public FuelTankBlueprint(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public Collection<AttributeEntry> getAttributes() {
        return List.of(
        );
    }

    @Override
    public String getAttrKey() {
        throw new IllegalStateException("FuelTankBlueprint does not have an attrKey assigned. Assign one in subclass.");
    }

    @Override
    public void updateItemData(ItemStack item) {
        var itemPdc = item.getPersistentDataContainer();
        super.updateItemData(item);
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.ITEM;
    }

    @Override
    public String getComponentPrefix() { return ""; }

    @Override
    public int getMaxDurability() {
        return 50_000 + IFueledEquipment.FUEL_OFFSET;
    }
}
