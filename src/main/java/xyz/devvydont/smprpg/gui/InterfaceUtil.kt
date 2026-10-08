package xyz.devvydont.smprpg.gui

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

object InterfaceUtil {
    @JvmStatic
    fun getNamedItem(material: Material, name: Component): ItemStack {
        val item = ItemStack(material)
        val meta = item.itemMeta
        meta.displayName(name.decoration(TextDecoration.ITALIC, false))
        item.setItemMeta(meta)
        return item
    }

    @JvmStatic
    fun getNamedItem(itemType: CustomItemType, name: Component): ItemStack {
        val item = ItemService.generate(itemType)
        val meta = item.itemMeta
        meta.displayName(name.decoration(TextDecoration.ITALIC, false))
        item.setItemMeta(meta)
        return item
    }

    fun getNamedItemWithDescription(material: Material, name: Component, lines: List<Component>): ItemStack {
        val item = getNamedItem(material, name)
        item.lore(ComponentUtils.cleanItalics(lines))
        return item
    }

    fun getNamedItemWithDescription(itemType: CustomItemType, name: Component, lines: List<Component>): ItemStack {
        val item = getNamedItem(itemType, name)
        item.lore(ComponentUtils.cleanItalics(lines))
        return item
    }

    @JvmStatic
    fun getNamedItemWithDescription(material: Material, name: Component, vararg lines: Component): ItemStack {
        return getNamedItemWithDescription(material, name, listOf(*lines))
    }

    @JvmStatic
    fun getNamedItemWithDescription(itemType: CustomItemType, name: Component, vararg lines: Component): ItemStack {
        return getNamedItemWithDescription(itemType, name, listOf(*lines))
    }
}
