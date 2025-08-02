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
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.enchantments.CustomEnchantment;
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity;
import xyz.devvydont.smprpg.services.EnchantmentService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;
import xyz.devvydont.smprpg.util.time.TickTime;

import java.util.Random;

public class ReplenishingBlessing extends CustomEnchantment implements Listener {

    private final Random random = new Random();

    public ReplenishingBlessing(String id) {
        super(id);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return ComponentUtils.create("Blessing of Replenishing", NamedTextColor.YELLOW);
    }

    @Override
    public @NotNull TextColor getEnchantColor() {
        return NamedTextColor.YELLOW;
    }

    @Override
    public @NotNull Component getDescription() {
        return ComponentUtils.merge(
                ComponentUtils.create("Crops are automatically replanted on harvest")
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
        return 1;
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
    public @NotNull RegistryKeySet<Enchantment> getConflictingEnchantments() {
        return RegistrySet.keySet(RegistryKey.ENCHANTMENT,
                EnchantmentService.KEEPING_BLESSING.getTypedKey(),
                EnchantmentService.MERCY_BLESSING.getTypedKey(),
                EnchantmentService.TELEKINESIS_BLESSING.getTypedKey(),
                EnchantmentService.VOIDSTRIDING_BLESSING.getTypedKey()
        );
    }

    @Override
    public int getSkillRequirement() { return 30; }

    /*
     * There may be multiple instances where we want to attempt to perform this enchant's ability on an item, so pull
     * out the behavior into a method. BlockDropItemEvent happens after ItemSpawnEvent, so we can delay the
     * telekinetic check until the next tick to try and capture it
     *
     * @param item the item to teleport into an owner's inventory if present
     * @return true if successful false otherwise
     */
    private int getReplantChance(int level) {
        return Math.min(100, level * 100);
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
        var blockMat = data.getPlacementMaterial();
        if (data instanceof Ageable) {
            Ageable ageable = (Ageable) data;
            if (ageable.getAge() != ageable.getMaximumAge()) {
                switch (blockMat) {
                    case Material.BAMBOO: case Material.SUGAR_CANE: break;
                    default: event.setCancelled(true); break;
                }
                return;
            }

            if (random.nextInt(100) <= passThreshold) {
                ageable.setAge(0);
                Bukkit.getScheduler().runTaskLater(SMPRPG.getPlugin(), () -> {
                    block.setType(block.getType());
                    block.setBlockData(ageable);
                }, TickTime.INSTANTANEOUSLY);
            }
        }
    }
}
