package xyz.devvydont.smprpg.items.blueprints.potion;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.items.interfaces.ExperienceThrowable;
import xyz.devvydont.smprpg.items.interfaces.ISellable;
import xyz.devvydont.smprpg.services.ItemService;

public class ExperienceBottle extends CustomItemBlueprint implements ExperienceThrowable, ISellable {

    @Nullable
    public static ItemStack getCraftingComponent(CustomItemType expBottleType) {
        return switch (expBottleType) {
            case EXPERIENCE_BOTTLE -> ItemService.generate(Material.LAPIS_LAZULI);
            case LARGE_EXPERIENCE_BOTTLE -> ItemService.generate(Material.LAPIS_BLOCK);
            case HEFTY_EXPERIENCE_BOTTLE -> ItemService.generate(CustomItemType.ENCHANTED_LAPIS);
            case GIGANTIC_EXPERIENCE_BOTTLE -> ItemService.generate(CustomItemType.ENCHANTED_LAPIS_BLOCK);
            case COLOSSAL_EXPERIENCE_BOTTLE -> ItemService.generate(CustomItemType.LAPIS_SINGULARITY);
            default -> null;
        };
    }

    public static int getExperienceForBottle(CustomItemType bottleType) {
        return switch (bottleType) {
            case EXPERIENCE_BOTTLE -> 20;
            case LARGE_EXPERIENCE_BOTTLE -> 200;
            case HEFTY_EXPERIENCE_BOTTLE -> 2_000;
            case GIGANTIC_EXPERIENCE_BOTTLE -> 20_000;
            case COLOSSAL_EXPERIENCE_BOTTLE -> 200_000;
            default -> 0;
        };
    }

    public ExperienceBottle(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public void updateItemData(ItemMeta meta) {
        super.updateItemData(meta);
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.CONSUMABLE;
    }

    @Override
    public int getExperience() {
        return getExperienceForBottle(getCustomItemType());
    }

    @Override
    public int getWorth(ItemStack itemStack) {
        return getExperienceForBottle(getCustomItemType()) * itemStack.getAmount();
    }
}
