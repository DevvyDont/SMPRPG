package xyz.devvydont.smprpg.items.listeners

import net.kyori.adventure.text.format.NamedTextColor
import net.momirealms.craftengine.bukkit.api.BukkitAdaptor
import net.momirealms.craftengine.core.util.Key as CEKey
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerInteractEvent
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.ability.AbilityContext
import xyz.devvydont.smprpg.block.CraftEngineBlockEnums
import xyz.devvydont.smprpg.events.abilities.AbilityCastEvent
import xyz.devvydont.smprpg.items.interfaces.IAbilityCaster
import xyz.devvydont.smprpg.items.interfaces.IAbilityCaster.AbilityEntry
import xyz.devvydont.smprpg.services.ActionBarService
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.services.ItemService.Companion.blueprint
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.listeners.ToggleableListener

/**
 * Implements the logic that lets items cast abilities.
 */
class AbilityCastingListener : ToggleableListener() {
    @EventHandler(priority = EventPriority.MONITOR)
    private fun __onInteractWithItem(event: PlayerInteractEvent) {
        // Retrieve the item involved with the interaction. Do nothing if it doesn't exist.

        val item = event.item
        if (item == null || item.type == Material.AIR) return

        if (event.getPlayer().hasCooldown(item)) return

        // Do nothing if the item is not a type of ability caster.
        val _blueprint = blueprint(item)
        if (_blueprint !is IAbilityCaster) return

        // Check if we are looking at a blacklisted block.
        val targetBlock = event.getPlayer().getTargetBlockExact(3)
        if (targetBlock != null) {
            val ceBlock = BukkitAdaptor.adapt(targetBlock)
            if (event.action.isRightClick && INTERACTABLE_BLOCK_BLACKLIST.contains(ceBlock.id())
            ) return
        }

        // Check every ability and see if the click type matches.
        val abilities: Collection<AbilityEntry> = _blueprint.getAbilities(item)
        val player = SMPRPG.getService(EntityService::class.java).getPlayerInstance(event.getPlayer())
        for (ability in abilities) {
            // Skip if click type isn't correct.

            if (!ability.activation.passes(event.action, event.getPlayer())) continue

            // Update our ability cost before we check our mana usage
            if (!ItemService.meetsRequirements(item, player)) return
            val cooldown = _blueprint.getCooldown(item)
            val abe = AbilityCastEvent(ability, player, item, cooldown)
            abe.callEvent()

            // Check if the cost is met.
            if (!abe.abilityCost.canUse(player)) {
                SMPRPG.getService(ActionBarService::class.java)
                    .addActionBarComponent(
                        event.getPlayer(),
                        ActionBarService.ActionBarSource.MISC,
                        ComponentUtils.create("NOT ENOUGH " + ability.cost.resource.name, NamedTextColor.RED),
                        1
                    )
                event.getPlayer().playSound(event.getPlayer().location, Sound.ENTITY_ENDERMAN_TELEPORT, .3f, .5f)
                continue
            }

            // Execute!
            val success = ability.ability.getHandler().execute(AbilityContext(event.getPlayer(), event.hand))
            if (!success) continue
            __onCastAbility(abe)
        }
    }

    private fun __onCastAbility(event: AbilityCastEvent) {
        val ability = event.ability
        val player = event.player
        val item = event.item
        event.abilityCost.spend(player)
        SMPRPG.getService(ActionBarService::class.java).addActionBarComponent(
            player.player,
            ActionBarService.ActionBarSource.MISC,
            ComponentUtils.merge(
                ComponentUtils.create(ability.ability.friendlyName, NamedTextColor.GOLD),
                ComponentUtils.SPACE,
                ComponentUtils.create(
                    "-" + event.abilityCost.amount + event.abilityCost.resource.symbol,
                    event.abilityCost.resource.color
                )
            ),
            3
        )
        player.player.setCooldown(item, event.cooldown.toInt())
    }

