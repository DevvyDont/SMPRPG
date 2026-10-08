package xyz.devvydont.smprpg.gui.enchantments

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.CustomModelData
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.attribute.AttributeModifier
import org.bukkit.block.EnchantingTable
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.block.CraftEngineBlockEnums
import xyz.devvydont.smprpg.events.CustomEnchantItemEvent
import xyz.devvydont.smprpg.gui.InterfaceUtil.getNamedItem
import xyz.devvydont.smprpg.gui.base.IRecipeDependentMenu
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.blueprints.resources.scrolls.DynamicEnchantingScroll
import xyz.devvydont.smprpg.services.AttributeService.Companion.instance
import xyz.devvydont.smprpg.services.EnchantmentService
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.craftengine.CraftEngineHelpers
import xyz.devvydont.smprpg.util.extensions.addEnchantment
import xyz.devvydont.smprpg.util.extensions.takeIfPresent
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.Symbols
import xyz.devvydont.smprpg.util.formatting.TooltipStyle
import java.util.function.Consumer

const val ITEM_SLOT = 13;
const val SCROLL_SLOT = 10;
const val BOOKSHELF_SLOT = 44;
val       INGREDIENT_SLOTS = intArrayOf(30, 31, 32, 39, 40, 41);

val       WHITELISTED_SLOTS = intArrayOf(ITEM_SLOT, SCROLL_SLOT)
val       RUNE_SLOTS = intArrayOf(18, 19, 20, 27, 28, 29, 36, 37, 38)
val       RUNE_POS_OFFSETS = arrayOf(Pair(-1, -1), Pair(0, -1), Pair(1, -1),
                                     Pair(-1, 0),  Pair(0, 0),  Pair(1, 0),
                                     Pair(-1, 1),  Pair(0, 1),  Pair(1, 1))
const val ACTION_SLOT = 16

enum class ActionButtonState {
    CALCULATING,
    DISABLED,
    ENABLED
}

enum class RuneType(val runeBlock: CraftEngineBlockEnums, val runeItem : CustomItemType) {
    RUNE_BLANK(CraftEngineBlockEnums.RUNE_BLANK, CustomItemType.RUNE_BLANK),
    RUNE_POTENTIAL(CraftEngineBlockEnums.RUNE_POTENTIAL, CustomItemType.RUNE_POTENTIAL),
    RUNE_AMBITION(CraftEngineBlockEnums.RUNE_AMBITION, CustomItemType.RUNE_AMBITION),
    RUNE_MEMORIZATION(CraftEngineBlockEnums.RUNE_MEMORIZATION, CustomItemType.RUNE_MEMORIZATION),
    RUNE_GREED(CraftEngineBlockEnums.RUNE_GREED, CustomItemType.RUNE_GREED),
    RUNE_INSIGHT(CraftEngineBlockEnums.RUNE_INSIGHT, CustomItemType.RUNE_INSIGHT),
    RUNE_FORTUITY(CraftEngineBlockEnums.RUNE_FORTUITY, CustomItemType.RUNE_FORTUITY),
    RUNE_DIVINITY(CraftEngineBlockEnums.RUNE_DIVINITY, CustomItemType.RUNE_DIVINITY)
}

class MenuEnchantingTable(owner: Player, private val enchantingTable: EnchantingTable) : MenuBase(owner, 5), IRecipeDependentMenu {
    private var actionButtonState = ActionButtonState.DISABLED
    private val runeBlocks : MutableMap<RuneType, Int> = getRuneMap(enchantingTable)
    private val shelfPower = getShelfPower(enchantingTable)
    private val runeProfKey = NamespacedKey(plugin, "rune_magic_proficiency_bonus")

    init {
        this.sounds.setMenuOpen(Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f)
        this.sounds.setMenuClose(Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 1f, 1f)
    }

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        // Prepare the inventory
        event.titleOverride(
            ComponentUtils.merge(
                ComponentUtils.create(Symbols.OFFSET_NEG_1 + Symbols.ENCHANTING_MENU, NamedTextColor.WHITE),
                ComponentUtils.create(
                    Symbols.OVERLAY_BG_OFFSET_STANDARD + Symbols.OFFSET_NEG_8 + Symbols.OFFSET_NEG_3 + "Enchant",
                    Symbols.INVENTORY_TITLE_COLOR
                )
            )
        )
        this.setMaxStackSize(100)

