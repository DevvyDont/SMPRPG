package xyz.devvydont.smprpg.recipe.core

import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.services.ItemService

/**
 * A namespaced reference to an item, e.g. `smprpg:steel_ingot` or `minecraft:gold_ingot`.
 *
 * Resolution (string -> ItemStack) and matching (does this stack count as me?) are delegated to
 * [ItemService] so the entire recipe system shares one source of truth for what an item key means.
 * Matching is intentionally type-level: an enchanted or reforged custom item still matches its base type.
 */
data class ItemIdentifier(val namespace: String, val path: String) {

    /** The canonical `namespace:path` form. */
    fun asString(): String = "$namespace:$path"

    /** Generate a fresh ItemStack for this identifier, or null if it does not resolve to a known item. */
    fun resolve(itemService: ItemService = service()): ItemStack? =
        itemService.resolveIdentifier(asString())

    /** True if the given stack is of this item type (ignores stack count and NBT such as enchants/reforges). */
    fun matches(item: ItemStack, itemService: ItemService = service()): Boolean =
        itemService.matchesIdentifier(item, asString())

    override fun toString(): String = asString()

    companion object {
        private fun service() = SMPRPG.getService(ItemService::class.java)

        /** Parse a `namespace:path` string. A bare `path` (no colon) defaults to the minecraft namespace. */
        fun parse(raw: String): ItemIdentifier {
            val trimmed = raw.trim()
            val idx = trimmed.indexOf(':')
            if (idx < 0)
                return ItemIdentifier("minecraft", trimmed.lowercase())
            return ItemIdentifier(trimmed.substring(0, idx).lowercase(), trimmed.substring(idx + 1).lowercase())
        }

        /** The identifier that best describes an existing stack (`smprpg:<key>` or `minecraft:<material>`). */
        fun of(item: ItemStack): ItemIdentifier = parse(service().getIdentifier(item))
    }
}
