package xyz.devvydont.smprpg.effects.tasks

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.util.Vector
import xyz.devvydont.smprpg.services.SpecialEffectService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.particles.ParticleUtil

class TetheredEffect(
    service: SpecialEffectService,
    player: Player,
    private val tetheredTo: LivingEntity,
    seconds: Int
) : SpecialEffectTask(service, player, seconds), Listener {
    private var currentlyBrokenLOSTicks = 0

    override val expiredComponent: Component
        get() = ComponentUtils.create("SEVERED!", NamedTextColor.RED)

    override val nameComponent: Component
        get() = ComponentUtils.create("Tethered!", NamedTextColor.GOLD)
    
    override val timerColor: TextColor
        get() = NamedTextColor.RED

    override fun tick() {
        // If we break LOS, then we add to the counter of broken LOS ticks.

        if (!tetheredTo.hasLineOfSight(player)) currentlyBrokenLOSTicks++

        // If we have enough of those ticks, we can remove the effect.
        if (currentlyBrokenLOSTicks >= LOS_BREAK_TICKS) {
            service.removeEffect(player, this.javaClass)
            return
        }

        ParticleUtil.spawnParticlesBetweenTwoPoints(
            Particle.FLAME,
            player.world,
            player.eyeLocation.toVector(),
            tetheredTo.eyeLocation.toVector(),
            30
        )
        player.fireTicks = 20
    }

    /*
     * When this effect expires, launch the player towards the entity we are tethered to.
     */
    override fun expire() {
        val dir = tetheredTo.location.toVector().subtract(player.location.toVector()).normalize()
        dir.multiply(5)
        dir.add(Vector(0, 2, 0))
        player.velocity = dir
        player.world.playSound(player.location, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1f, 1.25f)
        ParticleUtil.spawnParticlesBetweenTwoPoints(
            Particle.SOUL_FIRE_FLAME,
            player.world,
            player.eyeLocation.toVector(),
            tetheredTo.eyeLocation.toVector(),
            30
        )
    }

    override fun removed() {
        player.world.playSound(player.location, Sound.ENTITY_ITEM_BREAK, 1f, 1.9f)
        ParticleUtil.spawnParticlesBetweenTwoPoints(
            Particle.END_ROD,
            player.world,
            player.eyeLocation.toVector(),
            tetheredTo.eyeLocation.toVector(),
            30
        )
        player.fireTicks = 0
    }

    /*
     * If the entity that we are tethered to dies, remove the tether.
     */
    @EventHandler
    @Suppress("unused")
    private fun onTetherEntityDied(event: EntityDeathEvent) {

        if (event.getEntity() != tetheredTo)
            return
        service.removeEffect(player, this.javaClass)
    }

    companion object {
        // How many ticks do we need to break LOS to break the tether?
        const val LOS_BREAK_TICKS: Int = 10
    }
}
