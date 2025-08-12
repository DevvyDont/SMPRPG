package xyz.devvydont.smprpg.services

import io.papermc.paper.persistence.PersistentDataViewHolder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.damage.DamageType
import org.bukkit.entity.Enemy
import org.bukkit.entity.Firework
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.entity.*
import org.bukkit.event.inventory.InventoryPickupItemEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scoreboard.Team
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.entity.fishing.SeaCreature
import xyz.devvydont.smprpg.entity.interfaces.IDamageTrackable
import xyz.devvydont.smprpg.entity.player.LeveledPlayer
import xyz.devvydont.smprpg.events.CustomChancedItemDropSuccessEvent
import xyz.devvydont.smprpg.events.CustomItemDropRollEvent
import xyz.devvydont.smprpg.items.ItemRarity
import xyz.devvydont.smprpg.services.AttributeService.Companion.instance
import xyz.devvydont.smprpg.services.ItemService.Companion.blueprint
import xyz.devvydont.smprpg.util.crafting.ItemUtil
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.items.DropFireworkTask
import xyz.devvydont.smprpg.util.persistence.PDCAdapters
import xyz.devvydont.smprpg.util.tasks.VoidProtectionTask
import xyz.devvydont.smprpg.util.time.TickTime
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.ExecutionException
import java.util.function.Consumer
import kotlin.math.min

/**
 * In charge of managing item drops in the world.
 * - When a player dies, their items need to be protected and can only be picked up by them
 * - When an entity drops items, they drop items for whoever helped kill it so everyone gets items
 */
class DropsService : IService, Listener {
    enum class DropFlag {
        NULL,
        DEATH,
        LOOT,
        TELEKINESIS_FAIL;

        companion object {
            fun fromInt(flag: Int): DropFlag {
                if (flag < 0 || flag > entries.toTypedArray().size) return NULL
                return entries[flag]
            }
        }
    }

    // A list of drop announcements. We don't want to announce all drops at once.
    private val dropAnnouncementQueue: MutableList<Runnable> = ArrayList<Runnable>()
    private var dropAnnouncementTask: BukkitRunnable? = null

    // The owner tag for a drop, drops cannot be picked up by players unless they own it
    val itemOwnerKey: NamespacedKey
    private val ownerNameKey: NamespacedKey

    // The flag that an owner tag drop has. 1 = Death, 2 = Drop
    val dropFlagKey: NamespacedKey

    // A timestamp on when this drop should expire from the world and disappear. Varying rarity items have different times.
    private val dropExpireKey: NamespacedKey

    private val expiredItemQueue = CopyOnWriteArrayList<Item>()
    private var itemsToUpdateQueue = CopyOnWriteArrayList<Item>()

    // A task that cleans up items.
    private var itemCleanupTask: BukkitRunnable? = null

    // A task that runs every second to check if an item should expire or not.
    private var itemTimerTask: BukkitRunnable? = null

    init {
        val plugin = plugin
        this.itemOwnerKey = NamespacedKey(plugin, "drop-owner-uuid")
        this.ownerNameKey = NamespacedKey(plugin, "drop-owner-name")
        this.dropFlagKey = NamespacedKey(plugin, "drop-flag")
        this.dropExpireKey = NamespacedKey(plugin, "expiry")

        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        rarityToTeam.clear()
        for (rarity in ItemRarity.entries) {
            val team = if (scoreboard.getTeam(rarity.name) != null)
                scoreboard.getTeam(rarity.name)
            else
                scoreboard.registerNewTeam(rarity.name)

            checkNotNull(team)
            team.color(rarity.color)
            rarityToTeam.put(rarity, team)
        }
    }

    fun getTeam(rarity: ItemRarity): Team {
        val team = rarityToTeam[rarity]
        if (team == null)
            throw IllegalStateException("Missing team for $rarity. Did you set up rarity colors correctly?")
        return team
    }

