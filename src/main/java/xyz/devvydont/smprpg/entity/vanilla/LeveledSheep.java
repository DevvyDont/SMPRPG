package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Sheep;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledSheep extends VanillaEntity<Sheep> {

    public LeveledSheep(Sheep entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }
}
