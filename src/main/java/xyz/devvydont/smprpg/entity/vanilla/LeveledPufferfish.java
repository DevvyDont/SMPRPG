package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.PufferFish;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledPufferfish extends VanillaEntity<PufferFish> {

    public LeveledPufferfish(PufferFish entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.AQUATIC);
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }
}
