package xyz.devvydont.smprpg.items.blueprints.fishing;

import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.ItemRarity;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.items.interfaces.IEdible;
import xyz.devvydont.smprpg.items.interfaces.IHeaderDescribable;
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden;
import xyz.devvydont.smprpg.items.interfaces.ISellable;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.formatting.Symbols;
import xyz.devvydont.smprpg.util.persistence.PDCAdapters;
import xyz.devvydont.smprpg.util.persistence.KeyStore;
import xyz.devvydont.smprpg.util.rng.WeightedSelector;

import java.util.List;
import java.util.Map;

import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;
import static xyz.devvydont.smprpg.util.formatting.ComponentUtils.create;
import static xyz.devvydont.smprpg.util.formatting.ComponentUtils.merge;

/**
 * Represents a blueprint for a basic fish that you can fish up.
 */
public class FishBlueprint extends CustomItemBlueprint implements IModelOverridden, ISellable, IEdible {

    public FishBlueprint(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    /**
     * Get the base worth of this fish before applying a rarity modifier on it.
     * This essentially maps the COMMON worth of this item.
     * @return The base worth.
     */
    public static int getBaseWorth(CustomItemType fish) {
        return switch (fish) {
            case SALMON -> 100;
            case PUFFERFISH -> 1_500;
            case CLOWNFISH -> 12_000;
            case BLISTERFISH -> 250;
            case VOIDFIN -> 500;
            default -> 40;
        };
    }

    /**
     * Maps fish types to the amount of nutrition (hunger bars) that they replenish.
     * @param fish The fish type.
     * @return The amount of half hunger bars to replenish.
     */
    public static int getNutrition(CustomItemType fish) {
        return switch (fish) {
            case COD -> 1;
            case SALMON -> 1;
            case PUFFERFISH -> 1;
            case CLOWNFISH -> 2;
            case BLISTERFISH -> 2;
            case VOIDFIN -> 3;
            default -> 0;
        };
    }

    /**
     * Maps fish types to the amount of saturation (healing efficiency and delayed hunger decay) that they replenish.
     * @param fish The fish type.
     * @return The amount of saturation.
     */
    public static int getSaturation(CustomItemType fish) {
        return switch (fish) {
            case COD -> 1;
            case SALMON -> 1;
            case PUFFERFISH -> 1;
            case CLOWNFISH -> 2;
            case BLISTERFISH -> 3;
            case VOIDFIN -> 4;
            default -> 0;
        };
    }

    /**
     * Because we don't want fish to interact with furnaces, all custom fish should be clownfish items under the hood.
     * We can however overwrite what the fish looks like here. If we don't define a material, we are going to assume
     * we want the item to display as defined in {@link CustomItemType}.
     * @return The material texture override.
     */
    @Override
    public Material getDisplayMaterial() {
        return switch (this.getCustomItemType()) {
            case COD -> Material.COD;
            case SALMON, BLISTERFISH -> Material.SALMON;
            case PUFFERFISH, VOIDFIN -> Material.PUFFERFISH;
            case CLOWNFISH -> Material.TROPICAL_FISH;
            default -> this.getDisplayMaterial();
        };
    }


    @Override
    public int getWorth(ItemStack item) {
        return getBaseWorth(this.getCustomItemType()) * item.getAmount();
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.CONSUMABLE;
    }

    @Override
    public boolean canAlwaysEat(ItemStack item) {
        return false;
    }

    @Override
    public int getNutrition(ItemStack item) {
        return getNutrition(this.getCustomItemType());
    }

    @Override
    public float getSaturation(ItemStack item) {
        return getSaturation(this.getCustomItemType());
    }

    @Override
    public Consumable getConsumableComponent(ItemStack item) {
        var component = Consumable.consumable()
                .consumeSeconds(1.6f);
        if (this.getCustomItemType() == CustomItemType.PUFFERFISH)
            component.addEffect(ConsumeEffect.applyStatusEffects(
                    List.of(new PotionEffect(PotionEffectType.POISON, 20, 0, false, false)),
                    1.0f
            ));
        return component.build();
    }
}
