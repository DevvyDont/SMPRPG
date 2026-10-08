package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Bat;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledBat extends VanillaEntity<Bat> {

    public LeveledBat(Bat entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.ANIMAL);
        mobTypes.add(MobType.AIRBORNE);

        super.setup();
    }
}
