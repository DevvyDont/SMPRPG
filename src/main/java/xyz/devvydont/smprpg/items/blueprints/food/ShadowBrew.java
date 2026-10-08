package xyz.devvydont.smprpg.items.blueprints.food;

import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.registry.keys.SoundEventKeys;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NonNull;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.items.interfaces.IEdible;
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden;
import xyz.devvydont.smprpg.items.interfaces.ISellable;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.time.TickTime;

import java.util.List;

public class ShadowBrew extends CustomItemBlueprint implements ISellable, IEdible, IModelOverridden {

    public ShadowBrew(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    /**
     * Determine what type of item this is.
     */
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
        return 200;
    }

    @Override
    public boolean canAlwaysEat(ItemStack item) {
        return true;
    }

    @Override
    public @NonNull Consumable getConsumableComponent(ItemStack item) {
        return Consumable.consumable()
                .addEffect(ConsumeEffect.applyStatusEffects(List.of(
                        new PotionEffect(PotionEffectType.SPEED, (int) TickTime.seconds(30), 2, false, false),
                        new PotionEffect(PotionEffectType.INVISIBILITY, (int) TickTime.seconds(30), 0, false, false)
                ), 1f))
                .sound(SoundEventKeys.ENTITY_GENERIC_DRINK)
                .consumeSeconds(1.5f)
                .build();
    }

    /**
     * Get the material that this item should display as, regardless of what it actually is internally.
     * This allows you to change how an item looks without affecting its behavior.
     *
     * @return The material this item should render as.
     */
    @Override
    public Key getDisplayKey() {
        return IModelOverridden.ofMaterial(Material.OMINOUS_BOTTLE);
    }

    /**
     * Given this item stack, how much should it be able to sell for?
     * Keep in mind that the size of the stack needs to considered as well!
     *
     * @param item The item that can be sold.
     * @return The worth of the item.
     */
    @Override
    public int getWorth(ItemStack item) {
        return 100 * item.getAmount();
    }
}
