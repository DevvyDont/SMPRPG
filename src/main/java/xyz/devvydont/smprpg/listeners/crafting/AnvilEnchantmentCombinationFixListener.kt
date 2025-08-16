package xyz.devvydont.smprpg.listeners.crafting

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.Repairable
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.enchantments.CustomEnchantment
import xyz.devvydont.smprpg.enchantments.calculator.EnchantmentCalculator
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemEnchantedBook
import xyz.devvydont.smprpg.items.interfaces.ReforgeApplicator
import xyz.devvydont.smprpg.reforge.ReforgeType
import xyz.devvydont.smprpg.services.EnchantmentService
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.listeners.ToggleableListener
import java.util.function.Consumer

/**
 * Fixes anvil logic to conform to our plugin's rules. When you combine two items in an anvil, we need to dynamically
 * work out a result since there are many scenarios that can occur from an anvil combination.
 */
class AnvilEnchantmentCombinationFixListener : ToggleableListener() {
    @JvmRecord
    data class EnchantmentCombination(val result: ItemStack, val cost: Int)

    /**
     * Combines the enchantments of a 2nd item stack on to the first. Respects limits such as ItemRarity as well as
     * returns a cost to use if this was an anvil event.
     * @return A combination result from two inputs.
     */
    fun combineEnchantments(input: ItemStack, combine: ItemStack): EnchantmentCombination {
        // Retrieve the blueprints of both items

        val inputBlueprint = SMPRPG.getService(ItemService::class.java).getBlueprint(input)
        val combineBlueprint = SMPRPG.getService(ItemService::class.java).getBlueprint(combine)

        // Edge case, do we have two different types of enchanted books? We can't do this
        if (inputBlueprint is ItemEnchantedBook && combineBlueprint is ItemEnchantedBook) {
            val inputEnchantment = inputBlueprint.getEnchantment(input)
            val combineEnchantment = combineBlueprint.getEnchantment(combine)
            if (inputEnchantment != null && inputEnchantment != combineEnchantment) return EnchantmentCombination(
                input,
                0
            )
        }

        // Grab the enchantments we are trying to apply to the first item
        val toApply: MutableList<CustomEnchantment> = ArrayList()
        // If the item is a book, we have to retrieve the enchants differently
        val combineEnchantMap =
            if
                (!combineBlueprint.isCustom() && combine.type == Material.ENCHANTED_BOOK) (combine.itemMeta as EnchantmentStorageMeta).storedEnchants
            else
                combine.enchantments

        // Transform vanilla enchants to our enchant wrapper
        for (entry in combineEnchantMap.entries) toApply.add(
            SMPRPG.getService(EnchantmentService::class.java).getEnchantment(entry.key)!!
                .build(entry.value!!)
        )

        // Now we actually do the enchantment application. First, we need to figure out how many new enchants
        // this item is allowed to have so it doesn't go over its limit.
        var newEnchantmentSlots = inputBlueprint.getMaxAllowedEnchantments(input) - input.enchantments.size

        // Attempt to apply all the enchantments from the 2nd item.
        var cost = 0
        val result = input.clone()
        toApply.shuffle()
        var resultIsDifferent = false
        for (enchantment in toApply) {
            var levelPresent = result.getEnchantmentLevel(enchantment.enchantment)
            var levelToUse = combine.getEnchantmentLevel(enchantment.enchantment)

            // Fix for book logic
            if (result.itemMeta is EnchantmentStorageMeta) {
                val meta = result.itemMeta as EnchantmentStorageMeta
                levelPresent = meta.getStoredEnchantLevel(enchantment.enchantment)
            }
            if (combine.itemMeta is EnchantmentStorageMeta) {
                val meta = result.itemMeta as EnchantmentStorageMeta
                levelPresent = meta.getStoredEnchantLevel(enchantment.enchantment)
            }

            // Skip this enchantment if it is not valid for the input item if it is not a book.
            if (inputBlueprint !is ItemEnchantedBook && !inputBlueprint.getItemClassification().itemTagKeys
                    .contains(enchantment.getItemTypeTag())
            ) continue

            // Skip this enchantment if the input already contains a conflicting enchant.
            var conflicts = false
            for (appliedEnchantment in result.enchantments.keys) if (appliedEnchantment != enchantment.enchantment && appliedEnchantment.conflictsWith(
                    enchantment.enchantment
                )
            ) conflicts = true
            if (conflicts) continue

            // Skip this enchantment if a higher level is already present.
            if (levelPresent > levelToUse) continue

            // Skip this enchantment if we don't have room for another enchantment and we don't have it.
            if (levelPresent <= 0 && newEnchantmentSlots <= 0) continue

            // Skip this enchantment if the two levels are equal and also the max level
            if (levelPresent == levelToUse && levelToUse >= enchantment.enchantment.maxLevel) continue

            // If this enchantment is new, decrement the slots we have
            if (levelPresent <= 0) newEnchantmentSlots--

            // If the enchantment levels are equal, we should use a level higher if the level can go higher
            if (levelPresent == levelToUse && levelToUse < enchantment.enchantment.maxLevel) levelToUse++

            // Apply the enchantment to the result, again depending on what item we have we have to do it differently
            if (result.itemMeta is EnchantmentStorageMeta) {
                val meta = result.itemMeta as EnchantmentStorageMeta
                meta.addStoredEnchant(enchantment.enchantment, levelToUse, true)
                result.setItemMeta(meta)
            } else result.addUnsafeEnchantment(enchantment.enchantment, levelToUse)

            resultIsDifferent = true
            cost += enchantment.minimumCost.additionalPerLevelCost() * levelToUse + enchantment.minimumCost
                .baseCost()
        }

        // Did we even make a change? mark this combo as invalid
        if (!resultIsDifferent) return EnchantmentCombination(result, 0)

        // Now add on the repair cost from the item
        if (result.itemMeta is Repairable){
            val repairable = result.itemMeta as Repairable
            cost += repairable.repairCost
        }

        val resultBlueprint = SMPRPG.getService(ItemService::class.java).getBlueprint(result)
        resultBlueprint.updateItemData(result)
        return EnchantmentCombination(result, cost)
    }