    /**
     * Adds the necessary flags to this item that makes it behave like standard loot, with delayed item deletion and
     * loot drop owning.
     * @param item The item to tag.
     * @param owner The owner of the item.
     */
    fun addDefaultLootFlags(item: ItemStack?, owner: Player) {
        if (item == null || item.type == Material.AIR) return
        val meta = item.itemMeta
        val blueprint = SMPRPG.getService(ItemService::class.java).getBlueprint(item)
        setOwner(meta, owner)
        setFlag(meta, DropFlag.LOOT)
        setExpiryTimestamp(meta, System.currentTimeMillis() + getMillisecondsUntilExpiry(blueprint.getRarity(item)))
        item.setItemMeta(meta)
    }

    /**
     * Transfers loot flags from an item to an item entity that holds the item.
     * This is useful as we want to hold loot data for as little time as possible on ItemStack instances, but it's fine
     * to keep them on Item entities. With it set up this way, we can read owner/loot data from item entities so
     * that we don't mess with any item stacking behavior on pickup.
     * @param item The item to transfer tags to. The item stack on the entity will be used.
     */
    fun transferLootFlags(item: Item) {
        // Transfer owner.

        removeOwner(item)
        item.owner = null
        val owner = getOwner(item.itemStack)
        if (owner != null) {
            val player = Bukkit.getPlayer(owner)
            if (player != null) {
                setOwner(item, player)
                item.owner = owner
                item.setCanMobPickup(false)
            }
        }

        val timestamp = getExpiryTimestamp(item.itemStack)
        if (timestamp != 0L) {
            setExpiryTimestamp(item, timestamp)
            item.isUnlimitedLifetime = true // Expiry set items expire when we tell them to.
        }

        val flag = getFlag(item.itemStack)
        if (flag != DropFlag.NULL) {
            setFlag(item, flag)
            item.isInvulnerable = true // Loot tagged items cannot die.
        }

        // Wipe item stack data.
        removeAllTags(item.itemStack)
    }

    /**
     * Adds the necessary flags to this item that makes it behave like standard death drops, with delayed item deletion and
     * loot drop owning.
     * @param item The item to tag.
     * @param player The owner of the item.
     */
    fun addDefaultDeathFlags(item: ItemStack?, player: Player) {
        if (item == null || item.type == Material.AIR)
            return
        val meta = item.itemMeta
        val blueprint = SMPRPG.getService(ItemService::class.java).getBlueprint(item)
        setOwner(meta, player)
        setFlag(meta, DropFlag.DEATH)
        setExpiryTimestamp(meta, System.currentTimeMillis() + getMillisecondsUntilExpiry(blueprint.getRarity(item)))
        item.setItemMeta(meta)
    }

    /**
     * Marks this PDC as owned by a player
     *
     * @param holder
     * @param player
     */
    fun setOwner(holder: PersistentDataHolder, player: Player) {
        holder.persistentDataContainer.set(ownerNameKey, PersistentDataType.STRING, player.name)
        holder.persistentDataContainer.set(this.itemOwnerKey, PDCAdapters.UUID, player.uniqueId)
    }

    /**
     * Untags this item as being owned by someone
     *
     * @param holder
     */
    fun removeOwner(holder: PersistentDataHolder) {
        holder.persistentDataContainer.remove(ownerNameKey)
        holder.persistentDataContainer.remove(this.itemOwnerKey)
    }

    /**
     * Gets the UUID of the owner of this PDC. If no owner, null is returned
     *
     * @param holder
     * @return
     */
    fun getOwner(holder: PersistentDataViewHolder): UUID? {
        return holder.persistentDataContainer.get(this.itemOwnerKey, PDCAdapters.UUID)
    }

    /**
     * Gets the String name of the owner of this PDC. If no owner, null is returned
     *
     * @param holder
     * @return
     */
    fun getOwnerName(holder: PersistentDataViewHolder): String? {
        return holder.persistentDataContainer.get(ownerNameKey, PersistentDataType.STRING)
    }

    /**
     * Determines if this PDC contains the owner field
     *
     * @param holder
     * @return
     */
    fun hasOwner(holder: PersistentDataViewHolder): Boolean {
        return holder.persistentDataContainer.has(this.itemOwnerKey)
    }

