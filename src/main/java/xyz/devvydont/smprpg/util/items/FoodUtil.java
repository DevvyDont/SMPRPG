package xyz.devvydont.smprpg.util.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FoodUtil {

    public static float getEatTime(Material material) {
        return switch (material) {
            case DRIED_KELP, GLOW_BERRIES, SWEET_BERRIES -> 0.865f;
            case COOKIE -> 1.1f;
            default -> 1.61f;
        };
    }

    public static int getNutrition(Material material) {
        return switch (material) {
            case APPLE -> 4;
            case BAKED_POTATO -> 5;
            case BEETROOT -> 1;
            case BEETROOT_SOUP -> 6;
            case BREAD -> 6;
            case CHORUS_FRUIT -> 4;
            case COOKED_CHICKEN -> 6;
            case COOKED_COD -> 5;
            case COOKED_MUTTON -> 6;
            case COOKED_PORKCHOP -> 8;
            case COOKED_RABBIT -> 5;
            case COOKED_SALMON -> 6;
            case COOKIE -> 2;
            case DRIED_KELP -> 1;
            case ENCHANTED_GOLDEN_APPLE -> 10;
            case GOLDEN_APPLE -> 6;
            case HONEY_BOTTLE -> 6;
            case MELON_SLICE -> 2;
            case MUSHROOM_STEM -> 6;
            case POISONOUS_POTATO -> 2;
            case POTATO -> 1;
            case PUFFERFISH -> 1;
            case PUMPKIN_PIE -> 8;
            case RABBIT_STEW -> 10;
            case BEEF -> 3;
            case CHICKEN -> 2;
            case COD -> 2;
            case MUTTON -> 2;
            case PORKCHOP -> 3;
            case RABBIT -> 3;
            case SALMON -> 2;
            case ROTTEN_FLESH -> 4;
            case SPIDER_EYE -> 2;
            case COOKED_BEEF -> 8;
            case SUSPICIOUS_STEW -> 6;
            case SWEET_BERRIES -> 2;
            case TROPICAL_FISH -> 1;
            default -> 0;
        };
    }

    public static float getSaturation(Material material) {

        return switch (material) {
            case CARROT -> 20;
            case CHORUS_FRUIT -> 10;
            case ENCHANTED_GOLDEN_APPLE -> 50;
            case GOLDEN_APPLE -> 10;
            case GLOW_BERRIES -> 10;
            case GOLDEN_CARROT -> 80;
            default -> 0;
        };

    }

    public static boolean canEatAlways(Material material) {
        return true;
    }

    public record FoodEffectWrapper (PotionEffect effect, float probability){ }

    public static List<FoodEffectWrapper> getVanillaFoodEffects(Material material) {

        List<FoodEffectWrapper> effects = new ArrayList<>();

        switch (material) {
            case ENCHANTED_GOLDEN_APPLE:
                effects.add(new FoodEffectWrapper(new PotionEffect(PotionEffectType.REGENERATION, 30*20, 3), 1.0f));
                effects.add(new FoodEffectWrapper(new PotionEffect(PotionEffectType.ABSORPTION, 120*20, 3), 1.0f));
                effects.add(new FoodEffectWrapper(new PotionEffect(PotionEffectType.RESISTANCE, 300*20, 0), 1.0f));
                effects.add(new FoodEffectWrapper(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 300*20, 0), 1.0f));
                break;

            case GOLDEN_APPLE:
                effects.add(new FoodEffectWrapper(new PotionEffect(PotionEffectType.REGENERATION, 5*20, 1), 1.0f));
                effects.add(new FoodEffectWrapper(new PotionEffect(PotionEffectType.ABSORPTION, 120*20, 0), 1.0f));
                break;

            case POISONOUS_POTATO:
                effects.add(new FoodEffectWrapper(new PotionEffect(PotionEffectType.POISON, 5*20, 0), 0.6f));
                break;

            case PUFFERFISH:
                effects.add(new FoodEffectWrapper(new PotionEffect(PotionEffectType.WEAKNESS, 15*20, 2), 1.0f));
                effects.add(new FoodEffectWrapper(new PotionEffect(PotionEffectType.NAUSEA, 15*20, 0), 1.0f));
                effects.add(new FoodEffectWrapper(new PotionEffect(PotionEffectType.POISON, 60*20, 1), 1.0f));
                break;

            case CHICKEN:
                effects.add(new FoodEffectWrapper(new PotionEffect(PotionEffectType.WEAKNESS, 30*20, 0), 0.3f));
                break;

            case ROTTEN_FLESH:
                effects.add(new FoodEffectWrapper(new PotionEffect(PotionEffectType.WEAKNESS, 30*20, 0), 0.8f));
                break;

            case SPIDER_EYE:
                effects.add(new FoodEffectWrapper(new PotionEffect(PotionEffectType.POISON, 5*20, 0), 1f));
                break;

            case SUSPICIOUS_STEW:
                effects.add(new FoodEffectWrapper(new PotionEffect(getRandomPotionEffect(), ((int)(Math.random()*9+3)*20), 0), 1f));
                break;

        }

        return effects;

    }

    public static PotionEffectType getRandomPotionEffect() {
        PotionEffectType[] CHOICES = {PotionEffectType.REGENERATION, PotionEffectType.JUMP_BOOST,
                PotionEffectType.POISON, PotionEffectType.WITHER, PotionEffectType.WEAKNESS, PotionEffectType.BLINDNESS,
                PotionEffectType.FIRE_RESISTANCE, PotionEffectType.SATURATION, PotionEffectType.NIGHT_VISION};

        return CHOICES[(int)(Math.random()*CHOICES.length)];
    }

    @NotNull
    public static FoodComponent getVanillaFoodComponent(Material material) {

        ItemType item = material.asItemType();
        if (item == null)
            throw new IllegalArgumentException("Cannot use non-item!");

        if (!item.isEdible())
            throw new IllegalArgumentException("Cannot use non-edible item!");

        FoodComponent food = item.createItemStack().getItemMeta().getFood();
        food.setNutrition(getNutrition(material));
        food.setSaturation(getSaturation(material));
        food.setCanAlwaysEat(canEatAlways(material));
        return food;
    }

}
