package xyz.devvydont.smprpg.items.blueprints.food;

import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NonNull;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.items.interfaces.IEdible;
import xyz.devvydont.smprpg.items.interfaces.ISellable;
import xyz.devvydont.smprpg.services.ItemService;

import java.util.List;

public class ChiliPepper extends CustomItemBlueprint implements IEdible, ISellable {

    public ChiliPepper(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.CONSUMABLE;
    }

    @Override
    public int getNutrition(ItemStack item) {
        return 0;
    }

    @Override
    public float getSaturation(ItemStack item) {
        return 100;
    }

    @Override
    public int getWorth(ItemStack itemStack) {
        return 30 * itemStack.getAmount();
    }

    @Override
    public @NonNull Consumable getConsumableComponent(ItemStack item) {
        return Consumable.consumable()
                .consumeSeconds(.8f)
                .addEffect(ConsumeEffect.applyStatusEffects(List.of(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20*45, 0, true, true)), .2f))
                .build();
    }

    @Override
    public boolean canAlwaysEat(ItemStack item) {
        return true;
    }
}
