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

public class ItemShovel extends VanillaAttributeItem implements IBreakableEquipment {

    public static double getShovelDamage(Material material) {
        return switch (material) {
            case NETHERITE_SHOVEL -> 30;
            case DIAMOND_SHOVEL -> 25;
            case GOLDEN_SHOVEL -> 20;
            case IRON_SHOVEL -> 15;
            case STONE_SHOVEL -> 10;
            case WOODEN_SHOVEL -> 5;
            default -> 0;
        };
    }

    public static int getShovelRating(Material material) {
        return switch (material) {
            case NETHERITE_SHOVEL -> ToolGlobals.NETHERITE_TOOL_POWER;
            case DIAMOND_SHOVEL -> ToolGlobals.DIAMOND_TOOL_POWER;
            case GOLDEN_SHOVEL -> ToolGlobals.GOLD_TOOL_POWER;
            case IRON_SHOVEL -> ToolGlobals.IRON_TOOL_POWER;
            case STONE_SHOVEL -> ToolGlobals.STONE_TOOL_POWER;
            case WOODEN_SHOVEL -> ToolGlobals.WOOD_TOOL_POWER;
            default -> 1;
        };
    }

    public static double SHOVEL_ATTACK_SPEED_DEBUFF = -0.75;

    public ItemShovel(ItemService itemService, Material material) {
        super(itemService, material);
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.TOOL;
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiers(ItemStack item) {
        return List.of(
                new AdditiveAttributeEntry(AttributeWrapper.STRENGTH, getShovelDamage(material)),
                new MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, SHOVEL_ATTACK_SPEED_DEBUFF)
        );
    }

    @Override
    public int getPowerRating() {
        return getShovelRating(material);
    }

    @Override
    public EquipmentSlotGroup getActiveSlot() {
        return EquipmentSlotGroup.MAINHAND;
    }

    @Override
    public int getMaxDurability() {
        return switch (material) {
            case NETHERITE_SHOVEL -> ToolGlobals.NETHERITE_TOOL_DURABILITY;
            case DIAMOND_SHOVEL -> ToolGlobals.DIAMOND_TOOL_DURABILITY;
            case GOLDEN_SHOVEL -> ToolGlobals.GOLD_TOOL_DURABILITY;
            case IRON_SHOVEL -> ToolGlobals.IRON_TOOL_DURABILITY;
            case STONE_SHOVEL -> ToolGlobals.STONE_TOOL_DURABILITY;
            case WOODEN_SHOVEL -> ToolGlobals.WOOD_TOOL_DURABILITY;
            default -> 50_000;
        };
    }
}
