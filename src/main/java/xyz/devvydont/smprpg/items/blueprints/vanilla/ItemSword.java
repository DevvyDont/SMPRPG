package xyz.devvydont.smprpg.items.blueprints.vanilla;

import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry;
import xyz.devvydont.smprpg.items.base.VanillaAttributeItem;
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.items.ToolsUtil;

import java.util.Collection;
import java.util.List;


public class ItemSword extends VanillaAttributeItem implements IBreakableEquipment {

    public static double getSwordDamage(Material material) {
        return switch (material) {
            case NETHERITE_SWORD -> 80;
            case DIAMOND_SWORD -> 50;
            case GOLDEN_SWORD -> 35;
            case TRIDENT, IRON_SWORD -> 30;
            case STONE_SWORD -> 20;
            case WOODEN_SWORD -> 15;
            default -> 0;
        };
    }

    public static int getSwordRating(Material material) {
        return switch (material) {
            case NETHERITE_SWORD -> ToolsUtil.NETHERITE_TOOL_POWER;
            case DIAMOND_SWORD -> ToolsUtil.DIAMOND_TOOL_POWER;
            case GOLDEN_SWORD -> ToolsUtil.GOLD_TOOL_POWER;
            case TRIDENT, IRON_SWORD -> ToolsUtil.IRON_TOOL_POWER;
            case STONE_SWORD -> ToolsUtil.STONE_TOOL_POWER;
            case WOODEN_SWORD -> ToolsUtil.WOOD_TOOL_POWER;
            default -> 1;
        };
    }

    public static double SWORD_ATTACK_SPEED_DEBUFF = -0.6;

    public ItemSword(ItemService itemService, Material material) {
        super(itemService, material);
    }

    @Override
    public ItemClassification getItemClassification() {
        if (material.equals(Material.TRIDENT))
                return ItemClassification.TRIDENT;
        return ItemClassification.SWORD;
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiers(ItemStack item) {
        return List.of(
                new AdditiveAttributeEntry(AttributeWrapper.STRENGTH, getSwordDamage(material)),
                new MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, SWORD_ATTACK_SPEED_DEBUFF)
        );
    }

    @Override
    public int getPowerRating() {
        return getSwordRating(material);
    }

    @Override
    public EquipmentSlotGroup getActiveSlot() {
        return EquipmentSlotGroup.MAINHAND;
    }

    @Override
    public int getMaxDurability() {
        return switch (material) {
            case NETHERITE_SWORD -> ToolsUtil.NETHERITE_TOOL_DURABILITY;
            case DIAMOND_SWORD -> ToolsUtil.DIAMOND_TOOL_DURABILITY;
            case GOLDEN_SWORD -> ToolsUtil.GOLD_TOOL_DURABILITY;
            case IRON_SWORD -> ToolsUtil.IRON_TOOL_DURABILITY;
            case STONE_SWORD -> ToolsUtil.STONE_TOOL_DURABILITY;
            case WOODEN_SWORD -> ToolsUtil.WOOD_TOOL_DURABILITY;
            default -> 50_000;
        };
    }
}
