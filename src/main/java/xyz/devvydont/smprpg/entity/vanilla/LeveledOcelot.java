package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Ocelot;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledOcelot extends VanillaEntity<Ocelot> {

    public LeveledOcelot(Ocelot entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }
}
