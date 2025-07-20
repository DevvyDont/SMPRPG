package xyz.devvydont.smprpg.items.interfaces;

import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.FoodProperties;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;
import xyz.devvydont.smprpg.util.formatting.MinecraftStringUtils;

import java.util.ArrayList;
import java.util.List;

public interface IEdible extends IConsumable {

    int getNutrition(ItemStack item);
    float getSaturation(ItemStack item);
    boolean canAlwaysEat(ItemStack item);

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
        if (edible.getNutrition(item) != 0)
            lore.add(ComponentUtils.merge(
                ComponentUtils.create("* Nutrition: "),
                ComponentUtils.create("+" + edible.getNutrition(item), NamedTextColor.GOLD)
            ));

        if (edible.getSaturation(item) != 0)
            lore.add(ComponentUtils.merge(
                ComponentUtils.create("* Saturation: "),
                ComponentUtils.create("+" + MinecraftStringUtils.formatFloat(edible.getSaturation(item)), NamedTextColor.YELLOW)
            ));

        // Effects if they are present.
        if (!edible.getConsumableComponent(item).consumeEffects().isEmpty()) {
            lore.add(ComponentUtils.create("Additional Effects: "));

            // Loop through every effect. Depending on the type of interface that it implements, explain what it does.
            for (var effect : edible.getConsumableComponent(item).consumeEffects())
                lore.addAll(IConsumable.generateEffectComponent(effect));
        }

        lore.add(ComponentUtils.create(edible.canAlwaysEat(item) ? "Can be eaten any time" : "Can only eat when hungry", NamedTextColor.DARK_GRAY));
        return lore;
    }

    static IEdible fromVanillaData(FoodProperties foodProperties, Consumable consumable) {
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
            public Consumable getConsumableComponent(ItemStack item) {
                return consumable;
            }
        };
    }
}
