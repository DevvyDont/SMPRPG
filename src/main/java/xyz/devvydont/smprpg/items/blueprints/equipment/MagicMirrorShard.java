package xyz.devvydont.smprpg.items.blueprints.equipment;

import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.items.interfaces.ISellable;
import xyz.devvydont.smprpg.services.ItemService;

public class MagicMirrorShard extends CustomItemBlueprint implements ISellable, Listener {

    public MagicMirrorShard(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public void updateItemData(ItemStack itemStack) {
        super.updateItemData(itemStack);
        itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
    }

    /**
     * Determine what type of item this is.
     */
    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.ITEM;
    }

    public MagicMirror.MagicMirrorMode getMode() {
        return switch (this.getCustomItemType()) {
            case SLUMBER_SHARD -> MagicMirror.MagicMirrorMode.PLAYER_SPAWN;
            case CINDER_SHARD -> MagicMirror.MagicMirrorMode.NETHER_SPAWN;
            case VOID_SHARD -> MagicMirror.MagicMirrorMode.END_SPAWN;
            default -> throw new IllegalStateException("Unexpected value: " + this.getCustomItemType());
        };
    }


    public CustomItemType getMaterial() {
        return switch (this.getCustomItemType()) {
            case SLUMBER_SHARD -> CustomItemType.PLUTOS_ARTIFACT;
            case CINDER_SHARD -> CustomItemType.INFERNO_REMNANT;
            case VOID_SHARD -> CustomItemType.DRACONIC_CRYSTAL;
            default -> CustomItemType.ENCHANTED_DIAMOND;
        };
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
        return 20_000;
    }

}
