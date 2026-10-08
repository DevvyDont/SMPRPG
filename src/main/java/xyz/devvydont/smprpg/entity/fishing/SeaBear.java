package xyz.devvydont.smprpg.entity.fishing;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PolarBear;
import org.jetbrains.annotations.Nullable;
import xyz.devvydont.smprpg.entity.CustomEntityType;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.fishing.goals.SeaBearAttackGoal;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.services.EnchantmentService;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.items.ChancedItemDrop;
import xyz.devvydont.smprpg.util.items.LootDrop;
import xyz.devvydont.smprpg.util.items.QuantityLootDrop;

import java.util.Collection;
import java.util.List;

public class SeaBear extends SeaCreature<PolarBear> {

    /**
     * The catch quality requirement to catch this.
     */
    public static final int REQUIREMENT = 300;

    /**
     * An unsafe constructor to use to allow dynamic creation of custom entities.
     * This is specifically used as a casting hack for the CustomEntityType enum in order to dynamically create
     * entities.
     *
     * @param entity     The entity that should map the T type parameter.
     * @param entityType The entity type.
     */
    public SeaBear(LivingEntity entity, CustomEntityType entityType) {
        super((PolarBear) entity, entityType);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.SEA_CREATURE);
        mobTypes.add(MobType.AQUATIC);
        mobTypes.add(MobType.ANIMAL);

        super.setup();

        var kbItem = ItemService.generate(Material.STONE);
        kbItem.addUnsafeEnchantment(EnchantmentService.KNOCKBACK.getEnchantment(), 2);
        _entity.getEquipment().setItemInMainHand(kbItem);

        var mobGoals = Bukkit.getMobGoals();
        mobGoals.removeAllGoals(_entity);
        mobGoals.addGoal(_entity, 3, new SeaBearAttackGoal(_entity));
    }

    @Override
    public @Nullable Collection<LootDrop> getItemDrops() {
        return List.of(
                new QuantityLootDrop(ItemService.generate(CustomItemType.MIDNIGHT_HIDE), 1, 2, this),
                new ChancedItemDrop(lureScroll, 600, this),
                new ChancedItemDrop(abyssalInstinctScroll, 600, this),
                new ChancedItemDrop(impalingScroll, 600, this),
                new ChancedItemDrop(luckOfTheSeaScroll, 600, this)
        );
    }
}
