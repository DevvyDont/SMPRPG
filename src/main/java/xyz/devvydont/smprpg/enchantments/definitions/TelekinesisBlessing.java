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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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

import java.util.Map;
import java.util.UUID;

public class TelekinesisBlessing extends CustomEnchantment implements Listener {

    public TelekinesisBlessing(String id) {
        super(id);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return ComponentUtils.create("Blessing of Telekinesis", NamedTextColor.YELLOW);
    }

    @Override
    public @NotNull TextColor getEnchantColor() {
        return NamedTextColor.YELLOW;
    }

    @Override
    public @NotNull Component getDescription() {
        return ComponentUtils.merge(
            ComponentUtils.create("Loot is "),
            ComponentUtils.create("magically transported", NamedTextColor.DARK_PURPLE),
            ComponentUtils.create(" straight to your inventory")
        );
    }

    @Override
    public TagKey<ItemType> getItemTypeTag() {
        return ItemTypeTagKeys.ENCHANTABLE_HEAD_ARMOR;
    }

    @Override
    public int getAnvilCost() {
        return 1;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getWeight() {
        return EnchantmentRarity.BLESSING.getWeight();
    }

    @Override
    public EquipmentSlotGroup getEquipmentSlotGroup() {
        return EquipmentSlotGroup.HEAD;
    }

    @Override
    public int getSkillRequirement() {
        return 20;
    }

    @Override
    public @NotNull RegistryKeySet<Enchantment> getConflictingEnchantments() {
        return RegistrySet.keySet(RegistryKey.ENCHANTMENT,
                EnchantmentService.KEEPING_BLESSING.getTypedKey(),
                EnchantmentService.MERCY_BLESSING.getTypedKey(),
                EnchantmentService.VOIDSTRIDING_BLESSING.getTypedKey(),
                EnchantmentService.REPLENISHING.getTypedKey()
        );
    }

    /*
     * There may be multiple instances where we want to attempt to perform this enchant's ability on an item, so pull
     * out the behavior into a method. BlockDropItemEvent happens after ItemSpawnEvent, so we can delay the
     * telekinetic check until the next tick to try and capture it
     *
     * @param item the item to teleport into an owner's inventory if present
     * @return true if successful false otherwise
     */
    private boolean performTelekinesis(Item item) {

        // Does this item have an owner?
        UUID ownerID = SMPRPG.getService(DropsService.class).getOwner(item);
        if (ownerID == null)
            return false;

        // Is the owner of the item online?
        Player owner = Bukkit.getPlayer(ownerID);
        if (owner == null)
            return false;

        // Is this item marked with the "loot" tag?
        DropsService.DropFlag flag = SMPRPG.getService(DropsService.class).getFlag(item);
        if (!flag.equals(DropsService.DropFlag.LOOT))
            return false;

        // Do we have telekinesis?
        ItemStack helmet = owner.getInventory().getHelmet();
        if (helmet == null)
            return false;

        if (!helmet.containsEnchantment(getEnchantment()))
            return false;

        // Do we have an empty spot in our inventory?
        if (owner.getInventory().firstEmpty() == -1) {
            SMPRPG.getService(ActionBarService.class).addActionBarComponent(owner, ActionBarService.ActionBarSource.MISC, ComponentUtils.create("FULL INVENTORY!", NamedTextColor.RED), 2);
            owner.playSound(owner.getLocation(), Sound.BLOCK_CHEST_OPEN, .25f, 2f);
            return false;
        }

        // We have telekinesis and this drop belongs to us. Attempt to add it
        ItemStack drop = item.getItemStack();
        Map<Integer, ItemStack> overflow = owner.getInventory().addItem(drop);
        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_ITEM_PICKUP, .25f, 1.75f);

        // If the overflow map is empty, then we successfully transported items!
        if (overflow.isEmpty())
            return true;

        // We have overflow items, go ahead and spawn the items back into the world but with an alternative tag so that
        // we ignore this item when it drops.
        for (Map.Entry<Integer, ItemStack> entry : overflow.entrySet()) {
            entry.getValue().editMeta(meta -> SMPRPG.getService(DropsService.class).setFlag(meta, DropsService.DropFlag.TELEKINESIS_FAIL));
            owner.getWorld().dropItemNaturally(owner.getEyeLocation(), entry.getValue());
        }

        return true;
    }

