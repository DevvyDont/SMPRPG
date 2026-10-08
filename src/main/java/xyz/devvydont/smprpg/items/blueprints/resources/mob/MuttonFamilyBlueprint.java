package xyz.devvydont.smprpg.items.blueprints.resources.mob;

import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NonNull;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.items.interfaces.IConsumable;
import xyz.devvydont.smprpg.items.interfaces.IEdible;
import xyz.devvydont.smprpg.items.interfaces.ISellable;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.extensions.ItemExtensionsKt;

import java.util.ArrayList;
import java.util.List;

public class MuttonFamilyBlueprint extends CustomItemBlueprint implements ISellable, IEdible, IConsumable {

    public MuttonFamilyBlueprint(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public int getWorth(ItemStack itemStack) {
        return ItemExtensionsKt.calculateCompressedWorth(this, itemStack);
    }

    @Override
    public int getNutrition(ItemStack item) {
        return switch (getCustomItemType()) {
            case ENCHANTED_MUTTON -> 20;
            case PREMIUM_MUTTON -> 11;
            default -> 7;
        };
    }

    @Override
    public float getSaturation(ItemStack item) {
        return switch (getCustomItemType()) {
            case ENCHANTED_PORKCHOP -> 20;
            case PREMIUM_PORKCHOP -> 9;
            default -> 6;
        };
    }

    @Override
    public boolean canAlwaysEat(ItemStack item) {
        return true;
    }

    @Override
    public @NonNull Consumable getConsumableComponent(ItemStack item) {
        var effects = new ArrayList<ConsumeEffect>();

        if (getCustomItemType().equals(CustomItemType.ENCHANTED_MUTTON))
            effects.add(ConsumeEffect.applyStatusEffects(List.of(
                    new PotionEffect(PotionEffectType.ABSORPTION, (int) xyz.devvydont.smprpg.util.time.TickTime.minutes(5), 3),
                    new PotionEffect(PotionEffectType.REGENERATION, (int) xyz.devvydont.smprpg.util.time.TickTime.minutes(5), 0),
                    new PotionEffect(PotionEffectType.RESISTANCE, (int) xyz.devvydont.smprpg.util.time.TickTime.minutes(5), 1)
            ), 1f));

        if (getCustomItemType().equals(CustomItemType.PREMIUM_MUTTON))
            effects.add(ConsumeEffect.applyStatusEffects(List.of(
                    new PotionEffect(PotionEffectType.ABSORPTION, 20 * 60, 1),
                    new PotionEffect(PotionEffectType.RESISTANCE, 20 * 60, 0)
            ), 1f));

        return Consumable.consumable()
                .consumeSeconds(4)
                .addEffects(effects)
                .build();
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.CONSUMABLE;
    }
}
