package xyz.devvydont.smprpg.items.blueprints.food;

import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
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
import xyz.devvydont.smprpg.util.persistence.KeyStore;
import xyz.devvydont.smprpg.util.time.TickTime;

import java.util.List;

public class BreadboardBlueprint extends CustomItemBlueprint implements IEdible, ISellable, IModelOverridden {

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
        return 0;
    }

    @Override
    public int getWorth(ItemStack itemStack) {
        return 45 * itemStack.getAmount();
    }

    @Override
    public @NonNull Consumable getConsumableComponent(ItemStack item) {
        return Consumable.consumable()
                .consumeSeconds(1.5f)
                .addEffect(ConsumeEffect.applyStatusEffects(List.of(new PotionEffect(PotionEffectType.GLOWING, (int) TickTime.seconds(30), 0, true, true)), .5f))
                .sound(KeyStore.AUDIO_BREADBOARD_EAT)
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
        return IModelOverridden.ofMaterial(Material.IRON_TRAPDOOR);
    }

    @Override
    public boolean canAlwaysEat(ItemStack item) {
        return true;
    }

}
