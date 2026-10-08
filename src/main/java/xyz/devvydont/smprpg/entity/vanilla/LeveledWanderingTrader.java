package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.WanderingTrader;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledWanderingTrader extends VanillaEntity<WanderingTrader> {

    public LeveledWanderingTrader(WanderingTrader entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.HUMANOID);

        super.setup();
    }
}
