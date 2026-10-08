package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Donkey;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledDonkey extends VanillaEntity<Donkey> {

    public LeveledDonkey(Donkey entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }
}
