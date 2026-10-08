package xyz.devvydont.smprpg.enchantments.recipe;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class EnchantmentRecipe implements Keyed {
    int power;
    NamespacedKey key;
    Set<ItemStack> ingredients;

    public EnchantmentRecipe(@NotNull NamespacedKey key, int power, @NotNull ItemStack... ingredients) {
        this.power = power;
        this.key = key;
        this.ingredients = ImmutableSet.copyOf(ingredients);
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }

    public Set<ItemStack> getIngredients() {
        return ingredients;
    }

    public int getPower() {
        return power;
    }
}