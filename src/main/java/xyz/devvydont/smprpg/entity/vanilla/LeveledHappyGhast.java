package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Ghast;
import org.bukkit.entity.HappyGhast;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledHappyGhast extends VanillaEntity<HappyGhast> {

    public LeveledHappyGhast(HappyGhast entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.AIRBORNE);
        mobTypes.add(MobType.CUBIC);
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }
}
