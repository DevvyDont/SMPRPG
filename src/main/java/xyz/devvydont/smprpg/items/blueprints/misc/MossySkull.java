package xyz.devvydont.smprpg.items.blueprints.misc;

import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.items.interfaces.ICustomTextured;
import xyz.devvydont.smprpg.services.ItemService;

public class MossySkull extends CustomItemBlueprint implements ICustomTextured {

    public MossySkull(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    /**
     * Determine what type of item this is.
     */
    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.ITEM;
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
        return "8cda4d12e94d8d13128cc0662b50398ff0aab964192e77f4ffe1a2ce41a86ee0";
    }
}
