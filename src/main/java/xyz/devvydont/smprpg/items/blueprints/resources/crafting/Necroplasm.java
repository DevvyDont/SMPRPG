package xyz.devvydont.smprpg.items.blueprints.resources.crafting;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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

public class Necroplasm extends CustomItemBlueprint implements IHeaderDescribable, ISellable {

    public Necroplasm(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public List<Component> getHeader(ItemStack itemStack) {
        return List.of(
                ComponentUtils.create("A disgusting concoction"),
                ComponentUtils.create("formed by the remains"),
                ComponentUtils.merge(ComponentUtils.create("of various "), ComponentUtils.create("Sea Creatures", SeaCreature.NAME_COLOR)),
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
        return 100_000;
    }
}
