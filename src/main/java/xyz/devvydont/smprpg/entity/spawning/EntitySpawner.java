package xyz.devvydont.smprpg.entity.spawning;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.entity.CustomEntityType;
import xyz.devvydont.smprpg.entity.base.CustomEntityInstance;
import xyz.devvydont.smprpg.entity.base.LeveledEntity;
import xyz.devvydont.smprpg.services.EntityService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;
import xyz.devvydont.smprpg.util.persistence.PDCAdapters;

import java.util.*;

public class EntitySpawner extends CustomEntityInstance<Entity> implements Listener {

    public record SpawnerEntry (SpawnableEntity entity, int weight){

        public String toPrimitive() {
            return entity.toKey() + ":" + weight;
        }

        public static SpawnerEntry fromPrimitive(String primitive) {
            // The weight is the final ':' delimited segment; everything before it is the entity key (which may
            // itself contain no colon, but we split from the right to stay robust against future key formats).
            int separator = primitive.lastIndexOf(':');
            String key = primitive.substring(0, separator);
            int weight = Integer.parseInt(primitive.substring(separator + 1));
            return new SpawnerEntry(SpawnableEntity.fromKey(key), weight);
        }
    }

    public static class SpawnerOptions {

        public SpawnerOptions() {
            this.entries = new HashMap<>();
        }

        public SpawnerOptions(List<SpawnerEntry> entries, long limit, long radius, long level) {

            this.entries = new HashMap<>();
            for (SpawnerEntry entry : entries)
                this.entries.put(entry.entity, entry);

            this.limit = limit;
            this.radius = radius;
            this.level = level;
        }

        private static NamespacedKey key = new NamespacedKey(SMPRPG.getPlugin(), "spawner-options");

        Map<SpawnableEntity, SpawnerEntry> entries;

        // How many entities can this spawner manage at once?
        long limit = 5;

        // How far can entities spawn from this spawner?
        long radius = 5;

        // What level are the entities going to be that spawn from this spawner?
        long level = 10;

        public static void setKey(NamespacedKey key) {
            SpawnerOptions.key = key;
        }

        public void clearEntities() {
            entries.clear();
        }

        public void removeEntity(SpawnableEntity entity) {
            entries.remove(entity);
        }

        public void setWeight(SpawnableEntity entity, int weight) {
            removeEntity(entity);
            entries.put(entity, new SpawnerEntry(entity, weight));
        }

        public int getWeight(SpawnableEntity entity) {
            if (entries.containsKey(entity))
                return entries.get(entity).weight;
            return 0;
        }

        public void setLimit(long limit) {
            this.limit = limit;
        }

        public void setRadius(long radius) {
            this.radius = radius;
        }

        public void setLevel(long level) {
            this.level = level;
        }

        public Collection<SpawnerEntry> getEntries() {
            return entries.values();
        }

        public long getLimit() {
            return limit;
        }

        public long getRadius() {
            return radius;
        }

        public long getLevel() {
            return level;
        }

        public void save(PersistentDataHolder holder) {
            holder.getPersistentDataContainer().set(key, PDCAdapters.SPAWNER_OPTIONS, this);
        }

        public static SpawnerOptions load(PersistentDataHolder holder) {
            return holder.getPersistentDataContainer().getOrDefault(key, PDCAdapters.SPAWNER_OPTIONS, new SpawnerOptions());
        }
    }

    public class SpawnerTask extends BukkitRunnable {

        public static final int UPDATE_PERIOD = 2 * 20;
        public static final float SCALE = .25f;

        private final ItemDisplay display;
        Matrix4f matrix = new Matrix4f().scale(SCALE);
        int tick = 0;

        public SpawnerTask(ItemDisplay display) {
            this.display = display;
        }

        @Override
        public void run() {

            if (!display.isValid()) {
                cancel();
                return;
            }

            tick++;

            float y = (float) Math.cos(Math.PI * tick);
            display.setTransformationMatrix(matrix.rotateY(((float) Math.toRadians(180)) + 0.1F).translate(0, y, 0));
            display.setInterpolationDelay(0);
            display.setInterpolationDuration(UPDATE_PERIOD);
            _entity.getWorld().spawnParticle(Particle.END_ROD, _entity.getX(), _entity.getY(), _entity.getZ(), 5, .2, .1, .2, 0);

            for (var tracked : spawned.values().stream().toList()) {

                if (!tracked.getEntity().isValid()) {
                    spawned.remove(tracked.getEntity().getUniqueId());
                    continue;
                }

                if (tracked.getEntity().getTicksLived() > 600 * 20) {
                    tracked.getEntity().remove();
                    spawned.remove(tracked.getEntity().getUniqueId());
                }

            }


            if (tick % 5 == 0) {
                attemptSpawnMob();
                attemptSpawnMob();
            }
        }
    }

    private SpawnerOptions options;
    private SpawnerTask tickTask = null;

    Map<UUID, LeveledEntity<?>> spawned = new HashMap<>();


    public EntitySpawner(Entity entity, CustomEntityType entityType) {
        super(entity, entityType);
    }

    public void loadOptions() {
        options = SpawnerOptions.load(_entity);
    }

    public void saveOptions() {
        options.save(_entity);
    }

    public SpawnerOptions getOptions() {
        return options;
    }

