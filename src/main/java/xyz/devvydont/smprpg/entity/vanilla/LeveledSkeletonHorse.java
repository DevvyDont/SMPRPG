package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.SkeletonHorse;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledSkeletonHorse extends VanillaEntity<SkeletonHorse> {

    public LeveledSkeletonHorse(SkeletonHorse entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.UNDEAD);
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }
}
