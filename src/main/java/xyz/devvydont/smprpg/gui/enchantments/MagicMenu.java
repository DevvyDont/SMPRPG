package xyz.devvydont.smprpg.gui.enchantments;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;
import xyz.devvydont.smprpg.gui.InterfaceUtil;
import xyz.devvydont.smprpg.gui.MenuReforgeBrowser;
import xyz.devvydont.smprpg.gui.base.MenuBase;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;

public class MagicMenu extends MenuBase {

    public static final int ENCHANTMENTS_INDEX = 20;
    public static final int REFORGES_INDEX = 24;

    public MagicMenu(@NotNull Player player) {
        super(player, 5);
    }

    public MagicMenu(@NotNull Player player, MenuBase parentMenu) {
        super(player, 5, parentMenu);
    }

    @Override
    protected void handleInventoryClicked(InventoryClickEvent event) {
        event.setCancelled(true);
        this.playInvalidAnimation();
    }

    @Override
    protected void handleInventoryOpened(InventoryOpenEvent event) {
        event.titleOverride(Component.text("Magic Menu"));
        render();
    }

    public void render() {

        this.setBorderFull();
        this.setBackButton();

        this.setButton(ENCHANTMENTS_INDEX, InterfaceUtil.getNamedItemWithDescription(
                Material.ENCHANTING_TABLE,
                ComponentUtils.create("Enchantments", NamedTextColor.LIGHT_PURPLE),
                ComponentUtils.EMPTY,
                ComponentUtils.create("Click to view enchantments!", NamedTextColor.YELLOW)),
                e -> new EnchantmentMenu(this.player, this).openMenu()
        );

        this.setButton(REFORGES_INDEX, InterfaceUtil.getNamedItemWithDescription(
                        Material.ANVIL,
                        ComponentUtils.create("Reforges", NamedTextColor.BLUE),
                        ComponentUtils.EMPTY,
                        ComponentUtils.create("Click to view reforges!", NamedTextColor.YELLOW)),
                e -> new MenuReforgeBrowser(this.player, this).openMenu()
        );

        this.setBackButton();
    }
}
