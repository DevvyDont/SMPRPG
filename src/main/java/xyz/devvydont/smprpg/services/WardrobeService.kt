package xyz.devvydont.smprpg.services

import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.advancement.Advancement
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.persistence.PersistentDataType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.entity.player.PlayerWardrobe
import xyz.devvydont.smprpg.entity.player.UpgradeCategory
import xyz.devvydont.smprpg.entity.player.WardrobeUpgrade
import xyz.devvydont.smprpg.events.skills.SkillLevelUpEvent
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.persistence.KeyStore
import xyz.devvydont.smprpg.util.persistence.PDCAdapters

class WardrobeService : IService, Listener {

    private lateinit var entityService: EntityService
    private lateinit var economyService: EconomyService
    private lateinit var itemService: ItemService

    /**
     * Cached set of vanilla advancements that appear in the advancement menu.
     * Built once on setup since the advancement registry doesn't change at runtime.
     */
    private lateinit var vanillaAdvancements: Set<NamespacedKey>

    override fun setup() {
        entityService = SMPRPG.getService(EntityService::class.java)
        economyService = SMPRPG.getService(EconomyService::class.java)
        itemService = SMPRPG.getService(ItemService::class.java)
        vanillaAdvancements = buildVanillaAdvancementSet()
        SMPRPG.plugin.logger.info("Tracking ${vanillaAdvancements.size} vanilla advancements for wardrobe upgrade.")
    }

    override fun cleanup() {}

    /**
     * Reads the set of completed wardrobe upgrades from a player's PDC.
     */
    fun getCompletedUpgrades(player: Player): MutableSet<WardrobeUpgrade> {
        val pdc = player.persistentDataContainer
        val ordinals = pdc.get(KeyStore.WARDROBE_UPGRADES, PersistentDataType.INTEGER_ARRAY) ?: return mutableSetOf()
        val result = mutableSetOf<WardrobeUpgrade>()
        for (ordinal in ordinals) {
            if (ordinal >= 0 && ordinal < WardrobeUpgrade.entries.size)
                result.add(WardrobeUpgrade.entries[ordinal])
        }
        return result
    }

    /**
     * Checks if a player has a specific upgrade unlocked.
     */
    fun hasUpgrade(player: Player, upgrade: WardrobeUpgrade): Boolean {
        return getCompletedUpgrades(player).contains(upgrade)
    }

    /**
     * Grants a wardrobe upgrade to a player. Returns false if already owned.
     */
    fun grantUpgrade(player: Player, upgrade: WardrobeUpgrade): Boolean {
        val completed = getCompletedUpgrades(player)
        if (!completed.add(upgrade))
            return false
        saveUpgrades(player, completed)
        syncCapacity(player)
        notifyUpgrade(player, upgrade)
        return true
    }

    /**
     * Attempts to purchase the next available coin upgrade tier.
     * Returns false if there is no next tier or the player cannot afford it.
     */
    fun purchaseCoinUpgrade(player: Player): Boolean {
        val completed = getCompletedUpgrades(player)
        val nextUpgrade = WardrobeUpgrade.COIN_UPGRADES.firstOrNull { it !in completed } ?: return false
        val cost = WardrobeUpgrade.getCoinCost(nextUpgrade)

        if (economyService.getMoney(player) < cost)
            return false

        economyService.spendMoney(player, cost)
        return grantUpgrade(player, nextUpgrade)
    }

    /**
     * Attempts to consume the next required wardrobe token from the player's inventory.
     * Returns false if the player doesn't have the right token or the upgrade is already owned.
     */
    fun consumeToken(player: Player): Boolean {
        val completed = getCompletedUpgrades(player)
        val nextUpgrade = WardrobeUpgrade.TOKEN_UPGRADES.firstOrNull { it !in completed } ?: return false
        val requiredType = getTokenItemType(nextUpgrade) ?: return false

        val inventory = player.inventory
        for (i in 0 until inventory.size) {
            val item = inventory.getItem(i) ?: continue
            val key = itemService.getItemKey(item) ?: continue
            if (key == requiredType.getKey()) {
                item.amount -= 1
                return grantUpgrade(player, nextUpgrade)
            }
        }
        return false
    }

    /**
     * Recalculates and updates the player's wardrobe max capacity based on their completed upgrades.
     */
    fun syncCapacity(player: Player) {
        val completed = getCompletedUpgrades(player)
        val wardrobe = player.persistentDataContainer.getOrDefault(
            KeyStore.PLAYER_WARDROBE, PDCAdapters.WARDROBE_ADAPTER, PlayerWardrobe()
        )
        wardrobe.maxCapacity = WardrobeUpgrade.DEFAULT_SLOTS + completed.size
        wardrobe.save(player)
    }

