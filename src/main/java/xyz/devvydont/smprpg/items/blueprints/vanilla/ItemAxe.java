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
import xyz.devvydont.smprpg.util.items.ToolGlobals;

import java.util.Collection;
import java.util.List;

public class ItemAxe extends VanillaAttributeItem implements IBreakableEquipment {

    public static double getAxeFortune(Material material) {
        return switch (material) {
            case NETHERITE_AXE -> ItemPickaxe.getPickaxeFortune(Material.NETHERITE_PICKAXE);
            case DIAMOND_AXE -> ItemPickaxe.getPickaxeFortune(Material.DIAMOND_PICKAXE);
            case GOLDEN_AXE -> ItemPickaxe.getPickaxeFortune(Material.GOLDEN_PICKAXE);
            case IRON_AXE -> ItemPickaxe.getPickaxeFortune(Material.IRON_PICKAXE);
            case STONE_AXE -> ItemPickaxe.getPickaxeFortune(Material.STONE_PICKAXE);
            case WOODEN_AXE -> ItemPickaxe.getPickaxeFortune(Material.WOODEN_PICKAXE);
            default -> 0;
        };
    }

    public static double getAxeDamage(Material material) {
        return switch (material) {
            case NETHERITE_AXE -> 100;
            case DIAMOND_AXE -> 65;
            case GOLDEN_AXE -> 40;
            case IRON_AXE -> 35;
            case STONE_AXE -> 30;
            case WOODEN_AXE -> 20;

            default -> 0;
        };
    }

    public static int getAxeRating(Material material) {
        return switch (material) {
            case NETHERITE_AXE -> ToolGlobals.NETHERITE_TOOL_POWER;
            case DIAMOND_AXE -> ToolGlobals.DIAMOND_TOOL_POWER;
            case GOLDEN_AXE -> ToolGlobals.GOLD_TOOL_POWER;
            case IRON_AXE -> ToolGlobals.IRON_TOOL_POWER;
            case STONE_AXE -> ToolGlobals.STONE_TOOL_POWER;
            case WOODEN_AXE -> ToolGlobals.WOOD_TOOL_POWER;
            default -> 1;
        };
    }

    public static double AXE_ATTACK_SPEED_DEBUFF = -0.8;

    public ItemAxe(ItemService itemService, Material material) {
        super(itemService, material);
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.AXE;
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiers(ItemStack item) {
        return List.of(
                new AdditiveAttributeEntry(AttributeWrapper.STRENGTH, getAxeDamage(material)),
                new MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, AXE_ATTACK_SPEED_DEBUFF),
                new AdditiveAttributeEntry(AttributeWrapper.WOODCUTTING_FORTUNE, getAxeFortune(material))
        );
    }

    @Override
    public int getPowerRating() {
        return getAxeRating(material);
    }

    @Override
    public EquipmentSlotGroup getActiveSlot() {
        return EquipmentSlotGroup.MAINHAND;
    }

    @Override
    public int getMaxDurability() {
        return switch (material) {
            case NETHERITE_AXE -> ToolGlobals.NETHERITE_TOOL_DURABILITY;
            case DIAMOND_AXE -> ToolGlobals.DIAMOND_TOOL_DURABILITY;
            case GOLDEN_AXE -> ToolGlobals.GOLD_TOOL_DURABILITY;
            case IRON_AXE -> ToolGlobals.IRON_TOOL_DURABILITY;
            case STONE_AXE -> ToolGlobals.STONE_TOOL_DURABILITY;
            case WOODEN_AXE -> ToolGlobals.WOOD_TOOL_DURABILITY;
            default -> 50_000;
        };
    }
}