    companion object {
        // Items that will force the teleportation event to not fire in favor of interacting with the block.

        val INTERACTABLE_BLOCK_BLACKLIST: Set<CEKey> = setOf(
            CEKey.of(Material.CHEST.key.toString()),
            CEKey.of(Material.TRAPPED_CHEST.key.toString()),
            CEKey.of(Material.BARREL.key.toString()),
            CEKey.of(Material.ENDER_CHEST.key.toString()),
            CEKey.of(Material.CRAFTING_TABLE.key.toString()),
            CEKey.of(Material.FURNACE.key.toString()),
            CEKey.of(Material.BLAST_FURNACE.key.toString()),
            CEKey.of(Material.SMOKER.key.toString()),
            CEKey.of(Material.ANVIL.key.toString()),
            CEKey.of(Material.ENCHANTING_TABLE.key.toString()),
            CEKey.of(Material.GRINDSTONE.key.toString()),
            CEKey.of(Material.LECTERN.key.toString()),
            CEKey.of(Material.SMITHING_TABLE.key.toString()),
            CEKey.of(Material.STONECUTTER.key.toString()),
            CEKey.of(Material.CARTOGRAPHY_TABLE.key.toString()),
            CEKey.of(Material.LOOM.key.toString()),
            CEKey.of(Material.BELL.key.toString()),
            CEKey.of(Material.NOTE_BLOCK.key.toString()),
            CEKey.of(Material.REPEATER.key.toString()),
            CEKey.of(Material.COMPARATOR.key.toString()),
            CEKey.of(Material.STONE_BUTTON.key.toString()),
            CEKey.of(Material.OAK_BUTTON.key.toString()),
            CEKey.of(Material.SPRUCE_BUTTON.key.toString()),
            CEKey.of(Material.BIRCH_BUTTON.key.toString()),
            CEKey.of(Material.JUNGLE_BUTTON.key.toString()),
            CEKey.of(Material.ACACIA_BUTTON.key.toString()),
            CEKey.of(Material.DARK_OAK_BUTTON.key.toString()),
            CEKey.of(Material.CRIMSON_BUTTON.key.toString()),
            CEKey.of(Material.WARPED_BUTTON.key.toString()),
            CEKey.of(Material.MANGROVE_BUTTON.key.toString()),
            CEKey.of(Material.CHERRY_BUTTON.key.toString()),
            CEKey.of(Material.BAMBOO_BUTTON.key.toString()),
            CEKey.of(Material.IRON_DOOR.key.toString()),
            CEKey.of(Material.OAK_DOOR.key.toString()),
            CEKey.of(Material.SPRUCE_DOOR.key.toString()),
            CEKey.of(Material.BIRCH_DOOR.key.toString()),
            CEKey.of(Material.JUNGLE_DOOR.key.toString()),
            CEKey.of(Material.ACACIA_DOOR.key.toString()),
            CEKey.of(Material.DARK_OAK_DOOR.key.toString()),
            CEKey.of(Material.CRIMSON_DOOR.key.toString()),
            CEKey.of(Material.WARPED_DOOR.key.toString()),
            CEKey.of(Material.MANGROVE_DOOR.key.toString()),
            CEKey.of(Material.CHERRY_DOOR.key.toString()),
            CEKey.of(Material.BAMBOO_DOOR.key.toString()),
            CEKey.of(Material.ACACIA_TRAPDOOR.key.toString()),
            CEKey.of(Material.BAMBOO_TRAPDOOR.key.toString()),
            CEKey.of(Material.COPPER_TRAPDOOR.key.toString()),
            CEKey.of(Material.BIRCH_TRAPDOOR.key.toString()),
            CEKey.of(Material.JUNGLE_TRAPDOOR.key.toString()),
            CEKey.of(Material.MANGROVE_TRAPDOOR.key.toString()),
            CEKey.of(Material.CHERRY_TRAPDOOR.key.toString()),
            CEKey.of(Material.OXIDIZED_COPPER_TRAPDOOR.key.toString()),
            CEKey.of(Material.EXPOSED_COPPER_TRAPDOOR.key.toString()),
            CEKey.of(Material.WARPED_TRAPDOOR.key.toString()),
            CEKey.of(Material.SPRUCE_TRAPDOOR.key.toString()),
            CEKey.of(Material.IRON_TRAPDOOR.key.toString()),
            CEKey.of(Material.LEVER.key.toString()),

            CraftEngineBlockEnums.COOKING_POT.key,
            CraftEngineBlockEnums.CUTTING_BOARD.key,
            CraftEngineBlockEnums.FREEZER.key,
            CraftEngineBlockEnums.REFORGE_TABLE.key,
            CraftEngineBlockEnums.NETHERITE_ANVIL.key,
            CraftEngineBlockEnums.BINARY_BUTTON.key,
            CraftEngineBlockEnums.BINARY_TRAPDOOR.key,
            CraftEngineBlockEnums.GOLDEN_OAK_BUTTON.key,
            CraftEngineBlockEnums.GOLDEN_OAK_TRAPDOOR.key,
            CraftEngineBlockEnums.HOLYSTONE_BUTTON.key,
            CraftEngineBlockEnums.SKYROOT_BUTTON.key,
            CraftEngineBlockEnums.SKYROOT_TRAPDOOR.key,
            CraftEngineBlockEnums.ACACIA_CABINET.key,
            CraftEngineBlockEnums.TITANIUM_CACHE.key,
        )
    }
}