    /**
     * Allow anvil combinations to go as far as we want
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    @Suppress("unused")
    private fun onAnvilPreperation(event: PrepareAnvilEvent) {
        event.view.maximumRepairCost = 999999
    }

    /**
     * Completely override how enchantment combination behavior works when trying to combine two similar items.
     * Allow two items of the same type to be combined
     *
     * @param event
     */
    @EventHandler
    @Suppress("unused")
    private fun onAnvilCombination(event: PrepareAnvilEvent) {
        val anvil = event.inventory
        val firstItemStack = anvil.firstItem
        val secondItemStack = anvil.secondItem

        // If either item is null, we don't care about this event
        if (firstItemStack == null || secondItemStack == null) return

        // We know we have items in both slots, let's set result to null to be safe and prevent unwanted behavior
        event.view.repairCost = 1
        event.result = null

        plugin

        // We have to support books being supplied to us.
        val firstBlueprint = SMPRPG.getService(ItemService::class.java).getBlueprint(firstItemStack)
        val secondBlueprint = SMPRPG.getService(ItemService::class.java).getBlueprint(secondItemStack)
        val inputIsBook = EnchantmentCalculator.isEnchantedBook(firstBlueprint, firstItemStack)
        val combineItemIsBook = EnchantmentCalculator.isEnchantedBook(secondBlueprint, secondItemStack)

        // If the first item is a book and the second item is a non book, we can't do anything
        if (inputIsBook && !combineItemIsBook) return

        // If both items are non books and the items are not of the same type, we can't do anything. (Trying to combine a emerald chestplate with a diamond chestplate)
        if (!inputIsBook && !combineItemIsBook && !firstBlueprint.isItemOfType(secondItemStack)) return

        // Perform the combination!!!
        val combination = combineEnchantments(firstItemStack, secondItemStack)
        // If the cost is 0, that means this enchantment combination got nothing done
        if (combination.cost <= 0) {
            event.view.repairCost = 1
            event.result = null
            return
        }

        // Is the player allowed to perform this combination? Check if all the enchants are unlocked by them.
        val information: MutableList<Component?> = ArrayList()
        information.add(ComponentUtils.EMPTY)
        var allowed = true
        val magicLevel = SMPRPG.getService(EntityService::class.java)
            .getPlayerInstance(event.view.player as Player).magicSkill.level

        val enchantmentsToAnalyze = if (SMPRPG.getService(ItemService::class.java)
                .getBlueprint(combination.result) is ItemEnchantedBook
        ) (combination.result.itemMeta as EnchantmentStorageMeta).storedEnchants.entries else combination.result.enchantments.entries
        for (entries in enchantmentsToAnalyze) {
            // Magic skill req met?

            val enchantment = SMPRPG.getService(EnchantmentService::class.java)
                .getEnchantment(entries.key!!)
            val requirement = enchantment!!.getSkillRequirement()
            if (requirement > magicLevel) {
                information.add(
                    ComponentUtils.create(
                        "- Need Magic $requirement to apply locked enchantment ",
                        NamedTextColor.RED
                    ).append(
                        enchantment.getDisplayName().color(enchantment.enchantColor)
                    )
                )
                allowed = false
                continue
            }

            // Level of enchant is too high?
            // Determine how many levels we have over the requirement and what magic level would give us the max level
            val enchantLevelRequirement = enchantment.getSkillRequirementForLevel(entries.value!!)
            if (enchantLevelRequirement > magicLevel) {
                information.add(
                    ComponentUtils.create(
                        "- Need Magic $enchantLevelRequirement to apply high level enchantment ",
                        NamedTextColor.RED
                    ).append(
                        enchantment.enchantment.displayName(entries.value!!)
                            .color(enchantment.enchantColor)
                    )
                )
                allowed = false
            }
        }

        information.add(ComponentUtils.EMPTY)
        information.add(
            ComponentUtils.create("Experience Cost: ")
                .append(ComponentUtils.create(combination.cost.toString() + " Levels", NamedTextColor.GREEN))
        )

        if (!allowed) event.view.repairCost = 999999
        else event.view.repairCost = combination.cost

        combination.result.editMeta(Consumer { meta: ItemMeta? ->
            val newMeta = meta!!.lore()
            newMeta!!.addAll(information)
            meta.lore(ComponentUtils.cleanItalics(newMeta))
            if (meta is Repairable) meta.repairCost = meta.repairCost + 2
        })
        event.result = combination.result
    }

