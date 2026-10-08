package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.ZombieNautilus;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledZombieNautilus extends VanillaEntity<ZombieNautilus> {

    public LeveledZombieNautilus(ZombieNautilus entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.UNDEAD);
        mobTypes.add(MobType.AQUATIC);
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }
}
