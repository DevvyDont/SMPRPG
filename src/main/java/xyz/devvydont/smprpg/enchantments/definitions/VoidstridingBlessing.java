package xyz.devvydont.smprpg.enchantments.definitions;

import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.enchantments.CustomEnchantment;
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;
import xyz.devvydont.smprpg.util.time.TickTime;

public class VoidstridingBlessing extends CustomEnchantment implements Listener {

    private static final NamespacedKey key = new NamespacedKey("smprpg", "voidstriding_mult");

    public VoidstridingBlessing(String id) {
        super(id);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return ComponentUtils.create("Blessing of Voidstriding", NamedTextColor.YELLOW);
    }

    @Override
    public @NotNull TextColor getEnchantColor() {
        return NamedTextColor.YELLOW;
    }

    @Override
    public @NotNull Component getDescription() {
        return ComponentUtils.merge(
                ComponentUtils.create("Instead of falling through the void, you will glide.")
        );
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
        return EnchantmentRarity.BLESSING.getWeight();
    }

    @Override
    public TagKey<ItemType> getItemTypeTag() {
        return ItemTypeTagKeys.ENCHANTABLE_FOOT_ARMOR;
    }

    @Override
    public EquipmentSlotGroup getEquipmentSlotGroup() {
        return EquipmentSlotGroup.FEET;
    }

    @Override
    public int getSkillRequirement() { return 50; }

    private void damageBoots(Player player)
    {
        ItemStack boots = player.getEquipment().getBoots();
        if (boots != null)
        {
            if (playerHasAttributeActive(player)) {
                boots.damage(1, player);
                ItemService.blueprint(boots).updateItemData(boots);
            }
        }
    }

    private boolean playerHasAttributeActive(Player player)
    {
        AttributeInstance attributeInstance = player.getAttribute(Attribute.GRAVITY);
        if (attributeInstance == null)
            return false;

        if (attributeInstance.getModifier(key) != null) {
            return true;
        }

        return false;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onTouchVoid(PlayerMoveEvent event) {

        // Check that the item is enchanted.
        var boots = event.getPlayer().getInventory().getBoots();
        Player player = event.getPlayer();
        AttributeInstance attribute = player.getAttribute(Attribute.GRAVITY);
        if (boots == null) {
            // Player has no boots, can't have enchanted boots.
            if (attribute != null)
                attribute.removeModifier(key);
            return;
        }

        int enchLevel = boots.getEnchantmentLevel(getEnchantment());
        if (enchLevel == 0) {
            if (attribute != null)
                attribute.removeModifier(key);
            return;
        }

        Location destLoc = event.getTo();
        int minHeight = player.getWorld().getMinHeight();

        if ((destLoc.getY() <= minHeight) && (player.getVelocity().getY() < 0)) {
            player.setVelocity(player.getVelocity().setY(0));
            if (attribute != null) {
                attribute.removeModifier(key);
                attribute.addTransientModifier(new AttributeModifier(key, -1.0, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
            }
        } else if (destLoc.getY() > minHeight) {
            if (attribute != null)
                attribute.removeModifier(key);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerLaunch(ServerLoadEvent event)
    {
        Bukkit.getScheduler().runTaskTimer(SMPRPG.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                damageBoots(player);
            }
        }, TickTime.INSTANTANEOUSLY, TickTime.seconds(1));

    }
}
