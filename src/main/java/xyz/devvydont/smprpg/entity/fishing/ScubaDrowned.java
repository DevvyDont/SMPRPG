package xyz.devvydont.smprpg.entity.fishing;

import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.entity.CustomEntityType;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.items.ChancedItemDrop;
import xyz.devvydont.smprpg.util.items.LootDrop;

import java.util.Collection;
import java.util.List;

public class ScubaDrowned extends SeaCreature<LivingEntity> {

    public static final int RATING_REQUIREMENT = 450;

    public ScubaDrowned(LivingEntity entity, CustomEntityType entityType) {
        super(entity, entityType);
    }

    @Override
    public void updateAttributes() {
        super.updateAttributes();
        updateBaseAttribute(AttributeWrapper.KNOCKBACK_RESISTANCE, 1.0f);
    }

    @Override
    public @Nullable Collection<LootDrop> getItemDrops() {
        return List.of(

        );
    }
}
