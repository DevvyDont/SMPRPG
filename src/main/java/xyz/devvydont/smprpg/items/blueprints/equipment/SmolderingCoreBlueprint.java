package xyz.devvydont.smprpg.items.blueprints.equipment;

import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.interfaces.ICustomTextured;
import xyz.devvydont.smprpg.items.interfaces.ISellable;
import xyz.devvydont.smprpg.reforge.ReforgeType;
import xyz.devvydont.smprpg.services.ItemService;

public class SmolderingCoreBlueprint extends ReforgeStone implements ISellable, ICustomTextured {

    public SmolderingCoreBlueprint(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public String getTextureUrl() {
        return "eac5a3b17e8f1b98cda535abecf0667761a4f5991388af71efac2021f7bcce09";
    }

    @Override
    public ReforgeType getReforgeType() {
        return ReforgeType.OVERHEATING;
    }

    @Override
    public int getExperienceCost() {
        return 50;
    }

    @Override
    public int getWorth(ItemStack item) {
        return 70_000 * item.getAmount();
    }
}
