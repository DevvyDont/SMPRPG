package xyz.devvydont.smprpg.items.blueprints.resources.crafting;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.entity.fishing.SeaCreature;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.items.interfaces.IHeaderDescribable;
import xyz.devvydont.smprpg.items.interfaces.ISellable;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;

import java.util.List;

public class LatticedXenomatter extends CustomItemBlueprint implements IHeaderDescribable, ISellable {

    public LatticedXenomatter(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public List<Component> getHeader(ItemStack itemStack) {
        return List.of(
                ComponentUtils.merge(ComponentUtils.create("An intense "), ComponentUtils.create("hyper-stabilized", NamedTextColor.LIGHT_PURPLE), ComponentUtils.create(" material")),
                ComponentUtils.merge(ComponentUtils.create("formed from the "), ComponentUtils.create("mystical essence", NamedTextColor.LIGHT_PURPLE), ComponentUtils.create(" of the")),
                ComponentUtils.merge(ComponentUtils.create("most powerful "), ComponentUtils.create("creatures", NamedTextColor.RED), ComponentUtils.create(" that are lurking")),
                ComponentUtils.merge(ComponentUtils.create("in the deep")),
                ComponentUtils.EMPTY,
                ComponentUtils.merge(ComponentUtils.create("Used for crafting "), ComponentUtils.create("powerful fishing gear", SeaCreature.NAME_COLOR, TextDecoration.BOLD))
        );
    }

    /**
     * Determine what type of item this is.
     */
    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.ITEM;
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
        return 300_000 * item.getAmount();
    }
}
