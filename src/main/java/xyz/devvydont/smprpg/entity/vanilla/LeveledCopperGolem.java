package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.CopperGolem;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledCopperGolem extends VanillaEntity<CopperGolem> {

    public LeveledCopperGolem(CopperGolem entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.CONSTRUCT);

        super.setup();
    }
}
