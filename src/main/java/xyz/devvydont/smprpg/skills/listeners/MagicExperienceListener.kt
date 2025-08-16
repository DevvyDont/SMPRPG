package xyz.devvydont.smprpg.skills.listeners

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.inventory.BrewEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.AnvilInventory
import org.bukkit.inventory.BrewerInventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.entity.player.LeveledPlayer
import xyz.devvydont.smprpg.events.skills.SkillExperienceGainEvent.ExperienceSource
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemEnchantedBook
import xyz.devvydont.smprpg.items.interfaces.IAttributeItem
import xyz.devvydont.smprpg.services.EnchantmentService
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.services.ItemService
import java.util.function.Consumer
import kotlin.math.max

class MagicExperienceListener : Listener {
    private val experienceStowKey = NamespacedKey("smprpg", "stowed_exp")

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    private fun isBrewingOutputSlot(slot: Int): Boolean {
        return slot <= BREWING_OUTPUT_RIGHT && slot >= BREWING_OUTPUT_LEFT
    }

    private fun getExperienceForPotionExtract(item: ItemStack): Int {
        if (item.itemMeta !is PotionMeta)
            return 0

        val meta = item.itemMeta as PotionMeta

        var exp = 0

        // Find a potion type addition
        when (item.type) {
            Material.LINGERING_POTION -> exp += 50
            Material.SPLASH_POTION -> exp += 25
            else -> {}
        }

        // Find a potion effect addition
        if (meta.basePotionType != null) {
            when (meta.basePotionType) {
                PotionType.WATER -> exp += 2
                PotionType.MUNDANE, PotionType.AWKWARD, PotionType.THICK -> exp += 5
                else -> {}
            }
        }

        // Consider the base potion effect
        val potionType = meta.basePotionType
        if (potionType != null)
            for (effect in potionType.potionEffects)
                exp += ((effect.amplifier + 1) * effect.duration / 10)

        // Consider the extra potion effects
        for (effect in meta.customEffects)
            exp += ((effect.amplifier + 1) * effect.duration / 20)

        return exp
    }

    /**
     * Given any item stack, stows experience on it so that when someone picks it up from the brewing stand it will
     * award experience. Also supports stacking experience from previous brews
     *
     * @param item
     */
    private fun stowExperience(item: ItemStack, addition: Int) {
        item.editMeta(Consumer { meta: ItemMeta ->
            val oldXp: Int = meta.persistentDataContainer.getOrDefault(this.experienceStowKey, PersistentDataType.INTEGER, 0)
            meta.persistentDataContainer.set(this.experienceStowKey, PersistentDataType.INTEGER, oldXp + addition)
        })
    }

    private fun stowPotionExperience(item: ItemStack) {
        stowExperience(item, getExperienceForPotionExtract(item))
    }

    /**
     * Awards experience to a player from stowed experience on an itemstack and resets the stowed xp to 0
     *
     * @param player
     * @param item
     */
    private fun awardExperience(player: LeveledPlayer, item: ItemStack, source: ExperienceSource) {
        // Extract the experience from the item
        val exp: Int = item.persistentDataContainer.getOrDefault(this.experienceStowKey, PersistentDataType.INTEGER, 0)

        // Remove the stored experience on the item
        item.editMeta(Consumer { meta: ItemMeta ->
            meta.persistentDataContainer.remove(this.experienceStowKey)
        })

        // Award!
        if (exp > 0)
            player.magicSkill.addExperience(exp, source)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @Suppress("unused")
    private fun onEnchant(event: EnchantItemEvent) {
        if (event.isCancelled)
            return

        // Set a base experience earning for this enchant. At the start it is just the level of enchant we are performing.
        var exp = event.expLevelCost + 10

        // Loop through every enchant and see how much magic experience it gives.
        for (enchantment in SMPRPG.getService(EnchantmentService::class.java).getCustomEnchantments(event.enchantsToAdd))
            exp += enchantment.getMagicExperience()

        // Magic multiplier if the item has a power rating
        var multiplier = 1.0
        val blueprint = SMPRPG.getService(ItemService::class.java).getBlueprint(event.item)
        if (blueprint is IAttributeItem)
            multiplier += blueprint.getPowerRating() / 20.0

        val player = SMPRPG.getService(EntityService::class.java).getPlayerInstance(event.enchanter)
        player.magicSkill.addExperience((exp * multiplier).toInt(), ExperienceSource.ENCHANT)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @Suppress("unused")
    private fun onBrewEvent(event: BrewEvent) {
        if (event.isCancelled)
            return
        for (result in event.results) stowPotionExperience(result)
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    @Suppress("unused")
    private fun onBrewExtract(event: InventoryClickEvent) {
        val clickedInv = event.clickedInventory
        if (clickedInv == null)
            return

        // Only listen to brewing inventories and anvil inventories
        if (clickedInv.type != InventoryType.BREWING)
            return

        val brewerInventory = clickedInv as BrewerInventory

        // Only listen to output slot clicks
        if (!isBrewingOutputSlot(event.slot))
            return

        // Evaluate the item we are extracting, if it isn't there then ignore
        val extracted = brewerInventory.getItem(event.slot)
        if (extracted == null || extracted.type == Material.AIR)
            return

        // Determine experience
        val player = SMPRPG.getService(EntityService::class.java).getPlayerInstance(event.whoClicked as Player)
        awardExperience(player, extracted, ExperienceSource.BREW)
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    @Suppress("unused")
    fun onAnvilExtract(event: InventoryClickEvent) {
        // If we aren't clicking in an anvil we don't care

        if (event.clickedInventory !is AnvilInventory)
            return

        val anvil = event.clickedInventory as AnvilInventory

        // If the result is not real we don't care
        if (anvil.result == null)
            return

        // If we aren't clicking on the output slot we don't care
        if (event.slotType != InventoryType.SlotType.RESULT)
            return

        // Evaluate the item we are extracting, if it isn't there then ignore
        val extracted: ItemStack? = anvil.result
        if (extracted == null || extracted.type == Material.AIR)
            return

        // Determine experience
        val player = SMPRPG.getService(EntityService::class.java).getPlayerInstance(event.whoClicked as Player)
        awardExperience(player, extracted, ExperienceSource.FORGE)
    }

    /**
     * Add magic experience to items in the anvil.
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    @Suppress("unused")
    fun onAnvilPrepare(event: PrepareAnvilEvent) {

        // If the repair cost is too high for the player to forge it don't stow exp
        val player = event.view.player as Player
        if (event.view.repairCost > player.level)
            return

        val result = event.result
        if (result == null)
            return

        val blueprint = SMPRPG.getService(ItemService::class.java).getBlueprint(result)
        var multiplier = 1
        if (blueprint is IAttributeItem) multiplier = blueprint.getPowerRating()
        else if (blueprint is ItemEnchantedBook && blueprint.getEnchantment(result) != null) multiplier =
            blueprint.getRarity(result).ordinal + 3

        var exp = event.view.repairCost * multiplier

        // Something isn't right if we have this much....
        if (exp > 5000)
            exp = 5000

        stowExperience(result, exp)
        event.result = result
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    @Suppress("unused")
    private fun onPickupExperience(event: PlayerPickupExperienceEvent) {
        val player = SMPRPG.getService(EntityService::class.java).getPlayerInstance(event.getPlayer())
        player.magicSkill.addExperience(1, ExperienceSource.XP)
    }


    companion object {
        const val BREWING_OUTPUT_LEFT: Int = 0
        const val BREWING_OUTPUT_MIDDLE: Int = 1
        const val BREWING_OUTPUT_RIGHT: Int = 2
    }
}
