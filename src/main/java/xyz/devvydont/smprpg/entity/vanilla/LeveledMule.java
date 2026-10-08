package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Mule;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledMule extends VanillaEntity<Mule> {

    public LeveledMule(Mule entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }
}
