package xyz.devvydont.smprpg.skills.listeners

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.events.skills.SkillExperienceGainEvent
import xyz.devvydont.smprpg.fishing.events.FishingLootGenerateEvent
import xyz.devvydont.smprpg.fishing.utils.FishingGallery
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.persistence.KeyStore
import xyz.devvydont.smprpg.util.persistence.PDCAdapters

/**
 * Responsible for awarding fishing experience for general fishing events.
 */
class FishingExperienceListener : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    /**
     * Simply listen for when a fish loot event happens. Fishing EXP is on the reward itself.
     * We should also give a bonus for fishing higher quality fish.
     * @param event The [FishingLootGenerateEvent] event that provides us with relevant context.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    @Suppress("unused")
    private fun onFish(event: FishingLootGenerateEvent) {
        // All we need to do is give the player experience.

        var exp = event.calculationResult.Reward.Element.fishingExperience

        // Double it if it's the first one.
        val gallery = event.fishingContext.player.persistentDataContainer.getOrDefault(KeyStore.FISHING_GALLERY, PDCAdapters.FISHING_GALLERY, FishingGallery())
        val timesCaught = gallery.get(event.calculationResult.Reward.Element)
        if (timesCaught == 0) {
            event.fishingContext.player.sendMessage(
                ComponentUtils.alert(
                    ComponentUtils.merge(
                        ComponentUtils.create("UNIQUE CATCH!", NamedTextColor.GREEN)
                            .decoration(TextDecoration.BOLD, true),
                        ComponentUtils.create(" That's a new one! This fish gave you "),
                        ComponentUtils.create("double experience", NamedTextColor.AQUA),
                        ComponentUtils.create("!")
                    )
                )
            )
            event.fishingContext.player
                .playSound(event.fishingContext.player.location, Sound.ENTITY_VILLAGER_YES, 1f, 1.25f)
            exp *= 2
        }

        // Award.
        val player = SMPRPG.getService(EntityService::class.java).getPlayerInstance(event.fishingContext.player)
        player.fishingSkill.addExperience(exp, SkillExperienceGainEvent.ExperienceSource.FISH)
    }
}
