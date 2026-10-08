package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Vex;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledVex extends VanillaEntity<Vex> {

    public LeveledVex(Vex entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.FAE);
        mobTypes.add(MobType.ILLAGER);
        mobTypes.add(MobType.AIRBORNE);

        super.setup();
    }
}
