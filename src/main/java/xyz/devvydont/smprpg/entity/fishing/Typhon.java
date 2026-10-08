package xyz.devvydont.smprpg.entity.fishing;

import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.entity.CustomEntityType;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.blueprints.resources.scrolls.DynamicEnchantingScroll;
import xyz.devvydont.smprpg.services.EnchantmentService;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.items.ChancedItemDrop;
import xyz.devvydont.smprpg.util.items.LootDrop;

import java.util.Collection;
import java.util.List;

public class Typhon extends SeaCreature<Phantom> {
    /**
     * An unsafe constructor to use to allow dynamic creation of custom entities.
     * This is specifically used as a casting hack for the CustomEntityType enum in order to dynamically create
     * entities.
     *
     * @param entity     The entity that should map the T type parameter.
     * @param entityType The entity type.
     */
    public Typhon(LivingEntity entity, CustomEntityType entityType) {
        super((Phantom) entity, entityType);
    }

    public void setup() {
        mobTypes.add(MobType.SEA_CREATURE);
        mobTypes.add(MobType.NETHER);
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }

    @Override
    public void updateAttributes() {
        super.updateAttributes();
        updateBaseAttribute(AttributeWrapper.SCALE, 5);
    }

    @Override
    public @Nullable Collection<LootDrop> getItemDrops() {
        var fireAspectScroll = DynamicEnchantingScroll.getScrollWithEnchantment(EnchantmentService.FIRE_ASPECT);
        return List.of(
                new ChancedItemDrop(lureScroll, 75, this),
                new ChancedItemDrop(abyssalInstinctScroll, 75, this),
                new ChancedItemDrop(impalingScroll, 75, this),
                new ChancedItemDrop(luckOfTheSeaScroll, 75, this),
                new ChancedItemDrop(treasureHunterScroll, 75, this),
                new ChancedItemDrop(fireAspectScroll, 25, this)
        );
    }
}
