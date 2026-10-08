package xyz.devvydont.smprpg.entity.fishing;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Phantom;
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

public class Nidhogg extends SeaCreature<Phantom> {

    /**
     * An unsafe constructor to use to allow dynamic creation of custom entities.
     * This is specifically used as a casting hack for the CustomEntityType enum in order to dynamically create
     * entities.
     *
     * @param entity     The entity that should map the T type parameter.
     * @param entityType The entity type.
     */
    public Nidhogg(LivingEntity entity, CustomEntityType entityType) {
        super((Phantom) entity, entityType);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.SEA_CREATURE);
        mobTypes.add(MobType.RARE);
        mobTypes.add(MobType.ENDER);

        super.setup();
    }

    @Override
    public @Nullable Collection<LootDrop> getItemDrops() {
        return List.of(
                new QuantityLootDrop(ItemService.generate(CustomItemType.ECHO_MEMBRANE), 1, 2, this),
                new ChancedItemDrop(lureScroll, 1, this),
                new ChancedItemDrop(abyssalInstinctScroll, 1, this),
                new ChancedItemDrop(impalingScroll, 1, this),
                new ChancedItemDrop(luckOfTheSeaScroll, 1, this),
                new ChancedItemDrop(treasureHunterScroll, 1, this)
        );
    }

    @Override
    public double getSkillExperienceMultiplier() {
        return 100;
    }

    @Override
    public void updateAttributes() {
        super.updateAttributes();
        updateBaseAttribute(AttributeWrapper.SCALE, 12);
    }
}
