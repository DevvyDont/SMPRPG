package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Witch;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledWitch extends VanillaEntity<Witch> {

    public LeveledWitch(Witch entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.HUMANOID);
        mobTypes.add(MobType.ILLAGER);

        super.setup();
    }
}