        // Render the UI
        this.render()
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        super.handleInventoryClicked(event)
        val slot = event.slot

        if (event.clickedInventory == null) return

        if (event.isShiftClick) {
            handleShiftClicks(event)
            actionButtonState = ActionButtonState.CALCULATING
            Bukkit.getScheduler()
                .runTaskLater(plugin, Runnable { setSlot(ACTION_SLOT, generateEnchantButton()) }, 0L)
            return
        }

        // If we are clicking in the player inventory allow it to happen. We need to allow them to manage items.
        if (event.clickedInventory!!.type == InventoryType.PLAYER) {
            event.isCancelled = false
            return
        }

        if (slot == ACTION_SLOT) {
            if (actionButtonState != ActionButtonState.ENABLED) {
                event.isCancelled = true
                return
            }
            else {
                doEnchant()
            }
        }

        if (slot !in WHITELISTED_SLOTS) {
            event.isCancelled = true
            return
        }

        // Ingredient slots are not player accessible, they are there only to show cost.
        if (slot in INGREDIENT_SLOTS) {
            event.isCancelled = true
            return
        }

        // Scroll slot should ONLY accept dynamic scrolls.
        if (slot == SCROLL_SLOT) {
            val itemKey = SMPRPG.getService(ItemService::class.java).itemTypeKey
            val cursor = event.cursor
            if (!cursor.isEmpty) {
                if (cursor.persistentDataContainer.getOrDefault(
                        itemKey,
                        PersistentDataType.STRING,
                        "ERROR"
                    ) != CustomItemType.ENCHANTING_SCROLL.name.lowercase())
                {
                    actionButtonState = ActionButtonState.CALCULATING
                    Bukkit.getScheduler()
                        .runTaskLater(plugin, Runnable { setSlot(ACTION_SLOT, generateEnchantButton()) }, 0L)
                    event.isCancelled = true
                    return
                }
            }
            actionButtonState = ActionButtonState.CALCULATING
            Bukkit.getScheduler()
                .runTaskLater(plugin, Runnable { setSlot(ACTION_SLOT, generateEnchantButton()) }, 0L)
        }

