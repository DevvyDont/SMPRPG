package xyz.devvydont.smprpg.entity.creatures

import kr.toxicity.model.api.BetterModel
import kr.toxicity.model.api.bukkit.platform.BukkitAdapter
import kr.toxicity.model.api.data.renderer.ModelRenderer
import kr.toxicity.model.api.tracker.EntityTracker
import kr.toxicity.model.api.tracker.TrackerModifier
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.entity.CustomEntityType
import xyz.devvydont.smprpg.entity.MobType
import xyz.devvydont.smprpg.entity.base.CustomEntityInstance
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.services.ItemService.Companion.generate
import xyz.devvydont.smprpg.skills.SkillType
import xyz.devvydont.smprpg.skills.utils.SkillExperienceReward
import xyz.devvydont.smprpg.skills.utils.SkillExperienceReward.Companion.of
import xyz.devvydont.smprpg.util.items.ChancedItemDrop
import xyz.devvydont.smprpg.util.items.LootDrop
import xyz.devvydont.smprpg.util.items.QuantityLootDrop
import java.util.function.Function


class FlyingCow<T : LivingEntity?> : CustomEntityInstance<T?> {
    constructor(entity: Entity?, entityType: CustomEntityType?) : super(entity, entityType)

    constructor(entity: T?, entityType: CustomEntityType?) : super(entity, entityType)

    override fun setup() {
        mobTypes.add(MobType.HOLY)
        mobTypes.add(MobType.ANIMAL)
        super.setup()

        entityTracker = BetterModel.model("flying_cow")
            .map(Function { r: ModelRenderer? -> r!!.getOrCreate(BukkitAdapter.adapt(_entity!!)) })
            .orElse(null)

        // if (_entity == null) return;
    }

    override fun updateAttributes() {
        super.updateAttributes()
        updateBaseAttribute(AttributeWrapper.GRAVITY, .0125)
        updateBaseAttribute(AttributeWrapper.SAFE_FALL, 100.0)
    }

    override fun generateSkillExperienceReward(): SkillExperienceReward {
        return of(SkillType.FARMING, (level * 20 * getSkillExperienceMultiplier()).toInt())
    }

    override fun getItemDrops(): MutableCollection<LootDrop> {
        return mutableListOf(
            ChancedItemDrop(generate(CustomItemType.PREMIUM_LEATHER), 25, this),
            ChancedItemDrop(generate(CustomItemType.PREMIUM_STEAK), 25, this),
            QuantityLootDrop(generate(Material.LEATHER), 2, 5, this),
            QuantityLootDrop(generate(Material.BEEF), 2, 5, this)
        )
    }
}
