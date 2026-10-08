package xyz.devvydont.smprpg.items.blueprints.sets.emberclad;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry;
import xyz.devvydont.smprpg.items.base.CustomAttributeItem;
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemPickaxe;
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment;
import xyz.devvydont.smprpg.items.interfaces.IHeaderDescribable;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;
import xyz.devvydont.smprpg.util.items.AbilityUtil;

import java.util.*;

public class BoilingPickaxe extends CustomAttributeItem implements IHeaderDescribable, IBreakableEquipment {

    public BoilingPickaxe(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public int getWorth(ItemStack itemStack) {
        return 48_000 * itemStack.getAmount();
    }

    @Override
    public List<Component> getHeader(ItemStack itemStack) {
        List<Component> components = new ArrayList<>();
        components.add(AbilityUtil.getAbilityComponent("Boiling Touch", true));
        components.add(ComponentUtils.create("Automatically ").append(ComponentUtils.create("smelts", NamedTextColor.RED)).append(ComponentUtils.create(" blocks broken")));
        return components;
    }

    @Override
    public void updateItemData(ItemMeta meta) {
        super.updateItemData(meta);
        meta.removeEnchant(Enchantment.SILK_TOUCH);
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiers(ItemStack item) {
        return List.of(
                new AdditiveAttributeEntry(AttributeWrapper.MINING_POWER, 4),
                new AdditiveAttributeEntry(AttributeWrapper.STRENGTH, 45),
                new MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, ItemPickaxe.PICKAXE_ATTACK_SPEED_DEBUFF),
                new AdditiveAttributeEntry(AttributeWrapper.MINING_SPEED, 750.0),
                new AdditiveAttributeEntry(AttributeWrapper.MINING_FORTUNE, 80)
        );
    }

    @Override
    public int getPowerRating() {
        return 35;
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.PICKAXE;
    }

    @Override
    public EquipmentSlotGroup getActiveSlot() {
        return EquipmentSlotGroup.MAINHAND;
    }

    @Override
    public int getMaxDurability() {
        return 50_000;
    }
}