    /*
     * Listen for when we are trying to apply a reforge to an item. This should calculate AFTER we do our enchantment
     * combining shenanigans since this case is much simpler.
     */
    @EventHandler(priority = EventPriority.HIGH)
    @Suppress("unused")
    private fun onCombineReforgeWithItem(event: PrepareAnvilEvent) {
        // First, we need to make sure we are trying to do something valid before we can proceed.
        // Are both item slots filled?

        if (event.inventory.firstItem == null || event.inventory.secondItem == null) return

        plugin

        // Is the second slot a reforge stone?
        val secondBlueprint = SMPRPG.getService(ItemService::class.java).getBlueprint(event.inventory.secondItem!!)
        if (secondBlueprint !is ReforgeApplicator)
            return
        val reforgeApplicator = secondBlueprint as ReforgeApplicator

        // Can the first item have the reforge applied to it? And is the reforge actually valid?
        val reforgeType: ReforgeType = reforgeApplicator.getReforgeType()
        val reforge = SMPRPG.getService(ItemService::class.java).getReforge(reforgeType)
        if (reforge == null) return

        val input = SMPRPG.getService(ItemService::class.java).getBlueprint(event.inventory.firstItem!!)
        // Can this reforge type be applied to this type of item?
        if (!reforgeType.isAllowed(input.getItemClassification())) return

        // The item input is allowed to have this reforge stone's reforge. Let's set a result for them to choose if
        // they want.
        val result = event.inventory.firstItem!!.clone()
        reforge.apply(result)
        event.result = result
        event.view.repairCost = reforgeApplicator.getExperienceCost()
    }
}
