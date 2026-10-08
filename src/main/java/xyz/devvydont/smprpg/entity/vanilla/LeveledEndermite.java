package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Endermite;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledEndermite extends VanillaEntity<Endermite> {

    public LeveledEndermite(Endermite entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.ENDER);
        mobTypes.add(MobType.ARTHROPOD);

        super.setup();
    }
}