    fun getFlag(holder: PersistentDataViewHolder): DropFlag {
        val rawFlag: Int = holder.persistentDataContainer.getOrDefault(this.dropFlagKey, PersistentDataType.INTEGER, 0)
        return DropFlag.Companion.fromInt(rawFlag)
    }

    fun setFlag(holder: PersistentDataHolder, flag: DropFlag) {
        holder.persistentDataContainer.set(this.dropFlagKey, PersistentDataType.INTEGER, flag.ordinal)
    }

    /*
     * Checks if a PDC has an expiry timestamp set.
     */
    fun hasExpiryTimestamp(holder: PersistentDataViewHolder): Boolean {
        return holder.persistentDataContainer.has(dropExpireKey, PersistentDataType.LONG)
    }

    /*
     * Gets the expiry timestamp set on an item using System.currentTimeMillis()
     */
    fun getExpiryTimestamp(holder: PersistentDataViewHolder): Long {
        return holder.persistentDataContainer.getOrDefault(dropExpireKey, PersistentDataType.LONG, 0L)
    }

    /*
     * Flags a PDC holder to have an expiry timestamp at a certain timestamp using System.currentTimeMillis()
     */
    fun setExpiryTimestamp(holder: PersistentDataHolder, timestamp: Long) {
        holder.persistentDataContainer.set(dropExpireKey, PersistentDataType.LONG, timestamp)
    }

    /*
     * Removes the expiry field from an item. It is not needed anymore once it is picked up by a player.
     */
    fun removeExpiryTimestamp(holder: PersistentDataHolder) {
        holder.persistentDataContainer.remove(dropExpireKey)
    }

    fun removeFlag(holder: PersistentDataHolder) {
        holder.persistentDataContainer.remove(this.dropFlagKey)
    }

    fun removeAllTags(holder: PersistentDataHolder) {
        removeOwner(holder)
        removeFlag(holder)
        removeExpiryTimestamp(holder)
    }

    fun removeAllTags(itemStack: ItemStack) {
        itemStack.editMeta(Consumer { holder: ItemMeta? -> this.removeAllTags(holder!!) })
    }

    private fun stringifyTime(seconds: Long): String {
        if (seconds < 60) return seconds.toString() + "s"

        if (seconds < 3600) return (seconds / 60).toString() + "m"

        return (seconds / 3600).toString() + "h"
    }

