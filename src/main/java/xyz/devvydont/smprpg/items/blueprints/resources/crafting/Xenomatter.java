package xyz.devvydont.smprpg.items.blueprints.resources.crafting;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.items.interfaces.IHeaderDescribable;
import xyz.devvydont.smprpg.items.interfaces.ISellable;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;

import java.util.List;

public class Xenomatter extends CustomItemBlueprint implements IHeaderDescribable, ISellable {

    public Xenomatter(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public List<Component> getHeader(ItemStack itemStack) {
        return List.of(
                ComponentUtils.create("A warped mess of raw creature"),
                ComponentUtils.merge(ComponentUtils.create("essence from "), ComponentUtils.create("unnatural", NamedTextColor.LIGHT_PURPLE), ComponentUtils.create(" sources")),
                ComponentUtils.EMPTY,
                ComponentUtils.merge(ComponentUtils.create("Perhaps a "), ComponentUtils.create("slow roast", NamedTextColor.RED), ComponentUtils.create(" can make")),
                ComponentUtils.create("this stuff actually usable?"),
                ComponentUtils.EMPTY,
                ComponentUtils.merge(ComponentUtils.create("Used for various "), ComponentUtils.create("crafting", NamedTextColor.GOLD), ComponentUtils.create(" components"))
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
        return 250_000 * item.getAmount();
    }
}
