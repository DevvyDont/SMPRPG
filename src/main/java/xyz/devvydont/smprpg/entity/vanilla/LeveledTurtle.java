package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Turtle;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledTurtle extends VanillaEntity<Turtle> {

    public LeveledTurtle(Turtle entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.AQUATIC);
        mobTypes.add(MobType.ANIMAL);

        super.setup();
    }
}
