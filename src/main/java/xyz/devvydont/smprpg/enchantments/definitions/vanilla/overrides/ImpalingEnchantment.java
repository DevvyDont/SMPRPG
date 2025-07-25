package xyz.devvydont.smprpg.enchantments.definitions.vanilla.overrides;

import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity;
import xyz.devvydont.smprpg.enchantments.EnchantmentUtil;
import xyz.devvydont.smprpg.enchantments.definitions.vanilla.VanillaEnchantment;
import xyz.devvydont.smprpg.entity.fishing.SeaCreature;
import xyz.devvydont.smprpg.events.CustomEntityDamageByEntityEvent;
import xyz.devvydont.smprpg.services.EntityService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;

public class ImpalingEnchantment extends VanillaEnchantment implements Listener {

    public ImpalingEnchantment(TypedKey<Enchantment> key) {
        super(key);
    }

    public static int getDamagePercentageMultiplier(int level) {
        return SmiteEnchantment.getPercentageIncrease(level);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return ComponentUtils.create("Impaling");
    }

    @Override
    public @NotNull Component getDescription() {
        return ComponentUtils.merge(
            ComponentUtils.create("Increases damage dealt by "),
            ComponentUtils.create("+" + getDamagePercentageMultiplier(getLevel()) + "%", NamedTextColor.GREEN),
            ComponentUtils.create(" against "),
            ComponentUtils.create("wet enemies", SeaCreature.NAME_COLOR),
            ComponentUtils.create(" and "),
            ComponentUtils.create("sea creatures", SeaCreature.NAME_COLOR)
        );
    }

    @Override
    public TagKey<ItemType> getItemTypeTag() {
        return ItemTypeTagKeys.ENCHANTABLE_WEAPON;
    }

    @Override
    public int getAnvilCost() {
        return 1;
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
    public int getSkillRequirement() {
        return 17;
    }

    @EventHandler
    public void onWaterDamage(CustomEntityDamageByEntityEvent event) {

        if (!(event.getDealer() instanceof LivingEntity dealer))
            return;

        if (!(event.getDamaged() instanceof LivingEntity damaged))
            return;

        // Check if the damaged entity is either a sea creature or wet.
        var valid = event.getDamaged().isInWater() || event.getDamaged().isInRain();
        if (SMPRPG.getService(EntityService.class).getEntityInstance(damaged) instanceof SeaCreature)
            valid = true;

        if (!valid)
            return;

        int impalingLevel = EnchantmentUtil.getHoldingEnchantLevel(getEnchantment(), EquipmentSlotGroup.MAINHAND, dealer.getEquipment());
        if (impalingLevel <= 0)
            return;

        double multiplier = 1 + getDamagePercentageMultiplier(impalingLevel) / 100.0;
        event.multiplyDamage(multiplier);
    }
}
