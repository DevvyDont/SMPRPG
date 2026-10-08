package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.GlowSquid;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledGlowSquid extends VanillaEntity<GlowSquid> {

    public LeveledGlowSquid(GlowSquid entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.AQUATIC);
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }
}
