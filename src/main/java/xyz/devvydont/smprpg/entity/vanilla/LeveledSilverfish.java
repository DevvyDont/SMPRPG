package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Silverfish;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledSilverfish extends VanillaEntity<Silverfish> {

    public LeveledSilverfish(Silverfish entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.ARTHROPOD);

        super.setup();
    }
}
