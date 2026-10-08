package xyz.devvydont.smprpg.items.blueprints.food;

import io.papermc.paper.datacomponent.item.Consumable;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.items.interfaces.IEdible;
import xyz.devvydont.smprpg.items.interfaces.ISellable;
import xyz.devvydont.smprpg.services.ItemService;

public class PotatoChip extends CustomItemBlueprint implements IEdible, ISellable {

    public PotatoChip(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.CONSUMABLE;
    }

    @Override
    public int getWorth(ItemStack itemStack) {
        return 24 * itemStack.getAmount();
    }

    @Override
    public int getNutrition(ItemStack item) {
        return 6;
    }

    @Override
    public float getSaturation(ItemStack item) {
        return 0;
    }

    @Override
    public boolean canAlwaysEat(ItemStack item) {
        return true;
    }

    @Override
    public @NonNull Consumable getConsumableComponent(ItemStack item) {
        return Consumable.consumable()
                .consumeSeconds(.8f)
                .build();
    }
}
