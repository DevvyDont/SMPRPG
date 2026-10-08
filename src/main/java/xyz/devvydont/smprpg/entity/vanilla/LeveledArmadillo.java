package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Armadillo;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledArmadillo extends VanillaEntity<Armadillo> {

    public LeveledArmadillo(Armadillo entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }
}
