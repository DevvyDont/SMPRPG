package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.PolarBear;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledPolarBear extends VanillaEntity<PolarBear> {

    public LeveledPolarBear(PolarBear entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }
}
