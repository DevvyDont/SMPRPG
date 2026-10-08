package xyz.devvydont.smprpg.entity.fishing;

import org.bukkit.entity.LivingEntity;
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

public class SeaHag extends SeaCreature<LivingEntity> {

    public static final int RATING_REQUIREMENT = 175;

    public SeaHag(LivingEntity entity, CustomEntityType entityType) {
        super(entity, entityType);
    }

    public void setup() {
        mobTypes.add(MobType.SEA_CREATURE);
        mobTypes.add(MobType.AQUATIC);
        mobTypes.add(MobType.HUMANOID);

        super.setup();
    }

    @Override
    public @Nullable Collection<LootDrop> getItemDrops() {
        return List.of(
            new QuantityLootDrop(ItemService.generate(CustomItemType.HEXED_CLOTH), 1, 2, this),
                new ChancedItemDrop(lureScroll, 700, this),
                new ChancedItemDrop(abyssalInstinctScroll, 700, this),
                new ChancedItemDrop(impalingScroll, 700, this)
        );
    }
}
