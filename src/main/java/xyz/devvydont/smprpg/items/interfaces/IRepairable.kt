package xyz.devvydont.smprpg.items.interfaces

import org.bukkit.inventory.ItemStack

interface IRepairable {
    val repairMaterial: MutableCollection<ItemStack>
}
