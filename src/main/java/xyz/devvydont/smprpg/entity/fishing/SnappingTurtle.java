package xyz.devvydont.smprpg.entity.fishing;

import org.bukkit.entity.LivingEntity;
import xyz.devvydont.smprpg.entity.CustomEntityType;

public class SnappingTurtle extends SeaCreature<LivingEntity> {

    /**
     * The catch quality requirement to catch this.
     */
    public static final int REQUIREMENT = 50;

    /**
     * An unsafe constructor to use to allow dynamic creation of custom entities.
     * This is specifically used as a casting hack for the CustomEntityType enum in order to dynamically create
     * entities.
     *
     * @param entity     The entity that should map the T type parameter.
     * @param entityType The entity type.
     */
    public SnappingTurtle(LivingEntity entity, CustomEntityType entityType) {
        super(entity, entityType);
    }
}
