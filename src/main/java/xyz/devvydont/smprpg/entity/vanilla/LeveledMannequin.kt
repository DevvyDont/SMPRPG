package xyz.devvydont.smprpg.entity.vanilla

import org.bukkit.Bukkit
import org.bukkit.entity.Mannequin
import xyz.devvydont.smprpg.entity.MobType
import xyz.devvydont.smprpg.entity.base.VanillaEntity

class LeveledMannequin(entity: Mannequin) : VanillaEntity<Mannequin>(entity) {
    override fun setup() {
        mobTypes.add(MobType.HUMANOID)

        super.setup()
        _entity.description = null
    }
}