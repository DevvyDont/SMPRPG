package xyz.devvydont.smprpg.entity.slayer.illager

import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.persistence.PersistentDataType
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.entity.CustomEntityType
import xyz.devvydont.smprpg.entity.MobType
import xyz.devvydont.smprpg.entity.base.CustomEntityInstance
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.services.ItemService.Companion.generate
import xyz.devvydont.smprpg.skills.SkillType
import xyz.devvydont.smprpg.skills.utils.SkillExperienceReward
import xyz.devvydont.smprpg.skills.utils.SkillExperienceReward.Companion.of
import xyz.devvydont.smprpg.util.items.ChancedItemDrop
import xyz.devvydont.smprpg.util.items.LootDrop
import xyz.devvydont.smprpg.util.items.QuantityLootDrop
import xyz.devvydont.smprpg.util.persistence.KeyStore
import java.util.List

class IllagerPoppet<T : LivingEntity?> : CustomEntityInstance<T?> {
    constructor(entity: Entity?, entityType: CustomEntityType?) : super(entity, entityType)

    constructor(entity: T?, entityType: CustomEntityType?) : super(entity, entityType)

    override fun setup() {
        mobTypes.add(MobType.HUMANOID)
        mobTypes.add(MobType.ILLAGER)

        super.setup()
        removeEquipment()
        setNoDropEquipment()

        _entity!!.setAI(false)
        _entity.isSilent = true

        _entity.persistentDataContainer.set(
            KeyStore.SLAYER_SPAWN_TYPE,
            PersistentDataType.STRING,
            IllagerWarlockParent.SPAWN_MOB_FLAG
        )
    }

    override fun updateAttributes() {
        super.updateAttributes()
        updateBaseAttribute(AttributeWrapper.KNOCKBACK_RESISTANCE, 10.0)
        updateBaseAttribute(AttributeWrapper.SCALE, 0.25)
    }

    override fun getItemDrops(): Collection<LootDrop>? {
        return listOf(
            QuantityLootDrop(generate(CustomItemType.SPELL_POWDER), 1, 3, this)
        )
    }

    override fun generateSkillExperienceReward(): SkillExperienceReward {
        return of(SkillType.COMBAT, 1570)
    }
}