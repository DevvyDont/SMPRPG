package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.ZombieHorse;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledZombieHorse extends VanillaEntity<ZombieHorse> {

    public LeveledZombieHorse(ZombieHorse entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.UNDEAD);
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }
}
