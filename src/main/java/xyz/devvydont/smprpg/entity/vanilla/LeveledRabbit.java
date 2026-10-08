package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Rabbit;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledRabbit extends VanillaEntity<Rabbit> {

    public LeveledRabbit(Rabbit entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }
}
