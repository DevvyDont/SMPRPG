package xyz.devvydont.smprpg.gui.items

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.*
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.gui.InterfaceUtil.getNamedItem
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.gui.base.MenuButtonClickHandler
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.ItemRarity
import xyz.devvydont.smprpg.items.interfaces.IAttributeItem
import xyz.devvydont.smprpg.reforge.ReforgeBase
import xyz.devvydont.smprpg.reforge.ReforgeType
import xyz.devvydont.smprpg.services.EconomyService
import xyz.devvydont.smprpg.services.EconomyService.Companion.formatMoney
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import java.util.function.Consumer

class MenuReforge(player: Player) : MenuBase(player, ROWS) {
    fun getReforgeCost(rarity: ItemRarity): Long {
        return when (rarity) {
            ItemRarity.COMMON -> 250
            ItemRarity.UNCOMMON -> 500
            ItemRarity.RARE -> 1000
            ItemRarity.EPIC -> 2500
            ItemRarity.LEGENDARY -> 5000
            ItemRarity.MYTHIC -> 10000
            ItemRarity.DIVINE -> 25000
            else -> 50000
        }
    }

    val balance: Long
        /**
         * Shortcut method to get the balance of the player who owns this inventory.
         * @return The balance of the player
         */
        get() = SMPRPG.getService(EconomyService::class.java).getMoney(player)

    /**
     * Generates the button to be displayed in the anvil click slot. Updates based on the state of the interface.
     *
     * @return an ItemStack to be used as an item display.
     */
    fun generateAnvilButton(): ItemStack {
        val input = getItem(INPUT_SLOT)
        val anvil = getNamedItem(
            Material.ANVIL,
            ComponentUtils.create("Roll Random Reforge!", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false)
        )
        val lore: MutableList<Component> = ArrayList()

        // Has nothing been input yet?
        if (input == null || input.type == Material.AIR) {
            lore.add(ComponentUtils.EMPTY)
            lore.add(ComponentUtils.create("Input an item to reforge!", NamedTextColor.WHITE))
            anvil.editMeta(Consumer { meta: ItemMeta ->
                meta.lore(ComponentUtils.cleanItalics(lore))
            })
            return anvil
        }

        val blueprint = SMPRPG.getService(ItemService::class.java).getBlueprint(input)

        // Is this item not able to receive a reforge?
        if (getRandomReforge(
                blueprint.getItemClassification(),
                blueprint.getReforgeType(input)
            ).type == ReforgeType.ERROR
        ) {
            lore.add(ComponentUtils.EMPTY)
            lore.add(ComponentUtils.create("This item cannot be reforged!", NamedTextColor.RED))
            anvil.editMeta(Consumer { meta: ItemMeta ->
                meta.lore(ComponentUtils.cleanItalics(lore))
            })
            return anvil.withType(Material.BARRIER)
        }

        // Valid item
        val rarity = blueprint.getRarity(input)
        val cost = getReforgeCost(rarity)
        val bal = this.balance
        lore.add(ComponentUtils.EMPTY)
        lore.add(ComponentUtils.create("Click to reforge this item!", NamedTextColor.GRAY))
        lore.add(ComponentUtils.EMPTY)
        lore.add(ComponentUtils.create("Cost: ").append(ComponentUtils.create(formatMoney(cost), NamedTextColor.GOLD)))
        lore.add(
            ComponentUtils.create("Your balance: ").append(ComponentUtils.create(formatMoney(bal), NamedTextColor.GOLD))
        )
        if (cost > bal) lore.add(
            ComponentUtils.create(
                "Insufficient funds! You cannot afford this!",
                NamedTextColor.RED
            )
        )
        if (blueprint.isReforged(input)) {
            lore.add(ComponentUtils.EMPTY)
            lore.add(ComponentUtils.create("NOTE: Previous reforge will be overwritten!", NamedTextColor.RED))
        }
        anvil.editMeta(Consumer { meta: ItemMeta ->
            meta.lore(ComponentUtils.cleanItalics(lore))
            if (bal >= cost) meta.setEnchantmentGlintOverride(true)
        })

        return anvil
    }

