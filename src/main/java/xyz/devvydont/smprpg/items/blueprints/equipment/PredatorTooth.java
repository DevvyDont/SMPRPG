package xyz.devvydont.smprpg.items.blueprints.equipment;

import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.interfaces.ICustomTextured;
import xyz.devvydont.smprpg.items.interfaces.ISellable;
import xyz.devvydont.smprpg.reforge.ReforgeType;
import xyz.devvydont.smprpg.services.ItemService;

public class PredatorTooth extends ReforgeStone implements ICustomTextured, ISellable {

    public PredatorTooth(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    /**
     * Retrieve the URL to use for the custom head texture of this item.
     * The link that is set here should follow the following format:
     * Let's say you have the following link to a skin;
     * <a href="https://textures.minecraft.net/texture/18813764b2abc94ec3c3bc67b9147c21be850cdf996679703157f4555997ea63a">...</a>
     * You should only use the very last component of the URL, as the backend will fill in the rest.
     * Meaning we would end up using: "18813764b2abc94ec3c3bc67b9147c21be850cdf996679703157f4555997ea63a"
     *
     * @return The URL to the skin.
     */
    @Override
    public String getTextureUrl() {
        return "f0c84320f4b0fcd8391b8a20f53bd41612ca6a50e758309525971f77ace2d";
    }

    /**
     * Given this item stack, how much should it be able to sell for?
     * Keep in mind that the size of the stack needs to considered as well!
     *
     * @param item The item that can be sold.
     * @return The worth of the item.
     */
    @Override
    public int getWorth(ItemStack item) {
        return 75_000 * item.getAmount();
    }

    @Override
    public ReforgeType getReforgeType() {
        return ReforgeType.ALLURING;
    }

    @Override
    public int getExperienceCost() {
        return 50;
    }
}
