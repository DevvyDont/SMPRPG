package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.MushroomCow;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledMushroomCow extends VanillaEntity<MushroomCow> {

    public LeveledMushroomCow(MushroomCow entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.RARE);
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }
}
