package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Entity;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledWaterAnimal extends VanillaEntity<Entity> {

    public LeveledWaterAnimal(Entity entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.AQUATIC);
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }
}
