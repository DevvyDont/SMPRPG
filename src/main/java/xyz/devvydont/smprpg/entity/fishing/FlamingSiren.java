package xyz.devvydont.smprpg.entity.fishing;

import org.bukkit.entity.Blaze;
import org.bukkit.entity.Bogged;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.entity.CustomEntityType;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.items.ChancedItemDrop;
import xyz.devvydont.smprpg.util.items.LootDrop;
import xyz.devvydont.smprpg.util.items.QuantityLootDrop;

import java.util.Collection;
import java.util.List;

public class FlamingSiren extends SeaCreature<Bogged> {
    /**
     * An unsafe constructor to use to allow dynamic creation of custom entities.
     * This is specifically used as a casting hack for the CustomEntityType enum in order to dynamically create
     * entities.
     *
     * @param entity     The entity that should map the T type parameter.
     * @param entityType The entity type.
     */
    public FlamingSiren(LivingEntity entity, CustomEntityType entityType) {
        super((Bogged) entity, entityType);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.SEA_CREATURE);
        mobTypes.add(MobType.NETHER);
        mobTypes.add(MobType.HUMANOID);

        super.setup();
    }

    @Override
    public @Nullable Collection<LootDrop> getItemDrops() {
        return List.of(
                new QuantityLootDrop(ItemService.generate(CustomItemType.DISSIPATING_SEA_SHELL), 1, 2, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.HYPNOTIC_EYE), 25, this),
                new ChancedItemDrop(lureScroll, 400, this),
                new ChancedItemDrop(abyssalInstinctScroll, 400, this),
                new ChancedItemDrop(impalingScroll, 400, this),
                new ChancedItemDrop(luckOfTheSeaScroll, 400, this),
                new ChancedItemDrop(treasureHunterScroll, 400, this)
        );
    }
}
