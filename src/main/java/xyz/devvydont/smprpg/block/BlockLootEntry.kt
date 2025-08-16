package xyz.devvydont.smprpg.block

import com.google.common.collect.HashMultimap
import com.google.common.collect.ImmutableSet
import com.google.common.collect.Multimap
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.ItemClassification

/**
 * A member of the [BlockLootRegistry]. Holds a material and loot to drop under certain contexts.
 */
class BlockLootEntry(
    /**
     * Get the full loot context and loot map for this block.
     * @return A map that contains context to loot mappings for this block.
     */
    // What contexts lead to which loot items. In most circumstances, this is one item but there's support for many.
    val loot: Multimap<BlockLootContext, BlockLoot>,
    /**
     * Get the attribute that is set for fortune for this block. This can be null, in which it should be calculated
     * dynamically by the consumer on which attribute is desired.
     * @return An attribute (if set) that is used for fortune calculation.
     */
    val fortuneOverride: AttributeWrapper?,

    preferredTools: MutableSet<ItemClassification>
) {
    /**
     * Get the preferred tool for this block loot entry.
     * @return The preferred tool.
     */
    // What equipment type is preferable for breaking this block. Only affects drops, not mining speed.
    val preferredTools: MutableSet<ItemClassification> = ImmutableSet.copyOf(preferredTools)

    /**
     * Get the loot that is mapped to a certain context.
     * @param context The context that is relevant.
     * @return A collection of block loot entries. Will be empty if not present.
     */
    fun getLootForContext(context: BlockLootContext): Collection<BlockLoot> {
        return loot.get(context)
    }

    /**
     * A builder for ease of creation of [BlockLootEntry] instances.
     */
    class Builder(vararg preferredTool: ItemClassification) {
        private val preferredTool: MutableSet<ItemClassification> = ImmutableSet.copyOf(preferredTool)
        private val loot: Multimap<BlockLootContext, BlockLoot> = HashMultimap.create<BlockLootContext, BlockLoot>()
        private var fortuneOverride: AttributeWrapper? = null

        /**
         * Add a new block loot to the specified context. Will not overwrite previous calls.
         * @param context The context to add loot for.
         * @param loot The loot to add.
         * @return The same builder instance for proper builder pattern calls.
         */
        fun add(context: BlockLootContext, loot: BlockLoot): Builder {
            this.loot.put(context, loot)
            return this
        }

        /**
         * Adds an attribute override for this block. This is necessary to rare instances where the preferable tool
         * actually doesn't make sense and can't be automatically determined. A prime example of this is how axe is
         * the ideal tool for pumpkins and melons, which is farming. In other circumstances, we can assume the axe is
         * used for cutting trees.
         * @param attributeWrapper The attribute that will be used for fortune calculation.
         * @return The same builder instance for proper builder pattern calls.
         */
        fun uses(attributeWrapper: AttributeWrapper?): Builder {
            this.fortuneOverride = attributeWrapper
            return this
        }

        /**
         * Finish construction of the entry and build a [BlockLootEntry] instance.
         * @return The new entry.
         */
        fun build(): BlockLootEntry {
            return BlockLootEntry(this.loot, this.fortuneOverride, this.preferredTool)
        }
    }

    companion object {
        /**
         * Gets a builder for ease of creation for loot definitions. This version of the builder should be
         * used when a certain tool is required to break a block.
         * @param preferredTools The preferred tool to break a block for proper loot.
         * @return A new builder instance.
         */
        fun builder(vararg preferredTools: ItemClassification): Builder {
            return Builder(*preferredTools)
        }

        /**
         * Gets a builder for ease of creation for loot definitions. This version of the builder should be
         * used when no tool is necessary to break this block. Similar to dirt, where our fist is enough.
         * @return A new builder instance.
         */
        fun builder(): Builder {
            return Builder()
        }
    }
}
