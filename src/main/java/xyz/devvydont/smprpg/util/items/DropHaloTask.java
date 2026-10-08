package xyz.devvydont.smprpg.util.items;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import io.papermc.paper.entity.LookAnchor;
import net.kyori.adventure.key.Key;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.scheduler.BukkitRunnable;
import org.joml.Matrix4f;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.time.TickTime;

public class DropHaloTask extends BukkitRunnable {

    public static void start(Item item) {
        new DropHaloTask(item).runTaskTimer(SMPRPG.getPlugin(), TickTime.INSTANTANEOUSLY, TickTime.TICK);
    }

    private final Item item;
    int tick = 0;
    private ItemDisplay display;

    public static final int UPDATE_PERIOD = 2;
    public static final float SCALE = 0.5f;
    Matrix4f matrix = new Matrix4f().scale(SCALE);

    public DropHaloTask(Item item) {
        this.item = item;
        this.display = (ItemDisplay) item.getWorld().spawnEntity(item.getLocation(), EntityType.ITEM_DISPLAY);

        var itemStack = item.getItemStack();
        var itemBp = ItemService.blueprint(itemStack);
        var haloItem = ItemService.generate(Material.STICK);
        var rarityColor = itemBp.getRarity(itemStack).color;

        haloItem.setData(DataComponentTypes.ITEM_MODEL, Key.key("smprpg:drop_halo"));
        haloItem.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor().color(Color.fromRGB(rarityColor.red(), rarityColor.green(), rarityColor.blue())).build());
        display.setItemStack(haloItem);
        display.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.GROUND);
    }

    @Override
    public void run() {
        if (!item.isValid()) {
            display.remove();
            cancel();
        }

        var destLoc = item.getLocation().clone();
        destLoc.setY(destLoc.getY() + 0.13);
        display.teleport(destLoc);
    }
}
