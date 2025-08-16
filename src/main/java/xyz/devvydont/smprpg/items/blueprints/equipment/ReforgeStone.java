package xyz.devvydont.smprpg.items.blueprints.equipment;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.devvydont.smprpg.attribute.AttributeType;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.ItemRarity;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.items.interfaces.ReforgeApplicator;
import xyz.devvydont.smprpg.reforge.ReforgeBase;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.attributes.AttributeUtil;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;
import xyz.devvydont.smprpg.util.formatting.MinecraftStringUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class ReforgeStone extends CustomItemBlueprint implements ReforgeApplicator {

    public static final ItemRarity DISPLAY_RARITY = ItemRarity.EPIC;

    public ReforgeStone(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    public ReforgeBase getReforge() {
        return itemService.getReforge(getReforgeType());
    }

    @Override
    public List<Component> getReforgeInformation() {
        List<Component> lines = new ArrayList<>();

        ReforgeBase reforge = getReforge();
        Component reforgeName = ComponentUtils.create(getReforgeType().display() + " Reforge", NamedTextColor.BLUE);

        // First the description of what this item does.
        lines.add(ComponentUtils.create("Combine this item with equipment in an"));
        lines.add(ComponentUtils.create("anvil to apply the following reforge:"));
        lines.add(ComponentUtils.EMPTY);

        // The reforge tag that shows up on items when applied
        lines.add(reforgeName);
        lines.addAll(reforge.getDescription());
        lines.add(ComponentUtils.EMPTY);

        // Sample of statistics that get altered for a certain rarity
        // Is this attribute present on this item? If not skip it
        lines.add(ComponentUtils.create("Stat Modifiers:", NamedTextColor.BLUE));
        lines.addAll(reforge.formatAttributeModifiersWithRarity(DISPLAY_RARITY));
        lines.add(ComponentUtils.create("Example bonuses for " + DISPLAY_RARITY.name() +" item are shown.", NamedTextColor.DARK_GRAY));
        lines.add(ComponentUtils.create("Results vary based on item rarity!", NamedTextColor.DARK_GRAY));
        lines.add(ComponentUtils.EMPTY);
        lines.add(ComponentUtils.create("Valid Equipment:", NamedTextColor.BLUE));
        for (ItemClassification clazz : getReforgeType().allowedItems)
            lines.add(ComponentUtils.create("- " + MinecraftStringUtils.getTitledString(clazz.name())));
        lines.add(ComponentUtils.EMPTY);
        lines.add(ComponentUtils.create("Application Cost:", NamedTextColor.BLUE));
        lines.add(ComponentUtils.merge(ComponentUtils.create("- "), ComponentUtils.create(getExperienceCost() + " levels", NamedTextColor.GREEN)));
        return lines;
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.REFORGE_STONE;
    }

    @Override
    public void updateItemData(ItemStack itemStack) {
        super.updateItemData(itemStack);
        itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
    }
}
