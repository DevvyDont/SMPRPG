package xyz.devvydont.smprpg.items.blueprints.food;

import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.items.interfaces.IEdible;
import xyz.devvydont.smprpg.items.interfaces.ISellable;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.time.TickTime;

import java.util.List;

public class BreadboardBlueprint extends CustomItemBlueprint implements IEdible, ISellable {

    public BreadboardBlueprint(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.CONSUMABLE;
    }

    @Override
    public int getNutrition(ItemStack item) {
        return 5;
    }

    @Override
    public float getSaturation(ItemStack item) {
        return 2;
    }

    @Override
    public int getWorth(ItemStack itemStack) {
        return 45 * itemStack.getAmount();
    }

    @Override
    public Consumable getConsumableComponent(ItemStack item) {
        return Consumable.consumable()
                .consumeSeconds(1.5f)
                .addEffect(ConsumeEffect.applyStatusEffects(List.of(new PotionEffect(PotionEffectType.GLOWING, (int) TickTime.seconds(30), 0, true, true)), .5f))
                .build();
    }

    @Override
    public boolean canAlwaysEat(ItemStack item) {
        return false;
    }

}
