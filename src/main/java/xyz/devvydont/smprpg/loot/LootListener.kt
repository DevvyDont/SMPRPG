package xyz.devvydont.smprpg.loot

import io.papermc.paper.datacomponent.DataComponentTypes
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.EnchantmentOffer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.world.LootGenerateEvent
import org.bukkit.generator.structure.GeneratedStructure
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.loot.LootTable
import org.bukkit.loot.LootTables
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.enchantments.calculator.EnchantmentCalculator
import xyz.devvydont.smprpg.enchantments.definitions.vanilla.overrides.EfficiencyEnchantment
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.blueprints.resources.scrolls.DynamicEnchantingScroll
import xyz.devvydont.smprpg.listeners.entity.StructureEntitySpawnListener
import xyz.devvydont.smprpg.services.EnchantmentService
import xyz.devvydont.smprpg.services.ItemService.Companion.generate
import xyz.devvydont.smprpg.util.listeners.ToggleableListener
import java.util.*
import kotlin.math.roundToInt

/**
 * Class responsible for hooking into chest loot generation events and populating them with overrides if desired
 */
class LootListener : ToggleableListener() {
    // Our plugin injects custom loot tables into existing ones for extra items
    private val lootTableAdditions: MutableMap<NamespacedKey?, CustomLootTable?> =
        HashMap<NamespacedKey?, CustomLootTable?>()

    init {
        val SCROLL_EFFICIENCY = DynamicEnchantingScroll.getScrollWithEnchantment(EnchantmentService.EFFICIENCY)
        val SCROLL_FORTUNE = DynamicEnchantingScroll.getScrollWithEnchantment(EnchantmentService.FORTUNE)
        val SCROLL_BLESSED = DynamicEnchantingScroll.getScrollWithEnchantment(EnchantmentService.BLESSED)


        lootTableAdditions.put(
            LootTables.ABANDONED_MINESHAFT.key, CustomLootTable(
                LootTableMember(generate(CustomItemType.GRAPPLING_HOOK)).withChance(.005f),
                LootTableMember(generate(CustomItemType.SILVER_COIN)).withChance(.2f).withMax(5),
                LootTableMember(SCROLL_EFFICIENCY).withChance(.2f).withMax(3),
                LootTableMember(SCROLL_FORTUNE).withChance(.2f).withMax(2),
            )
        )

        lootTableAdditions.put(
            LootTables.SIMPLE_DUNGEON.key, CustomLootTable(
                LootTableMember(generate(CustomItemType.GRAPPLING_HOOK)).withChance(.005f),
                LootTableMember(generate(CustomItemType.SILVER_COIN)).withChance(.2f).withMax(5)
            )
        )

        lootTableAdditions.put(
            LootTables.RUINED_PORTAL.key, CustomLootTable(
                LootTableMember(SCROLL_BLESSED).withChance(.2f).withMax(2),
                LootTableMember(generate(CustomItemType.SILVER_COIN)).withChance(.2f).withMax(5)
            )
        )

        lootTableAdditions.put(
            LootTables.END_CITY_TREASURE.key, CustomLootTable(
                LootTableMember(generate(Material.NETHERITE_HELMET)).withChance(.04f).withEnchants(true, 70),
                LootTableMember(generate(Material.NETHERITE_CHESTPLATE)).withChance(.04f).withEnchants(true, 70),
                LootTableMember(generate(Material.NETHERITE_LEGGINGS)).withChance(.04f).withEnchants(true, 70),
                LootTableMember(generate(Material.NETHERITE_BOOTS)).withChance(.04f).withEnchants(true, 70),
                LootTableMember(generate(Material.NETHERITE_SWORD)).withChance(.04f).withEnchants(true, 70),
                LootTableMember(generate(Material.NETHERITE_PICKAXE)).withChance(.04f).withEnchants(true, 70),
                LootTableMember(generate(Material.NETHERITE_SHOVEL)).withChance(.04f).withEnchants(true, 70),
                LootTableMember(generate(Material.NETHERITE_AXE)).withChance(.04f).withEnchants(true, 70),
                LootTableMember(generate(CustomItemType.ENCHANTED_DIAMOND)).withChance(.01f),
                LootTableMember(generate(CustomItemType.PREMIUM_SHULKER_SHELL)).withChance(.05f).withMax(2),
                LootTableMember(generate(CustomItemType.GOLD_COIN)).withChance(.3f).withMax(5)
            )
        )
    }

    /**
     * Analyzes loot generation events that occur in structures and are caused by players.
     * Enchants are then re-rolled to match the level of the structure, and custom loot tables are then
     * injected on top of the loot from the vanilla loot table.
     *
     * @param event The [LootGenerateEvent] event that provides us with relevant context.
     */
    @EventHandler
    @Suppress("unused")
    private fun onLootChestGenerate(event: LootGenerateEvent) {
        // If the entity involved in this loot generation isn't a player ignore

        if (event.entity !is Player)
            return
        val player = event.entity as Player

        for (item in event.loot.stream().toList()) {
            // Wipe emeralds from chests.
            if (item.type == Material.EMERALD) {
                event.loot.remove(item)
                val shard = ItemStack(Material.AMETHYST_SHARD)
                shard.amount = item.amount
                event.loot.add(shard)
            }

            // Convert Enchanted Books into scrolls.
            if (item.type == Material.ENCHANTED_BOOK) {
                var enchants = item.getData(DataComponentTypes.STORED_ENCHANTMENTS)?.enchantments()
                if (enchants != null) {
                    // Grab first enchant off the scroll, that's what we will use
                    var customEnch = SMPRPG.getService(EnchantmentService::class.java).getEnchantment(enchants.keys.random())
                    event.loot.remove(item)
                    val scroll = DynamicEnchantingScroll.getScrollWithEnchantment(customEnch!!)
                    event.loot.add(scroll)
                }
            }
        }

        // Attempt to find a structure this chest is contained in
        var containedStructure: GeneratedStructure? = null
        val location = event.lootContext.location
        for (structure in event.lootContext.location.chunk
            .structures) if (structure.boundingBox
                .contains(location.x, location.y, location.z)
        ) containedStructure = structure

        // If the structure is null, we can't properly override loot
        if (containedStructure == null) return

        val structureLevel = StructureEntitySpawnListener.getMinimumEntityLevel(containedStructure)

        // Go through all the items. If there is anything with enchants on them, we need to re roll them with our logic.
        for (loot in event.loot) {
            if (loot.enchantments.isEmpty()) continue

            // We have enchants, completely re roll them
            loot.removeEnchantments()
            val calculator = EnchantmentCalculator(loot, EnchantmentCalculator.MAX_BOOKSHELF_BONUS, structureLevel)
            val offers: MutableList<EnchantmentOffer> =
                calculator.calculate()[EnchantmentCalculator.EnchantmentSlot.EXPENSIVE]!!
            for (offer in offers) loot.addUnsafeEnchantment(offer.enchantment, offer.enchantmentLevel)
        }

        // Now handle custom item injections if the loot tables desires it.
        val customLootTable = lootTableAdditions[event.lootTable.key]
        if (customLootTable == null) return

        // Count how many empty slots we have to work with
        val emptySlots = 9 * 3 - event.loot.size

        // Roll items and add them to the empty slots
        val extras = customLootTable.rollItems(player, emptySlots)
        event.loot.addAll(extras)
        Collections.shuffle(event.loot)
    }
}
