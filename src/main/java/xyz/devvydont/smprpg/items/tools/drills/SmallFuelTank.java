package xyz.devvydont.smprpg.items.tools.drills;

import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.interfaces.IFueledEquipment;
import xyz.devvydont.smprpg.services.ItemService;

public class SmallFuelTank extends FuelTankBlueprint {

    public static final String attrKey = "small_fuel_tank";

    public SmallFuelTank(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public String getAttrKey() {
        return attrKey;
    }

    @Override
    public int getMaxDurability() {
        return 5_000 + IFueledEquipment.FUEL_OFFSET;
    }
}
