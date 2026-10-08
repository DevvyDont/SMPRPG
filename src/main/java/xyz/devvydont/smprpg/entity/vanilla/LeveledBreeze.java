package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Breeze;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledBreeze extends VanillaEntity<Breeze> {

    public LeveledBreeze(Breeze entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.AIRBORNE);
        mobTypes.add(MobType.HOLY);
        mobTypes.add(MobType.ELEMENTAL);

        super.setup();
    }
}
