package xyz.devvydont.smprpg.items.blueprints.sets.neptune;

import org.bukkit.Color;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.interfaces.IDyeable;
import xyz.devvydont.smprpg.items.interfaces.ITrimmable;
import xyz.devvydont.smprpg.services.ItemService;

public class NeptuneLeggings extends NeptuneArmorSet implements IDyeable, ITrimmable {

    public NeptuneLeggings(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public TrimPattern getTrimPattern() {
        return TrimPattern.WAYFINDER;
    }

    @Override
    public int getDefense() {
        return 105;
    }

    @Override
    public int getStrength() {
        return 30;
    }

    @Override
    public Color getColor() {
        return Color.fromRGB(0x3ab3da);
    }

    @Override
    public TrimMaterial getTrimMaterial() {
        return TrimMaterial.EMERALD;
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.LEGGINGS;
    }
}
