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
import xyz.devvydont.smprpg.util.time.TickTime;

import java.util.ArrayList;
import java.util.List;

public class FleshFamilyBlueprint extends CustomItemBlueprint implements ISellable, IEdible, IConsumable {

    public FleshFamilyBlueprint(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public int getWorth(ItemStack itemStack) {
        return ItemExtensionsKt.calculateCompressedWorth(this, itemStack);
    }

    @Override
    public int getNutrition(ItemStack item) {
        return switch (getCustomItemType()) {
            case ENCHANTED_FLESH -> 12;
            case PREMIUM_FLESH -> 4;
            default -> 2;
        };
    }

    @Override
    public float getSaturation(ItemStack item) {
        return switch (getCustomItemType()) {
            case ENCHANTED_FLESH -> 12;
            case PREMIUM_FLESH -> 4;
            default -> 2;
        };
    }

    @Override
    public boolean canAlwaysEat(ItemStack item) {
        return true;
    }

    @Override
    public @NonNull Consumable getConsumableComponent(ItemStack item) {
        var effects = new ArrayList<ConsumeEffect>();

        if (getCustomItemType().equals(CustomItemType.ENCHANTED_FLESH))
            effects.add(ConsumeEffect.applyStatusEffects(List.of(
                    new PotionEffect(PotionEffectType.ABSORPTION, (int) TickTime.minutes(5), 2),
                    new PotionEffect(PotionEffectType.NIGHT_VISION, (int) TickTime.minutes(5), 0)
            ), 1f));

        if (getCustomItemType().equals(CustomItemType.PREMIUM_FLESH))
            effects.add(ConsumeEffect.applyStatusEffects(List.of(
                    new PotionEffect(PotionEffectType.ABSORPTION, (int) TickTime.minutes(5), 0)
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
