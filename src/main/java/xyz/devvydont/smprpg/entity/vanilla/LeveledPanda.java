package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Panda;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledPanda extends VanillaEntity<Panda> {

    public LeveledPanda(Panda entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }
}
