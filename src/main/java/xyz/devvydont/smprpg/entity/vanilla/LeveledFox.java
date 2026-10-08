package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Fox;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledFox extends VanillaEntity<Fox> {

    public LeveledFox(Fox entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }
}
