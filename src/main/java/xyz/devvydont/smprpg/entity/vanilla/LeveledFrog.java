package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Frog;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledFrog extends VanillaEntity<Frog> {

    public LeveledFrog(Frog entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.AQUATIC);
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }
}
