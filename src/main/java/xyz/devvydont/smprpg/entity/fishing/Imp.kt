package xyz.devvydont.smprpg.entity.fishing

import com.destroystokyo.paper.entity.ai.VanillaGoal
import kr.toxicity.model.api.BetterModel
import kr.toxicity.model.api.bukkit.platform.BukkitAdapter
import kr.toxicity.model.api.data.renderer.ModelRenderer
import kr.toxicity.model.api.tracker.ModelScaler
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.entity.Blaze
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import xyz.devvydont.smprpg.entity.CustomEntityType
import xyz.devvydont.smprpg.entity.MobType
import xyz.devvydont.smprpg.entity.fishing.goals.ImpAttackGoal
import xyz.devvydont.smprpg.items.blueprints.resources.scrolls.DynamicEnchantingScroll.Companion.getScrollWithEnchantment
import xyz.devvydont.smprpg.services.EnchantmentService
import xyz.devvydont.smprpg.util.items.ChancedItemDrop
import xyz.devvydont.smprpg.util.items.LootDrop
import java.util.function.Function

class Imp
/**
 * An unsafe constructor to use to allow dynamic creation of custom entities.
 * This is specifically used as a casting hack for the CustomEntityType enum in order to dynamically create
 * entities.
 *
 * @param entity     The entity that should map the T type parameter.
 * @param entityType The entity type.
 */
    (entity: LivingEntity?, entityType: CustomEntityType?) : SeaCreature<Blaze?>(entity as Blaze?, entityType) {

    override fun setup() {
        mobTypes.add(MobType.SEA_CREATURE)
        mobTypes.add(MobType.NETHER)
        mobTypes.add(MobType.AIRBORNE)
        mobTypes.add(MobType.HUMANOID)

        super.setup()

        entityTracker = BetterModel.model("imp")
            .map(Function { r: ModelRenderer? -> r!!.getOrCreate(BukkitAdapter.adapt(_entity!!)) })
            .orElse(null)
        entityTracker.scaler(ModelScaler.value(0.75f))
        entityTracker.forceUpdate(true)

        _entity!!.isSilent = true
        val mobGoals = Bukkit.getMobGoals()
        mobGoals.removeGoal(_entity, VanillaGoal.BLAZE_ATTACK)
        mobGoals.addGoal(_entity, 3, ImpAttackGoal(_entity, this))
    }

    override fun updateAttributes() {
        super.updateAttributes()
    }

    override fun getItemDrops(): MutableCollection<LootDrop>? {
        val fireAspectScroll = getScrollWithEnchantment(EnchantmentService.FIRE_ASPECT)
        return mutableListOf(
            ChancedItemDrop(lureScroll, 700, this),
            ChancedItemDrop(abyssalInstinctScroll, 700, this),
            ChancedItemDrop(impalingScroll, 700, this),
            ChancedItemDrop(luckOfTheSeaScroll, 700, this),
            ChancedItemDrop(treasureHunterScroll, 700, this),
            ChancedItemDrop(fireAspectScroll, 200, this)
        )
    }

    @EventHandler
    fun onImpTakeDamage(event: EntityDamageEvent) {
        val entity = event.entity
        if (entity == this._entity) {
            entity.location.world.playSound(entity.location, Sound.ENTITY_SHULKER_HURT, 1.0f, 1.5f)
        }
    }

    @EventHandler
    fun onImpDeath(event: EntityDeathEvent) {
        val entity = event.entity
        if (entity == this._entity) {
            event.deathSound = Sound.ENTITY_VEX_DEATH
            event.deathSoundVolume = 1.0f
            event.deathSoundPitch = 0.75f
            event.deathSoundCategory = SoundCategory.HOSTILE
        }
    }
}
