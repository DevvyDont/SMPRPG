package xyz.devvydont.smprpg.enchantments.definitions;

import io.papermc.paper.event.entity.EntityMoveEvent;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.enchantments.CustomEnchantment;
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity;
import xyz.devvydont.smprpg.services.EnchantmentService;
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
    public @NotNull RegistryKeySet<@NotNull Enchantment> getConflictingEnchantments() {
        return RegistrySet.keySet(RegistryKey.ENCHANTMENT,
                EnchantmentService.KEEPING_BLESSING.getTypedKey(),
                EnchantmentService.MERCY_BLESSING.getTypedKey(),
                EnchantmentService.TELEKINESIS_BLESSING.getTypedKey(),
                EnchantmentService.REPLENISHING.getTypedKey()
        );
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

        return attributeInstance.getModifier(key) != null;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onTouchVoid(EntityMoveEvent event) {

        if (!event.hasChangedBlock())
            return;

        // Check that the item is enchanted.
        var equipment = event.getEntity().getEquipment();
        if (equipment == null)
            return;

        var boots = equipment.getBoots();
        AttributeInstance attribute = event.getEntity().getAttribute(Attribute.GRAVITY);
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
        int minHeight = event.getEntity().getWorld().getMinHeight();

        if ((destLoc.getY() <= minHeight) && (event.getEntity().getVelocity().getY() < 0)) {
            event.getEntity().setVelocity(event.getEntity().getVelocity().setY(0));
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
        Bukkit.getScheduler().runTaskTimer(SMPRPG.getPlugin(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                damageBoots(player);
            }
        }, TickTime.INSTANTANEOUSLY, TickTime.seconds(1));

    }
}
