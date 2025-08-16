package xyz.devvydont.smprpg.events

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.entity.base.LeveledEntity
import xyz.devvydont.smprpg.util.items.LootSource

class CustomItemDropRollEvent(
    /**
     * The player involved with this drop roll
     *
     * @return
     */
    @JvmField val player: Player,
    /**
     * The tool used to trigger this rolling event
     *
     * @return
     */
    @JvmField val tool: ItemStack?,
    /**
     * The initial unchanged drop chance
     *
     * @return
     */
    val initialChance: Double, drop: ItemStack, source: LootSource
) : Event() {
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    /**
     * Returns the item currently held in the offhand
     *
     * @return
     */
    val offhand: ItemStack?
    private val drop: ItemStack
    /**
     * The chance so far that this roll is gonna use
     *
     * @return
     */
    /**
     * Update the drop chance of this event
     *
     * @param chance
     */
    @JvmField
    var chance: Double

    /**
     * Gets the source of this drop. This is typically a [LeveledEntity].
     * @return The source.
     */
    @JvmField
    val source: LootSource

    init {
        this.offhand = player.getInventory().getItemInOffHand()
        this.chance = initialChance
        this.drop = drop
        this.source = source
    }

    /**
     * The drop to be potentially dropped
     *
     * @return
     */
    fun getDrop(): ItemStack {
        return drop.clone()
    }

    companion object {
        @JvmStatic
        val handlerList: HandlerList = HandlerList()
    }
}
