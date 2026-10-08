package xyz.devvydont.smprpg.util.particles;

import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ParticleUtil {

    public static void spawnParticlesBetweenTwoPoints(Particle particle, World world, Vector p1, Vector p2, int density) {

        Vector direction = p2.subtract(p1);
        Location origin = new Location(world, p1.getX(), p1.getY(), p1.getZ());
        for (int i = 0; i < density; i++){
            Location particleLocation = origin.clone();
            float multiplier = (float) i / density;
            particleLocation.add(direction.clone().multiply(multiplier));
            world.spawnParticle(particle, particleLocation.getX(), particleLocation.getY(), particleLocation.getZ(), 1, 0, 0, 0, 0);
        }

    }

    /**
     * Newer Minecraft versions made several particles that previously needed no data suddenly require it (e.g. on
     * 26.1.x both {@link Particle#DRAGON_BREATH} and {@link Particle#FLASH} now take a Float). Spawning such a particle
     * without data throws {@code IllegalArgumentException: missing required data class ...}.
     * <p>
     * This fills in a sensible default for whatever data type the <em>running server</em> reports for the builder's
     * particle, but only when the caller hasn't already supplied data, so explicit colors/blockdata/etc. are left
     * untouched. Driving off the runtime data type keeps this correct across version bumps that reshuffle which
     * particles need what.
     *
     * @return the same builder, for chaining.
     */
    public static ParticleBuilder withDefaultData(ParticleBuilder builder) {
        if (builder.data() != null)
            return builder;
        Object data = defaultDataFor(builder.particle());
        if (data != null)
            builder.data(data);
        return builder;
    }

    /**
     * @return A reasonable default data value for the given particle based on the running server's required data type,
     *         or {@code null} if the particle needs no data (or uses a type we don't have a default for).
     */
    public static Object defaultDataFor(Particle particle) {
        Class<?> type = particle.getDataType();
        if (type == Float.class)
            return 1.0f;
        if (type == Integer.class)
            return 0;
        if (type == Color.class)
            return Color.WHITE;
        if (type == BlockData.class)
            return Material.STONE.createBlockData();
        if (type == ItemStack.class)
            return new ItemStack(Material.STONE);
        if (type == Particle.DustOptions.class)
            return new Particle.DustOptions(Color.WHITE, 1.0f);
        if (type == Particle.DustTransition.class)
            return new Particle.DustTransition(Color.WHITE, Color.BLACK, 1.0f);
        return null;
    }

}
