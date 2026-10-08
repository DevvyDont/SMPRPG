package xyz.devvydont.smprpg.listeners.damage

import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent
import xyz.devvydont.smprpg.events.MeleeAttackEvent
import xyz.devvydont.smprpg.items.interfaces.IMageBeam
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.listeners.ToggleableListener
import xyz.devvydont.smprpg.util.particles.ParticleUtil

class MeleeVisualListener : ToggleableListener() {
    @EventHandler()
    private fun onMeleeAttackHit(event: MeleeAttackEvent) {
        val entity = event.entity.entity;
        if (entity is LivingEntity) {
            val eyeLoc = entity.eyeLocation;
            if (event.blueprint is IMageBeam) {
                val bp = event.blueprint;
                ParticleUtil.spawnParticlesBetweenTwoPoints(bp.hitParticle, entity.world,
                    eyeLoc.add(eyeLoc.direction).toVector(),
                    eyeLoc.add(eyeLoc.direction.multiply(bp.particleRange)).toVector(),
                    bp.particleDensity
                );
            }
        }
    }

    @EventHandler()
    private fun onMeleeAttackMiss(event: PlayerInteractEvent) {
        if (event.action.isRightClick()) {
            return;
        }

        // TODO: Revisit when Spigot API updates to account for attack_range data component. Currently,
        // it will fire if you are outside of the player interaction range, and will not disregard with
        // respect for attack_range.

        // Play the miss animation

        val player = event.player;
        val bp = ItemService.blueprint(player.inventory.itemInMainHand)
        if (bp is IMageBeam) {
            val eyeLoc = player.eyeLocation
            ParticleUtil.spawnParticlesBetweenTwoPoints(bp.missParticle, player.world,
                eyeLoc.add(eyeLoc.direction).toVector(),
                eyeLoc.add(eyeLoc.direction.multiply(bp.particleRange)).toVector(),
                bp.particleDensity
            );
        }
    }
}