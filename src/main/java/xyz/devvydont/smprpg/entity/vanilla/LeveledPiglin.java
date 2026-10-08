package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Piglin;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledPiglin extends VanillaEntity<Piglin> {

    public LeveledPiglin(Piglin entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.NETHER);
        mobTypes.add(MobType.ANIMAL);
        mobTypes.add(MobType.HUMANOID);

        super.setup();
    }
}
