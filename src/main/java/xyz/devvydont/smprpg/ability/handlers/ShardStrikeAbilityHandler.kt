package xyz.devvydont.smprpg.ability.handlers

import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.Snowball
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.ability.AbilityContext
import xyz.devvydont.smprpg.ability.AbilityHandler
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.services.AttributeService.Companion.instance
import xyz.devvydont.smprpg.services.EntityDamageCalculatorService
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.time.TickTime
import java.util.*

class ShardStrikeAbilityHandler : AbilityHandler {

    override val cooldown: Long get() = TickTime.seconds(COOLDOWN)

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

        val projectile = ctx.caster.launchProjectile(
            Snowball::class.java,
            ctx.caster.location.getDirection().normalize().multiply(2)
        )
        val dmg = EntityDamageCalculatorService.getIntelligenceScaledDamage(
            DAMAGE.toDouble() + instance.getOrCreateAttribute(ctx.caster,
            AttributeWrapper.STRENGTH).value,
            instance.getOrCreateAttribute(ctx.caster, AttributeWrapper.INTELLIGENCE).value,
            ABILITY_SCALING + instance.getOrCreateAttribute(ctx.caster, AttributeWrapper.ARCANE_RATING).value)
        projectile.item = ItemService.generate(Material.AMETHYST_SHARD);
        projectile.setGravity(false)
        SMPRPG.getService(EntityDamageCalculatorService::class.java)
            .setBaseProjectileDamage(projectile, dmg)
        setShardProjectile(projectile)
        ctx.caster.world.playSound(ctx.caster.location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, .4f, 1f)

        if (ctx.caster is Player && ctx.hand != null) ctx.caster.setCooldown(
            ctx.caster.equipment.getItem(ctx.hand), TickTime.seconds(
                COOLDOWN.toLong()
            ).toInt()
        )

        return true
    }

    companion object {
        const val COOLDOWN: Long = 3
        const val DAMAGE: Int = 200
        const val SHATTER_RADIUS = 2.0
        const val ABILITY_SCALING = 0.05

        // We need a reference to projectiles that we shoot so that we can handle them at different stages in its life
        // since PDCs do not work during the EntityExplodeEvent.
        private val projectiles: MutableMap<UUID, Entity> = HashMap<UUID, Entity>()

        fun isShardProjectile(projectile: Entity): Boolean {
            return projectiles.containsKey(projectile.uniqueId)
        }

        fun setShardProjectile(projectile: Entity) {
            projectiles.put(projectile.uniqueId, projectile)
            projectile.addScoreboardTag("shard_strike")
        }

        fun removeShardProjectile(projectile: Entity) {
            projectiles.remove(projectile.uniqueId)
        }
    }
}
