package xyz.devvydont.smprpg.items.blueprints.equipment;

import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.interfaces.ICustomTextured;
import xyz.devvydont.smprpg.items.interfaces.ISellable;
import xyz.devvydont.smprpg.reforge.ReforgeType;
import xyz.devvydont.smprpg.services.ItemService;

public class DesolatedStone extends ReforgeStone implements ISellable, ICustomTextured {

    public DesolatedStone(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public String getTextureUrl() {
        return "895a05992afa37b3806b81f0003ca617b3c1cbb9170a2309115b9c6a03eb73af";
    }

    @Override
    public ReforgeType getReforgeType() {
        return ReforgeType.WITHERED;
    }

    @Override
    public int getExperienceCost() {
        return 50;
    }

    @Override
    public int getWorth(ItemStack item) {
        return 50_000 * item.getAmount();
    }
}
