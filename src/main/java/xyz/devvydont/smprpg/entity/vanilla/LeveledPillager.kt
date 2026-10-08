package xyz.devvydont.smprpg.entity.vanilla

import org.bukkit.NamespacedKey
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Pillager
import org.bukkit.persistence.PersistentDataType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.entity.MobType
import xyz.devvydont.smprpg.entity.base.VanillaEntity
import xyz.devvydont.smprpg.entity.slayer.illager.IllagerWarlockParent
import xyz.devvydont.smprpg.services.AttributeService
import xyz.devvydont.smprpg.util.persistence.KeyStore

class LeveledPillager(entity: Pillager?) : VanillaEntity<Pillager?>(entity) {
    override fun setup() {
        mobTypes.add(MobType.HUMANOID)
        mobTypes.add(MobType.ILLAGER)

        super.setup()
        val attr = SMPRPG.getService(AttributeService::class.java)
            .getAttribute(_entity!!, AttributeWrapper.STRENGTH)

        // Nerf the bow damage a bit. This is because base damage stacks on top of the damage from the bow.
        if (attr != null) attr.addModifier(
            AttributeModifier(
                NamespacedKey("smprpg", "pillager_nerf"),
                -.3,
                AttributeModifier.Operation.MULTIPLY_SCALAR_1
            )
        )

        _entity.getPersistentDataContainer().set(
            KeyStore.SLAYER_SPAWN_TYPE,
            PersistentDataType.STRING,
            IllagerWarlockParent.SPAWN_MOB_FLAG
        )
    }
}
