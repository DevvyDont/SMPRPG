package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Horse;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledHorse extends VanillaEntity<Horse> {

    public LeveledHorse(Horse entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }
}
