package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Squid;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledSquid extends VanillaEntity<Squid> {

    public LeveledSquid(Squid entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.AQUATIC);
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }
}
