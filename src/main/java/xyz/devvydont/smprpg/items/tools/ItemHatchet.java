package xyz.devvydont.smprpg.items.tools;

import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry;
import xyz.devvydont.smprpg.items.base.CustomAttributeItem;
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment;
import xyz.devvydont.smprpg.services.ItemService;

import java.util.Collection;
import java.util.List;

public class ItemHatchet extends CustomAttributeItem implements IBreakableEquipment {

    public double getHatchetDamage() {
        return 1;
    }

    public double getHatchetFortune() {
        return 1;
    }

    public int getPowerRating() {
        return 0;
    }

    public static double HATCHET_ATTACK_SPEED_DEBUFF = -0.8;

    public ItemHatchet(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.HATCHET;
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiers(ItemStack item) {
        return List.of(
                new AdditiveAttributeEntry(AttributeWrapper.STRENGTH, getHatchetDamage()),
                new MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, HATCHET_ATTACK_SPEED_DEBUFF),
                new AdditiveAttributeEntry(AttributeWrapper.WOODCUTTING_FORTUNE, getHatchetFortune()),
                new AdditiveAttributeEntry(AttributeWrapper.FARMING_FORTUNE, getHatchetFortune())
        );
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
