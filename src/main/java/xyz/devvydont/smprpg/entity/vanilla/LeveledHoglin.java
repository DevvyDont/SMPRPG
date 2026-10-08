package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Hoglin;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledHoglin extends VanillaEntity<Hoglin> {

    public LeveledHoglin(Hoglin entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.NETHER);
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }
}
