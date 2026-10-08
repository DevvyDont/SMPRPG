package xyz.devvydont.smprpg.entity.slayer.illager

import org.bukkit.entity.Entity
import org.bukkit.entity.Ravager
import org.bukkit.persistence.PersistentDataType
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.entity.CustomEntityType
import xyz.devvydont.smprpg.entity.MobType
import xyz.devvydont.smprpg.entity.base.CustomEntityInstance
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.services.ItemService.Companion.generate
import xyz.devvydont.smprpg.skills.SkillType
import xyz.devvydont.smprpg.skills.utils.SkillExperienceReward
import xyz.devvydont.smprpg.skills.utils.SkillExperienceReward.Companion.of
import xyz.devvydont.smprpg.util.items.LootDrop
import xyz.devvydont.smprpg.util.items.QuantityLootDrop
import xyz.devvydont.smprpg.util.persistence.KeyStore
import java.util.List

class RavagerFamiliar(entity: Ravager?, entityType: CustomEntityType?) :
    CustomEntityInstance<Ravager?>(entity, entityType) {
    constructor(entity: Entity?, type: CustomEntityType?) : this(entity as Ravager?, type)

    override fun setup() {
        mobTypes.add(MobType.ILLAGER)
        mobTypes.add(MobType.ANIMAL)

        super.setup()

        _entity!!.getEquipment().setHelmet(null)
        _entity.getEquipment().setChestplate(null)
        _entity.getEquipment().setLeggings(null)
        _entity.getEquipment().setBoots(null)

        _entity.getPersistentDataContainer().set<String?, String?>(
            KeyStore.SLAYER_SPAWN_TYPE,
            PersistentDataType.STRING,
            IllagerWarlockParent.SPAWN_MOB_FLAG
        )
    }

    override fun updateAttributes() {
        super.updateAttributes()
        updateBaseAttribute(AttributeWrapper.SCALE, 0.5)
    }

    override fun getItemDrops(): Collection<LootDrop>? {
        return listOf(
            QuantityLootDrop(generate(CustomItemType.SPELL_POWDER), 1, 3, this)
        )
    }

    override fun generateSkillExperienceReward(): SkillExperienceReward {
        return of(SkillType.COMBAT, 3910)
    }
}
