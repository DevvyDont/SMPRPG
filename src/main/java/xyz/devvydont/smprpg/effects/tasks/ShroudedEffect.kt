package xyz.devvydont.smprpg.effects.tasks

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityTargetEvent
import org.bukkit.event.world.LootGenerateEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import xyz.devvydont.smprpg.services.SpecialEffectService
import xyz.devvydont.smprpg.events.CustomEntityDamageByEntityEvent
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class ShroudedEffect(service: SpecialEffectService, player: Player, seconds: Int) : SpecialEffectTask(service, player, seconds), Listener {


    override val expiredComponent: Component
        get() = ComponentUtils.create("REVEALED!", NamedTextColor.RED)

    override val nameComponent: Component
        get() = ComponentUtils.create("Shrouded!", NamedTextColor.AQUA)

    override val timerColor: TextColor
        get() = NamedTextColor.GREEN


    override fun tick() {
        player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 50, 2, true, true))
        player.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 50, 1, true, true))
        player.addPotionEffect(PotionEffect(PotionEffectType.FIRE_RESISTANCE, 50, 0, true, true))
        player.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, 50, 0, true, true))
        player.allowFlight = true
        player.foodLevel = 20
        player.saturation = 20f
        // If we are flying, subtract a second depending on the tick. This will make it appear like it's draining.
        if (player.isFlying) seconds--
    }

    override fun expire() {
        if (!player.gameMode.isInvulnerable) player.allowFlight = false
    }

    override fun removed() {
        if (!player.gameMode.isInvulnerable) player.allowFlight = false
    }

    /*
     * When an entity targets our player that has the effect, don't allow it to happen
     */
    @EventHandler
    @Suppress("unused")
    private fun onPlayerTargeted(event: EntityTargetEvent) {
        // We don't care for untarget events
        if (event.target == null) return

        // We don't care for non player targets
        if (event.target !is Player) return

        // We don't care unless the player targeted is the owner of this effect.
        if (event.target != player) return

        // Our player is being targeted.
        event.isCancelled = true
    }

    /*
     * If our player receives damage from another entity while shrouded, don't do any damage.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    @Suppress("unused")
    private fun onReceiveDamageWhileShrouded(event: EntityDamageByEntityEvent) {
        // Ignore non players

        if (event.getEntity() !is Player)
            return

        if (event.entity != player)
            return

        event.setDamage(0.0)
    }

    /*
     * If our player deals damage, remove their shrouded effect.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    @Suppress("unused")
    private fun onPlayerDealDamage(event: CustomEntityDamageByEntityEvent) {
        // Ignore non players
        if (event.dealer !is Player)
            return

        if (event.dealer != player)
            return

        service.removeEffect(event.dealer)
    }

    /**
     * If a player triggers a loot event, remove their pacifist
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    @Suppress("unused")
    private fun onOpenLootChest(event: LootGenerateEvent) {
        // Ignore non players

        if (event.entity !is Player)
            return
        val eventPlayer = event.entity as Player

        // Ignore players that aren't our player
        if (event.entity != player)
            return

        service.removeEffect(eventPlayer)
    }
}