        // Item slot accepts any item, but we need to determine if the enchantment in the scroll slot is valid.
        if (slot == ITEM_SLOT) {
            // Use our cursor item for logic, since it's what actually is going to be used for the enchantment
            actionButtonState = ActionButtonState.CALCULATING
            Bukkit.getScheduler().runTaskLater(plugin, Runnable { setSlot(ACTION_SLOT, generateEnchantButton()) }, 0L)
        }
    }

    override fun handleInventoryClosed(event: InventoryCloseEvent) {
        super.handleInventoryClosed(event)
        giveItemToPlayer(ITEM_SLOT, true)
        giveItemToPlayer(SCROLL_SLOT, true)
    }

    fun generateEnchantButton(): ItemStack {
        val input = getItem(ITEM_SLOT)
        val scroll = getItem(SCROLL_SLOT)
        val book = getNamedItem(
            Material.BOOK,
            ComponentUtils.create("Enchant Item", NamedTextColor.DARK_AQUA).decoration(TextDecoration.ITALIC, false)
        )
        val valid = validateEnchant(input, scroll)
        val lore: MutableList<Component?> = ArrayList()

        actionButtonState = ActionButtonState.DISABLED
        // Check our validity cases.
        if (!valid) {
            clearIngredientDisplay()
            // Input item is missing
            if (input == null || input.type == Material.AIR) {
                lore.add(ComponentUtils.EMPTY)
                lore.add(ComponentUtils.create("Input an item to enchant.", NamedTextColor.GRAY))
                book.editMeta(Consumer { meta: ItemMeta? ->
                    meta!!.lore(ComponentUtils.cleanItalics(lore))
                })
                return book
            }

            // Input exists at this point, is a scroll missing?
            if (scroll == null || scroll.type == Material.AIR) {
                lore.add(ComponentUtils.EMPTY)
                lore.add(ComponentUtils.create("You need a Scroll of Imbuement to enchant!", NamedTextColor.RED))
                book.editMeta(Consumer { meta: ItemMeta? ->
                    meta!!.lore(ComponentUtils.cleanItalics(lore))
                })
                return book.withType(Material.BARRIER)
            }

            // Scroll and Input both exist at this point, only case left for invalid check is invalid enchantment
            val enchant = getEnchant(scroll)!!
            lore.add(ComponentUtils.EMPTY)
            var hasConflict = false

            // Special case check for OFA, super short.
            if (enchant.key == EnchantmentService.ONE_FOR_ALL.key && input.enchantments.keys.size > 0) {
                hasConflict = true
                lore.add(ComponentUtils.merge(
                    ComponentUtils.gradient("One For All", NamedTextColor.DARK_PURPLE, TextColor.color(255, 0, 0)),
                    ComponentUtils.create(" is not compatible with any other enchantments!", NamedTextColor.RED)
                ))
            }

            if (!hasConflict) {
                for (ench in input.enchantments.keys) {
                    if (enchant.conflictsWith(ench) && ench.key != enchant.key) {
                        hasConflict = true
                        lore.add(
                            ComponentUtils.create(
                                "This enchantment conflicts with one or more enchantments!",
                                NamedTextColor.RED
                            )
                        )
                        break
                    }
                }
            }

            if (!hasConflict)
                lore.add(ComponentUtils.create("This enchantment cannot be applied to this item!", NamedTextColor.RED))
            book.editMeta(Consumer { meta: ItemMeta? ->
                meta!!.lore(ComponentUtils.cleanItalics(lore))
            })
            return book.withType(Material.BARRIER)
        }

        // Populate our recipe components
        val enchant = getEnchant(scroll)!!
        val customEnch = SMPRPG.getService(EnchantmentService::class.java).getEnchantment(enchant)
        val enchantLevel = input!!.getEnchantmentLevel(enchant) + 1
        var recipe = customEnch?.getRecipe(enchantLevel)
        if (recipe == null) {
            // Either this recipe isn't implemented, or we are max level.
            lore.add(ComponentUtils.EMPTY)
            if (enchantLevel > enchant.maxLevel)
                lore.add(ComponentUtils.create("This enchantment has already reached its maximum potential!", NamedTextColor.GOLD))
            else
                lore.add(ComponentUtils.create("This is a missing recipe! Contact a developer to let them know.", NamedTextColor.RED))
            book.editMeta(Consumer { meta: ItemMeta? ->
                meta!!.lore(ComponentUtils.cleanItalics(lore))
            })
            return book.withType(Material.BARRIER)
        }

        val reqLevel = getRequiredLevelForRecipe(recipe.power)
        val lvlPlayer = SMPRPG.getService(EntityService::class.java).getPlayerInstance(player)
        if (reqLevel > lvlPlayer.magicSkill.level) {
            lore.add(ComponentUtils.EMPTY)
            lore.add(ComponentUtils.create("Your magic level is not high enough to apply this enchantment!", NamedTextColor.RED))
            lore.add(ComponentUtils.create("Required level: $reqLevel", NamedTextColor.RED))
            book.editMeta(Consumer { meta: ItemMeta? ->
                meta!!.lore(ComponentUtils.cleanItalics(lore))
            })
            return book.withType(Material.BARRIER)
        }

        if (shelfPower < reqLevel) {
            lore.add(ComponentUtils.EMPTY)
            lore.add(ComponentUtils.create("Your bookshelf power is not high enough to apply this enchantment!", NamedTextColor.RED))
            lore.add(ComponentUtils.create("Required level: $reqLevel", NamedTextColor.RED))
            book.editMeta(Consumer { meta: ItemMeta? ->
                meta!!.lore(ComponentUtils.cleanItalics(lore))
            })
            return book.withType(Material.BARRIER)
        }

        val numDivinityRunes = runeBlocks.getOrDefault(RuneType.RUNE_DIVINITY, 0)
        if (numDivinityRunes < 4 && customEnch!!.isBlessing) {
            lore.add(ComponentUtils.EMPTY)
            lore.add(ComponentUtils.merge(
                ComponentUtils.create("You do not have enough ", NamedTextColor.RED),
                ComponentUtils.create("Runes of Divinity", NamedTextColor.DARK_PURPLE),
                ComponentUtils.create(" to apply this blessing!", NamedTextColor.RED)))
            lore.add(ComponentUtils.merge(
                ComponentUtils.create("You have: ", NamedTextColor.RED),
                ComponentUtils.create(numDivinityRunes, NamedTextColor.DARK_AQUA),
                ComponentUtils.create("/", NamedTextColor.GRAY),
                ComponentUtils.create(4, NamedTextColor.AQUA)
            ))
            book.editMeta(Consumer { meta: ItemMeta? ->
                meta!!.lore(ComponentUtils.cleanItalics(lore))
            })
            return book.withType(Material.BARRIER)
        }

        var i = 0
        val ingredients = getDiscountedIngredients(recipe.ingredients!!.toSet())
        if (ingredients.size > 6)
            throw IllegalStateException("Enchanting recipes cannot be longer than 6 ItemStacks.")
        for (ingredient in ingredients) {
            setSlot(INGREDIENT_SLOTS[i], ingredient)
            i++
        }

        // Do we have the required materials to enchant?
        for (ingredient in ingredients) {
            // We need to use the base blueprint for the item, as we adjust the item stack size for display
            val bp = ItemService.blueprint(ingredient)
            val trueIngr = bp.generate()
            bp.updateItemData(trueIngr)
            if (!(player.inventory.containsAtLeast(trueIngr, ingredient.amount))) {
                lore.add(ComponentUtils.EMPTY)
                lore.add(ComponentUtils.create("This enchantment is missing reagents!", NamedTextColor.RED))
                book.editMeta(Consumer { meta: ItemMeta? ->
                    meta!!.lore(ComponentUtils.cleanItalics(lore))
                })
                return book.withType(Material.BARRIER)
            }
        }

        actionButtonState = ActionButtonState.ENABLED
        // Valid item
        lore.add(ComponentUtils.EMPTY)
        lore.add(ComponentUtils.create("Click to enchant this item!", NamedTextColor.GRAY))
        book.editMeta(Consumer { meta: ItemMeta? ->
            meta!!.lore(ComponentUtils.cleanItalics(lore))
            meta.setEnchantmentGlintOverride(true)
        })

        return book.withType(Material.ENCHANTED_BOOK)
    }

    fun generateBookshelfButton() : ItemStack {
        val shelf = getNamedItem(
            Material.BOOKSHELF,
            ComponentUtils.create("Bookshelf Power", NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false)
        )
        shelf.setData(DataComponentTypes.TOOLTIP_STYLE, TooltipStyle.INFO.key)
        val lore: MutableList<Component?> = ArrayList()
        lore.add(ComponentUtils.EMPTY)
        lore.add(ComponentUtils.create("Bookshelf Power increases the maximum potential", NamedTextColor.WHITE))
        lore.add(ComponentUtils.create("for enchantments from this table. Each Bookshelf", NamedTextColor.WHITE))
        lore.add(ComponentUtils.create("grants 1 Bookshelf Power.", NamedTextColor.WHITE))
        lore.add(ComponentUtils.EMPTY)
        lore.add(ComponentUtils.merge(
            ComponentUtils.create("This Enchanting Table currently has ", NamedTextColor.GRAY),
            ComponentUtils.create(shelfPower, NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD),
            ComponentUtils.create(" Bookshelf Power.", NamedTextColor.GRAY)
        ))
        shelf.editMeta(Consumer { meta: ItemMeta? ->
            meta!!.lore(ComponentUtils.cleanItalics(lore))
        })
        return shelf
    }

    fun validateEnchant(itemToEnchant : ItemStack?, scrollItem : ItemStack?) : Boolean {
        if (itemToEnchant == null) {
            return false
        }
        val scrollItem = this.getItem(SCROLL_SLOT)
        if (scrollItem != null) {
            val enchant : Enchantment = getEnchant(scrollItem)!!
            val customEnch = SMPRPG.getService(EnchantmentService::class.java).getEnchantment(enchant)
            val blueprint = ItemService.blueprint(itemToEnchant)
            if (blueprint.itemClassification.getItemTagKeys().contains(customEnch!!.itemTypeTag)) {
                for (ench in itemToEnchant.enchantments.keys) {
                    if (ench.conflictsWith(enchant) && ench.key != enchant.key)  // Failfast if we find a conflicting enchantment
                        return false
                }
                return true
            }
        }
        return false
    }

    fun clearIngredientDisplay() {
        for (slot in INGREDIENT_SLOTS) {
            clearSlot(slot)
        }
    }

    fun doEnchant() : Boolean {
        val scrollItem : ItemStack = this.getItem(SCROLL_SLOT)!!
        val enchant : Enchantment = getEnchant(scrollItem)!!
        val enchantItem : ItemStack = this.getItem(ITEM_SLOT)!!
        val enchantLevel = enchantItem.getEnchantmentLevel(enchant) + 1
        var recipe = SMPRPG.getService(EnchantmentService::class.java).getEnchantment(enchant)?.getRecipe(enchantLevel)
        val ingredients = getDiscountedIngredients(recipe!!.ingredients!!.toSet())

        if (player.inventory.takeIfPresent(*ingredients.toTypedArray())) {
            val numMemorizationRunes = runeBlocks.getOrDefault(RuneType.RUNE_MEMORIZATION, 0)
            val numFortuityRunes = runeBlocks.getOrDefault(RuneType.RUNE_FORTUITY, 0)
            val proficiencyBonus = (runeBlocks.getOrDefault(RuneType.RUNE_POTENTIAL, 0) * 2) + (runeBlocks.getOrDefault(RuneType.RUNE_INSIGHT, 0) * 8)
            val profInst = instance.getOrCreateAttribute(player, AttributeWrapper.MAGIC_PROFICIENCY)
            profInst.addModifier(
                AttributeModifier(
                    runeProfKey,
                    proficiencyBonus.toDouble(),
                    AttributeModifier.Operation.ADD_NUMBER
                )
            )
            profInst.save(player, AttributeWrapper.MAGIC_PROFICIENCY)
            val random = Math.random()

            val customEnch = SMPRPG.getService(EnchantmentService::class.java).getEnchantment(enchant)!!
            enchantItem.addEnchantment(customEnch, enchantItem.getEnchantmentLevel(enchant) + 1)
            val enchantEvent = CustomEnchantItemEvent(player, enchantingTable.block, enchantItem, enchant, enchantLevel)
            enchantEvent.callEvent()
            if (numFortuityRunes > 0) {
                if (random <= numFortuityRunes * 0.0025) {
                    if (attemptDoubleEnchant(enchantItem, enchantLevel, enchant))
                        enchantEvent.callEvent()
                }
            }
            if (numMemorizationRunes > 0) {
                // Roll 5% per Memorization rune to not consume scroll.
                if ((numMemorizationRunes * 0.05) < random)
                    scrollItem.amount--
                else {
                    player.playSound(player.location, Sound.ENTITY_MOOSHROOM_CONVERT, 1f, 2f)
                    player.playSound(player.location, Sound.ITEM_BOOK_PAGE_TURN, 1f, 1.25f)
                }
            }
            else
                scrollItem.amount--
            player.playSound(player.location, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f)
            ItemService.blueprint(enchantItem).updateItemData(enchantItem)
            profInst.removeModifier(runeProfKey)
            profInst.save(player, AttributeWrapper.MAGIC_PROFICIENCY)
            generateEnchantButton()
            return true
        }
        return false
    }

    fun getEnchant(scroll : ItemStack?) : Enchantment? {
        if (scroll != null) {
            val enchant = scroll.getData(DataComponentTypes.STORED_ENCHANTMENTS)!!.enchantments().keys.toTypedArray()[0]
            return enchant
        }
        return null
    }

    fun render() {
        this.setBorderFull()

        for (slotIdx in INGREDIENT_SLOTS + WHITELISTED_SLOTS) {
            this.clearSlot(slotIdx)
        }
        this.setSlot(ACTION_SLOT, generateEnchantButton())
        this.setSlot(BOOKSHELF_SLOT, generateBookshelfButton())

        var i = 0
        for (slotIdx in RUNE_SLOTS) {
            val xz = RUNE_POS_OFFSETS.get(i)
            val block = enchantingTable.block.getRelative(xz.first, -1, xz.second)
            for (runeType in RuneType.entries) {
                val blockKey = CraftEngineHelpers.getBlockKey(block)
                if (blockKey == null)
                    continue
                if (runeType.runeBlock.key == blockKey) {
                    val item = ItemService.generate(runeType.runeItem)
                    val modelData = item.getData(DataComponentTypes.CUSTOM_MODEL_DATA)
                    item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString(modelData!!.strings().get(0) + "_ui"))
                    // Rune directly under table will glow
                    if (i == 4)
                        item.editMeta { meta -> meta.setEnchantmentGlintOverride(true) }
                    this.setSlot(slotIdx, item)
                    break
                }
            }
            i++
        }
    }

    fun handleShiftClicks(event : InventoryClickEvent) {
        val fromInv : Inventory
        val toInv : Inventory

        if (event.clickedInventory!!.type == InventoryType.PLAYER) {
            fromInv = player.inventory
            toInv = this.inventory
        }
        else if (event.clickedInventory == this.inventory) {
            fromInv = this.inventory
            toInv = player.inventory

            if (event.slot !in WHITELISTED_SLOTS) {
                event.isCancelled = true
                return
            }
        }
        else
            return

        val clickedItem = event.clickedInventory!!.getItem(event.slot)
        if (clickedItem == null)
            return

        val clickedBp = ItemService.blueprint(clickedItem)
        if (clickedBp is DynamicEnchantingScroll) {
            val scrollItem = this.inventory.getItem(SCROLL_SLOT)
            if (toInv == this.inventory) {
                // Scroll slot is empty, fill it up
                if (scrollItem == null) {
                    toInv.setItem(SCROLL_SLOT, clickedItem)
                    // Remove entire stack since it filled the slot.
                    fromInv.setItem(event.slot, null)
                }
                else {
                    val maxStack = scrollItem.maxStackSize

                    // Scroll is already full, return
                    if (scrollItem.amount == maxStack)
                        return

                    if (scrollItem.isSimilar(clickedItem)) {
                        val maxRemovable = maxStack - scrollItem.amount
                        val removed = clickedItem.amount - maxRemovable
                        scrollItem.amount = maxStack + removed
                    }
                }
            }
        }
        else {
            // Anything else goes into the item slot, as long as there is room.
            if (toInv == this.inventory) {
                if (toInv.getItem(ITEM_SLOT) == null) {
                    val clickedItem = event.clickedInventory!!.getItem(event.slot)
                    if (clickedItem == null)
                        return

                    toInv.setItem(ITEM_SLOT, clickedItem)
                    fromInv.setItem(event.slot, null)
                }
            }
        }
    }

    /**
     * Returns the bookshelf power of an enchanting table at a given location
     */

    fun getShelfPower(table : EnchantingTable) : Int {
        // Not a huge fan of this algorithm, I tried using bounding boxes
        // This also does not account for raytracing from the table origin (yet)
        var numShelves = 0
        for (x in -7..7) {
            for (y in 0..7) {
                for(z in -7..7) {
                    var blockAt = table.block.getRelative(x, y, z)
                    if (blockAt.type == Material.BOOKSHELF)
                        numShelves++
                }
            }
        }
        return numShelves
    }


    fun getRuneMap(table : EnchantingTable) : MutableMap<RuneType, Int> {
        val runes = HashMap<RuneType, Int>()
        var inc = 1
        for (x in -1..1) {
            for(z in -1..1) {
                val blockAt = table.block.getRelative(x, -1, z)
                if (x == 0 && z == 0) // Block directly under table is counted twice
                    inc = 2
                else
                    inc = 1
                if (CraftEngineBlocks.isCustomBlock(blockAt)) {
                    val resourceKey = CraftEngineHelpers.getBlockKey(blockAt)
                    when (resourceKey) {
                        CraftEngineBlockEnums.RUNE_BLANK.key -> {
                            val value = runes[RuneType.RUNE_BLANK] ?: 0
                            runes[RuneType.RUNE_BLANK] = value + inc
                        }

                        CraftEngineBlockEnums.RUNE_POTENTIAL.key -> {
                            val value = runes[RuneType.RUNE_POTENTIAL] ?: 0
                            runes[RuneType.RUNE_POTENTIAL] = value + inc
                        }

                        CraftEngineBlockEnums.RUNE_AMBITION.key -> {
                            val value = runes[RuneType.RUNE_AMBITION] ?: 0
                            runes[RuneType.RUNE_AMBITION] = value + inc
                        }

                        CraftEngineBlockEnums.RUNE_MEMORIZATION.key -> {
                            val value = runes[RuneType.RUNE_MEMORIZATION] ?: 0
                            runes[RuneType.RUNE_MEMORIZATION] = value + inc
                        }

                        CraftEngineBlockEnums.RUNE_GREED.key -> {
                            val value = runes[RuneType.RUNE_GREED] ?: 0
                            runes[RuneType.RUNE_GREED] = value + inc
                        }

                        CraftEngineBlockEnums.RUNE_INSIGHT.key -> {
                            val value = runes[RuneType.RUNE_INSIGHT] ?: 0
                            runes[RuneType.RUNE_INSIGHT] = value + inc
                        }

                        CraftEngineBlockEnums.RUNE_FORTUITY.key -> {
                            val value = runes[RuneType.RUNE_FORTUITY] ?: 0
                            runes[RuneType.RUNE_FORTUITY] = value + inc
                        }

                        CraftEngineBlockEnums.RUNE_DIVINITY.key -> {
                            val value = runes[RuneType.RUNE_DIVINITY] ?: 0
                            runes[RuneType.RUNE_DIVINITY] =
                                value + 1  // Divinity is hardcoded to NOT be boosted by extra bonus from being under table.
                        }
                    }
                }
            }
        }
        return runes
    }

    fun getRequiredLevelForRecipe(power : Int) : Int {
        val discount = 1 - (runeBlocks.getOrDefault(RuneType.RUNE_AMBITION, 0) * 0.02)
        return (power * discount).toInt()
    }

    fun getDiscountedIngredients(ingredients : Set<ItemStack>) : Set<ItemStack> {
        val discount =  1 - (runeBlocks.getOrDefault(RuneType.RUNE_GREED, 0) * 0.015)
        for (ingredient in ingredients)
            ingredient.amount = Math.ceil(ingredient.amount * discount).toInt()
        return ingredients
    }

    fun attemptDoubleEnchant(enchantItem : ItemStack, enchantLevel : Int, enchant : Enchantment) : Boolean {
        // Can't double enchant past max level.
        if ((enchantLevel + 1) >= enchant.maxLevel) {
            return false
        }

        // Check that we meet the magic level requirement for the next tier of enchantment.
        val nextRecipe = SMPRPG.getService(EnchantmentService::class.java).getEnchantment(enchant)?.getRecipe(enchantLevel + 1)
        val lvlPlayer = SMPRPG.getService(EntityService::class.java).getPlayerInstance(player)
        if (getRequiredLevelForRecipe(nextRecipe!!.power) > lvlPlayer.magicSkill.level) {
            return false
        }

        enchantItem.addUnsafeEnchantment(enchant, enchantItem.getEnchantmentLevel(enchant) + 1)
        player.playSound(player.location, Sound.BLOCK_BEACON_ACTIVATE, 1f, 2f)
        player.playSound(player.location, Sound.BLOCK_BEACON_POWER_SELECT, 1f, 2f)
        return true
    }

    @EventHandler
    fun onPlayerInventoryChange(event : PlayerInventorySlotChangeEvent) {
        // Reset our icon if our inventory changes
        if ((event.player == player) && actionButtonState == ActionButtonState.ENABLED) {
            generateEnchantButton()
        }
    }
}