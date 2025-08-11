package xyz.devvydont.smprpg.entity.fishing;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Ghast;
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

public class GhostKraken extends SeaCreature<Ghast> {
    /**
     * An unsafe constructor to use to allow dynamic creation of custom entities.
     * This is specifically used as a casting hack for the CustomEntityType enum in order to dynamically create
     * entities.
     *
     * @param entity     The entity that should map the T type parameter.
     * @param entityType The entity type.
     */
    public GhostKraken(LivingEntity entity, CustomEntityType entityType) {
        super((Ghast) entity, entityType);
    }

    @Override
    public void updateAttributes() {
        super.updateAttributes();
        updateBaseAttribute(AttributeWrapper.SCALE, 2);
    }

    @Override
    public void updateNametag() {
        super.updateNametag();
        _entity.customName(Component.text("Dinnerbone"));
        _entity.setCustomNameVisible(false);
    }

    @Override
    public @Nullable Collection<LootDrop> getItemDrops() {
        return List.of(
                new ChancedItemDrop(ItemService.generate(CustomItemType.SPOOKY_TENDRIL), 1, this)
        );
    }
}
