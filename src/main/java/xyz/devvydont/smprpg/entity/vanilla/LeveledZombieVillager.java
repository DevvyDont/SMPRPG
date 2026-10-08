package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.persistence.PersistentDataType;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;
import xyz.devvydont.smprpg.entity.slayer.shambling.ShamblingAbominationParent;
import xyz.devvydont.smprpg.util.formatting.MinecraftStringUtils;
import xyz.devvydont.smprpg.util.persistence.KeyStore;

public class LeveledZombieVillager extends VanillaEntity<ZombieVillager> {

    public LeveledZombieVillager(ZombieVillager entity) {
        super(entity);
    }

    @Override
    public void updateAttributes() {
        super.updateAttributes();
        updateBaseAttribute(AttributeWrapper.DEFENSE, 100);
    }

    @Override
    public String getEntityName() {

        Villager.Profession profession = _entity.getVillagerProfession();
        if (!profession.equals(Villager.Profession.NONE))
            return MinecraftStringUtils.getTitledString(profession.key().value());

        return super.getEntityName();
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.UNDEAD);
        mobTypes.add(MobType.HUMANOID);

        _entity.getPersistentDataContainer().set(KeyStore.SLAYER_SPAWN_TYPE, PersistentDataType.STRING, ShamblingAbominationParent.SPAWN_MOB_FLAG);

        super.setup();
    }
}
