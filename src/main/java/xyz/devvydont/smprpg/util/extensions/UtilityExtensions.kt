package xyz.devvydont.smprpg.util.extensions

import com.google.common.base.Preconditions
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.enchantments.CustomEnchantment
import xyz.devvydont.smprpg.services.ItemService

fun Inventory.takeIfPresent(vararg r: Pair<ItemStack, Int>): Boolean {

    val req : MutableSet<Pair<ItemStack, Int>> = HashSet()
    for (i in r) {
        val bp = ItemService.blueprint(i.first)
        val realItem = bp.generate()
        bp.updateItemData(realItem)
        req.add(Pair<ItemStack, Int>(realItem, i.second))
    }

    if (req.isEmpty()) return true

    val remaining = req.associate { it.first to it.second }.toMutableMap()

    // PASS 1 — verify we have enough items
    for (stack in contents) {
        if (stack == null) continue

        for ((target, count) in remaining) {
            if (count <= 0) continue
            if (!stack.isSimilar(target)) continue

            val newRemaining = count - stack.amount
            remaining[target] = maxOf(0, newRemaining)
        }
    }

    if (remaining.values.any { it > 0 })
        return false

    // PASS 2 — remove items
    val toRemove = req.associate { it.first to it.second }.toMutableMap()

    for (stack in contents) {
        if (stack == null) continue

        for ((target, count) in toRemove) {
            if (count <= 0) continue
            if (!stack.isSimilar(target)) continue

            val taken = minOf(stack.amount, count)
            stack.amount -= taken
            toRemove[target] = count - taken
        }
    }

    return true
}

/**
 * Given a list of ItemStacks, use their amounts as the take value
 * for the item type.
 */
fun Inventory.takeIfPresent(vararg req : ItemStack) : Boolean {
    var itemsToTake = ArrayList<Pair<ItemStack, Int>>()
    for (i in req) {
        itemsToTake.add(Pair(i, i.amount))
    }
    return takeIfPresent(*itemsToTake.toTypedArray())
}

/**
 * Shorthand for addEnchantment that allows for CustomEnchantment support
 * Also overrides the canEnchant functionality to work with ItemTypeTags
 */
fun ItemStack.addEnchantment(enchant: CustomEnchantment, level: Int) {
    val blueprint = ItemService.blueprint(this)
    val canEnchant = blueprint.itemClassification.getItemTagKeys().contains(enchant.itemTypeTag)
    Preconditions.checkArgument(enchant != null, "CustomEnchantment cannot be null")
    require(!((level < enchant.enchantment.getStartLevel()) || (level > enchant.maxLevel))) { "Enchantment level is either too low or too high (given " + level + ", bounds are " + enchant.enchantment.startLevel + " to " + enchant.enchantment.maxLevel + ")" }
    require(canEnchant) { "Specified enchantment cannot be applied to this itemstack" }

    addUnsafeEnchantment(enchant.enchantment, level)
}

/**
 * Given two item ItemStacks, transfer amt quantity from itemStack1 to itemStack2
 * @param amt           Amount of items to transfer
 * @param itemStack    I temStack that is being transfered to
 * @return              true if the operation was successful, false if not.
 */
fun ItemStack.transfer(amt: Int, itemStack: ItemStack) : Boolean {
    if (!this.isSimilar(itemStack)) return false

    if (amt > this.amount) {
        itemStack.amount += this.amount
        this.amount = 0
    }
    else {
        this.amount -= amt
        itemStack.amount += amt
    }
    return true
}