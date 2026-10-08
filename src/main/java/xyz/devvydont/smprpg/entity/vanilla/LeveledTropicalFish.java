package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.TropicalFish;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledTropicalFish extends VanillaEntity<TropicalFish> {

    public LeveledTropicalFish(TropicalFish entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.AQUATIC);
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }
}
