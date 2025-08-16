package xyz.devvydont.smprpg.effects.tasks

import com.destroystokyo.paper.ParticleBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.NamespacedKey
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.services.SpecialEffectService
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class DisintegratingEffect(service: SpecialEffectService, player: Player, seconds: Int) :
    SpecialEffectTask(service, player, seconds), Listener {
    private var hpDrain = 0
    private var scaleDrain = 0.0

    override val expiredComponent: Component
        get() = ComponentUtils.create("STABILIZED!", NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true)

    override val nameComponent: Component
        get() {
            var text = "DISINTEGRATING"
            if (ticks < TIER_2_TICK_THRESHOLD) text = "CORRODING"
            else if (ticks < TIER_3_TICK_THRESHOLD) text = "FRACTURING"
            return ComponentUtils.encrypt(text, this.tier / 10f * 1.5).color(NamedTextColor.LIGHT_PURPLE)
                .decoration(TextDecoration.BOLD, true)
        }

    override val timerColor: TextColor 
        get() = NamedTextColor.DARK_PURPLE
        

    fun updateAttribute(attribute: Attribute, modifier: Double) {
        val attr = player.getAttribute(attribute)
        if (attr == null)
            return

        attr.removeModifier(MODIFIER_KEY)
        attr.addTransientModifier(AttributeModifier(MODIFIER_KEY, modifier, AttributeModifier.Operation.ADD_NUMBER))
    }

    fun clearAttributes() {
        val hp = player.getAttribute(Attribute.MAX_HEALTH)
        hp?.removeModifier(MODIFIER_KEY)
        val scale = player.getAttribute(Attribute.SCALE)
        scale?.removeModifier(MODIFIER_KEY)
        player.removePotionEffect(PotionEffectType.SLOWNESS)
        player.removePotionEffect(PotionEffectType.NAUSEA)
    }

    val tier: Int
        get() {
            if (ticks >= TIER_3_TICK_THRESHOLD) return 3
            if (ticks >= TIER_2_TICK_THRESHOLD) return 2
            return 1
        }

    override fun tick() {
        val tier = this.tier
        hpDrain += tier
        scaleDrain += .002 * tier

        ParticleBuilder(Particle.PORTAL)
            .location(player.eyeLocation)
            .offset(.25, .1, .25)
            .count(2)
            .receivers(20)
            .spawn()

        if (ticks == TIER_3_TICK_THRESHOLD) player.addPotionEffect(
            PotionEffect(
                PotionEffectType.NAUSEA,
                20 * 10,
                0,
                true,
                true
            )
        )

        if (ticks == TIER_2_TICK_THRESHOLD) player.addPotionEffect(
            PotionEffect(
                PotionEffectType.SLOWNESS,
                20 * 20,
                0,
                true,
                true
            )
        )

        if (ticks == TIER_3_TICK_THRESHOLD || ticks == TIER_2_TICK_THRESHOLD) {
            player.world.playSound(player.location, Sound.BLOCK_GLASS_BREAK, 1f, .5f)
            ParticleBuilder(Particle.FLASH)
                .location(player.eyeLocation)
                .spawn()
        }

        // Damage the player for whatever tier we are in.
        if (player.health > 1) player.health = player.health - 1

        updateAttribute(Attribute.MAX_HEALTH, -hpDrain.toDouble())
        updateAttribute(Attribute.SCALE, -scaleDrain)

        player.healthScale = SMPRPG.getService(EntityService::class.java).getPlayerInstance(player)
            .getHealthScale().toDouble()
    }

    override fun expire() {
        clearAttributes()
        val ses: SpecialEffectService = SMPRPG.getService(SpecialEffectService::class.java)
        player.world.playSound(player.location, Sound.ENTITY_WITHER_BREAK_BLOCK, 1f, 1f)
        ParticleBuilder(Particle.DRAGON_BREATH)
            .location(player.eyeLocation)
            .offset(.1, .1, .1)
            .count(5)
            .spawn()
        ses.giveEffect(player, HollowedEffect(ses, player, 10))
    }

    override fun removed() {
        clearAttributes()
    }

    /**
     * Any teleport event of any kind will clear the effect, pretty simple.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    @Suppress("unused")
    private fun onTeleport(event: PlayerTeleportEvent) {
        if (event.player != player) return

        SMPRPG.getService(SpecialEffectService::class.java).removeEffect(player)
    }

    companion object {
        const val SECONDS: Int = 30

        const val TIER_2_TICK_THRESHOLD: Int = SECONDS / 3 * 10
        const val TIER_3_TICK_THRESHOLD: Int = SECONDS / 3 * 2 * 10

        private val MODIFIER_KEY = NamespacedKey("smprpg", "disintegration")
    }
}
