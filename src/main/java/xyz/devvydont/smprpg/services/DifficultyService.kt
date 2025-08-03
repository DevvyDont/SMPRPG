package xyz.devvydont.smprpg.services

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent
import org.bukkit.NamespacedKey
import org.bukkit.OfflinePlayer
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.EquipmentSlotGroup
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.entity.player.ProfileDifficulty
import xyz.devvydont.smprpg.events.skills.SkillExperienceGainEvent
import xyz.devvydont.smprpg.gui.player.MenuDifficultyChooser

class DifficultyService : IService, Listener {

    private val difficultyKey: NamespacedKey = NamespacedKey(plugin, "profile_type")

    @Throws(RuntimeException::class)
    override fun setup() {
    }

    override fun cleanup() {
    }

    /**
     * Queries for the difficulty that this player is currently playing on.
     * @param player The player to check difficulty for.
     * @return The ProfileDifficulty enum that they are currently set to.
     */
    fun getDifficulty(player: Player): ProfileDifficulty {
        return player.persistentDataContainer.getOrDefault(difficultyKey, ProfileDifficulty.ADAPTER, ProfileDifficulty.NOT_CHOSEN)
    }

    fun getDifficulty(player: OfflinePlayer): ProfileDifficulty {
        return player.persistentDataContainer.getOrDefault(difficultyKey, ProfileDifficulty.ADAPTER, ProfileDifficulty.NOT_CHOSEN)
    }

    /**
     * Sets the difficulty of a player. Also handles any necessary side effects that a difficulty change would require.
     * @param player The player to set a difficulty for.
     * @param difficulty The difficulty to set.
     */
    fun setDifficulty(player: Player, difficulty: ProfileDifficulty) {
        // Don't do anything if the difficulty is not changing.

        val oldDifficulty = getDifficulty(player)
        if (oldDifficulty == difficulty) return

        // Store the difficulty on the player.
        player.persistentDataContainer.set(difficultyKey, ProfileDifficulty.ADAPTER, difficulty)
        val playerWrapper = SMPRPG.getService(EntityService::class.java).getPlayerInstance(player)

        // Set the state of the player necessary for this difficulty.
        // For now, we just have to make sure their stats are sanity checked, as everything else is dynamically handled.
        applyDifficultyModifiers(player, difficulty)
        SMPRPG.getService(SkillService::class.java).syncSkillAttributes(playerWrapper)
        playerWrapper.setConfiguration(playerWrapper.getDefaultConfiguration())
    }

    /**
     * Removes all difficulty related attribute modifiers, and adds new ones based on the difficulty they are on.
     * @param player The player to tweak attributes of.
     * @param difficulty The difficulty they want modifiers for.
     */
    fun applyDifficultyModifiers(player: Player, difficulty: ProfileDifficulty) {
        // As of now, the only global difficulty modifier is luck. First remove it.
        val luck = AttributeService.instance.getOrCreateAttribute(player, AttributeWrapper.LUCK)
        luck.removeModifier(DIFFICULTY_MODIFIER_KEY)

        // If we don't have a multiplier to give, no reason to add a modifier.
        val boost: Int = getAdditiveDropRateFor(difficulty)
        if (boost == 0) {
            luck.save(player, AttributeWrapper.LUCK)
            return
        }

        // Apply a luck modifier based on the difficulty.
        luck.addModifier(
            AttributeModifier(
                DIFFICULTY_MODIFIER_KEY,
                boost.toDouble(),
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlotGroup.ANY
            )
        )
        luck.save(player, AttributeWrapper.LUCK)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    @Suppress("unused")
    private fun onPlayerJoin(event: PlayerJoinEvent) {
        // When a player joins, we need to make sure they have a difficulty selected so they can play.

        val difficulty = getDifficulty(event.getPlayer())
        if (difficulty != ProfileDifficulty.NOT_CHOSEN) {
            applyDifficultyModifiers(event.getPlayer(), difficulty)
            return
        }

        // They haven't chosen! Open the interface.
        val gui = MenuDifficultyChooser(event.getPlayer())
        gui.openMenu()
        gui.lock()
    }

    /**
     * When a player earns skill experience, modify the experience depending on the difficulty they are playing on.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    @Suppress("unused")
    private fun onPlayerEarnSkillExperience(event: SkillExperienceGainEvent) {

        // Ignore experience gained from commands, otherwise the skill set command will behave wonky.
        if (event.source == SkillExperienceGainEvent.ExperienceSource.COMMANDS)
            return

        val multiplier: Float = getSkillExperienceMultiplier(getDifficulty(event.player))
        event.multiplyExperienceEarned(multiplier.toDouble())
    }

    /**
     * When a player on hard mode picks up an experience orb, multiply its experience.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    @Suppress("unused")
    private fun onPlayerEarnMinecraftExperience(event: PlayerPickupExperienceEvent) {
        if (getDifficulty(event.getPlayer()) == ProfileDifficulty.HARD)
            event.experienceOrb.experience = (event.experienceOrb.experience * 1.5).toInt()
    }

    companion object {
        val DIFFICULTY_MODIFIER_KEY: NamespacedKey = NamespacedKey(plugin, "difficulty_modifier")

        /**
         * Given a difficulty, determine the skill experience multiplier.
         * @param difficulty The difficulty a player is on.
         * @return The multiplier of skill experience they gain.
         */
        fun getSkillExperienceMultiplier(difficulty: ProfileDifficulty): Float {
            return when (difficulty) {
                ProfileDifficulty.EASY -> 1.25f
                ProfileDifficulty.HARD -> 0.75f
                else -> 1.0f
            }
        }

        /**
         * Given a difficulty, determine the incoming damage multiplier.
         * @param difficulty The difficulty a player is on.
         * @return The multiplier of incoming damage they receive.
         */
        @JvmStatic
        fun getDamageMultiplier(difficulty: ProfileDifficulty): Float {
            return when (difficulty) {
                ProfileDifficulty.EASY -> .5f
                ProfileDifficulty.HARD -> 2f
                else -> 1.0f
            }
        }

        /**
         * Given a difficulty, determine the luck boost.
         * @param difficulty The difficulty a player is on.
         * @return The luck boost they receive.
         */
        fun getAdditiveDropRateFor(difficulty: ProfileDifficulty): Int {
            return when (difficulty) {
                ProfileDifficulty.EASY -> -50
                ProfileDifficulty.HARD -> 100
                else -> 0
            }
        }
    }
}
