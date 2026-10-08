package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Chicken;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledChicken extends VanillaEntity<Chicken> {

    public LeveledChicken(Chicken entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }
}
