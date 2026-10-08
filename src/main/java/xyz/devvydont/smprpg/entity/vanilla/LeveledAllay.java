package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Allay;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledAllay extends VanillaEntity<Allay> {

    public LeveledAllay(Allay entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.FAE);
        mobTypes.add(MobType.AIRBORNE);

        super.setup();
    }
}
