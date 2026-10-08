package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Dolphin;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledDolphin extends VanillaEntity<Dolphin> {

    public LeveledDolphin(Dolphin entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.AQUATIC);
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }
}