    @Throws(RuntimeException::class)
    override fun setup() {
        val plugin = plugin

        // Make a task that will gradually pop off drop announcements in the drop queue.
        dropAnnouncementTask = object : BukkitRunnable() {
            override fun run() {
                // Is there an announcement in queue?
                if (dropAnnouncementQueue.isEmpty())
                    return

                // Pop off the next item and run it.
                val task: Runnable = dropAnnouncementQueue.first()
                task.run()
                dropAnnouncementQueue.removeFirst()
            }
        }
        dropAnnouncementTask!!.runTaskTimerAsynchronously(plugin, TickTime.INSTANTANEOUSLY, TickTime.HALF_SECOND)

        // A synchronous cleanup job for items that are considered expired. Our async task is in charge of populating
        // the list of stale items for us. The reason for this is so we can async observe items without lagging the TPS.
        itemCleanupTask = object : BukkitRunnable() {
            override fun run() {
                // Update any items that were flagged as needing it. We should only be allowed to do this ~1,000 times
                // on a single tick, so hard cap the amount of items we do.
                var toProcess = ArrayList(itemsToUpdateQueue)
                if (toProcess.size > 1000) {
                    toProcess = ArrayList(toProcess.subList(0, 1000))
                    itemsToUpdateQueue =
                        CopyOnWriteArrayList<Item>(itemsToUpdateQueue.subList(1000, itemsToUpdateQueue.size))
                } else {
                    itemsToUpdateQueue.clear()
                }

                for (item in toProcess) {
                    // Common items don't display a tag.
                    if (blueprint(item.itemStack).getRarity(item.itemStack) == ItemRarity.COMMON) {
                        item.isCustomNameVisible = false
                        continue
                    }

                    val itemName = generateItemName(item)
                    item.customName(itemName)
                }

                // Simple. Delete any items in the queue.
                for (item in expiredItemQueue.stream().toList()) {
                    // We need to try except this because it can fail. It's not a serious issue though.
                    try {
                        item.remove()
                        expiredItemQueue.remove(item)
                    } catch (e: Exception) {
                        SMPRPG.plugin.logger.warning(e.message)
                    }
                }
            }
        }
        itemCleanupTask!!.runTaskTimer(SMPRPG.plugin, 0, TickTime.TICK)

        // Make a task that will slowly decrement items on the ground so they despawn eventually.
        itemTimerTask = object : BukkitRunnable() {
            override fun run() {
                val now = System.currentTimeMillis()

                // We are about to refresh the queue so clear it.
                expiredItemQueue.clear()
                itemsToUpdateQueue.clear()


                var entities: MutableList<Item>? = null
                try {
                    entities = Bukkit.getScheduler().callSyncMethod(SMPRPG.plugin) { allLoadedItems }.get()
                } catch (e: InterruptedException) {
                    SMPRPG.plugin.logger.warning("Item cleanup query task was interrupted. " + e.message)
                } catch (e: ExecutionException) {
                    SMPRPG.plugin.logger.warning("Item cleanup query task ran into an error. " + e.message)
                }
                if (entities == null) return

                // Loop through every item loaded on the server currently.
                for (item in entities) {
                    // If this item doesn't have the expiry tag, we can't do anything with it

                    if (!hasExpiryTimestamp(item)) continue

                    // This item has an expiry time, see if it has expired, if it has then removed it
                    val expiresAt = getExpiryTimestamp(item)
                    if (expiresAt < now) {
                        expiredItemQueue.add(item)
                        continue
                    }
                    itemsToUpdateQueue.add(item)
                }
            }
        }

        itemTimerTask!!.runTaskTimerAsynchronously(plugin, 0, TickTime.seconds(1))

        VoidProtectionTask().runTaskTimer(plugin, TickTime.INSTANTANEOUSLY, TickTime.TICK)
    }

    override fun cleanup() {

        if (itemTimerTask != null)
            itemTimerTask!!.cancel()

        if (dropAnnouncementTask != null)
            dropAnnouncementTask!!.cancel()

        if (itemCleanupTask != null)
            itemCleanupTask!!.cancel()

        itemTimerTask = null
        dropAnnouncementTask = null
        itemCleanupTask = null
    }

    /**
     * Generates the name component for an item for how it should display as a nametag over an item entity.
     * The difference with this and normal item names, is that this includes stack size.
     * @param item The item.
     * @return A reusable component.
     */
    private fun generateItemName(item: Item): Component? {

        // Time left?
        var timeLeftComponent = ComponentUtils.EMPTY
        if (hasExpiryTimestamp(item)) {
            val now = System.currentTimeMillis()
            val expiresAt = getExpiryTimestamp(item)
            val secLeft = (expiresAt - now) / 1000
            val timeleft = stringifyTime(secLeft)
            timeLeftComponent = ComponentUtils.create(
                " ($timeleft) ",
                if (secLeft <= 300) NamedTextColor.RED else NamedTextColor.DARK_GRAY
            )
        }

        val blueprint = blueprint(item.itemStack)

        // Multiple items?
        var stack = ComponentUtils.create(item.itemStack.amount.toString() + "x", NamedTextColor.GRAY)
        if (item.itemStack.amount == 1) stack = ComponentUtils.EMPTY

        // Owner?
        val owner = getOwnerName(item)
        val ownerComponent = if (owner != null) ComponentUtils.create(
            String.format(" (%s)", owner),
            NamedTextColor.DARK_GRAY
        ) else ComponentUtils.EMPTY

        return ComponentUtils.merge(
            timeLeftComponent,
            stack,
            blueprint.getNameComponent(item.itemStack),
            ownerComponent
        )
    }

