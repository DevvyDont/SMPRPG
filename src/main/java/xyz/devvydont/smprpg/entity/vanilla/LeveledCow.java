package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Cow;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledCow extends VanillaEntity<Cow> {

    public LeveledCow(Cow entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }
}
