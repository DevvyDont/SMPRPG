package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Goat;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledGoat extends VanillaEntity<Goat> {

    public LeveledGoat(Goat entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }
}
