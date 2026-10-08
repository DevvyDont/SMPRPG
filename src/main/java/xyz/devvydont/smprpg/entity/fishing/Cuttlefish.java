package xyz.devvydont.smprpg.entity.fishing;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import xyz.devvydont.smprpg.entity.CustomEntityType;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.util.items.ChancedItemDrop;
import xyz.devvydont.smprpg.util.items.LootDrop;
import xyz.devvydont.smprpg.util.items.QuantityLootDrop;

import java.util.Collection;
import java.util.List;

/**
 * A common, beginner-friendly sea creature caught under the same conditions as the {@link Minnow}.
 * Its purpose is to be a reliable, generous source of ink sacs, dropping noticeably more than a vanilla squid.
 */
public class Cuttlefish extends SeaCreature<LivingEntity> {

    private static final int INK_SAC_MIN_DROP = 2;
    private static final int INK_SAC_MAX_DROP = 5;

    /**
     * An unsafe constructor to use to allow dynamic creation of custom entities.
     * This is specifically used as a casting hack for the CustomEntityType enum in order to dynamically create
     * entities.
     *
     * @param entity     The entity that should map the T type parameter.
     * @param entityType The entity type.
     */
    public Cuttlefish(LivingEntity entity, CustomEntityType entityType) {
        super(entity, entityType);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.SEA_CREATURE);
        mobTypes.add(MobType.AQUATIC);
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }

    @Override
    public @Nullable Collection<LootDrop> getItemDrops() {
        return List.of(
                new QuantityLootDrop(new ItemStack(Material.INK_SAC), INK_SAC_MIN_DROP, INK_SAC_MAX_DROP, this),
                new ChancedItemDrop(lureScroll, 1000, this),
                new ChancedItemDrop(abyssalInstinctScroll, 1000, this)
        );
    }
}
