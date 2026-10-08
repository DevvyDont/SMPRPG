package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.PiglinBrute;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledPiglinBrute extends VanillaEntity<PiglinBrute> {

    public LeveledPiglinBrute(PiglinBrute entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.RARE);
        mobTypes.add(MobType.NETHER);
        mobTypes.add(MobType.ANIMAL);
        mobTypes.add(MobType.HUMANOID);

        super.setup();
    }
}
