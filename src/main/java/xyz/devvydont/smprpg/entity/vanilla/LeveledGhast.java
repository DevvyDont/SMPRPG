package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledGhast extends VanillaEntity<Ghast> {

    public LeveledGhast(Ghast entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.NETHER);
        mobTypes.add(MobType.AIRBORNE);
        mobTypes.add(MobType.CUBIC);
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }
}