    /**
     * A location is a good spawnpoint if the block we have is air and the block below it is solid
     *
     * @param location
     * @return
     */
    private boolean isGoodSpawnpoint(Location location) {
        return location.getBlock().isEmpty() && location.getBlock().getRelative(BlockFace.DOWN).isSolid();
    }

    private @Nullable Location generateSpawnpointLocation() {

        long radius = getOptions().getRadius();

        double rng = radius * Math.sqrt(Math.random());
        double theta = Math.random() * 2 * Math.PI;

        double centerX = _entity.getLocation().getX();
        double centerZ = _entity.getLocation().getZ();
        double x = centerX + rng * Math.cos(theta);
        double z = centerZ + rng * Math.sin(theta);
        double y = _entity.getLocation().getY();

        Location location = new Location(_entity.getWorld(), x, y, z);
        int yoffset = 0;
        while (!isGoodSpawnpoint(location)) {

            if (yoffset > 32)
                return null;

            location = location.subtract(0, 1, 0);
            yoffset++;
        }

        return location;
    }

    public boolean attemptSpawnMob() {

        if (spawned.size() >= getOptions().getLimit())
            return false;

        if (getOptions().getEntries().isEmpty())
            return false;

        spawnMob();
        return true;
    }

    public void spawnMob() {

        // Attempt to find a good location
        Location location = null;
        int attempts = 0;
        while (location == null && attempts < 25) {
            location = generateSpawnpointLocation();
            attempts++;
        }

        // No location found, just use spawner location
        if (location == null)
            location = _entity.getLocation();

        // Roll for an entity using the weight map.
        // todo do this more optimally
        List<SpawnableEntity> types = new ArrayList<>();
        for (SpawnerEntry entry : getOptions().getEntries())
            for (int i = 0; i < entry.weight(); i++)
                types.add(entry.entity);

        if (types.isEmpty())
            return;

        Collections.shuffle(types);
        SpawnableEntity chosen = types.getFirst();

        var instance = chosen.spawn(SMPRPG.getService(EntityService.class), location, (int) getOptions().getLevel());
        if (instance == null)
            return;

        instance.getEntity().setPersistent(false);

        Vector direction = instance.getEntity().getLocation().toVector().subtract(_entity.getLocation().toVector());
        for (int i = 0; i < 25; i++){
            Location particleLocation = _entity.getLocation().clone();
            float multiplier = i / 25.0f;
            particleLocation.add(direction.clone().multiply(multiplier));
            _entity.getWorld().spawnParticle(Particle.END_ROD, particleLocation.getX(), particleLocation.getY(), particleLocation.getZ(), 1, 0, 0, 0, 0);
        }
        _entity.getWorld().playSound(instance.getEntity().getLocation(), Sound.ENTITY_CHICKEN_EGG, .5f, .5f);

        spawned.put(instance.getEntity().getUniqueId(), instance);
    }

    @Override
    public Component getPowerComponent() {
        return ComponentUtils.EMPTY;
    }

    @Override
    public Component getHealthComponent() {
        return ComponentUtils.EMPTY;
    }

    public ItemDisplay getBlockDisplay() {
        return (ItemDisplay) _entity;
    }

    // A display spawned while an admin looks up or down inherits their pitch (and yaw), leaving the
    // beacon tilted. Force a level, forward-facing orientation so spawners always render upright.
    private static final float UPRIGHT_YAW = 0f;
    private static final float UPRIGHT_PITCH = 0f;

    private void orientUpright(ItemDisplay display) {
        // Only adjust rotation; a full teleport re-applies position and is refused by the server while
        // the entity is still being added to the world (processing a section status update).
        display.setRotation(UPRIGHT_YAW, UPRIGHT_PITCH);
    }

    @Override
    public void setup() {
        super.setup();
        loadOptions();

        ItemDisplay display = getBlockDisplay();
        display.setItemStack(new ItemStack(Material.BEACON));
        orientUpright(display);

        if (tickTask != null)
            tickTask.cancel();
        tickTask = new SpawnerTask(display);
        tickTask.runTaskTimer(_plugin, 1, SpawnerTask.UPDATE_PERIOD);
    }

    @Override
    public void updateNametag() {
        super.updateNametag();
        _entity.setCustomNameVisible(false);
    }

    @Override
    public void cleanup() {

        // cleanup() can run from within an EntityRemoveFromWorldEvent (e.g. the spawner unloading with
        // its chunk). The server refuses to remove entities while it is processing section status updates,
        // so removing the children inline triggers a warning and silently fails. Defer the removal to the
        // next tick to escape that window. During a plugin disable the scheduler rejects new tasks (and no
        // chunk tick is in progress), so fall back to removing them immediately.
        var children = List.copyOf(spawned.keySet());
        spawned.clear();
        Runnable removeChildren = () -> {
            for (var uuid : children) {
                var bukkitEntity = Bukkit.getEntity(uuid);
                if (bukkitEntity != null && bukkitEntity.isValid())
                    bukkitEntity.remove();
            }
        };

        if (_plugin.isEnabled())
            Bukkit.getScheduler().runTask(_plugin, removeChildren);
        else
            removeChildren.run();

        if (tickTask != null)
            tickTask.cancel();

        tickTask = null;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onSpawnedEntityDeath(EntityRemoveFromWorldEvent event) {
        spawned.remove(event.getEntity().getUniqueId());
    }
}
