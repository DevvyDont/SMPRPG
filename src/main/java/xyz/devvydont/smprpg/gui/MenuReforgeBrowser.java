package xyz.devvydont.smprpg.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.gui.base.MenuBase;
import xyz.devvydont.smprpg.items.ItemRarity;
import xyz.devvydont.smprpg.reforge.ReforgeType;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;
import xyz.devvydont.smprpg.util.formatting.MinecraftStringUtils;
import xyz.devvydont.smprpg.util.formatting.Symbols;

import java.util.ArrayList;
import java.util.List;

import static xyz.devvydont.smprpg.util.formatting.ComponentUtils.create;
import static xyz.devvydont.smprpg.util.formatting.ComponentUtils.merge;

/**
 * Renders all the reforges in the game for people to browse.
 */
public class MenuReforgeBrowser extends MenuBase {

    public static final int ROWS = 6;

    public MenuReforgeBrowser(@NotNull Player player) {
        super(player, ROWS);
    }

    @Override
    protected void handleInventoryOpened(InventoryOpenEvent event) {
        super.handleInventoryOpened(event);
        this.render();
    }

    @Override
    protected void handleInventoryClicked(InventoryClickEvent event) {
        super.handleInventoryClicked(event);
        event.setCancelled(true);
    }

    public ItemStack generateReforgeButton(ReforgeType type) {
        ItemStack button = createNamedItem(type.getDisplayMaterial(), ComponentUtils.create(type.display()));
        var reforge = SMPRPG.getService(ItemService.class).getReforge(type);
        if (reforge == null)
            return button;

        var rarity = ItemRarity.RARE;
        List<Component> lore = new ArrayList<>();
        lore.add(ComponentUtils.EMPTY);
        var rollable = type.isRollable();
        lore.add(merge(create("Rollable? "), create(rollable ? Symbols.CHECK : Symbols.X, rollable ? NamedTextColor.GREEN : NamedTextColor.RED)));
        lore.add(ComponentUtils.EMPTY);
        lore.addAll(reforge.getDescription());
        lore.add(ComponentUtils.EMPTY);
        lore.add(merge(create("Showing stats for "), create(rarity.name(), rarity.color)));
        lore.addAll(reforge.formatAttributeModifiersWithRarity(ItemRarity.RARE));
        button.lore(ComponentUtils.cleanItalics(lore));
        return button;
    }

    public void handleButtonClicked(ReforgeType reforgeType) {

    }

    public void render() {
        setBorderEdge();

        int reforgeIndex = 0;
        // todo paginate if there are too many reforges
        for (int slot = 0; slot < getInventorySize(); slot++) {

            // Already occupied?
            if (getItem(slot) != null)
                continue;

            // No more reforges?
            if (reforgeIndex >= ReforgeType.values().length)
                break;

            // Render
            ReforgeType reforgeType = ReforgeType.values()[reforgeIndex];
            setButton(slot, generateReforgeButton(reforgeType), event -> handleButtonClicked(reforgeType));
            reforgeIndex++;
        }

        // Create a back button
        this.setBackButton((ROWS-1)*9 + 4);
    }
}