    /*
     * There may be multiple instances where we want to attempt to perform this enchant's ability on an item, so pull
     * out the behavior into a method. BlockDropItemEvent happens after ItemSpawnEvent, so we can delay the
     * telekinetic check until the next tick to try and capture it
     *
     * @param item the item to teleport into an owner's inventory if present
     * @return true if successful false otherwise
     */
    private boolean performTelekinesis(ItemStack item) {

        // Does this item have an owner?
        UUID ownerID = SMPRPG.getService(DropsService.class).getOwner(item);
        if (ownerID == null)
            return false;

        // Is the owner of the item online?
        Player owner = Bukkit.getPlayer(ownerID);
        if (owner == null)
            return false;

        // Is this item marked with the "loot" tag?
        DropsService.DropFlag flag = SMPRPG.getService(DropsService.class).getFlag(item);
        if (!flag.equals(DropsService.DropFlag.LOOT))
            return false;

        // Do we have telekinesis?
        ItemStack helmet = owner.getInventory().getHelmet();
        if (helmet == null)
            return false;

        if (!helmet.containsEnchantment(getEnchantment()))
            return false;

        // Do we have an empty spot in our inventory?
        if (owner.getInventory().firstEmpty() == -1) {
            SMPRPG.getService(ActionBarService.class).addActionBarComponent(owner, ActionBarService.ActionBarSource.MISC, ComponentUtils.create("FULL INVENTORY!", NamedTextColor.RED), 2);
            owner.playSound(owner.getLocation(), Sound.BLOCK_CHEST_OPEN, .25f, 2f);
            return false;
        }

        // We have telekinesis and this drop belongs to us. Attempt to add it
        ItemStack drop = item.clone();
        SMPRPG.getService(DropsService.class).removeAllTags(drop);
        Map<Integer, ItemStack> overflow = owner.getInventory().addItem(drop);
        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_ITEM_PICKUP, .25f, 1.75f);

        // If the overflow map is empty, then we successfully transported items!
        if (overflow.isEmpty())
            return true;

        // We have overflow items, go ahead and spawn the items back into the world but with an alternative tag so that
        // we ignore this item when it drops.
        for (Map.Entry<Integer, ItemStack> entry : overflow.entrySet()) {
            entry.getValue().editMeta(meta -> SMPRPG.getService(DropsService.class).setFlag(meta, DropsService.DropFlag.TELEKINESIS_FAIL));
            owner.getWorld().dropItemNaturally(owner.getEyeLocation(), entry.getValue());
        }

        return true;
    }

    /*
     * When an item is spawned into the world, attempt to perform telekinesis on it on the next available tick.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemSpawnWhileWearing(EntityDeathEvent event) {

        // Run through every drop this entity is going to drop. If we successfully transport the item directly to
        // a player inventory, then remove it from the drops.
        for (ItemStack item : event.getDrops().stream().toList()) {

            // Attempt telekinesis, if we were successful then remove the drop.
            boolean success = performTelekinesis(item);
            if (success)
                event.getDrops().remove(item);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockDropItemWhileWearing(BlockDropItemEvent event) {

        // Run through every drop this block is going to drop. If we successfully transport the item directly to
        // a player inventory, then remove it from the drops.
        for (Item item : event.getItems().stream().toList()) {

            // Attempt telekinesis, if we were successful then remove the drop.
            boolean success = performTelekinesis(item.getItemStack());
            // Failed on the item stack itself. Maybe try the normal item?
            if (!success)
                success = performTelekinesis(item);

            if (success) {
                event.getItems().remove(item);
                item.remove();
            }
        }
    }
}
