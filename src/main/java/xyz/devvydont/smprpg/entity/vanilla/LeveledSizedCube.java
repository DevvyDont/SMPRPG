package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Slime;
import xyz.devvydont.smprpg.entity.EntityGlobals;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;
import xyz.devvydont.smprpg.entity.components.EntityConfiguration;

/**
 * Wrapper for slimes and magma cubes. Scale their stats according to how large they are.
 * We can allow magma cubes to use this handler as well, since {@link MagmaCube} is a child of {@link Slime} :p
 */
public class LeveledSizedCube extends VanillaEntity<Slime> {

    /**
     * The rate at which the size of the cube affects its attributes.
     */
    public final double SIZE_ATTRIBUTE_MODIFIER = 0.3;

    public LeveledSizedCube(Slime entity) {
        super(entity);
    }

    public LeveledSizedCube(MagmaCube entity) {
        super(entity);
    }

    @Override
    public EntityConfiguration getDefaultConfiguration() {

        // Get the default configuration, but depending on our size, scale it.
        // When naturally spawned, the size will only be 0, 1, and 3.
        var cfg = super.getDefaultConfiguration();
        var newHp = cfg.getBaseHealth() * getAttributeMultiplier();
        var newDmg = cfg.getBaseDamage() * getAttributeMultiplier();
        return EntityConfiguration.builder()
                .withLevel(cfg.getBaseLevel())
                .withHealth(EntityGlobals.softRoundHealth(newHp))
                .withDamage(Math.round(newDmg))
                .build();
    }

    /**
     * Works out how we should scale the entity's attributes. Lower sized slimes should not be as deadly.
     * @return A multiplier dependent on the size of the cube.
     */
    private double getAttributeMultiplier() {
        return (_entity.getSize() + 1) * SIZE_ATTRIBUTE_MODIFIER;
    }
}
