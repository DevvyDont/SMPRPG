package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Llama;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledLlama extends VanillaEntity<Llama> {

    public LeveledLlama(Llama entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }
}
