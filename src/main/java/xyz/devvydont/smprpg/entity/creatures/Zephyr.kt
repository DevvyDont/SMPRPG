package xyz.devvydont.smprpg.entity.creatures

import com.destroystokyo.paper.entity.ai.Goal
import com.destroystokyo.paper.entity.ai.GoalKey
import com.destroystokyo.paper.entity.ai.GoalType
import com.destroystokyo.paper.entity.ai.VanillaGoal
import kr.toxicity.model.api.BetterModel
import kr.toxicity.model.api.animation.AnimationModifier
import kr.toxicity.model.api.bukkit.platform.BukkitAdapter
import kr.toxicity.model.api.data.renderer.ModelRenderer
import kr.toxicity.model.api.tracker.ModelScaler
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.damage.DamageType
import org.bukkit.entity.Entity
import org.bukkit.entity.Ghast
import org.bukkit.entity.Snowball
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.util.Vector
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.entity.CustomEntityType
import xyz.devvydont.smprpg.entity.MobType
import xyz.devvydont.smprpg.entity.base.CustomEntityInstance
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.services.ItemService.Companion.generate
import xyz.devvydont.smprpg.util.items.ChancedItemDrop
import xyz.devvydont.smprpg.util.items.LootDrop
import xyz.devvydont.smprpg.util.items.QuantityLootDrop
import xyz.devvydont.smprpg.util.persistence.KeyStore
import java.util.EnumSet
import java.util.function.Function
import kotlin.math.cos
import kotlin.math.sin

class Zephyr : CustomEntityInstance<Ghast?>, Listener {
    constructor(entity: Entity?, entityType: CustomEntityType?) : super(entity, entityType)

    constructor(entity: Ghast?, entityType: CustomEntityType?) : super(entity, entityType)

    val snowballs = mutableListOf<Snowball>()

    override fun getItemDrops(): Collection<LootDrop>? {
        return listOf(
            QuantityLootDrop(generate(CustomItemType.COLD_AERCLOUD), 1, 2, this),
            ChancedItemDrop(generate(CustomItemType.BLUE_AERCLOUD), 4, this),
        )
    }

    override fun setup() {
        mobTypes.add(MobType.AIRBORNE)
        mobTypes.add(MobType.ELEMENTAL)
        mobTypes.add(MobType.HOLY)

        super.setup()

        entityTracker = BetterModel.model("zephyr")
            .map(Function { r: ModelRenderer? -> r!!.getOrCreate(BukkitAdapter.adapt(_entity!!)) })
            .orElse(null)
        entityTracker.scaler(ModelScaler.value(4.0f))
        entityTracker.forceUpdate(true)

        _entity!!.isSilent = true

        val mobGoals = Bukkit.getMobGoals()
        mobGoals.removeGoal(_entity, VanillaGoal.GHAST_SHOOT_FIREBALL)
        mobGoals.addGoal(_entity, 7, ZephyrShootSnowballGoal(this))

    }

    companion object {
        class ZephyrShootSnowballGoal(val zephyr: Zephyr) : Goal<Ghast> {
            val ghast : Ghast = zephyr._entity!!
            val goalKey : GoalKey<Ghast> = GoalKey.of(Ghast::class.java, NamespacedKey(SMPRPG.plugin, "zephyr_shoot_snowball_goal"))
            var chargeTime : Int = 0

            override fun shouldActivate(): Boolean {
                return ghast.target != null
            }

            override fun getKey(): GoalKey<Ghast> {
                return goalKey
            }

            override fun getTypes(): EnumSet<GoalType> {
                return EnumSet.of(GoalType.UNKNOWN_BEHAVIOR)
            }

            override fun shouldStayActive(): Boolean {
                return true
            }

            override fun start() {
                this.chargeTime = 0
            }

            override fun stop() {
                zephyr.entityTracker.animate("idle")
            }

            override fun tick() {
                val target = ghast.target
                if (target != null) {
                    val distance = 64.0
                    if (target.location.distance(ghast.location) <= distance && ghast.hasLineOfSight(target)) {
                        this.chargeTime++
                        if (this.chargeTime == 10) ghast.location.world.playSound(ghast.location, KeyStore.AUDIO_ZEPHYR_CALL.toString(), 2f, 1f)
                        if (this.chargeTime == 15) zephyr.entityTracker.animate("attack", AnimationModifier.DEFAULT_WITH_PLAY_ONCE, { zephyr.entityTracker.animate("idle") })

                        if (this.chargeTime == 20) {
                            ghast.location.world.playSound(ghast.location, KeyStore.AUDIO_ZEPHYR_SHOOT.toString(), 2f, 1f)
                            val snowball = ghast.launchProjectile(Snowball::class.java, target.eyeLocation.toVector().subtract(ghast.eyeLocation.toVector()).normalize().multiply(1.2))
                            snowball.setGravity(false)
                            zephyr.snowballs.add(snowball)
                            this.chargeTime = -40
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    fun onZephyrTakeDamage(event: EntityDamageEvent) {
        val entity = event.entity
        if (entity == this._entity) {
            if (event.damageSource.damageType == DamageType.IN_WALL) {
                event.isCancelled = true
                return
            }
            entity.location.world.playSound(entity.location, KeyStore.AUDIO_ZEPHYR_HURT.toString(), 1.0f, 1.0f)
        }
    }

    @EventHandler
    fun onZephyrDeath(event: EntityDeathEvent) {
        val entity = event.entity
        if (entity == this._entity) {
            entity.location.world.playSound(entity.location, KeyStore.AUDIO_ZEPHYR_DEATH.toString(), 1.0f, 1.0f)
        }

        for (snowball in snowballs) {
            snowball.remove()
        }
        snowballs.clear()
    }

    @EventHandler
    fun onSnowballHit(event: ProjectileHitEvent) {
        if (event.entity in snowballs) {
            snowballs.remove(event.entity)
            event.isCancelled = true
            event.entity.remove()

            if (event.hitEntity != null) {
                val he = event.hitEntity!!
                val yaw = he.location.yaw
                val normalizedDir = Vector(
                    -sin(Math.toRadians(yaw.toDouble())),
                    -0.6,
                    cos(Math.toRadians(yaw.toDouble()))
                ).multiply(-1)
                he.velocity = Vector(1.0, 1.0, 1.0).multiply(normalizedDir).multiply(2.0)
            }
        }
    }
}
