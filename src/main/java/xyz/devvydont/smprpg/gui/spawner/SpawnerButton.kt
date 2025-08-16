package xyz.devvydont.smprpg.gui.spawner

import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

abstract class SpawnerButton {
    abstract fun getItem(gui: InterfaceSpawnerMainMenu?): ItemStack?

    abstract fun handleClick(gui: InterfaceSpawnerMainMenu?, player: Player?, clickType: ClickType?)
}
