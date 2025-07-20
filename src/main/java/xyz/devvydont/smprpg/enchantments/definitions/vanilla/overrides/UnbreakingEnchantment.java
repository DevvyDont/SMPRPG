package xyz.devvydont.smprpg.enchantments.definitions.vanilla.overrides;

import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity;
import xyz.devvydont.smprpg.enchantments.definitions.vanilla.VanillaEnchantment;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;

import java.util.Random;

public class UnbreakingEnchantment extends VanillaEnchantment implements Listener {

    private final Random random = new Random();

    public UnbreakingEnchantment(TypedKey<Enchantment> key) {
        super(key);
    }

    public static int getDurabilityIgnoreChance(int level) {
        return (int) ((1-(1.0/(level+1)))*100);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return ComponentUtils.create("Unbreaking");
    }

    @Override
    public @NotNull Component getDescription() {
        return ComponentUtils.merge(
            ComponentUtils.create("Durability is ignored "),
            ComponentUtils.create(getDurabilityIgnoreChance(getLevel()) + "%", NamedTextColor.GREEN),
            ComponentUtils.create(" of the time when used")
        );
    }

    @Override
    public TagKey<ItemType> getItemTypeTag() {
        return ItemTypeTagKeys.ENCHANTABLE_DURABILITY;
    }

    @Override
    public int getAnvilCost() {
        return 1;
    }

    @Override
    public int getMaxLevel() {
        return 10;
    }

    @Override
    public int getWeight() {
        return EnchantmentRarity.COMMON.getWeight();
    }

    @Override
    public EquipmentSlotGroup getEquipmentSlotGroup() {
        return EquipmentSlotGroup.ANY;
    }

    @Override
    public int getSkillRequirement() {
        return 0;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void __onUnbreakingProc(PlayerItemDamageEvent event) {

        var chanceToIgnore = getDurabilityIgnoreChance(event.getItem().getEnchantmentLevel(Enchantment.UNBREAKING));
        if (chanceToIgnore <= 0)
            return;

        if (random.nextInt(100) < chanceToIgnore)
            event.setCancelled(true);
    }
}
