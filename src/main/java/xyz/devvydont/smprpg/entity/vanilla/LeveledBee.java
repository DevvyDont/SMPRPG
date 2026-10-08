package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Bee;
import org.bukkit.entity.Zombie;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledBee extends VanillaEntity<Bee> {

    public LeveledBee(Bee entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.ARTHROPOD);

        super.setup();
    }
}
