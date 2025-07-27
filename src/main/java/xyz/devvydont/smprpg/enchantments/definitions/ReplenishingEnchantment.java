package xyz.devvydont.smprpg.enchantments.definitions;

import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.enchantments.CustomEnchantment;
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity;
import xyz.devvydont.smprpg.services.ActionBarService;
import xyz.devvydont.smprpg.services.DropsService;
import xyz.devvydont.smprpg.services.EnchantmentService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;
import xyz.devvydont.smprpg.util.time.TickTime;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class ReplenishingEnchantment extends CustomEnchantment implements Listener {

    private final Random random = new Random();

    public ReplenishingEnchantment(String id) {
        super(id);
    }

    @Override
    public @NotNull Component getDisplayName() { return ComponentUtils.create("Replenishing"); }

    @Override
    public @NotNull Component getDescription() {
        return ComponentUtils.merge(
                ComponentUtils.create("Grants a "),
                ComponentUtils.create("+" + getReplantChance(getLevel()) + "%", NamedTextColor.GREEN),
                ComponentUtils.create(" chance for crops to replant when harvested.")
        );
    }

    @Override
    public TagKey<ItemType> getItemTypeTag() {
        return ItemTypeTagKeys.HOES;
    }

    @Override
    public int getAnvilCost() {
        return 0;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public int getWeight() {
        return EnchantmentRarity.UNCOMMON.getWeight();
    }

    @Override
    public EquipmentSlotGroup getEquipmentSlotGroup() {
        return EquipmentSlotGroup.MAINHAND;
    }

    @Override
    public int getSkillRequirement() { return 25; }

    /*
     * There may be multiple instances where we want to attempt to perform this enchant's ability on an item, so pull
     * out the behavior into a method. BlockDropItemEvent happens after ItemSpawnEvent, so we can delay the
     * telekinetic check until the next tick to try and capture it
     *
     * @param item the item to teleport into an owner's inventory if present
     * @return true if successful false otherwise
     */
    private int getReplantChance(int level) {
        return Math.min(100, level * 20);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBreakBlock(BlockBreakEvent event) {

        // Calculate our odds of replanting the crop
        // If we are successful, replace the block at the location
        // with the stage 0 crop.

        // Check that the item in hand is enchanted.
        int enchLevel = event.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(getEnchantment());
        if (enchLevel == 0)
            return;

        var passThreshold = getReplantChance(enchLevel);
        Block block = event.getBlock();
        BlockData data = block.getBlockData();
        if (data instanceof Ageable) {
            Ageable ageable = (Ageable) data;
            if (ageable.getAge() != ageable.getMaximumAge()) {
                event.setCancelled(true);
                return;
            }

            if (random.nextInt(100) <= passThreshold) {
                ageable.setAge(0);
                Bukkit.getScheduler().runTaskLater(SMPRPG.getInstance(), () -> {
                    block.setType(block.getType());
                    block.setBlockData(ageable);
                }, TickTime.INSTANTANEOUSLY);
            }
        }
    }
}
