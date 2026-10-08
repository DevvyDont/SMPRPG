package xyz.devvydont.smprpg.market.bazaar

import org.bukkit.Material
import xyz.devvydont.smprpg.items.CustomItemType

/**
 * Runtime tree node representing a bazaar category or subcategory.
 * Built by scanning item paths at startup; not persisted.
 */
class BazaarCategoryNode(
    val name: String,
    val path: String,
    val children: List<BazaarCategoryNode>,
    val items: List<BazaarItem>
) {

    val allItems: List<BazaarItem> by lazy {
        items + children.flatMap { it.allItems }
    }

    val hasChildren: Boolean get() = children.isNotEmpty()

    val posterItem: BazaarItem? by lazy {
        items.firstOrNull() ?: children.firstNotNullOfOrNull { it.posterItem }
    }

    val icon: Material by lazy { resolveIcon(posterItem) }

    companion object {

        fun resolveIcon(item: BazaarItem?): Material {
            if (item == null) return Material.CHEST
            val customType = CustomItemType.entries.find { it.getKey() == item.key }
            if (customType != null) return customType.DisplayMaterial
            return try {
                Material.valueOf(item.key.uppercase())
            } catch (_: IllegalArgumentException) {
                Material.PAPER
            }
        }

        /**
         * Builds a forest of category nodes from a flat collection of bazaar items.
         * Items are grouped by their path segments (e.g. "Mining/Ores" → root "Mining", child "Ores").
         * Roots are sorted according to [displayOrder]; unlisted roots appear at the end alphabetically.
         */
        fun buildTree(items: Collection<BazaarItem>, displayOrder: List<String>): List<BazaarCategoryNode> {
            data class Bucket(
                val directItems: MutableList<BazaarItem> = mutableListOf(),
                val childBuckets: MutableMap<String, Bucket> = mutableMapOf()
            )

            val roots = mutableMapOf<String, Bucket>()

            for (item in items) {
                val segments = item.category.split("/")
                val rootName = segments[0]
                val rootBucket = roots.getOrPut(rootName) { Bucket() }

                var currentBucket = rootBucket
                for (i in 1 until segments.size) {
                    currentBucket = currentBucket.childBuckets.getOrPut(segments[i]) { Bucket() }
                }
                currentBucket.directItems.add(item)
            }

            fun buildNode(name: String, path: String, bucket: Bucket): BazaarCategoryNode {
                val childNodes = bucket.childBuckets.entries
                    .sortedBy { it.key }
                    .map { (childName, childBucket) ->
                        buildNode(childName, "$path/$childName", childBucket)
                    }
                return BazaarCategoryNode(name, path, childNodes, bucket.directItems.toList())
            }

            val orderMap = displayOrder.withIndex().associate { (i, v) -> v to i }

            return roots.entries
                .sortedWith(compareBy({ orderMap[it.key] ?: Int.MAX_VALUE }, { it.key }))
                .map { (rootName, bucket) -> buildNode(rootName, rootName, bucket) }
        }
    }
}
