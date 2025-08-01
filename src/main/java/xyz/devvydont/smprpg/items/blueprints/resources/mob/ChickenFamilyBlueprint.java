package xyz.devvydont.smprpg.items.blueprints.resources.mob;

import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.base.CustomCompressableBlueprint;
import xyz.devvydont.smprpg.items.interfaces.IEdible;
import xyz.devvydont.smprpg.items.interfaces.IConsumable;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.crafting.CompressionRecipeMember;
import xyz.devvydont.smprpg.util.crafting.MaterialWrapper;
import xyz.devvydont.smprpg.util.time.TickTime;

import java.util.ArrayList;
import java.util.List;

public class ChickenFamilyBlueprint extends CustomCompressableBlueprint implements IEdible, IConsumable {

    public static final List<CompressionRecipeMember> COMPRESSION_FLOW = List.of(
            new CompressionRecipeMember(new MaterialWrapper(Material.COOKED_CHICKEN)),
            new CompressionRecipeMember(new MaterialWrapper(CustomItemType.PREMIUM_CHICKEN)),
            new CompressionRecipeMember(new MaterialWrapper(CustomItemType.ENCHANTED_CHICKEN))
    );

    public ChickenFamilyBlueprint(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }


    @Override
    public List<CompressionRecipeMember> getCompressionFlow() {
        return COMPRESSION_FLOW;
    }

    @Override
    public int getNutrition(ItemStack item) {
        return switch (getCustomItemType()) {
            case ENCHANTED_PORKCHOP -> 20;
            case PREMIUM_PORKCHOP -> 12;
            default -> 8;
        };
    }

    @Override
    public float getSaturation(ItemStack item) {
        return switch (getCustomItemType()) {
            case ENCHANTED_PORKCHOP -> 20;
            case PREMIUM_PORKCHOP -> 12;
            default -> 8;
        };
    }

    @Override
    public boolean canAlwaysEat(ItemStack item) {
        return true;
    }

    @Override
    public Consumable getConsumableComponent(ItemStack item) {

        var effects = new ArrayList<ConsumeEffect>();

        if (getCustomItemType().equals(CustomItemType.ENCHANTED_CHICKEN))
            effects.add(ConsumeEffect.applyStatusEffects(List.of(
                    new PotionEffect(PotionEffectType.ABSORPTION, (int) TickTime.minutes(5), 2),
                    new PotionEffect(PotionEffectType.REGENERATION, (int) TickTime.minutes(5), 0),
                    new PotionEffect(PotionEffectType.SLOW_FALLING, (int) TickTime.minutes(5), 0),
                    new PotionEffect(PotionEffectType.SPEED, (int) TickTime.minutes(5), 0)
            ), 1f));

        if (getCustomItemType().equals(CustomItemType.PREMIUM_CHICKEN))
            effects.add(ConsumeEffect.applyStatusEffects(List.of(
                    new PotionEffect(PotionEffectType.ABSORPTION, (int) TickTime.minutes(5), 0),
                    new PotionEffect(PotionEffectType.SLOW_FALLING, (int) TickTime.minutes(2), 0)
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
