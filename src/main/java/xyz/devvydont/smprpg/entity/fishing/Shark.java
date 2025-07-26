package xyz.devvydont.smprpg.entity.fishing;

import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.entity.CustomEntityType;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.items.ChancedItemDrop;
import xyz.devvydont.smprpg.util.items.LootDrop;
import xyz.devvydont.smprpg.util.items.QuantityLootDrop;

import java.util.Collection;
import java.util.List;

public class Shark extends SeaCreature<LivingEntity> {

    public static final int RATING_REQUIREMENT = 175;

    public Shark(LivingEntity entity, CustomEntityType entityType) {
        super(entity, entityType);
    }

    @Override
    public void updateAttributes() {
        super.updateAttributes();
        updateBaseAttribute(AttributeWrapper.SCALE, 5);
    }

    @Override
    public @Nullable Collection<LootDrop> getItemDrops() {
        return List.of(
            new QuantityLootDrop(ItemService.generate(CustomItemType.SHARK_FIN), 1, 1, this),
            new ChancedItemDrop(ItemService.generate(CustomItemType.HYPNOTIC_EYE), 20, this)
        );
    }
}