    /**
     * When players roll for a drop, consider their luck stat as a factor for an item
     *
     * @param event
     */
    @EventHandler
    @Suppress("unused")
    private fun onConsiderLuckRollForGear(event: CustomItemDropRollEvent) {
        val luck = instance.getAttribute<Player>(event.player, AttributeWrapper.LUCK)

        if (luck == null) return

        // Divide the luck in such a way that 100 luck means there's no effect.
        val multiplier = luck.getValue() / 100.0
        event.chance = event.chance * multiplier
    }

    /**
     * When a player dies, mark all their items as being owned by them
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    @Suppress("unused")
    private fun onPlayerDeath(event: PlayerDeathEvent) {
        // Don't lose any levels.

        event.keepLevel = true
        event.droppedExp = 0

        // Go through all the drops on the player and tag it as being owned
        for (drop in event.drops) {
            drop.editMeta(Consumer { meta: ItemMeta? ->
                val rarity =
                    SMPRPG.getService(ItemService::class.java).getBlueprint(drop).getRarity(drop)
                setOwner(meta!!, event.player)
                setFlag(meta, DropFlag.DEATH)
                setExpiryTimestamp(meta, System.currentTimeMillis() + getMillisecondsUntilExpiry(rarity))
            })
        }
    }

    /**
     * When an item spawns into the world, check if it is owned by someone
     * and transfer the PDC value over to the item entity
     * Also add rarity glow to the item
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    @Suppress("unused")
    private fun onItemSpawn(event: ItemSpawnEvent) {

        // Right away, transfer loot flags to the item entity.
        transferLootFlags(event.getEntity())

        // Set the rarity glow of the item
        event.getEntity().isGlowing = true
        val item = event.getEntity().itemStack
        val rarity = SMPRPG.getService(ItemService::class.java).getBlueprint(item).getRarity(item)
        getTeam(rarity).addEntity(event.getEntity())

        // Items with enchantments cannot die.
        if (!item.enchantments.isEmpty())
            event.getEntity().isInvulnerable = true

        // Rare+ items cannot die.
        if (rarity.ordinal >= ItemRarity.RARE.ordinal) event.getEntity().isInvulnerable = true

        val name = generateItemName(event.getEntity())

        val nameVisible = rarity.ordinal >= ItemRarity.UNCOMMON.ordinal
        if (nameVisible)
            event.getEntity().customName(name)
        event.getEntity().isCustomNameVisible = nameVisible

        // If this is a drop and the rarity is above rare, add the firework task
        if (getFlag(event.getEntity()) == DropFlag.LOOT && rarity.ordinal >= ItemRarity.RARE.ordinal) DropFireworkTask.start(
            event.getEntity()
        )
    }

    @EventHandler(priority = EventPriority.LOWEST)
    @Suppress("unused")
    private fun onFireworkDamageFromDrop(event: EntityDamageEvent) {

        // We only care about fireworks
        if (event.damageSource.damageType != DamageType.FIREWORKS)
            return

        val firework = event.damageSource.directEntity as Firework?
        if (firework == null)
            return

        // Custom fireworks don't do damage
        if (firework.entitySpawnReason != CreatureSpawnEvent.SpawnReason.CUSTOM)
            return

        event.setDamage(EntityDamageEvent.DamageModifier.BASE, 0.0)
        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    @Suppress("unused")
    private fun onEntityHasDrops(event: EntityDeathEvent) {
        val entity = SMPRPG.getService(EntityService::class.java).getEntityInstance(event.getEntity())

        // Clear the drops from the vanilla roll if desired
        if (!entity.hasVanillaDrops())
            event.drops.clear()

        // Set experience dropped to the level of the entity if it is not a player
        if (entity !is LeveledPlayer)
            event.droppedExp = entity.minecraftExperienceDropped

        // Drop override?
        if (entity.itemDrops == null)
            return

        // Is there a killer involved?
        val killer = event.getEntity().killer
        if (killer == null)
            return

        // Loop through all players that helped kill this entity and did at least some meaningful damage
        val involvedPlayers: MutableMap<Player, Double> = HashMap<Player, Double>()
        involvedPlayers.put(killer, 1.0) // Ensure killer at least gets credit for the kill

        // If this is a sea creature, the person who spawned it in should get credit no matter what.
        if (entity is SeaCreature<*> && entity.spawnedBy != null) {
            val spawner = Bukkit.getPlayer(entity.spawnedBy!!)
            if (spawner != null)
                involvedPlayers.put(spawner, 1.0)
        }

        // If this entity has a damage map go through all participants and add them to the involved players
        if (entity is IDamageTrackable)
            for (entry in entity.getDamageTracker().getPlayerDamageTracker().entries)  // Add this player damage to max hp ratio
                involvedPlayers.put(entry.key, min(entry.value / entity.getMaxHp(), 1.0))

        // Loop through every involved player
        for (entry in involvedPlayers.entries) {
            val player: Player = entry.key
            var damageRatio: Double = entry.value

            // If an entity does at least some % damage to an entity, they should get full credit for drops
            damageRatio /= entity.damageRatioRequirement
            damageRatio = min(damageRatio, 1.0)

            // Now test for coins
            // Some chance to add more money
            if (Math.random() < .2) {
                val moneyItem = ItemUtil.getOptimalCoinStacks(SMPRPG.getService(ItemService::class.java), (entity.level * 3 * (Math.random() * 3)).toInt())
                for (money in moneyItem)
                    addDefaultLootFlags(money, player)
                event.drops.addAll(moneyItem)
            }

            // Loop through all the droppable items from the entity
            val drops = entity.itemDrops
            if (drops == null || drops.isEmpty())
                return

            for (drop in drops) {
                val allInvolvedPlayersDrops: MutableList<ItemStack> = ArrayList<ItemStack>()

                // Test for items to drop
                val roll = drop.roll(player, player.inventory.itemInMainHand, damageRatio)
                if (roll != null)
                    allInvolvedPlayersDrops.addAll(roll)

                // If we didn't roll anything skip
                if (allInvolvedPlayersDrops.isEmpty())
                    continue

                // Tag all the drops as loot drops
                for (item in allInvolvedPlayersDrops)
                    addDefaultLootFlags(item, player)

                // Extend the list of items
                event.drops.addAll(allInvolvedPlayersDrops)
            }
        }
    }

    /*
     * When a player breaks a block and causes items to drop, mark it as loot for the player so they own it.
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    @Suppress("unused")
    private fun onBlockDroppedItemEvent(event: BlockDropItemEvent) {

        // Tag all the drops as loot drops. Since we are given item entities, we need to .
        for (itemEntity in event.items) {
            val item = itemEntity.itemStack
            SMPRPG.getService(ItemService::class.java).ensureItemStackUpdated(item)
            addDefaultLootFlags(item, event.player)
            transferLootFlags(itemEntity)
            itemEntity.customName(generateItemName(itemEntity))
        }
    }

    @EventHandler
    @Suppress("unused")
    private fun onRareDropObtained(event: CustomChancedItemDropSuccessEvent) {

        // Find out information about the item.
        val blueprint = SMPRPG.getService(ItemService::class.java).getBlueprint(event.item)
        val rarityOfDrop = blueprint.getRarity(event.item)

        // Start construction of the message.
        val prefix: Component = ComponentUtils.alert(
            ComponentUtils.create(rarityOfDrop.name + " DROP!!! ", rarityOfDrop.color, TextDecoration.BOLD),
            NamedTextColor.YELLOW
        )
        val player = SMPRPG.getService(ChatService::class.java).getPlayerDisplay(event.player)
        val item = event.item.displayName().hoverEvent(event.item.asHoverEvent())
        val suffix: Component = ComponentUtils.create(" found ").append(item).append(ComponentUtils.create(" from "))
            .append(event.source.getAsComponent()).append(ComponentUtils.create("!"))
        val chance: Component = ComponentUtils.create(" (" + event.formattedChance + ")", NamedTextColor.DARK_GRAY)

        // Should we tell the entire server this drop happened? Legendary always gets announced, epic only if under 5%.
        var broadcastServer = rarityOfDrop.ordinal >= ItemRarity.LEGENDARY.ordinal
        if (event.chance <= .05 && rarityOfDrop == ItemRarity.EPIC) broadcastServer = true

        // We have 3 levels of "rare drop" obtaining based on the chance.
        // If the drop is worth broadcasting to the entire server...
        if (broadcastServer) {
            val message = prefix.append(player).append(suffix).append(chance)

            // Queue the announcement.
            dropAnnouncementQueue.add(Runnable {
                for (p in Bukkit.getOnlinePlayers()) p.playSound(p.location, Sound.ENTITY_CHICKEN_EGG, 1f, 1f)
                Bukkit.broadcast(message)
            })
            return
        }

        var tellPlayer = rarityOfDrop.ordinal >= ItemRarity.RARE.ordinal
        if (event.chance < rarityOfDrop.ordinal * rarityOfDrop.ordinal / 25.0) tellPlayer = true

        if (!tellPlayer)
            return

        // Just show the message to the player since it's not THAT crazy. We should do this a bit later tho...
        val message = prefix.append(ComponentUtils.create("You")).append(suffix).append(chance)
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            event.player.sendMessage(message)
            event.player.playSound(event.player.location, Sound.ENTITY_CHICKEN_EGG, 1f, 1f)
        }, TickTime.TICK * 5)
    }

    /**
     * When something picks up a drop, and it is marked as owned by someone, don't let it be picked up. Unless of course
     * they own the drop.
     */
    @EventHandler
    @Suppress("unused")
    private fun onEntityPickupItem(event: EntityPickupItemEvent) {

        // Never allow enemies to pickup items.
        if (event.getEntity() is Enemy) {
            event.isCancelled = true
            return
        }

        val owner = getOwner(event.getEntity())
        // No owner? don't do anything.
        if (owner == null)
            return

        // Entity owns the item? Don't do anything.
        if (owner == event.getEntity().uniqueId)
            return

        // Trying to pick up an item we don't own.
        event.isCancelled = true
    }

