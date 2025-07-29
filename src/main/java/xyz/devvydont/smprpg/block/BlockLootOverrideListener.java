package xyz.devvydont.smprpg.block;

import org.bukkit.block.data.Ageable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDropItemEvent;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.blueprints.sets.emberclad.BoilingPickaxe;
import xyz.devvydont.smprpg.services.AttributeService;
import xyz.devvydont.smprpg.services.DropsService;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.listeners.ToggleableListener;
import xyz.devvydont.smprpg.util.world.ChunkUtil;

public class BlockLootOverrideListener extends ToggleableListener {

    /**
     * Using an item type, try to guess which fortune stat is desired to be use when an override isn't set.
     * For example, if we are using a pickaxe it's safe to assume we want to use mining fortune.
     * Keep in mind, this isn't reliable and {@link BlockLootEntry#getFortuneOverride()} is a desirable choice.
     * @param tool The tool to guess a fortune attribute for.
     * @return The attribute that we should use, null if inconclusive.
     */
    private AttributeWrapper predictDesiredFortuneAttribute(ItemClassification tool) {
        return switch (tool) {
            case PICKAXE -> AttributeWrapper.MINING_FORTUNE;
            case HOE -> AttributeWrapper.FARMING_FORTUNE;
            case AXE -> AttributeWrapper.WOODCUTTING_FORTUNE;
            default -> null;
        };
    }

    /**
     * The backbone of how all block drop loot functions in the plugin.
     * The idea is to check if our {@link BlockLootRegistry} has an override for the block broken.
     * If it does, we need to determine what fortune attribute to use based on the context (the tool!)
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void __onBlockBreak(BlockDropItemEvent event) {

        // If this block is ageable, then it needs to be at its max age before we consider custom logic.
        if (event.getBlockState().getBlockData() instanceof Ageable ageable)
            if (ageable.getAge() != ageable.getMaximumAge())
                return;

        // If this block was placed by a player in some way use vanilla logic.
        if (ChunkUtil.isBlockSkillInvalid(event.getBlockState()))
            return;

        // Check if this block is flagged. If it isn't, let vanilla handle the logic.
        var entry = BlockLootRegistry.get(event.getBlockState().getType());
        if (entry == null)
            return;

        // The block is flagged. We need context. Essentially, we have manual checks that determine this.
        var ctx = BlockLootContext.INCORRECT_TOOL;
        var toolPreference = entry.getPreferredTool();
        var itemUsedToBreak = event.getPlayer().getInventory().getItemInMainHand();
        var itemUsedToBreakBlueprint = ItemService.blueprint(itemUsedToBreak);
        var usedTool = itemUsedToBreakBlueprint.getItemClassification();

        // If there's no tool preference for the block or there's a tool preference match, then we are using correct tool.
        if (toolPreference == null)
            ctx = BlockLootContext.CORRECT_TOOL;
        else if (usedTool.equals(toolPreference))
            ctx = BlockLootContext.CORRECT_TOOL;

        // If we are using silk touch on our tool because of the enchantment...
        if (itemUsedToBreak.containsEnchantment(Enchantment.SILK_TOUCH))
            ctx = BlockLootContext.SILK_TOUCH;

        // todo: turn boiling pick maybe into enchant? It is more sustainable that way.
        // If we are using boiling touch... (auto smelt)
        if (itemUsedToBreakBlueprint instanceof BoilingPickaxe)
            ctx = BlockLootContext.AUTO_SMELT;

        // We now have context. If the block doesn't define the given context, we can let vanilla take over.
        var loot = entry.getLootForContext(ctx);
        if (loot == null)
            return;

        // Given the preferred tool, we can actually work out which fortune stat is ideal. No preferred tool
        // means that no fortune is applicable to this drop.
        var fortune = 0.0;
        var attribute = this.predictDesiredFortuneAttribute(usedTool);
        if (entry.getFortuneOverride() != null)
            attribute = entry.getFortuneOverride();

        // If we found one, increment the fortune to use in drop calculation.
        // The idea here is that base fortune is 0, and 100 fortune would yield 2x drops (scalarly scales).
        if (attribute != null) {
            var attrInstance = AttributeService.getInstance().getAttribute(event.getPlayer(), attribute);
            if (attrInstance != null) {
                fortune += attrInstance.getValue() / 100;
            }
        }

        // Set the drops manually! Take into account the base drop chance, and the fortune chance.
        event.getItems().clear();
        for (var drop : loot) {

            // The amount of drops is equal to the chance. Drops with chances of 1 or higher are additive, otherwise multiplicative.
            var amount = drop.getChance();
            if (drop.getChance() < 1.0)
                amount *= (1 + fortune);
            else
                amount += fortune;

            var leftover = amount - Math.floor(amount);
            if (Math.random() < leftover)
                amount++;

            var item = drop.getLoot();
            item.setAmount((int) amount);

            // This might seem scuffed, but there's a reason.
            // In order for loot tagging to be detected properly by things like telekinesis, we have to first add the
            // loot flags to the item, then manually and immediately transfer it to the item entity itself
            // BEFORE it spawns in. When we do it this way, succeeding BlockDropItemEvent calls will be able to detect
            // this. If we spawn it here and now, the ItemSpawnEvent will hijack us and mess everything up.
            var ent = event.getBlock().getWorld().dropItem(event.getBlock().getLocation().toCenterLocation(), item, entity -> {
                SMPRPG.getService(DropsService.class).addDefaultLootFlags(item, event.getPlayer());
                entity.setItemStack(item);
                SMPRPG.getService(DropsService.class).transferLootFlags(entity);
            });

            event.getItems().add(ent);
        }
    }
}
