package xyz.devvydont.smprpg.ability.handlers

import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.ability.AbilityContext
import xyz.devvydont.smprpg.ability.AbilityHandler
import xyz.devvydont.smprpg.util.time.TickTime

class SugarRushAbilityHandler : AbilityHandler {
    override fun execute(ctx: AbilityContext): Boolean {
        // Adds a speed boost to the player, and removes it 30s later.
        val speed = ctx.caster.getAttribute(Attribute.MOVEMENT_SPEED)
        if (speed == null) return false

        if (speed.getModifier(ATTRIBUTE_KEY) != null) return false

        ctx.caster.world
            .playSound(ctx.caster.location, Sound.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, .25f, 2f)
        speed.removeModifier(ATTRIBUTE_KEY)
        speed.addTransientModifier(
            AttributeModifier(
                ATTRIBUTE_KEY,
                BOOST / 100.0,
                AttributeModifier.Operation.ADD_SCALAR
            )
        )
        Bukkit.getScheduler().runTaskLater(
            plugin, Runnable { speed.removeModifier(ATTRIBUTE_KEY) }, TickTime.seconds(
                DURATION.toLong()
            )
        )
        return true
    }

    companion object {
        const val BOOST: Int = 50
        const val DURATION: Int = 30
        val ATTRIBUTE_KEY: NamespacedKey = NamespacedKey("smprpg", "speed_boost_ability")
    }
}
