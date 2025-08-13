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
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.services.SpecialEffectService
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class HollowedEffect(service: SpecialEffectService, player: Player, seconds: Int) : SpecialEffectTask(service, player, seconds) {

    override val expiredComponent: Component
        get() = ComponentUtils.create("RECONSTRUCTED!", NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true)

    override val nameComponent: Component
        get() = ComponentUtils.create("HOLLOWED!", NamedTextColor.BLACK).decoration(TextDecoration.BOLD, true)

    override val timerColor: TextColor
        get() = NamedTextColor.RED
    
    override fun tick() {
        player.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, 50, 0, true, true))
        updateAttribute(Attribute.MAX_HEALTH)
        updateAttribute(Attribute.SCALE)
        player.healthScale = SMPRPG.getService(EntityService::class.java).getPlayerInstance(player).getHealthScale().toDouble()
        ParticleBuilder(Particle.SMOKE)
            .location(player.eyeLocation)
            .count(5)
            .offset(.1, .1, .1)
            .spawn()
    }

    override fun expire() {
        clearAttributes()
        player.removePotionEffect(PotionEffectType.INVISIBILITY)
        player.healthScale = SMPRPG.getService(EntityService::class.java).getPlayerInstance(player)
            .getHealthScale().toDouble()
        player.playSound(player.location, Sound.BLOCK_TRIAL_SPAWNER_OMINOUS_ACTIVATE, 1f, 1f)
    }

    override fun removed() {
        clearAttributes()
        player.removePotionEffect(PotionEffectType.INVISIBILITY)
        player.healthScale = SMPRPG.getService(EntityService::class.java).getPlayerInstance(player)
            .getHealthScale().toDouble()
    }

    private fun updateAttribute(attribute: Attribute) {
        val attr = player.getAttribute(attribute)
        if (attr == null) return

        attr.removeModifier(ATTRIBUTE_KEY)
        attr.addTransientModifier(
            AttributeModifier(
                ATTRIBUTE_KEY,
                -0.99999,
                AttributeModifier.Operation.MULTIPLY_SCALAR_1
            )
        )
    }

    private fun clearAttributes() {
        val hp = player.getAttribute(Attribute.MAX_HEALTH)
        hp?.removeModifier(ATTRIBUTE_KEY)
        val scale = player.getAttribute(Attribute.SCALE)
        scale?.removeModifier(ATTRIBUTE_KEY)
    }

    companion object {
        private val ATTRIBUTE_KEY = NamespacedKey("smprpg", "hollowed")
    }
}
