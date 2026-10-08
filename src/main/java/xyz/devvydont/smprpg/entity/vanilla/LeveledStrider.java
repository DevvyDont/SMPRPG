package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Strider;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledStrider extends VanillaEntity<Strider> {

    public LeveledStrider(Strider entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.NETHER);
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }
}
