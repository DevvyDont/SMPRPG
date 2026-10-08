package xyz.devvydont.smprpg.entity.fishing;

import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import xyz.devvydont.smprpg.entity.CustomEntityType;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.items.ChancedItemDrop;
import xyz.devvydont.smprpg.util.items.LootDrop;
import xyz.devvydont.smprpg.util.items.QuantityLootDrop;

import java.util.Collection;
import java.util.List;

public class EchoRay extends SeaCreature<LivingEntity> {
    /**
     * An unsafe constructor to use to allow dynamic creation of custom entities.
     * This is specifically used as a casting hack for the CustomEntityType enum in order to dynamically create
     * entities.
     *
     * @param entity     The entity that should map the T type parameter.
     * @param entityType The entity type.
     */
    public EchoRay(LivingEntity entity, CustomEntityType entityType) {
        super(entity, entityType);
    }

    @Override
    public @Nullable Collection<LootDrop> getItemDrops() {
        return List.of(
                new QuantityLootDrop(ItemService.generate(CustomItemType.ECHO_MEMBRANE), 1, 2, this),
                new ChancedItemDrop(lureScroll, 500, this),
                new ChancedItemDrop(abyssalInstinctScroll, 500, this),
                new ChancedItemDrop(impalingScroll, 500, this),
                new ChancedItemDrop(luckOfTheSeaScroll, 500, this),
                new ChancedItemDrop(treasureHunterScroll, 500, this)
        );
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.SEA_CREATURE);
        mobTypes.add(MobType.ENDER);
        mobTypes.add(MobType.AIRBORNE);

        super.setup();
    }

}
