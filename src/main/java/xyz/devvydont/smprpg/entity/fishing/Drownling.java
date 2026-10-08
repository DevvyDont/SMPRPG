package xyz.devvydont.smprpg.entity.fishing;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.entity.CustomEntityType;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.util.items.ChancedItemDrop;
import xyz.devvydont.smprpg.util.items.LootDrop;
import xyz.devvydont.smprpg.util.items.QuantityLootDrop;

import java.util.Collection;
import java.util.List;

/**
 * A common, beginner-friendly sea creature caught under the same conditions as the {@link Minnow}.
 * A lesser cousin of the vanilla drowned that serves as a reliable, generous source of nautilus shells.
 */
public class Drownling extends SeaCreature<LivingEntity> {

    private static final int NAUTILUS_MIN_DROP = 1;
    private static final int NAUTILUS_MAX_DROP = 3;

    /**
     * Scaled down relative to a vanilla drowned to sell the "drownling" (lesser drowned) identity.
     */
    private static final float SIZE_SCALE = 0.6f;

    /**
     * An unsafe constructor to use to allow dynamic creation of custom entities.
     * This is specifically used as a casting hack for the CustomEntityType enum in order to dynamically create
     * entities.
     *
     * @param entity     The entity that should map the T type parameter.
     * @param entityType The entity type.
     */
    public Drownling(LivingEntity entity, CustomEntityType entityType) {
        super(entity, entityType);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.SEA_CREATURE);
        mobTypes.add(MobType.AQUATIC);
        mobTypes.add(MobType.HUMANOID);

        super.setup();
    }

    @Override
    public void updateAttributes() {
        super.updateAttributes();
        updateBaseAttribute(AttributeWrapper.SCALE, SIZE_SCALE);
    }

    @Override
    public @Nullable Collection<LootDrop> getItemDrops() {
        return List.of(
                new QuantityLootDrop(new ItemStack(Material.NAUTILUS_SHELL), NAUTILUS_MIN_DROP, NAUTILUS_MAX_DROP, this),
                new ChancedItemDrop(lureScroll, 1000, this),
                new ChancedItemDrop(abyssalInstinctScroll, 1000, this)
        );
    }
}
