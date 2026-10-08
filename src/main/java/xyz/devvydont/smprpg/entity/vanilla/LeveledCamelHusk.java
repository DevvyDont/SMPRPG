package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.CamelHusk;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledCamelHusk extends VanillaEntity<CamelHusk> {

    public LeveledCamelHusk(CamelHusk entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.UNDEAD);
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }
}
