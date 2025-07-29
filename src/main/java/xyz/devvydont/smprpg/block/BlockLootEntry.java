package xyz.devvydont.smprpg.block;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.jetbrains.annotations.Nullable;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.items.ItemClassification;

import java.util.Collection;

/**
 * A member of the {@link BlockLootRegistry}. Holds a material and loot to drop under certain contexts.
 */
public class BlockLootEntry {

    // What equipment type is preferable for breaking this block. Only affects drops, not mining speed.
    private final @Nullable ItemClassification preferredTool;

    // What contexts lead to which loot items. In most circumstances, this is one item but there's support for many.
    private final Multimap<BlockLootContext, BlockLoot> loot;

    private final @Nullable AttributeWrapper fortuneOverride;

    public BlockLootEntry(
            @Nullable ItemClassification preferredTool,
            Multimap<BlockLootContext, BlockLoot> loot,
            @Nullable AttributeWrapper fortuneOverride) {
        this.preferredTool = preferredTool;
        this.loot = loot;
        this.fortuneOverride = fortuneOverride;
    }

    public BlockLootEntry(@Nullable ItemClassification preferredTool, Multimap<BlockLootContext, BlockLoot> loot) {
        this(preferredTool, loot, null);
    }

    /**
     * Get the preferred tool for this block loot entry.
     * @return The preferred tool.
     */
    public @Nullable ItemClassification getPreferredTool() {
        return preferredTool;
    }

    /**
     * Get the full loot context and loot map for this block.
     * @return A map that contains context to loot mappings for this block.
     */
    public Multimap<BlockLootContext, BlockLoot> getLoot() {
        return loot;
    }

    /**
     * Get the loot that is mapped to a certain context.
     * @param context The context that is relevant.
     * @return A collection of block loot entries. Will be empty if not present.
     */
    public Collection<BlockLoot> getLootForContext(BlockLootContext context) {
        return loot.get(context);
    }

    /**
     * Get the attribute that is set for fortune for this block. This can be null, in which it should be calculated
     * dynamically by the consumer on which attribute is desired.
     * @return An attribute (if set) that is used for fortune calculation.
     */
    public @Nullable AttributeWrapper getFortuneOverride() {
        return fortuneOverride;
    }

    /**
     * Gets a builder for ease of creation for loot definitions. This version of the builder should be
     * used when a certain tool is required to break a block.
     * @param preferredTool The preferred tool to break a block for proper loot.
     * @return A new builder instance.
     */
    public static Builder builder(ItemClassification preferredTool) {
        return new Builder(preferredTool);
    }

    /**
     * Gets a builder for ease of creation for loot definitions. This version of the builder should be
     * used when no tool is necessary to break this block. Similar to dirt, where our fist is enough.
     * @return A new builder instance.
     */
    public static Builder builder() {
        return new Builder(null);
    }

    /**
     * A builder for ease of creation of {@link BlockLootEntry} instances.
     */
    public static class Builder {

        private final @Nullable ItemClassification preferredTool;
        private final Multimap<BlockLootContext, BlockLoot> loot = HashMultimap.create();
        private @Nullable AttributeWrapper fortuneOverride = null;

        private Builder(@Nullable ItemClassification preferredTool) {
            this.preferredTool = preferredTool;
        }

        /**
         * Add a new block loot to the specified context. Will not overwrite previous calls.
         * @param context The context to add loot for.
         * @param loot The loot to add.
         * @return The same builder instance for proper builder pattern calls.
         */
        public Builder add(BlockLootContext context, BlockLoot loot) {
            this.loot.put(context, loot);
            return this;
        }

        /**
         * Adds an attribute override for this block. This is necessary to rare instances where the preferable tool
         * actually doesn't make sense and can't be automatically determined. A prime example of this is how axe is
         * the ideal tool for pumpkins and melons, which is farming. In other circumstances, we can assume the axe is
         * used for cutting trees.
         * @param attributeWrapper The attribute that will be used for fortune calculation.
         * @return The same builder instance for proper builder pattern calls.
         */
        public Builder uses(AttributeWrapper attributeWrapper) {
            this.fortuneOverride = attributeWrapper;
            return this;
        }

        /**
         * Finish construction of the entry and build a {@link BlockLootEntry} instance.
         * @return The new entry.
         */
        public BlockLootEntry build() {
            return new BlockLootEntry(this.preferredTool, this.loot, this.fortuneOverride);
        }


    }

}
