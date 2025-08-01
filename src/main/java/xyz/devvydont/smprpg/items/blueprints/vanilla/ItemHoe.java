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

public class ItemHoe extends VanillaAttributeItem implements IBreakableEquipment {

    public static double getHoeFortune(Material material) {
        return switch (material) {
            case NETHERITE_HOE -> ItemPickaxe.getPickaxeFortune(Material.NETHERITE_PICKAXE);
            case DIAMOND_HOE -> ItemPickaxe.getPickaxeFortune(Material.DIAMOND_PICKAXE);
            case GOLDEN_HOE -> ItemPickaxe.getPickaxeFortune(Material.GOLDEN_PICKAXE);
            case IRON_HOE -> ItemPickaxe.getPickaxeFortune(Material.IRON_PICKAXE);
            case STONE_HOE -> ItemPickaxe.getPickaxeFortune(Material.STONE_PICKAXE);
            case WOODEN_HOE -> ItemPickaxe.getPickaxeFortune(Material.WOODEN_PICKAXE);
            default -> 0;
        };
    }

    public static double getHoeDamage(Material material) {
        return switch (material) {
            case NETHERITE_HOE -> 20;
            case DIAMOND_HOE -> 16;
            case GOLDEN_HOE -> 12;
            case IRON_HOE -> 10;
            case STONE_HOE -> 5;
            case WOODEN_HOE -> 3;
            default -> 0;
        };
    }

    public static double getHoeAttackSpeedDebuff(Material material) {
        return switch (material) {
            case NETHERITE_HOE -> -0.05;
            case DIAMOND_HOE -> -0.15;
            case IRON_HOE -> -0.20;
            case STONE_HOE -> -0.25;
            case WOODEN_HOE, GOLDEN_HOE -> -0.35;
            default -> 0;
        };
    }

    public static int getHoeRating(Material material) {
        return switch (material) {
            case NETHERITE_HOE -> ToolsUtil.NETHERITE_TOOL_POWER;
            case DIAMOND_HOE -> ToolsUtil.DIAMOND_TOOL_POWER;
            case GOLDEN_HOE -> ToolsUtil.GOLD_TOOL_POWER;
            case IRON_HOE -> ToolsUtil.IRON_TOOL_POWER;
            case STONE_HOE -> ToolsUtil.STONE_TOOL_POWER;
            case WOODEN_HOE -> ToolsUtil.WOOD_TOOL_POWER;
            default -> 1;
        };
    }

    public ItemHoe(ItemService itemService, Material material) {
        super(itemService, material);
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.HOE;
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiers(ItemStack item) {
        return List.of(
                new AdditiveAttributeEntry(AttributeWrapper.STRENGTH, getHoeDamage(material)),
                new MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, getHoeAttackSpeedDebuff(material)),
                new AdditiveAttributeEntry(AttributeWrapper.FARMING_FORTUNE, getHoeFortune(material))
        );
    }

    @Override
    public int getPowerRating() {
        return getHoeRating(material);
    }

    @Override
    public EquipmentSlotGroup getActiveSlot() {
        return EquipmentSlotGroup.MAINHAND;
    }


    @Override
    public int getMaxDurability() {
        return switch (material) {
            case NETHERITE_HOE -> ToolsUtil.NETHERITE_TOOL_DURABILITY;
            case DIAMOND_HOE -> ToolsUtil.DIAMOND_TOOL_DURABILITY;
            case GOLDEN_HOE -> ToolsUtil.GOLD_TOOL_DURABILITY;
            case IRON_HOE -> ToolsUtil.IRON_TOOL_DURABILITY;
            case STONE_HOE -> ToolsUtil.STONE_TOOL_DURABILITY;
            case WOODEN_HOE -> ToolsUtil.WOOD_TOOL_DURABILITY;
            default -> 50_000;
        };
    }
    
}
