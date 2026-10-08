package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.TraderLlama;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledTraderLlama extends VanillaEntity<TraderLlama> {

    public LeveledTraderLlama(TraderLlama entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }
}
