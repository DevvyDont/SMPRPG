package xyz.devvydont.smprpg.loot

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class CustomLootTable(vararg elements: LootTableMember) {
    val table: List<LootTableMember>

    init {
        require(elements.isNotEmpty()) { "You cannot create an empty table!" }
        this.table = listOf(*elements)
    }

    fun rollItems(player: Player, limit: Int): MutableCollection<ItemStack> {
        val results: MutableList<ItemStack> = ArrayList()

        // Loop through every possible drop in this table
        for (member in table) {

            // Perform a roll for every drop we have
            for (unused in 0..<member.rolls) {
                val roll = member.roll(player)

                // If we failed the roll go to the next iteration
                if (roll == null)
                    continue

                results.add(roll)

                // Have we hit the limit?
                if (results.size >= limit)
                    return results
            }
        }

        return results
    }
}
