package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Sniffer;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledSniffer extends VanillaEntity<Sniffer> {

    public LeveledSniffer(Sniffer entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.RARE);
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }
}
