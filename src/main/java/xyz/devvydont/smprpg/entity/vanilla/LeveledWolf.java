package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Cat;
import org.bukkit.entity.Wolf;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledWolf extends VanillaEntity<Wolf> {

    public LeveledWolf(Wolf entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }
}
