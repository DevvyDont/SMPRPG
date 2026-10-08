package xyz.devvydont.smprpg.ability

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import xyz.devvydont.smprpg.entity.base.LeveledEntity
import xyz.devvydont.smprpg.entity.player.LeveledPlayer
import xyz.devvydont.smprpg.util.formatting.Symbols
import kotlin.math.roundToInt

/**
 * A cost associated with an ability.
 * @param resource
 * @param amount
 */
@JvmRecord
data class AbilityCost(@JvmField val resource: Resource, @JvmField val amount: Int) {
    enum class Resource(val symbol: String, val color: TextColor) {
        MANA(Symbols.MANA, NamedTextColor.AQUA),
        HEALTH(Symbols.HEART, NamedTextColor.RED),
        ;
    }

    /**
     * Checks if the entity can afford this.
     * @param entity The entity to check.
     * @return True if they can afford the cost.
     */
    fun canUse(entity: LeveledEntity<*>): Boolean {
        if (entity.getEntity() is Player && (entity.entity as Player).gameMode.isInvulnerable)
            return true

        return when (resource) {
            Resource.MANA -> {
                if (entity !is LeveledPlayer) false
                (entity as LeveledPlayer).mana >= amount
            }

            Resource.HEALTH -> entity.getEntity() is LivingEntity && (entity.entity as LivingEntity).health > amount
        }
    }

    /**
     * Spends the resource. Assumes that they were already checked to have enough.
     * @param entity The entity to modify.
     */
    fun spend(entity: LeveledEntity<*>) {
        when (resource) {
            Resource.MANA -> {
                if (entity !is LeveledPlayer) return
                entity.useMana(amount)
            }

            Resource.HEALTH -> {
                if (entity.entity !is LivingEntity)
                    return
                val living = entity.entity as LivingEntity
                living.health = living.health - amount
            }
        }
    }

    companion object {
        @JvmStatic
        fun of(resource: Resource, amount: Int): AbilityCost {
            return AbilityCost(resource, amount)
        }
    }

    fun reduce(reductionPercentage: Double): AbilityCost {
        return AbilityCost(resource, (amount * reductionPercentage).roundToInt())
    }
}
