package xyz.devvydont.smprpg.effects.tasks

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.damage.DamageSource
import org.bukkit.damage.DamageType
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import xyz.devvydont.smprpg.services.SpecialEffectService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

/**
 * An effect that if is timed out, it will remove fire resistance from the entity and damage them
 * for half the health they currently have.
 */
class OverheatingEffect(service: SpecialEffectService, player: Player, seconds: Int) : SpecialEffectTask(service, player, seconds) {

    override val expiredComponent: Component
        get() = ComponentUtils.create("SCORCHED!", NamedTextColor.DARK_RED)

    override val nameComponent: Component
        get() = ComponentUtils.create("Overheating!", NamedTextColor.RED)

    override val timerColor: TextColor
        get() = NamedTextColor.RED
    
    override fun tick() {
        if (ticks % 10 != 0) return

        if (seconds <= 0) return

        player.damage(player.health / 10, DamageSource.builder(DamageType.MAGIC).build())
        player.noDamageTicks = 0
        player.world.playSound(player, Sound.ENTITY_BLAZE_HURT, 1f, 2f)
        player.world.spawnParticle(Particle.ASH, player.eyeLocation, 10)
    }

    override fun expire() {
        // When the timer runs out, remove fire resistance and damage them for quite a bit.
        val fireRes = player.getPotionEffect(PotionEffectType.FIRE_RESISTANCE)
        if (fireRes != null) player.addPotionEffect(
            PotionEffect(
                PotionEffectType.POISON,
                fireRes.duration,
                fireRes.amplifier
            )
        )
        player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE)
        player.damage(player.health / 2, DamageSource.builder(DamageType.MAGIC).build())
        player.noDamageTicks = 0
        player.world.playSound(player, Sound.ENTITY_BLAZE_DEATH, 1f, 2f)
        player.world.spawnParticle(Particle.SOUL_FIRE_FLAME, player.eyeLocation, 25)
    }

    override fun removed() {
        // Nothing happens :)
    }
}
