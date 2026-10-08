package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Parrot;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledParrot extends VanillaEntity<Parrot> {

    public LeveledParrot(Parrot entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.ANIMAL);
        mobTypes.add(MobType.AIRBORNE);

        super.setup();
    }
}
