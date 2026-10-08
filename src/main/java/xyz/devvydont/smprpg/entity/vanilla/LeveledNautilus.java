package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Nautilus;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledNautilus extends VanillaEntity<Nautilus> {

    public LeveledNautilus(Nautilus entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.AQUATIC);
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }
}
