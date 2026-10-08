package xyz.devvydont.smprpg.items.tools.drills;

import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.interfaces.IFueledEquipment;
import xyz.devvydont.smprpg.services.ItemService;

public class LargeFuelTank extends FuelTankBlueprint {

    public static final String attrKey = "large_fuel_tank";

    public LargeFuelTank(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public String getAttrKey() {
        return attrKey;
    }

    @Override
    public int getMaxDurability() {
        return 15_000 + IFueledEquipment.FUEL_OFFSET;
    }
}
