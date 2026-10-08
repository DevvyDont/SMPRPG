package xyz.devvydont.smprpg.entity.spawning;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.devvydont.smprpg.entity.CustomEntityType;
import xyz.devvydont.smprpg.entity.base.LeveledEntity;
import xyz.devvydont.smprpg.gui.base.IMenuDisplayable;
import xyz.devvydont.smprpg.services.EntityService;
import xyz.devvydont.smprpg.util.formatting.MinecraftStringUtils;

/**
 * Represents something an {@link EntitySpawner} is able to spawn. This is either one of our plugin's
 * {@link CustomEntityType custom entities}, or a plain vanilla {@link EntityType}. Wrapping both behind a single
 * value object lets a spawner mix custom and vanilla mobs in one weighted pool without the rest of the spawner
 * code caring which kind it is dealing with.
 *
 * @param custom      The custom entity this spawnable represents, or {@code null} if it is a vanilla entity.
 * @param vanillaType The underlying bukkit entity type. For custom entities this mirrors
 *                    {@link CustomEntityType#getType()}; for vanilla entities it is the entity itself.
 */
public record SpawnableEntity(@Nullable CustomEntityType custom, @NotNull EntityType vanillaType) implements IMenuDisplayable {

    // Marks a serialized key as a vanilla entity. Custom keys are enum names (which never contain '@'), so this
    // prefix unambiguously distinguishes the two without colliding with any custom entity name.
    private static final String VANILLA_KEY_PREFIX = "vanilla@";

    // Shown for vanilla entities that have no matching spawn egg material.
    private static final Material DEFAULT_VANILLA_DISPLAY = Material.EGG;

    /**
     * Wraps one of our custom entity types.
     */
    public static SpawnableEntity of(CustomEntityType type) {
        return new SpawnableEntity(type, type.getType());
    }

    /**
     * Wraps a vanilla entity type.
     */
    public static SpawnableEntity of(EntityType type) {
        return new SpawnableEntity(null, type);
    }

    /**
     * @return True if this represents one of our custom entities, false if it is a plain vanilla entity.
     */
    public boolean isCustom() {
        return custom != null;
    }

    /**
     * @return A human-readable name for this spawnable.
     */
    public String displayName() {
        return custom != null ? custom.getName() : MinecraftStringUtils.getTitledString(vanillaType.name());
    }

    /**
     * Serializes this spawnable to a stable string key for persistence.
     */
    public String toKey() {
        return custom != null ? custom.name() : VANILLA_KEY_PREFIX + vanillaType.name();
    }

    /**
     * Reconstructs a spawnable from a key produced by {@link #toKey()}.
     */
    public static SpawnableEntity fromKey(String key) {
        if (key.startsWith(VANILLA_KEY_PREFIX))
            return of(EntityType.valueOf(key.substring(VANILLA_KEY_PREFIX.length())));
        return of(CustomEntityType.valueOf(key));
    }

    @Override
    public @NotNull Material getDisplayMaterial() {
        if (custom != null)
            return custom.getDisplayMaterial();

        Material spawnEgg = Material.matchMaterial(vanillaType.name() + "_SPAWN_EGG");
        return spawnEgg != null ? spawnEgg : DEFAULT_VANILLA_DISPLAY;
    }

    /**
     * Spawns this entity into the world at the desired level.
     *
     * @param entityService The entity service used to wrap and track the spawned entity.
     * @param location      Where to spawn the entity.
     * @param level         The level to spawn the entity at. Values of 0 or below leave the entity at its natural level.
     * @return The leveled wrapper of the spawned entity, or {@code null} if it could not be spawned.
     */
    public @Nullable LeveledEntity<?> spawn(EntityService entityService, Location location, int level) {

        if (custom != null) {
            var instance = entityService.spawnCustomEntity(custom, location);
            if (instance != null && level > 0)
                instance.setLevel(level);
            return instance;
        }

        // Vanilla entities are spawned directly and then wrapped by the entity service so they gain levels/stats.
        Entity entity = location.getWorld().spawnEntity(location, vanillaType, CreatureSpawnEvent.SpawnReason.CUSTOM);
        if (!(entity instanceof LivingEntity living)) {
            entity.remove();
            return null;
        }

        var leveled = entityService.getEntityInstance(living);
        leveled.setup();
        if (level > 0)
            leveled.setLevel(level);
        return leveled;
    }
}
