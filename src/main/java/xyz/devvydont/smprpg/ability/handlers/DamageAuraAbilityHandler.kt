package xyz.devvydont.smprpg.ability.handlers

import com.destroystokyo.paper.ParticleBuilder
import io.papermc.paper.datacomponent.DataComponentTypes
import net.momirealms.craftengine.bukkit.api.CraftEngineItems
import net.momirealms.craftengine.bukkit.entity.projectile.ProjectileItems
import net.momirealms.craftengine.core.item.ItemBuildContext
import net.momirealms.craftengine.core.util.Key
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.damage.DamageSource
import org.bukkit.damage.DamageType
import org.bukkit.entity.*
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Transformation
import org.joml.AxisAngle4f
import org.joml.Vector3f
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.ability.AbilityContext
import xyz.devvydont.smprpg.ability.AbilityHandler
import xyz.devvydont.smprpg.ability.handlers.FireballAbilityHandler.Companion.ABILITY_SCALING
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.entity.slayer.illager.goals.AuraType
import xyz.devvydont.smprpg.services.AttributeService.Companion.instance
import xyz.devvydont.smprpg.services.EntityDamageCalculatorService
import xyz.devvydont.smprpg.services.ItemService.Companion.generate
import xyz.devvydont.smprpg.util.time.TickTime
import java.util.*

class DamageAuraAbilityHandler : AbilityHandler {

    override val cooldown: Long get() = COOLDOWN

    /**
     * Attempts to execute the ability.
     *
     * @param ctx The context of the ability.
     * @return True if the ability succeeded and should have cost reduced, false otherwise.
     */
    override fun execute(ctx: AbilityContext): Boolean {
        if (ctx.caster is Player && ctx.hand != null) if (ctx.caster.hasCooldown(
                ctx.caster.equipment.getItem(ctx.hand)
            )
        ) return false

        val projectileItem = CraftEngineItems.byId(Key.of("smprpg:damage_aura_projectile"))!!.buildItem(ItemBuildContext.empty())
        val projectile = ProjectileItems.createProjectileByItem(ctx.caster.eyeLocation, projectileItem, ctx.caster, false)


        projectile.setGravity(false)
        projectile.location.direction = ctx.caster.eyeLocation.getDirection()
        projectile.velocity = ctx.caster.eyeLocation.getDirection().normalize().multiply(2)
        SMPRPG.getService(EntityDamageCalculatorService::class.java).setBaseProjectileDamage(projectile, 0.0)
        setAuraProjectile(projectile)

        Bukkit.getScheduler().runTaskLater(SMPRPG.plugin, Runnable {
            // Force kill the projectile KILL_TIME ticks later, to prevent lingering projectiles.
            if (projectile.isValid) {
                removeAuraProjectile(projectile)
                projectile.remove()
            }
        }, KILL_TIME)

        if (ctx.caster is Player && ctx.hand != null) ctx.caster.setCooldown(
            ctx.caster.equipment.getItem(ctx.hand), cooldown.toInt()
        )

        return true
    }

    companion object {
        val COOLDOWN: Long = TickTime.seconds(10)
        const val AURA_RADIUS: Double = 3.0
        const val DAMAGE = 2
        val KILL_TIME: Long = TickTime.seconds(60)

        // We need a reference to projectiles that we shoot so that we can handle them at different stages in its life
        // since PDCs do not work during the EntityExplodeEvent.
        private val projectiles: MutableMap<UUID, Projectile> = HashMap<UUID, Projectile>()

        fun isAuraProjectile(projectile: Projectile): Boolean {
            return projectiles.containsKey(projectile.uniqueId)
        }

        fun setAuraProjectile(projectile: Projectile) {
            projectiles.put(projectile.uniqueId, projectile)
            projectile.addScoreboardTag("damage_aura")
        }

        fun removeAuraProjectile(projectile: Projectile) {
            projectiles.remove(projectile.uniqueId)
        }
    }

    class DamageAuraTask(location: Location, player: Player) {
        init {
            val runnable = object : BukkitRunnable() {
                private var radius = AURA_RADIUS
                private val display = location.world.spawnEntity(location, EntityType.ITEM_DISPLAY) as ItemDisplay
                private var shieldAngle: Float = 0.0f
                private val cachedDamage: Double = EntityDamageCalculatorService.getIntelligenceScaledDamage(
                    FireballAbilityHandler.Companion.DAMAGE.toDouble() + instance.getOrCreateAttribute(player,
                        AttributeWrapper.STRENGTH).value,
                    instance.getOrCreateAttribute(player, AttributeWrapper.INTELLIGENCE).value,
                    ABILITY_SCALING + instance.getOrCreateAttribute(player, AttributeWrapper.ARCANE_RATING).value)
                private val damageSource = DamageSource.builder(DamageType.MAGIC).withDirectEntity(player).withCausingEntity(player).build()
                private var scaleRad = 0.0f

                init {
                    val haloItem = generate(Material.STICK)
                    haloItem.setData(DataComponentTypes.ITEM_MODEL, net.kyori.adventure.key.Key.key("smprpg:illager_warlock_shield"))
                    haloItem.setData(DataComponentTypes.DYED_COLOR, AuraType.DAMAGE.color)
                    display.setItemStack(haloItem)
                    display.itemDisplayTransform =ItemDisplay.ItemDisplayTransform.GROUND
                }

                override fun run() {

                    shieldAngle += 0.1f
                    shieldAngle = shieldAngle % 360

                    display.transformation = Transformation(Vector3f(), AxisAngle4f(), Vector3f(scaleRad, 1f, scaleRad), AxisAngle4f(shieldAngle, 0f, 1f, 0f))
                    display.interpolationDelay = 0;
                    display.interpolationDuration = 5;
                    for (entity in location.world.getNearbyEntities(location, radius, 0.01, radius, SyphonAbilityHandler.PLAYER_FILTER)) {
                        val living = entity as LivingEntity
                        living.damage(cachedDamage, damageSource)
                    }
                    scaleRad = radius.toFloat() * 8.0f
                    ParticleBuilder(Particle.INSTANT_EFFECT)
                        .location(location)
                        .count(64)
                        .data(Particle.Spell(AuraType.DAMAGE.color.color(), 2.0f))
                        .extra(0.0)
                        .offset(radius - 1.0, 0.5, radius - 1.0)
                        .spawn()
                    location.world.playSound(location, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 0.5f, radius.toFloat() * 0.5f)
                    radius -= 0.01f
                    if (radius <= 0f) {
                        location.world.playSound(location, Sound.BLOCK_FIRE_EXTINGUISH, 1f, 2f)
                        this.cancel()
                    }
                }

                override fun cancel() {
                    super.cancel()
                    // Always clean up the visual entity when the task ends, otherwise it lingers forever.
                    if (display.isValid)
                        display.remove()
                }
            }.runTaskTimer(plugin, TickTime.INSTANTANEOUSLY, TickTime.INSTANTANEOUSLY)
        }
    }
}
