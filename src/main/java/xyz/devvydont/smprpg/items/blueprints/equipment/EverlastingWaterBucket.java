package xyz.devvydont.smprpg.items.blueprints.equipment;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.items.interfaces.IHeaderDescribable;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;
import xyz.devvydont.smprpg.util.items.AbilityUtil;
import xyz.devvydont.smprpg.util.time.TickTime;

import java.util.List;

public class EverlastingWaterBucket extends CustomItemBlueprint implements IHeaderDescribable, Listener {

    public EverlastingWaterBucket(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    private final String BUCKET_STRING = "everlasting_water_bucket";
    private final NamespacedKey ITEM_TYPE_KEY = new NamespacedKey("smprpg", "item-type");

    @Override
    public List<Component> getHeader(ItemStack itemStack) {
        return List.of(
                ComponentUtils.merge(AbilityUtil.getAbilityComponent("Unquenchable Thirst"), ComponentUtils.create(" (Right Click)", NamedTextColor.DARK_GRAY)),
                ComponentUtils.create("Use to place a water source, infinitely!")
        );
    }

    /**
     * Determine what type of item this is.
     */
    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.EQUIPMENT;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onUseBucket(PlayerBucketEmptyEvent event) {
        // If equipped bucket is everlasting, set it as the return ItemStack for this event.
        var bucketInHand = event.getPlayer().getInventory().getItemInMainHand();
        var bucketVal = bucketInHand.getPersistentDataContainer().getOrDefault(ITEM_TYPE_KEY, PersistentDataType.STRING, "");
        if (bucketVal.equals(BUCKET_STRING)) {
            // Cancel the vanilla event, as we are replicating functionality.
            event.setCancelled(true);

            // We gotta redo bucket logic here, since it acts wonky otherwise.
            Block block = event.getBlock();

            if (block.getWorld().isUltraWarm())
                return;  // Can't place water in ultrawarm environments.

            // Waterlog the block if possible
            if (block.getBlockData() instanceof Waterlogged waterlog) {
                waterlog.setWaterlogged(true);  // Can't unwaterlog if bucket is always full, force true.
                block.setBlockData(waterlog);
                Bukkit.getScheduler().runTaskLater(SMPRPG.getPlugin(), block::fluidTick, TickTime.HALF_SECOND);  // Half second isn't completely accurate, but its a very close approximation.
            } else {
                event.getPlayer().getWorld().getBlockAt(block.getX(), block.getY(), block.getZ()).setType(Material.WATER);
            }
        }
    }
}