    /**
     * Checks if any level-based upgrades should be granted based on the player's current skill average.
     */
    fun checkAndGrantLevelUpgrades(player: Player) {
        val leveledPlayer = entityService.getPlayerInstance(player)
        val average = leveledPlayer.averageSkillLevel
        val completed = getCompletedUpgrades(player)

        for ((index, upgrade) in WardrobeUpgrade.LEVEL_UPGRADES.withIndex()) {
            if (upgrade in completed)
                continue
            if (average >= WardrobeUpgrade.LEVEL_THRESHOLDS[index])
                grantUpgrade(player, upgrade)
        }
    }

    /**
     * Returns the next available coin upgrade and its cost, or null if all are completed.
     */
    fun getNextCoinUpgrade(player: Player): Pair<WardrobeUpgrade, Long>? {
        val completed = getCompletedUpgrades(player)
        val nextUpgrade = WardrobeUpgrade.COIN_UPGRADES.firstOrNull { it !in completed } ?: return null
        return nextUpgrade to WardrobeUpgrade.getCoinCost(nextUpgrade)
    }

    /**
     * Returns the next available token upgrade, or null if all are completed.
     */
    fun getNextTokenUpgrade(player: Player): WardrobeUpgrade? {
        val completed = getCompletedUpgrades(player)
        return WardrobeUpgrade.TOKEN_UPGRADES.firstOrNull { it !in completed }
    }

    private fun saveUpgrades(player: Player, upgrades: Set<WardrobeUpgrade>) {
        val ordinals = upgrades.map { it.ordinal }.toIntArray()
        player.persistentDataContainer.set(KeyStore.WARDROBE_UPGRADES, PersistentDataType.INTEGER_ARRAY, ordinals)
    }

    private fun notifyUpgrade(player: Player, upgrade: WardrobeUpgrade) {
        player.sendMessage(
            ComponentUtils.merge(
                ComponentUtils.create("Wardrobe Slot Unlocked! ", NamedTextColor.GREEN),
                ComponentUtils.create(upgrade.displayName, upgrade.category.color)
            )
        )
        player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.5f)
    }

    @EventHandler
    fun onSkillLevelUp(event: SkillLevelUpEvent) {
        checkAndGrantLevelUpgrades(event.player)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        syncCapacity(event.player)
        checkAndGrantLevelUpgrades(event.player)
        checkAllAdvancements(event.player)
    }

    @EventHandler
    fun onAdvancementDone(event: PlayerAdvancementDoneEvent) {
        val key = event.advancement.key
        if (key !in vanillaAdvancements)
            return
        checkAllAdvancements(event.player)
    }

    /**
     * Checks if the player has completed every vanilla advancement and grants the upgrade if so.
     */
    private fun checkAllAdvancements(player: Player) {
        if (hasUpgrade(player, WardrobeUpgrade.SPECIAL_ALL_ADVANCEMENTS))
            return

        for (key in vanillaAdvancements) {
            val advancement = Bukkit.getAdvancement(key) ?: return
            val progress = player.getAdvancementProgress(advancement)
            if (!progress.isDone)
                return
        }
        grantUpgrade(player, WardrobeUpgrade.SPECIAL_ALL_ADVANCEMENTS)
    }

    /**
     * Builds the set of vanilla advancements that appear in the advancement menu.
     * Filters to only "minecraft" namespace advancements that have a display component,
     * excluding recipe unlocks which also use the advancement system.
     */
    private fun buildVanillaAdvancementSet(): Set<NamespacedKey> {
        val result = mutableSetOf<NamespacedKey>()
        val iterator = Bukkit.advancementIterator()
        while (iterator.hasNext()) {
            val advancement = iterator.next()
            val key = advancement.key
            if (key.namespace != NamespacedKey.MINECRAFT)
                continue
            if (key.key.startsWith("recipes/"))
                continue
            if (advancement.display == null)
                continue
            result.add(key)
        }
        return result
    }

    companion object {
        /**
         * Maps each token upgrade to its corresponding CustomItemType.
         */
        val TOKEN_ITEM_TYPES = mapOf(
            WardrobeUpgrade.TOKEN_COMMON to CustomItemType.WARDROBE_SLOT_COMMON,
            WardrobeUpgrade.TOKEN_UNCOMMON to CustomItemType.WARDROBE_SLOT_UNCOMMON,
            WardrobeUpgrade.TOKEN_RARE to CustomItemType.WARDROBE_SLOT_RARE,
            WardrobeUpgrade.TOKEN_EPIC to CustomItemType.WARDROBE_SLOT_EPIC,
            WardrobeUpgrade.TOKEN_LEGENDARY to CustomItemType.WARDROBE_SLOT_LEGENDARY
        )

        fun getTokenItemType(upgrade: WardrobeUpgrade): CustomItemType? {
            return TOKEN_ITEM_TYPES[upgrade]
        }
    }
}
