package xyz.devvydont.smprpg.items

import net.momirealms.craftengine.bukkit.api.BukkitAdaptor
import net.momirealms.craftengine.core.item.Item
import net.momirealms.craftengine.core.item.ItemBuildContext
import net.momirealms.craftengine.core.plugin.compatibility.ItemSource
import xyz.devvydont.smprpg.services.ItemService

class CraftEngineItemSource(val pluginId: String): ItemSource {
    override fun plugin(): String {
        return pluginId
    }

    override fun build(id: String, context: ItemBuildContext): Item? {
        try {
            val type = CustomItemType.valueOf(id.capitalize())
            return BukkitAdaptor.adapt(ItemService.generate(type))
        }
        catch (e: IllegalArgumentException) {
            return null
        }
    }

    override fun id(item: Item): String {
        return item.id().toString()
    }
}