package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Entity;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledSnowGolem extends VanillaEntity<Entity> {

    public LeveledSnowGolem(Entity entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.CONSTRUCT);
        mobTypes.add(MobType.ELEMENTAL);

        super.setup();
    }
}
