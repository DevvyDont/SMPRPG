package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.IronGolem;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledIronGolem extends VanillaEntity<IronGolem> {

    public LeveledIronGolem(IronGolem entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.CONSTRUCT);

        super.setup();
    }
}
