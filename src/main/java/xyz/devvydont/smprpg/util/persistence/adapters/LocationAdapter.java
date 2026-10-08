package xyz.devvydont.smprpg.util.persistence.adapters;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Allows you to cleanly store a {@link Location} on a PDC as a nested container.
 * The world is stored by its UUID so that the exact world (overworld, nether, end, ...) is preserved.
 */
public class LocationAdapter implements PersistentDataType<PersistentDataContainer, Location> {

    private static final UUIDAdapter WORLD_UUID = new UUIDAdapter();

    private static final NamespacedKey WORLD_KEY = new NamespacedKey("smprpg", "world");
    private static final NamespacedKey X_KEY = new NamespacedKey("smprpg", "x");
    private static final NamespacedKey Y_KEY = new NamespacedKey("smprpg", "y");
    private static final NamespacedKey Z_KEY = new NamespacedKey("smprpg", "z");
    private static final NamespacedKey YAW_KEY = new NamespacedKey("smprpg", "yaw");
    private static final NamespacedKey PITCH_KEY = new NamespacedKey("smprpg", "pitch");

    @Override
    public @NotNull Class<PersistentDataContainer> getPrimitiveType() {
        return PersistentDataContainer.class;
    }

    @Override
    public @NotNull Class<Location> getComplexType() {
        return Location.class;
    }

    @Override
    public @NotNull PersistentDataContainer toPrimitive(Location complex, @NotNull PersistentDataAdapterContext context) {
        PersistentDataContainer container = context.newPersistentDataContainer();
        World world = complex.getWorld();
        if (world != null)
            container.set(WORLD_KEY, WORLD_UUID, world.getUID());
        container.set(X_KEY, PersistentDataType.DOUBLE, complex.getX());
        container.set(Y_KEY, PersistentDataType.DOUBLE, complex.getY());
        container.set(Z_KEY, PersistentDataType.DOUBLE, complex.getZ());
        container.set(YAW_KEY, PersistentDataType.FLOAT, complex.getYaw());
        container.set(PITCH_KEY, PersistentDataType.FLOAT, complex.getPitch());
        return container;
    }

    @Override
    public @NotNull Location fromPrimitive(@NotNull PersistentDataContainer primitive, @NotNull PersistentDataAdapterContext context) {
        UUID worldId = primitive.get(WORLD_KEY, WORLD_UUID);
        // The world may be null if it was never set or is no longer loaded. Location tolerates a null world,
        // and consumers are expected to validate before using it.
        World world = worldId == null ? null : Bukkit.getWorld(worldId);
        double x = primitive.getOrDefault(X_KEY, PersistentDataType.DOUBLE, 0.0);
        double y = primitive.getOrDefault(Y_KEY, PersistentDataType.DOUBLE, 0.0);
        double z = primitive.getOrDefault(Z_KEY, PersistentDataType.DOUBLE, 0.0);
        float yaw = primitive.getOrDefault(YAW_KEY, PersistentDataType.FLOAT, 0.0f);
        float pitch = primitive.getOrDefault(PITCH_KEY, PersistentDataType.FLOAT, 0.0f);
        return new Location(world, x, y, z, yaw, pitch);
    }
}
