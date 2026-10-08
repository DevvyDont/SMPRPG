package xyz.devvydont.smprpg.items.blueprints.wardrobe;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.items.interfaces.IHeaderDescribable;
import xyz.devvydont.smprpg.services.ItemService;

import net.kyori.adventure.text.format.NamedTextColor;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;

import java.util.List;

public class WardrobeSlotToken extends CustomItemBlueprint implements IHeaderDescribable {

    public WardrobeSlotToken(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public List<Component> getHeader(ItemStack itemStack) {
        return List.of(
                ComponentUtils.create("Consume in the Wardrobe Upgrades"),
                ComponentUtils.create("menu to permanently unlock a"),
                ComponentUtils.merge(ComponentUtils.create("new "), ComponentUtils.create("wardrobe slot", NamedTextColor.AQUA), ComponentUtils.create("!"))
        );
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.ITEM;
    }
}
