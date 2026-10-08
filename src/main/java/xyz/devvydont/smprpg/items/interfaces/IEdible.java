package xyz.devvydont.smprpg.items.interfaces;

import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.FoodProperties;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;
import xyz.devvydont.smprpg.util.formatting.Symbols;

import java.util.ArrayList;
import java.util.List;

public interface IEdible extends IConsumable {

    int getNutrition(ItemStack item);
    float getSaturation(ItemStack item);
    boolean canAlwaysEat(ItemStack item);
    float DEFAULT_EAT_SPEED = 1.6f;

    static String generateShankComponent(int fill, boolean isSaturation) {
        String comp = "";
        boolean isOdd = fill % 2 == 1;
        if (isOdd)
            fill -= 1;

        for (int i = 0; i < fill; i += 2) {
            if (isSaturation)
                comp += Symbols.SATURATION_FULL;
            else
                comp += Symbols.NUTRITION_FULL;
        }

        if (isOdd)
            if (isSaturation)
                comp += Symbols.SATURATION_HALF;
            else
                comp += Symbols.NUTRITION_HALF;
        return comp;
    }

    /**
     * Generates a section that is suitable to use for a chat/lore component for an item that implements this.
     *
     * @param item
     * @param edible The item to generate a component for.
     */
    static List<Component> generateEdibilityComponent(ItemStack item, IEdible edible) {

        var lore = new ArrayList<Component>();

        // Start with the header. Letting them know this is food and how long it takes to eat.
        lore.add(ComponentUtils.merge(
                ComponentUtils.create("When eaten: ", NamedTextColor.GOLD),
                ComponentUtils.create(String.format("(%.1fs)", edible.getConsumableComponent(item).consumeSeconds()), NamedTextColor.DARK_GRAY)
        ));

        // Now, nutrition and saturation.
        String nutritionComp = generateShankComponent(edible.getNutrition(item), false);

        // Nutrition value is used for item healing
        if (edible.getNutrition(item) != 0)
            lore.add(ComponentUtils.merge(
                ComponentUtils.create("* Healing: "),
                ComponentUtils.create("+" + edible.getNutrition(item) + "%", NamedTextColor.RED)
            ));

        // Saturation value is used for mana regain, though is a flat value, not percentage based.
        if (edible.getSaturation(item) != 0)
            lore.add(ComponentUtils.merge(
                    ComponentUtils.create("* Mana: "),
                    ComponentUtils.create("+" + Math.round(edible.getSaturation(item)), NamedTextColor.AQUA)
            ));

        // Effects if they are present.
        if (!edible.getConsumableComponent(item).consumeEffects().isEmpty()) {
            lore.add(ComponentUtils.create("Additional Effects: "));

            // Loop through every effect. Depending on the type of interface that it implements, explain what it does.
            for (var effect : edible.getConsumableComponent(item).consumeEffects())
                lore.addAll(IConsumable.generateEffectComponent(effect));
        }
        return lore;
    }

    static IEdible fromVanillaData(@NotNull FoodProperties foodProperties, @NotNull Consumable consumable) {
        return new IEdible() {
            @Override
            public int getNutrition(ItemStack item) {
                return foodProperties.nutrition();
            }

            @Override
            public float getSaturation(ItemStack item) {
                return foodProperties.saturation();
            }

            @Override
            public boolean canAlwaysEat(ItemStack item) {
                return foodProperties.canAlwaysEat();
            }

            @Override
            public @NonNull Consumable getConsumableComponent(ItemStack item) {
                return consumable;
            }
        };
    }
}
