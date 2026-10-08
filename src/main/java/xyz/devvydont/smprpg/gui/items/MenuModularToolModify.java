package xyz.devvydont.smprpg.gui.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemContainerContents;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import xyz.devvydont.smprpg.gui.base.MenuBase;
import xyz.devvydont.smprpg.items.interfaces.IItemContainer;
import xyz.devvydont.smprpg.items.interfaces.IModularToolComponent;
import xyz.devvydont.smprpg.items.tools.ItemDrill;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;
import xyz.devvydont.smprpg.util.formatting.Symbols;

import java.util.ArrayList;

public class MenuModularToolModify extends MenuBase {

    private final ItemDrill blueprint;
    private final ItemStack drill;
    private NamespacedKey[] partNames = new NamespacedKey[3];
    private int BEGIN_SLOTS = 3;

    public MenuModularToolModify(@NotNull Player player, ItemDrill blueprint, ItemStack drill) {
        super(player, 1);
        this.blueprint = blueprint;
        this.drill = drill;
        this.sounds.setMenuOpen(Sound.BLOCK_ANVIL_USE, 1, .5f);
        this.sounds.setMenuClose(Sound.ITEM_ARMOR_EQUIP_GENERIC, 1, .5f);
        this.sounds.setPageNext(Sound.ITEM_ARMOR_EQUIP_LEATHER, 1, .8f);
        this.sounds.setPageNext(Sound.ITEM_ARMOR_EQUIP_LEATHER, 1, 1.3f);
        partNames[0] = ItemDrill.drillBaseKey;
        partNames[1] = ItemDrill.drillHeadKey;
        partNames[2] = ItemDrill.drillTankKey;
        render();
    }

    public void render() {
        this.setBorderFull();
        var data = drill.getData(DataComponentTypes.CONTAINER);
        var drillParts = data.contents();
        for (int i = BEGIN_SLOTS; i <= BEGIN_SLOTS + 2; i++) {
            this.clearSlot(i);
            this.setSlot(i, drillParts.get(i - BEGIN_SLOTS));
        }
    }

    public void saveData() {
        ArrayList<ItemStack> newParts = new ArrayList<>();
        for (int i = BEGIN_SLOTS; i <= BEGIN_SLOTS + 2; i++) {
            newParts.add(inventory.getItem(i));
        }
        drill.setData(DataComponentTypes.CONTAINER, ItemContainerContents.containerContents(newParts));

        // I hate to do this, but this is a developer UI so...
        IModularToolComponent bp = (IModularToolComponent) ItemService.blueprint(newParts.get(0));
        final String itemName0 = bp.getAttrKey();
        drill.editPersistentDataContainer(pdc -> pdc.set(partNames[0], PersistentDataType.STRING, itemName0));
        bp = (IModularToolComponent) ItemService.blueprint(newParts.get(1));
        final String itemName1 = bp.getAttrKey();
        drill.editPersistentDataContainer(pdc -> pdc.set(partNames[1], PersistentDataType.STRING, itemName1));
        bp = (IModularToolComponent) ItemService.blueprint(newParts.get(2));
        final String itemName2 = bp.getAttrKey();
        drill.editPersistentDataContainer(pdc -> pdc.set(partNames[2], PersistentDataType.STRING, itemName2));
        blueprint.updateItemData(drill);
    }

    @Override
    protected void handleInventoryClicked(InventoryClickEvent event) {
        // Under any circumstances, NEVER let any other backpacks (or gui elements) be clicked or modified.
        var clicked = event.getCurrentItem();
        if (clicked == null)
            return;

        if (!(ItemService.blueprint(clicked) instanceof IModularToolComponent)) {
            event.setCancelled(true);
        }
    }

    @Override
    protected void handleInventoryOpened(InventoryOpenEvent event) {
        event.titleOverride(ComponentUtils.merge(ComponentUtils.create(Symbols.OFFSET_NEG_1 + Symbols.TOOL_MODIFICATION_MENU, NamedTextColor.WHITE),
                ComponentUtils.create(Symbols.OFFSET_NEG_128 + Symbols.OFFSET_NEG_32 + Symbols.OFFSET_NEG_2 + "Tool Modification", NamedTextColor.BLACK)));
        event.getInventory().setMaxStackSize(1);
    }

    @Override
    protected void handleInventoryClosed(InventoryCloseEvent event) {
        // When this inventory closes, our inventory is the source of truth, so we should copy everything we have over.
        this.saveData();
    }
}