    /**
     * Randomly rolls a reforge.
     *
     * @param classification The classification of the item that is being reforged.
     * @param exclude The reforge type to exclude when rolling a reforge. Can be null to consider all available reforges.
     * @return An randomly selected instance of a registered ReforgeBase singleton that is compatible with the classification.
     */
    fun getRandomReforge(classification: ItemClassification, exclude: ReforgeType?): ReforgeBase {
        // Construct a list of reforges to choose from by looping through all reforges and analyzing its compatibility.

        val choices: MutableList<ReforgeBase> = ArrayList<ReforgeBase>()
        for (type in ReforgeType.entries) {
            // Do we want to exclude this reforge?

            if (type == exclude) continue

            // Is this reforge allowed to be rolled in a reforge station?
            if (!type.isRollable) continue

            // Is this reforge allowed for this item type?
            if (!type.isAllowed(classification)) continue

            // Valid!
            choices.add(SMPRPG.getService(ItemService::class.java).getReforge(type)!!)
        }

        // If we found no valid reforges, default to the error reforge type. Error reforge type should be handled by caller
        if (choices.isEmpty()) return SMPRPG.getService(ItemService::class.java)
            .getReforge(ReforgeType.ERROR)!!

        // Return a random choice
        return choices[(Math.random() * choices.size).toInt()]
    }

    /**
     * Called every time we click the reforge button regardless of the state of the GUI.
     */
    fun reforge() {
        // Check if we have an item in the input

        val item = getItem(INPUT_SLOT)
        if (item == null) {
            playInvalidAnimation()
            return
        }

        // Check if this item is able to store attributes. Reforges can't add attributes to attributeless items!
        val blueprint = SMPRPG.getService(ItemService::class.java).getBlueprint(item)
        if (blueprint !is IAttributeItem) {
            playInvalidAnimation()
            return
        }

        // Analyze the current reforge on the gear and determine if we can even roll another reforge without erroring
        val currentReforgeType = blueprint.getReforgeType(item)
        val newReforge = getRandomReforge(blueprint.getItemClassification(), currentReforgeType)
        var success = newReforge.type != ReforgeType.ERROR

        // Determine if we can afford this reforge
        val cost = getReforgeCost(blueprint.getRarity(item))
        if (this.balance < cost) success = false

        // Apply reforge and take their money if we had no issues
        if (success) {
            newReforge.apply(item)
            SMPRPG.getService(EconomyService::class.java).spendMoney(player, cost)
            val player = SMPRPG.getService(EntityService::class.java).getPlayerInstance(this.player)
            player.magicSkill
                .addExperience((blueprint.getRarity(item).ordinal + 1) * blueprint.getPowerRating() / 10)
        }

        val soundOrigin = player.location.add(player.location.getDirection().normalize().multiply(2))
        player.world
            .playSound(soundOrigin, if (success) Sound.BLOCK_ANVIL_USE else Sound.ENTITY_VILLAGER_NO, .5f, .75f)
        blueprint.updateItemData(item)

        if (success) playSuccessAnimation()
        else playInvalidAnimation()
    }

    /**
     * Renders the GUI.
     */
    fun render() {
        this.setBorderFull()
        this.clearSlot(INPUT_SLOT)
        this.setButton(BUTTON_SLOT, generateAnvilButton()) { event: InventoryClickEvent ->
            if (event.action == InventoryAction.PICKUP_ALL) reforge()
        }
    }

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        super.handleInventoryOpened(event)
        this.render()
        event.titleOverride(ComponentUtils.create("Tool Reforging", NamedTextColor.BLACK))
    }

    public override fun handleInventoryClicked(event: InventoryClickEvent) {
        super.handleInventoryClicked(event)

        // Treat click events as a whitelist style
        event.isCancelled = true

        if (event.clickedInventory == null) return

        // Update the anvil button on the next tick to react to the state of the GUI
        Bukkit.getScheduler().runTaskLater(plugin, Runnable { setSlot(BUTTON_SLOT, generateAnvilButton()) }, 0L)

        // If we are clicking in the player inventory allow it to happen. We need to allow them to manage items.
        if (event.clickedInventory!!.type == InventoryType.PLAYER) {
            event.isCancelled = false
            return
        }

        // If we are clicking in the input slot allow it to happen. The user owns this slot.
        if (event.clickedInventory == inventory && event.slot == INPUT_SLOT) {
            event.isCancelled = false
            return
        }

        // If the event is canceled at this point and we clicked a slot that wasn't something we have control over,
        // play invalid animation
        if (event.isCancelled && event.rawSlot != INPUT_SLOT && event.rawSlot != BUTTON_SLOT) playInvalidAnimation()
    }

    /**
     * When the inventory closes, make sure the item in the input slot is not lost.
     *
     * @param event The inventory close event.
     */
    public override fun handleInventoryClosed(event: InventoryCloseEvent) {
        super.handleInventoryClosed(event)
        giveItemToPlayer(INPUT_SLOT, true)
    }

    companion object {
        const val ROWS: Int = 5

        const val INPUT_SLOT: Int = 13
        const val BUTTON_SLOT: Int = 31
    }
}
