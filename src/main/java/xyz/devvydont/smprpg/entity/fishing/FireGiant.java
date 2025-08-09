package xyz.devvydont.smprpg.entity.fishing;

import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.entity.CustomEntityType;

public class FireGiant extends SeaCreature<IronGolem> {
    /**
     * An unsafe constructor to use to allow dynamic creation of custom entities.
     * This is specifically used as a casting hack for the CustomEntityType enum in order to dynamically create
     * entities.
     *
     * @param entity     The entity that should map the T type parameter.
     * @param entityType The entity type.
     */
    public FireGiant(LivingEntity entity, CustomEntityType entityType) {
        super((IronGolem) entity, entityType);
    }

    @Override
    public void updateAttributes() {
        super.updateAttributes();
        updateBaseAttribute(AttributeWrapper.SCALE, 3);
    }
}
