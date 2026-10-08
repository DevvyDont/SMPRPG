package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Zoglin;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledZoglin extends VanillaEntity<Zoglin> {

    public LeveledZoglin(Zoglin entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.NETHER);
        mobTypes.add(MobType.UNDEAD);
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }
}
