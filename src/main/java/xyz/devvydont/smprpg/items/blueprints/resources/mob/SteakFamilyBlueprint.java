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

public class SteakFamilyBlueprint extends CustomCompressableBlueprint implements IEdible, IConsumable {

    public static final List<CompressionRecipeMember> COMPRESSION_FLOW = List.of(
            new CompressionRecipeMember(new MaterialWrapper(Material.COOKED_BEEF)),
            new CompressionRecipeMember(new MaterialWrapper(CustomItemType.PREMIUM_STEAK)),
            new CompressionRecipeMember(new MaterialWrapper(CustomItemType.ENCHANTED_STEAK))
    );

    public SteakFamilyBlueprint(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public List<CompressionRecipeMember> getCompressionFlow() {
        return COMPRESSION_FLOW;
    }

    @Override
    public int getNutrition(ItemStack item) {
        return switch (getCustomItemType()) {
            case ENCHANTED_STEAK -> 20;
            case PREMIUM_STEAK -> 12;
            default -> 8;
        };
    }

    @Override
    public float getSaturation(ItemStack item) {
        return switch (getCustomItemType()) {
            case ENCHANTED_STEAK -> 20;
            case PREMIUM_STEAK -> 12;
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

        if (getCustomItemType().equals(CustomItemType.ENCHANTED_STEAK))
            effects.add(ConsumeEffect.applyStatusEffects(List.of(
                    new PotionEffect(PotionEffectType.STRENGTH, (int) TickTime.minutes(5), 1),
                    new PotionEffect(PotionEffectType.REGENERATION, (int) TickTime.minutes(5), 1),
                    new PotionEffect(PotionEffectType.RESISTANCE, (int) TickTime.minutes(5), 0)
            ), 1f));

        if (getCustomItemType().equals(CustomItemType.PREMIUM_STEAK))
            effects.add(ConsumeEffect.applyStatusEffects(List.of(
                    new PotionEffect(PotionEffectType.STRENGTH, (int) TickTime.minutes(1), 0),
                    new PotionEffect(PotionEffectType.REGENERATION, (int) TickTime.minutes(1), 0)
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
