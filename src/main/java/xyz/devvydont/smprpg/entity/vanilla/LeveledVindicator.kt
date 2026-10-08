package xyz.devvydont.smprpg.entity.vanilla

import org.bukkit.entity.Vindicator
import org.bukkit.persistence.PersistentDataType
import xyz.devvydont.smprpg.entity.MobType
import xyz.devvydont.smprpg.entity.base.VanillaEntity
import xyz.devvydont.smprpg.entity.slayer.illager.IllagerWarlockParent
import xyz.devvydont.smprpg.util.persistence.KeyStore

class LeveledVindicator(entity: Vindicator?) : VanillaEntity<Vindicator?>(entity) {
    override fun setup() {
        mobTypes.add(MobType.HUMANOID)
        mobTypes.add(MobType.ILLAGER)

        super.setup()

        _entity!!.persistentDataContainer.set(
            KeyStore.SLAYER_SPAWN_TYPE,
            PersistentDataType.STRING,
            IllagerWarlockParent.SPAWN_MOB_FLAG
        )
    }
}
