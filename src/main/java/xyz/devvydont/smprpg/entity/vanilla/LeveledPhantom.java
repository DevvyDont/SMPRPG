package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Phantom;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledPhantom extends VanillaEntity<Phantom> {

    public LeveledPhantom(Phantom entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.ENDER);
        mobTypes.add(MobType.AIRBORNE);

        super.setup();
    }
}