    /**
     * Prevent hopper like blocks from picking up loot tagged items.
     * This is to prevent players being able to pick up other players drops by using redstone blocks.
     */
    @EventHandler
    @Suppress("unused")
    private fun onHopperTaggedItem(event: InventoryPickupItemEvent) {
        if (getOwner(event.item) != null)
            event.isCancelled = true

        if (event.item.owner != null)
            event.isCancelled = true
    }

    /**
     * Don't allow items to move through dimensions if tagged.
     */
    @EventHandler
    @Suppress("unused")
    fun onItemAttemptDimensionTransition(event: EntityPortalEnterEvent) {

        if (event.getEntity() !is Item)
            return

        val item = event.entity as Item
        if (getOwner(item) != null || item.owner != null) event.isCancelled = true

    }

    companion object {
        // How long items will last on the ground in seconds when marked as a drop.
        var COMMON_EXPIRE_SECONDS: Int = 60 * 20 // 20min
        var UNCOMMON_EXPIRE_SECONDS: Int = 60 * 60 // 1hr
        var RARE_EXPIRE_SECONDS: Int = 60 * 60 * 2 // 2hr
        var EPIC_EXPIRE_SECONDS: Int = 60 * 60 * 4 // 4hr
        var LEGENDARY_EXPIRE_SECONDS: Int = 60 * 60 * 12 // 12hr

        /*
     * Helper method to determine how long an item should last based on its rarity
     */
        @JvmStatic
        fun getMillisecondsUntilExpiry(rarity: ItemRarity): Long {
            return when (rarity) {
                ItemRarity.COMMON -> COMMON_EXPIRE_SECONDS
                ItemRarity.UNCOMMON -> UNCOMMON_EXPIRE_SECONDS
                ItemRarity.RARE -> RARE_EXPIRE_SECONDS
                ItemRarity.EPIC -> EPIC_EXPIRE_SECONDS
                else -> LEGENDARY_EXPIRE_SECONDS
            } * 1000L
        }

        val rarityToTeam: MutableMap<ItemRarity, Team> = HashMap<ItemRarity, Team>()

        val allLoadedItems: MutableList<Item>
            get() {
                val items = ArrayList<Item>()
                for (world in Bukkit.getWorlds())
                    items.addAll(world.getEntitiesByClass<Item>(Item::class.java))
                return items
            }
    }
}
