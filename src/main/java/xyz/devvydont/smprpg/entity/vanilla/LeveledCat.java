package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Cat;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledCat extends VanillaEntity<Cat> {

    public LeveledCat(Cat entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }
}
