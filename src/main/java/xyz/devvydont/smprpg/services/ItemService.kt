package xyz.devvydont.smprpg.services

import com.destroystokyo.paper.event.inventory.PrepareResultEvent
import io.papermc.paper.datacomponent.DataComponentTypes
import net.kyori.adventure.key.Key
import net.kyori.adventure.key.Keyed
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.momirealms.craftengine.bukkit.api.BukkitAdaptor
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.ItemMergeEvent
import org.bukkit.event.entity.ItemSpawnEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.event.inventory.PrepareSmithingEvent
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.world.LootGenerateEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.recipe.CraftingBookCategory
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable
import org.checkerframework.checker.index.qual.NonNegative
import org.checkerframework.checker.index.qual.Positive
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.entity.player.LeveledPlayer
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemRarity
import xyz.devvydont.smprpg.items.SMPItemQuery
import xyz.devvydont.smprpg.items.base.ChargedItemBlueprint
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint
import xyz.devvydont.smprpg.items.base.SMPItemBlueprint
import xyz.devvydont.smprpg.items.base.VanillaItemBlueprint
import xyz.devvydont.smprpg.items.blueprints.augment.Recombobulator
import xyz.devvydont.smprpg.items.blueprints.craftengine.CraftEngineBlueprint
import xyz.devvydont.smprpg.items.blueprints.equipment.WalletBlueprint
import xyz.devvydont.smprpg.items.blueprints.potion.PotionBlueprint
import xyz.devvydont.smprpg.items.blueprints.resources.VanillaCompressibleBlueprint
import xyz.devvydont.smprpg.items.blueprints.resources.VanillaResource
import xyz.devvydont.smprpg.items.blueprints.resources.slayer.drops.NecronomiconExcerpts
import xyz.devvydont.smprpg.items.blueprints.tomes.TomeBlueprint
import xyz.devvydont.smprpg.items.blueprints.vanilla.*
import xyz.devvydont.smprpg.items.interfaces.*
import xyz.devvydont.smprpg.items.listeners.*
import xyz.devvydont.smprpg.items.tools.drills.FuelTankBlueprint
import xyz.devvydont.smprpg.listeners.crafting.CustomItemFurnacePreventions
import xyz.devvydont.smprpg.reforge.ReforgeBase
import xyz.devvydont.smprpg.reforge.ReforgeType
import xyz.devvydont.smprpg.util.attributes.AttributeUtil
import xyz.devvydont.smprpg.util.crafting.ItemUtil
import xyz.devvydont.smprpg.util.crafting.MaterialWrapper
import xyz.devvydont.smprpg.recipe.CompressionGraph
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.MinecraftStringUtils
import xyz.devvydont.smprpg.util.formatting.Symbols
import xyz.devvydont.smprpg.util.items.AbilityUtil
import xyz.devvydont.smprpg.util.listeners.ToggleableListener
import xyz.devvydont.smprpg.util.time.TickTime
import java.lang.reflect.InvocationTargetException
import java.util.*
import kotlin.collections.iterator
import kotlin.math.abs
import kotlin.math.round

class ItemService : IService, Listener {

    val itemVersionKey: NamespacedKey = NamespacedKey("smprpg", "item-version")
    val itemTypeKey: NamespacedKey = NamespacedKey("smprpg", "item-type")
    val reforgeTypeKey: NamespacedKey = NamespacedKey("smprpg", "reforge")

    private val vanillaBlueprintResolver: MutableMap<Material, VanillaItemBlueprint> = HashMap<Material, VanillaItemBlueprint>()

    private val blueprints: MutableMap<CustomItemType, SMPItemBlueprint> = HashMap<CustomItemType, SMPItemBlueprint>()
    private val keyMappings: MutableMap<String, CustomItemType> = HashMap<String, CustomItemType>()
    private val reforges: MutableMap<String, ReforgeBase> = HashMap<String, ReforgeBase>()
    private val registeredRecipes: MutableList<Recipe> = ArrayList<Recipe>()
    private val listeners = ArrayList<ToggleableListener>()
    private val materialToRecipeUnlocks: MutableMap<Material, MutableList<NamespacedKey>> =
        HashMap<Material, MutableList<NamespacedKey>>()
    private val customItemToRecipeUnlocks: MutableMap<CustomItemType, MutableList<NamespacedKey>> =
        HashMap<CustomItemType, MutableList<NamespacedKey>>()

    init {
        listeners.add(ShieldBlockingListener())
        listeners.add(ExperienceBottleListener())
        listeners.add(BackpackInteractionListener())
        listeners.add(AbilityCastingListener())
        listeners.add(CustomItemFurnacePreventions())
        listeners.add(VelocitySensitiveItemListener())
        listeners.add(TomeInteractionListener())
        listeners.add(WalletListener())
        listeners.add(PocketCompressorListener())
    }

    @Throws(RuntimeException::class)
    override fun setup() {
        registerReforges()

        val plugin = SMPRPG.plugin
        plugin.logger.info(String.format("Successfully registered %d reforges", reforges.size))

        registerCustomItems()
        plugin.logger.info(
            String.format(
                "Successfully associated %d vanilla materials with blueprints",
                vanillaBlueprintResolver.size
            )
        )
        plugin.logger.info(String.format("Successfully registered %d custom item blueprints", blueprints.size))

        // Crafting and compression recipes are registered from the data-driven registry by RecipeService.
        Bukkit.updateRecipes()

        // Make the listeners start working.
        for (listener in listeners) listener.start()
    }

    override fun cleanup() {
        val plugin = SMPRPG.plugin
        plugin.logger.info("Cleaning up ItemService")

        // Unregister all recipes that were registered during setup (craftable, compression, netherite, etc.).
        for (recipe in registeredRecipes) {
            val key = (recipe as? Keyed)?.key()?.asString()?.let(NamespacedKey::fromString) ?: continue
            plugin.server.removeRecipe(key)
        }

        // Make the listeners stop functioning.
        for (listener in listeners) listener.stop()
    }

    /**
     * Used to count recipes registered on the server, mostly used for logging when this service starts
     *
     * @return How many recipes are registered.
     */
    private fun countRecipes(): Int {
        val plugin = SMPRPG.plugin
        var n = 0
        val recipeIterator = plugin.server.recipeIterator()
        while (recipeIterator.hasNext()) {
            n++
            recipeIterator.next()
        }
        return n
    }

    private fun registerCustomItems() {
        registerVanillaMaterialResolver(Material.SHEARS, ItemShears::class.java)

        registerVanillaMaterialResolver(Material.WOODEN_SWORD, ItemSword::class.java)
        registerVanillaMaterialResolver(Material.COPPER_SWORD, ItemSword::class.java)
        registerVanillaMaterialResolver(Material.GOLDEN_SWORD, ItemSword::class.java)
        registerVanillaMaterialResolver(Material.IRON_SWORD, ItemSword::class.java)
//        registerVanillaMaterialResolver(Material.DIAMOND_SWORD, ItemSword::class.java)
        registerVanillaMaterialResolver(Material.NETHERITE_SWORD, ItemSword::class.java)
        registerVanillaMaterialResolver(Material.SHIELD, ItemShield::class.java)

        registerVanillaMaterialResolver(Material.TRIDENT, ItemSword::class.java)

        registerVanillaMaterialResolver(Material.WOODEN_AXE, ItemAxe::class.java)
        registerVanillaMaterialResolver(Material.COPPER_AXE, ItemAxe::class.java)
        registerVanillaMaterialResolver(Material.GOLDEN_AXE, ItemAxe::class.java)
        registerVanillaMaterialResolver(Material.IRON_AXE, ItemAxe::class.java)
//        registerVanillaMaterialResolver(Material.DIAMOND_AXE, ItemAxe::class.java)
        registerVanillaMaterialResolver(Material.NETHERITE_AXE, ItemAxe::class.java)

        registerVanillaMaterialResolver(Material.WOODEN_SPEAR, ItemSpear::class.java)
        registerVanillaMaterialResolver(Material.COPPER_SPEAR, ItemSpear::class.java)
        registerVanillaMaterialResolver(Material.GOLDEN_SPEAR, ItemSpear::class.java)
        registerVanillaMaterialResolver(Material.IRON_SPEAR, ItemSpear::class.java)
//        registerVanillaMaterialResolver(Material.DIAMOND_SPEAR, ItemSpear::class.java)
        registerVanillaMaterialResolver(Material.NETHERITE_SPEAR, ItemSpear::class.java)

        registerVanillaMaterialResolver(Material.BOW, ItemBow::class.java)
        registerVanillaMaterialResolver(Material.CROSSBOW, ItemCrossbow::class.java)

        registerVanillaMaterialResolver(Material.WOODEN_PICKAXE, ItemPickaxe::class.java)
        registerVanillaMaterialResolver(Material.COPPER_PICKAXE, ItemPickaxe::class.java)
        registerVanillaMaterialResolver(Material.GOLDEN_PICKAXE, ItemPickaxe::class.java)
        registerVanillaMaterialResolver(Material.IRON_PICKAXE, ItemPickaxe::class.java)
//        registerVanillaMaterialResolver(Material.DIAMOND_PICKAXE, ItemPickaxe::class.java)
        registerVanillaMaterialResolver(Material.NETHERITE_PICKAXE, ItemPickaxe::class.java)

        registerVanillaMaterialResolver(Material.WOODEN_SHOVEL, ItemShovel::class.java)
        registerVanillaMaterialResolver(Material.COPPER_SHOVEL, ItemShovel::class.java)
        registerVanillaMaterialResolver(Material.GOLDEN_SHOVEL, ItemShovel::class.java)
        registerVanillaMaterialResolver(Material.IRON_SHOVEL, ItemShovel::class.java)
//        registerVanillaMaterialResolver(Material.DIAMOND_SHOVEL, ItemShovel::class.java)
        registerVanillaMaterialResolver(Material.NETHERITE_SHOVEL, ItemShovel::class.java)

        registerVanillaMaterialResolver(Material.WOODEN_HOE, ItemHoe::class.java)
        registerVanillaMaterialResolver(Material.COPPER_HOE, ItemHoe::class.java)
        registerVanillaMaterialResolver(Material.GOLDEN_HOE, ItemHoe::class.java)
        registerVanillaMaterialResolver(Material.IRON_HOE, ItemHoe::class.java)
//        registerVanillaMaterialResolver(Material.DIAMOND_HOE, ItemHoe::class.java)
        registerVanillaMaterialResolver(Material.NETHERITE_HOE, ItemHoe::class.java)

        registerVanillaMaterialResolver(Material.LEATHER_HELMET, ItemArmor::class.java)
        registerVanillaMaterialResolver(Material.LEATHER_CHESTPLATE, ItemArmor::class.java)
        registerVanillaMaterialResolver(Material.LEATHER_LEGGINGS, ItemArmor::class.java)
        registerVanillaMaterialResolver(Material.LEATHER_BOOTS, ItemArmor::class.java)

        registerVanillaMaterialResolver(Material.CHAINMAIL_HELMET, ItemArmor::class.java)
        registerVanillaMaterialResolver(Material.CHAINMAIL_CHESTPLATE, ItemArmor::class.java)
        registerVanillaMaterialResolver(Material.CHAINMAIL_LEGGINGS, ItemArmor::class.java)
        registerVanillaMaterialResolver(Material.CHAINMAIL_BOOTS, ItemArmor::class.java)

        registerVanillaMaterialResolver(Material.COPPER_HELMET, ItemArmor::class.java)
        registerVanillaMaterialResolver(Material.COPPER_CHESTPLATE, ItemArmor::class.java)
        registerVanillaMaterialResolver(Material.COPPER_LEGGINGS, ItemArmor::class.java)
        registerVanillaMaterialResolver(Material.COPPER_BOOTS, ItemArmor::class.java)

        registerVanillaMaterialResolver(Material.GOLDEN_HELMET, ItemArmor::class.java)
        registerVanillaMaterialResolver(Material.GOLDEN_CHESTPLATE, ItemArmor::class.java)
        registerVanillaMaterialResolver(Material.GOLDEN_LEGGINGS, ItemArmor::class.java)
        registerVanillaMaterialResolver(Material.GOLDEN_BOOTS, ItemArmor::class.java)

        registerVanillaMaterialResolver(Material.IRON_HELMET, ItemArmor::class.java)
        registerVanillaMaterialResolver(Material.IRON_CHESTPLATE, ItemArmor::class.java)
        registerVanillaMaterialResolver(Material.IRON_LEGGINGS, ItemArmor::class.java)
        registerVanillaMaterialResolver(Material.IRON_BOOTS, ItemArmor::class.java)

//        registerVanillaMaterialResolver(Material.DIAMOND_HELMET, ItemArmor::class.java)
//        registerVanillaMaterialResolver(Material.DIAMOND_CHESTPLATE, ItemArmor::class.java)
//        registerVanillaMaterialResolver(Material.DIAMOND_LEGGINGS, ItemArmor::class.java)
//        registerVanillaMaterialResolver(Material.DIAMOND_BOOTS, ItemArmor::class.java)

        registerVanillaMaterialResolver(Material.NETHERITE_HELMET, ItemArmor::class.java)
        registerVanillaMaterialResolver(Material.NETHERITE_CHESTPLATE, ItemArmor::class.java)
        registerVanillaMaterialResolver(Material.NETHERITE_LEGGINGS, ItemArmor::class.java)
        registerVanillaMaterialResolver(Material.NETHERITE_BOOTS, ItemArmor::class.java)

        registerVanillaMaterialResolver(Material.ELYTRA, ItemArmor::class.java)
        registerVanillaMaterialResolver(Material.TURTLE_HELMET, ItemArmor::class.java)
        registerVanillaMaterialResolver(Material.LEATHER_HORSE_ARMOR, ItemArmor::class.java)
        registerVanillaMaterialResolver(Material.GOLDEN_HORSE_ARMOR, ItemArmor::class.java)
        registerVanillaMaterialResolver(Material.IRON_HORSE_ARMOR, ItemArmor::class.java)
        registerVanillaMaterialResolver(Material.DIAMOND_HORSE_ARMOR, ItemArmor::class.java)
        registerVanillaMaterialResolver(Material.WOLF_ARMOR, ItemArmor::class.java)

        registerVanillaMaterialResolver(Material.FISHING_ROD, ItemFishingRod::class.java)

        registerVanillaMaterialResolver(Material.MACE, ItemMace::class.java)

        registerVanillaMaterialResolver(Material.ENCHANTED_BOOK, ItemEnchantedBook::class.java)

        registerVanillaMaterialResolver(Material.POTION, PotionBlueprint::class.java)
        registerVanillaMaterialResolver(Material.ENDER_PEARL, EnderPearlBlueprint::class.java)

        // Register vanilla materials that are part of compression chains.
        // Mining — single vanilla base items
        registerVanillaMaterialResolver(Material.COBBLESTONE, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.COBBLED_DEEPSLATE, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.DIRT, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.FLINT, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.OBSIDIAN, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.CHARCOAL, VanillaCompressibleBlueprint::class.java)
        // Mining — two-vanilla-member chains (ingot/dust + block)
        registerVanillaMaterialResolver(Material.IRON_INGOT, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.IRON_BLOCK, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.GOLD_INGOT, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.GOLD_BLOCK, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.DIAMOND, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.DIAMOND_BLOCK, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.EMERALD, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.EMERALD_BLOCK, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.COAL, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.COAL_BLOCK, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.AMETHYST_SHARD, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.AMETHYST_BLOCK, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.COPPER_INGOT, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.COPPER_BLOCK, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.GLOWSTONE_DUST, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.GLOWSTONE, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.LAPIS_LAZULI, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.LAPIS_BLOCK, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.NETHERITE_INGOT, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.NETHERITE_BLOCK, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.QUARTZ, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.QUARTZ_BLOCK, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.REDSTONE, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.REDSTONE_BLOCK, VanillaCompressibleBlueprint::class.java)
        // Mob — single vanilla base items
        registerVanillaMaterialResolver(Material.BONE, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.GUNPOWDER, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.ROTTEN_FLESH, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.LEATHER, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.FEATHER, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.COOKED_CHICKEN, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.COOKED_MUTTON, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.COOKED_PORKCHOP, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.COOKED_BEEF, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.SPIDER_EYE, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.BLAZE_ROD, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.BREEZE_ROD, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.ECHO_SHARD, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.INK_SAC, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.MAGMA_CREAM, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.NAUTILUS_SHELL, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.NETHER_STAR, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.PHANTOM_MEMBRANE, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.PRISMARINE_CRYSTALS, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.PRISMARINE_SHARD, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.RABBIT_HIDE, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.SHULKER_SHELL, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.STRING, VanillaCompressibleBlueprint::class.java)
        // Mob — two-vanilla-member chain (slime)
        registerVanillaMaterialResolver(Material.SLIME_BALL, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.SLIME_BLOCK, VanillaCompressibleBlueprint::class.java)
        // Farming — vanilla chain members
        registerVanillaMaterialResolver(Material.MELON_SLICE, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.MELON, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.SUGAR_CANE, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.WHEAT, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.HAY_BLOCK, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.POTATO, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.CARROT, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.BEETROOT, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.PUMPKIN, VanillaCompressibleBlueprint::class.java)
        // Woodcutting — vanilla chain members
        registerVanillaMaterialResolver(Material.OAK_LOG, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.SPRUCE_LOG, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.BIRCH_LOG, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.JUNGLE_LOG, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.DARK_OAK_LOG, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.ACACIA_LOG, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.MANGROVE_LOG, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.CHERRY_LOG, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.PALE_OAK_LOG, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.CRIMSON_STEM, VanillaCompressibleBlueprint::class.java)
        registerVanillaMaterialResolver(Material.WARPED_STEM, VanillaCompressibleBlueprint::class.java)

        // Register vanilla items that should have a sell price.
        for (entry in VanillaResource.getMaterialWorthMap().entries) {

            if (vanillaBlueprintResolver.containsKey(entry.key)) continue

            registerVanillaMaterialResolver(entry.key, VanillaResource::class.java)
        }

        val plugin = SMPRPG.plugin
        // Loop through all the custom items and use reflection to register a handler
        for (customItemType in CustomItemType.entries) {
            val blueprint: CustomItemBlueprint?
            try {
                blueprint = customItemType.Handler.getConstructor(ItemService::class.java, CustomItemType::class.java)
                    .newInstance(this, customItemType)
            } catch (e: InvocationTargetException) {
                plugin.logger.severe("Failed to register custom item: " + customItemType + e.message)
                continue
            } catch (e: InstantiationException) {
                plugin.logger.severe("Failed to register custom item: " + customItemType + e.message)
                continue
            } catch (e: IllegalAccessException) {
                plugin.logger.severe("Failed to register custom item: " + customItemType + e.message)
                continue
            } catch (e: NoSuchMethodException) {
                plugin.logger.severe("Failed to register custom item: " + customItemType + e.message)
                continue
            }

            registerCustomItem(blueprint)
        }

        // Recipe + unlock registration for every blueprint that provides recipes (craftable items, compression
        // chain members, etc.) happens uniformly in registerProvidedRecipes(), called once every blueprint exists.
        registerNetheriteRecipes()
        Bukkit.getScheduler().runTaskLater(SMPRPG.plugin, Runnable {
            removeUnusedVanillaRecipes()
        }, 1L)
    }

    private fun registerReforges() {
        val plugin = SMPRPG.plugin
        for (reforgeType in ReforgeType.entries) {
            val handler: ReforgeBase = reforgeType.createHandler()
            if (handler is Listener) plugin.server.pluginManager.registerEvents(handler as Listener, plugin)
            reforges.put(reforgeType.key(), handler)
        }
    }

    private fun registerVanillaMaterialResolver(
        material: Material,
        wrapper: Class<out VanillaItemBlueprint>
    ): VanillaItemBlueprint {
        val plugin = SMPRPG.plugin
        plugin.logger.finest(
            String.format(
                "Assigned vanilla material %s with wrapper class %s",
                material.name,
                wrapper.getName()
            )
        )

        val alreadyRegisteredBlueprint = vanillaBlueprintResolver[material]
        if (alreadyRegisteredBlueprint != null) {
            throw IllegalStateException("${material.name} is already registered under ${alreadyRegisteredBlueprint.javaClass.name}!")
        }

        // Use reflection to create an instance of the wrapper.
        val instance: VanillaItemBlueprint?
        try {
            instance = wrapper.getConstructor(ItemService::class.java, Material::class.java).newInstance(this, material)
        } catch (e: Exception) {
            throw IllegalStateException("Failed to instantiate vanilla material handler ${material.name} - ${e.message}")
        }

        vanillaBlueprintResolver.put(material, instance)
        if (instance is Listener) plugin.server.pluginManager.registerEvents(instance, plugin)
        return instance
    }

    private fun registerNetheriteRecipes() {
        val netherite = getCustomItem(Material.NETHERITE_INGOT)
        val rod = getCustomItem(CustomItemType.SULFUR_TREATED_TOOL_SHAFT)
        val plugin = SMPRPG.plugin

        val pickRecipe = ShapedRecipe(NamespacedKey(plugin, "vanilla_netherite_pickaxe-recipe"), generate(Material.NETHERITE_PICKAXE))
        pickRecipe.shape(
            "nnn",
            " s ",
            " s "
        )
        pickRecipe.setIngredient('n', netherite)
        pickRecipe.setIngredient('s', rod)
        pickRecipe.setCategory(CraftingBookCategory.EQUIPMENT)
        plugin.server.addRecipe(pickRecipe)

        val axeRecipe = ShapedRecipe(NamespacedKey(plugin, "vanilla_netherite_axe-recipe"), generate(Material.NETHERITE_AXE))
        axeRecipe.shape(
            "nn ",
            "ns ",
            " s "
        )
        axeRecipe.setIngredient('n', netherite)
        axeRecipe.setIngredient('s', rod)
        axeRecipe.setCategory(CraftingBookCategory.EQUIPMENT)
        plugin.server.addRecipe(axeRecipe)

        val swordRecipe = ShapedRecipe(NamespacedKey(plugin, "vanilla_netherite_sword-recipe"), generate(Material.NETHERITE_SWORD))
        swordRecipe.shape(
            " n ",
            " n ",
            " s "
        )
        swordRecipe.setIngredient('n', netherite)
        swordRecipe.setIngredient('s', rod)
        swordRecipe.setCategory(CraftingBookCategory.EQUIPMENT)
        plugin.server.addRecipe(swordRecipe)

        val hoeRecipe = ShapedRecipe(NamespacedKey(plugin, "vanilla_netherite_hoe-recipe"), generate(Material.NETHERITE_HOE))
        hoeRecipe.shape(
            "nn ",
            " s ",
            " s "
        )
        hoeRecipe.setIngredient('n', netherite)
        hoeRecipe.setIngredient('s', rod)
        hoeRecipe.setCategory(CraftingBookCategory.EQUIPMENT)
        plugin.server.addRecipe(hoeRecipe)

        val shovelRecipe = ShapedRecipe(NamespacedKey(plugin, "vanilla_netherite_shovel-recipe"), generate(Material.NETHERITE_SHOVEL))
        shovelRecipe.shape(
            " n ",
            " s ",
            " s "
        )
        shovelRecipe.setIngredient('n', netherite)
        shovelRecipe.setIngredient('s', rod)
        shovelRecipe.setCategory(CraftingBookCategory.EQUIPMENT)
        plugin.server.addRecipe(shovelRecipe)

        val spearRecipe = ShapedRecipe(NamespacedKey(plugin, "vanilla_netherite_spear-recipe"), generate(Material.NETHERITE_SPEAR))
        spearRecipe.shape(
            "  n",
            " s ",
            "s  "
        )
        spearRecipe.setIngredient('n', netherite)
        spearRecipe.setIngredient('s', rod)
        spearRecipe.setCategory(CraftingBookCategory.EQUIPMENT)
        plugin.server.addRecipe(spearRecipe)

        val bootsRecipe = ShapedRecipe(NamespacedKey(plugin, "vanilla_netherite_boots-recipe"), generate(Material.NETHERITE_BOOTS))
        bootsRecipe.shape(
            "n n",
            "n n"
        )
        bootsRecipe.setIngredient('n', netherite)
        bootsRecipe.setCategory(CraftingBookCategory.EQUIPMENT)
        plugin.server.addRecipe(bootsRecipe)

        val helmetRecipe = ShapedRecipe(NamespacedKey(plugin, "vanilla_netherite_helmet-recipe"), generate(Material.NETHERITE_HELMET))
        helmetRecipe.shape(
            "nnn",
            "n n"
        )
        helmetRecipe.setIngredient('n', netherite)
        helmetRecipe.setCategory(CraftingBookCategory.EQUIPMENT)
        plugin.server.addRecipe(helmetRecipe)

        val leggingsRecipe = ShapedRecipe(NamespacedKey(plugin, "vanilla_netherite_leggings-recipe"), generate(Material.NETHERITE_LEGGINGS))
        leggingsRecipe.shape(
            "nnn",
            "n n",
            "n n"
        )
        leggingsRecipe.setIngredient('n', netherite)
        leggingsRecipe.setCategory(CraftingBookCategory.EQUIPMENT)
        plugin.server.addRecipe(leggingsRecipe)

        val chestplateRecipe = ShapedRecipe(NamespacedKey(plugin, "vanilla_netherite_chestplate-recipe"), generate(Material.NETHERITE_CHESTPLATE))
        chestplateRecipe.shape(
            "n n",
            "nnn",
            "nnn"
        )
        chestplateRecipe.setIngredient('n', netherite)
        chestplateRecipe.setCategory(CraftingBookCategory.EQUIPMENT)
        plugin.server.addRecipe(chestplateRecipe)

        registeredRecipes.add(pickRecipe)
        registeredRecipes.add(axeRecipe)
        registeredRecipes.add(swordRecipe)
        registeredRecipes.add(hoeRecipe)
        registeredRecipes.add(shovelRecipe)
        registeredRecipes.add(spearRecipe)
        registeredRecipes.add(bootsRecipe)
        registeredRecipes.add(helmetRecipe)
        registeredRecipes.add(leggingsRecipe)
        registeredRecipes.add(chestplateRecipe)
    }

    private fun removeUnusedVanillaRecipes() {
        val plugin = SMPRPG.plugin
        val mcNs = NamespacedKey.MINECRAFT_NAMESPACE

        plugin.server.removeRecipe(NamespacedKey(mcNs, "stone_pickaxe"))
        plugin.server.removeRecipe(NamespacedKey(mcNs, "stone_sword"))
        plugin.server.removeRecipe(NamespacedKey(mcNs, "stone_axe"))
        plugin.server.removeRecipe(NamespacedKey(mcNs, "stone_shovel"))
        plugin.server.removeRecipe(NamespacedKey(mcNs, "stone_hoe"))
        plugin.server.removeRecipe(NamespacedKey(mcNs, "stone_spear"))

        plugin.server.removeRecipe(NamespacedKey(mcNs, "diamond_pickaxe"))
        plugin.server.removeRecipe(NamespacedKey(mcNs, "diamond_sword"))
        plugin.server.removeRecipe(NamespacedKey(mcNs, "diamond_axe"))
        plugin.server.removeRecipe(NamespacedKey(mcNs, "diamond_shovel"))
        plugin.server.removeRecipe(NamespacedKey(mcNs, "diamond_hoe"))
        plugin.server.removeRecipe(NamespacedKey(mcNs, "diamond_spear"))
        plugin.server.removeRecipe(NamespacedKey(mcNs, "diamond_helmet"))
        plugin.server.removeRecipe(NamespacedKey(mcNs, "diamond_chestplate"))
        plugin.server.removeRecipe(NamespacedKey(mcNs, "diamond_leggings"))
        plugin.server.removeRecipe(NamespacedKey(mcNs, "diamond_boots"))

        plugin.server.removeRecipe(NamespacedKey(mcNs, "netherite_pickaxe_smithing"))
        plugin.server.removeRecipe(NamespacedKey(mcNs, "netherite_sword_smithing"))
        plugin.server.removeRecipe(NamespacedKey(mcNs, "netherite_axe_smithing"))
        plugin.server.removeRecipe(NamespacedKey(mcNs, "netherite_shovel_smithing"))
        plugin.server.removeRecipe(NamespacedKey(mcNs, "netherite_hoe_smithing"))
        plugin.server.removeRecipe(NamespacedKey(mcNs, "netherite_spear_smithing"))
        plugin.server.removeRecipe(NamespacedKey(mcNs, "netherite_helmet_smithing"))
        plugin.server.removeRecipe(NamespacedKey(mcNs, "netherite_chestplate_smithing"))
        plugin.server.removeRecipe(NamespacedKey(mcNs, "netherite_leggings_smithing"))
        plugin.server.removeRecipe(NamespacedKey(mcNs, "netherite_boots_smithing"))

        plugin.server.updateRecipes()
    }

    private fun registerCustomItem(blueprint: CustomItemBlueprint) {
        val plugin = SMPRPG.plugin
        plugin.logger.finest(
            String.format(
                "Registering custom item %s {key=%s}",
                blueprint.customItemType.ItemName,
                blueprint.customItemType.key
            )
        )

        blueprints[blueprint.customItemType] = blueprint
        keyMappings[blueprint.customItemType.key] = blueprint.customItemType

        // If this blueprint needs to hook into events register them.
        if (blueprint is Listener) plugin.server.pluginManager.registerEvents(blueprint, plugin)
    }

    /**
     * Registers every recipe contributed by any blueprint (craftable items, compression chain members, etc.) along
     * with the unlock links that reveal each recipe in the recipe book. This is the single source of truth for recipe
     * registration so that adding a recipe and wiring up its discovery can never drift apart.
     */
    private fun registerUnlockRecipe(unlockBlueprint: Any, recipeKey: NamespacedKey) {
        when (unlockBlueprint) {
            is CustomItemBlueprint -> {
                customItemToRecipeUnlocks
                    .getOrPut(unlockBlueprint.customItemType) { ArrayList() }
                    .add(recipeKey)
            }

            is VanillaItemBlueprint -> {
                materialToRecipeUnlocks
                    .getOrPut(unlockBlueprint.material) { ArrayList() }
                    .add(recipeKey)
            }
        }
    }

    /** Wire a recipe-book unlock so that acquiring [unlockItem] reveals the recipe identified by [recipeKey]. */
    fun addRecipeUnlock(unlockItem: ItemStack, recipeKey: NamespacedKey) {
        registerUnlockRecipe(getBlueprint(unlockItem), recipeKey)
    }

    fun getItemVersion(item: ItemStack): Int {
        return item.persistentDataContainer.getOrDefault(itemVersionKey, PersistentDataType.INTEGER, 0)
    }

    fun setItemVersion(item: ItemStack, version: Int) {
        item.editMeta { meta -> meta.persistentDataContainer.set(itemVersionKey, PersistentDataType.INTEGER, version)}
    }

    /**
     * Some items don't need to be updated, for example GUI items, weird custom items, and probably debug items
     *
     * @param itemStack The item that should or should not listen for updates.
     * @return true if it should not be updated ever.
     */
    fun shouldIgnoreMetaUpdate(itemStack: ItemStack): Boolean {
        return getItemVersion(itemStack) == VERSION_NO_UPDATE
    }

    fun setIgnoreMetaUpdate(itemStack: ItemStack) {
        setItemVersion(itemStack, VERSION_NO_UPDATE)
    }

    /**
     * Attempt to extract a custom item key from the given item meta. null if it is not a custom item from this plugin.
     *
     * @param meta The ItemMeta of the item.
     * @return The unique identifier for the item type.
     */
    fun getItemKey(meta: ItemMeta?): String? {
        if (meta == null)
            return null

        val key = meta.persistentDataContainer.getOrDefault(itemTypeKey, PersistentDataType.STRING, "")
        if (key.isEmpty()) {
            return null
        }

        return key
    }

    /**
     * Attempt to extract a custom item key from the given item stack. null if it is not a custom item from this plugin.
     *
     * @param itemStack The item.
     * @return The unique identifier for the item type.
     */
    fun getItemKey(itemStack: ItemStack?): String? {
        if (itemStack == null)
            return null

        if (itemStack.itemMeta == null)
            return null

        if (!itemStack.hasItemMeta())
            return null

        return getItemKey(itemStack.itemMeta)
    }

    /**
     * Given a string representation of a key that should be stored for a custom item, attempt to retrieve it.
     * If null is returned, that means the key is not registered.
     *
     * @param key The unique identifier from getItemKey().
     * @return The CustomItemType enum associated with the unique identifier.
     */
    fun getItemTypeFromKey(key: String?): CustomItemType? {
        return keyMappings.getOrDefault(key, null)
    }

    /**
     * Given a custom item type, retreive the blueprint stored for it. We can guarantee that the blueprint
     * queried is going to be a custom item blueprint.
     *
     * @param type The CustomItemType to retrieve a blueprint for.
     * @return The item wrapper/handler for the given type of item.
     */
    fun getBlueprint(type: CustomItemType?): CustomItemBlueprint {
        return blueprints[type] as CustomItemBlueprint
    }

    val customBlueprints: MutableCollection<SMPItemBlueprint>
        /**
         * Returns a collection of all blueprints that are registered as "custom items"
         *
         * @return Returns all the items that are considered custom.
         */
        get() = blueprints.values

    /**
     * Completely ignores any meta stored on an item. Return a vanilla blueprint for a material.
     * If the material resolver detects a certain class to handle this material, use that.
     * If nothing is found, use the default vanilla item blueprint.
     * Can throw a lot of exceptions, but as long as everything is defined correctly this will never happen
     *
     * @param item The item that we are querying a blueprint for.
     * @return The vanilla item blueprint wrapper to interact with this item.
     */
    fun getVanillaBlueprint(item: ItemStack): VanillaItemBlueprint {

        // If we contain the key already for this handler, just return it.
        val present = vanillaBlueprintResolver[item.type]
        if (present != null)
            return present

        // If we don't contain the key for what we desire, simply just create a new one and register it.
        return registerVanillaMaterialResolver(item.type, VanillaItemBlueprint::class.java)
    }

    /**
     * Convenience overload that resolves a vanilla blueprint directly by material rather than by item stack.
     */
    fun getVanillaBlueprint(material: Material): VanillaItemBlueprint {
        val present = vanillaBlueprintResolver[material]
        if (present != null)
            return present
        return registerVanillaMaterialResolver(material, VanillaItemBlueprint::class.java)
    }

    /**
     * Returns all registered vanilla blueprints, including those that implement ICompressible.
     */
    fun getAllVanillaBlueprints(): Collection<VanillaItemBlueprint> = vanillaBlueprintResolver.values

    /**
     * Given an itemstack, retrieve a blueprint that can interact with the item.
     * When an item is custom, the defined custom item blueprint is returned.
     * When an item is vanilla, a fresh transient VanillaItemBlueprint instance is returned.
     * This instance is unique to the item stack and allows modifications similar to what the custom
     * item blueprints do.
     *
     * @param itemStack The item whose blueprint is desired.
     * @return The blueprint of this item.
     */
    fun getBlueprint(itemStack: ItemStack): SMPItemBlueprint {
        // Retrieve the custom item identifier. If it is null, return a brand new VanillaItemBlueprint.

        val key = getItemKey(itemStack)
        if (key == null) return getVanillaBlueprint(itemStack)

        // Retrieve the custom item type. If this is also null, that means this key was not valid. This item is legacy
        val type = getItemTypeFromKey(key)
        if (type == null) return getBlueprint(CustomItemType.LEGACY_ITEM)

        return getBlueprint(type)
    }

    /**
     * Use to generate a new ItemStack using a certain custom item enum
     *
     * @param type The type of item desired.
     * @return A newly generated item of the desired type.
     */
    fun getCustomItem(type: CustomItemType): ItemStack {
        return getBlueprint(type).generate()
    }

    /**
     * Get a vanilla item and make sure it is up to the standard of our SMP items.
     *
     * @param material The vanilla material desired.
     * @return The fully updated and converted vanilla item.
     */
    fun getCustomItem(material: Material): ItemStack {
        val item = ItemStack(material)
        ensureItemStackUpdated(item)
        return item
    }

    /**
     * Use to generate a new ItemStack using the unique identifier for a custom item. can be null if invalid key given.
     *
     * @param key The unique identifier of a desired item.
     * @return The updated and newly generated item.
     */
    fun getCustomItem(key: String): ItemStack? {
        // Check if the key given is valid

        val type = getItemTypeFromKey(key)
        if (type == null)
            return null

        // Make a new item
        return getBlueprint(type).generate()
    }

    /**
     * Resolve a namespaced item key into a freshly generated ItemStack.
     * Supports `smprpg:<custom_key>` (custom items) and `minecraft:<material>` (vanilla items).
     * A bare key with no namespace is assumed to be `minecraft`. Returns null if it does not resolve.
     *
     * @param key The namespaced key, e.g. "smprpg:steel_ingot" or "minecraft:gold_ingot".
     * @return A new ItemStack for the identifier, or null if unknown.
     */
    fun resolveIdentifier(key: String): ItemStack? {
        val (namespace, path) = splitNamespacedKey(key)
        return when (namespace) {
            "smprpg" -> getCustomItem(path)
            "minecraft" -> {
                val material = Material.matchMaterial("minecraft:$path") ?: return null
                if (!material.isItem) return null
                getCustomItem(material)
            }
            else -> null
        }
    }

    /**
     * Resolve the namespaced key that best describes an existing item stack.
     * Custom items return `smprpg:<key>`; everything else returns `minecraft:<material>`.
     *
     * @param item The item to identify.
     * @return The canonical namespaced key for the item.
     */
    fun getIdentifier(item: ItemStack): String {
        val key = getItemKey(item)
        if (key != null)
            return "smprpg:$key"
        return "minecraft:" + item.type.key().value()
    }

    /**
     * Test whether an item stack is of the type described by a namespaced key (type-level, ignoring stack
     * count and NBT such as enchants/reforges). A custom item only matches a `minecraft:` key when it carries
     * no custom item key, so custom items that render as a vanilla material never falsely match the vanilla one.
     *
     * @param item The item to test.
     * @param key The namespaced key to test against.
     * @return true if the item is of that type.
     */
    fun matchesIdentifier(item: ItemStack, key: String): Boolean {
        val (namespace, path) = splitNamespacedKey(key)
        return when (namespace) {
            "smprpg" -> getItemKey(item) == path
            "minecraft" -> getItemKey(item) == null && item.type == Material.matchMaterial("minecraft:$path")
            else -> false
        }
    }

    /** Split a "namespace:path" key into its parts, defaulting to the minecraft namespace when none is given. */
    private fun splitNamespacedKey(key: String): Pair<String, String> {
        val trimmed = key.trim()
        val idx = trimmed.indexOf(':')
        if (idx < 0)
            return "minecraft" to trimmed.lowercase()
        return trimmed.substring(0, idx).lowercase() to trimmed.substring(idx + 1).lowercase()
    }

    /**
     * Get a reforge instance using the reforge type. Will be null if it never registered.
     */
    fun getReforge(type: ReforgeType): ReforgeBase? {
        return reforges[type.key()]
    }

    /**
     * Get the reforge that is contained on the item. Null if not reforged.
     *
     * @param meta The meta of the reforged item.
     * @return The reforge instance on this item.
     */
    fun getReforge(meta: ItemMeta): ReforgeBase? {
        if (!meta.persistentDataContainer.has(reforgeTypeKey)) return null

        val appliedReforgeKey = meta.persistentDataContainer.get(reforgeTypeKey, PersistentDataType.STRING)
        if (!reforges.containsKey(appliedReforgeKey))
            return null

        return reforges[appliedReforgeKey]
    }

    fun getReforge(item: ItemStack?): ReforgeBase? {
        if (item == null || item.type == Material.AIR || !item.hasItemMeta())
            return null

        return getReforge(item.itemMeta)
    }

    /**
     * Given an ItemStack, return the information about this item. Useful if you need to do multiple checks
     * and need blueprint shortcuts since SMPItemQuery contains the blueprint as well.
     *
     * @param itemStack The ItemStack to query.
     * @return A record containing information about the item.
     */
    fun getItemInformation(itemStack: ItemStack): SMPItemQuery {
        val blueprint = getBlueprint(itemStack)
        val type = if (blueprint is CustomItemBlueprint) SMPItemQuery.ItemType.CUSTOM else SMPItemQuery.ItemType.VANILLA

        return SMPItemQuery(type, blueprint)
    }

    /**
     * Given an ItemStack, generate a list of components that is to be used for the lore (tooltip) on the item.
     * This can not only be used for actually setting ItemStack lore, but also in other circumstances such as
     * generating a hoverable chat component or buttons on a GUI.
     * This method consists of basically a disgusting amount of isinstance checks for all item interface types in
     * items/interfaces where we decide how we want to render all the attributes/components an item can have.
     * While I am usually against large methods, it makes sense to have this gross logic all in one place, and it allows
     * items to be very customizable and modular while also having a set order for how components should display.
     *
     * @param itemStack The item to render lore for.
     * @return A list of components describing the item.
     */
    fun renderItemStackLore(itemStack: ItemStack): MutableList<Component?> {
        // First, we need to extract the blueprint of this item so we know how to display it.

        val blueprint = getBlueprint(itemStack)
        val meta = itemStack.itemMeta

        // The lore that we are going to return at the end.
        val lore: MutableList<Component?> = ArrayList<Component?>()


        // Check for stats that the item will apply if equipped.
        if (blueprint is IAttributeItem) {
            val power = blueprint.getPowerRating() + AttributeUtil.getPowerBonus(meta)
            lore.add(
                ComponentUtils.create("Power Rating: ")
                    .append(ComponentUtils.create(Symbols.POWER + power, NamedTextColor.YELLOW))
            )
            lore.add(ComponentUtils.EMPTY)
            lore.addAll(AttributeUtil.getAttributeLore(blueprint, itemStack))
            if (blueprint is TomeBlueprint) {
                val numExcerpts = itemStack.persistentDataContainer.getOrDefault(NecronomiconExcerpts.TOME_SPELL_COUNT_MODIFIER, PersistentDataType.INTEGER, 0)
                var excerptsAddon: Component

                if (numExcerpts > 0) excerptsAddon = ComponentUtils.create(" (+${numExcerpts})", NamedTextColor.DARK_RED)
                else excerptsAddon = ComponentUtils.EMPTY

                lore.add(ComponentUtils.merge(
                    ComponentUtils.create("Max Spell Slots: "),
                    ComponentUtils.create(blueprint.maxSpellSlots.toString(), NamedTextColor.LIGHT_PURPLE),
                    excerptsAddon
                ))
                val cooldownMult = blueprint.cooldownMult
                if (cooldownMult != 1.0) {
                    var operator = "+"
                    var color: TextColor = NamedTextColor.GREEN
                    if (cooldownMult > 1.0) {
                        operator = "-"
                        color = NamedTextColor.RED
                    }
                    // TODO: Just turn this into an attribute at some point.
                    lore.add(
                        ComponentUtils.merge(
                            ComponentUtils.create("Casting Recovery: "),
                            ComponentUtils.create(
                                operator + round(abs((1.0 - cooldownMult) * 100.0)).toInt().toString() + "%",
                                color
                            )
                        )
                    )
                }
            }
            lore.add(
                ComponentUtils.create(
                    "Slot: " + blueprint.getActiveSlot().toString().lowercase(Locale.getDefault()),
                    NamedTextColor.DARK_GRAY
                )
            )
        }

        // Check for a description.
        if (blueprint is IHeaderDescribable) {
            lore.add(ComponentUtils.EMPTY)
            lore.addAll(blueprint.getHeader(itemStack))
        }

        // Check if this is furnace fuel.
        if (blueprint is IFurnaceFuel) {
            lore.add(ComponentUtils.EMPTY)
            lore.add(ComponentUtils.merge(
                ComponentUtils.create("Burns for "),
                ComponentUtils.create(String.format("%ds", blueprint.burnTime/20), NamedTextColor.GOLD),
                ComponentUtils.create(" in furnaces ")
            ))
        }

        // Check if this is a reforge applicator.
        if (blueprint is ReforgeApplicator) {
            lore.add(ComponentUtils.EMPTY)
            lore.addAll(blueprint.getReforgeInformation())
        }

        // If this item is a shield add the shield stats
        if (blueprint is IShield) {
            lore.add(ComponentUtils.EMPTY)
            lore.add(
                ComponentUtils.create("Blocking Resistance: ").append(
                    ComponentUtils.create(
                        "-" + (blueprint.getDamageBlockingPercent() * 100).toInt() + "%",
                        NamedTextColor.GREEN
                    )
                )
            )
            lore.add(
                ComponentUtils.create("Blocking Delay: ")
                    .append(ComponentUtils.create("+" + (blueprint.getShieldDelay() / 20.0) + "s", NamedTextColor.RED))
            )
        }

        if (blueprint is IMace) {
            lore.add(ComponentUtils.EMPTY)
            lore.add(
                ComponentUtils.merge(
                    ComponentUtils.create("Velocity Efficiency: "),
                    ComponentUtils.create(
                        ((blueprint.getVelocityMultiplier() * 100).toInt()).toString() + "%",
                        NamedTextColor.GREEN
                    )
                )
            )
        }

        if (blueprint is IMageBeam) {
            lore.add(ComponentUtils.EMPTY)
            lore.add(
                ComponentUtils.merge(
                    ComponentUtils.create("Mana Cost: "),
                    ComponentUtils.create(
                        (blueprint.manaCost).toString(),
                        NamedTextColor.AQUA
                    )
                )
            )
        }

        // First, enchants. Are we not forcing glow? Only display enchants when we are not forcing glow (and have some).
        if (!itemStack.enchantments.isEmpty()) lore.addAll(blueprint.getEnchantsComponent(itemStack))

        // If this is a fishing rod
        if (blueprint is IFishingRod) {
            lore.add(ComponentUtils.EMPTY)
            lore.add(ComponentUtils.create("This rod is capable of:"))
            for (flag in blueprint.getFishingFlags()) lore.add(
                ComponentUtils.merge(
                    ComponentUtils.create("- "),
                    ComponentUtils.create(flag.Display + " Fishing", flag.Color)
                )
            )
        }

        if (blueprint is ISlayerProficiencyBoost) {
            lore.add(ComponentUtils.EMPTY)
            lore.add(ComponentUtils.merge(
                ComponentUtils.create("Boosts "),
                ComponentUtils.create(blueprint.slayerToBoost.display(), NamedTextColor.DARK_PURPLE, TextDecoration.BOLD),
                ComponentUtils.create(" Slayer quest"))
            )
            lore.add(ComponentUtils.create("progress by +${blueprint.slayerProficiencyBoost}% per kill"))
        }

        if (blueprint is IPassiveProvider) {
            for (passive in blueprint.passives) {
                lore.add(ComponentUtils.EMPTY)
                lore.add(ComponentUtils.merge(
                        AbilityUtil.getAbilityComponent(MinecraftStringUtils.getTitledString(passive.name), passive = true)
                ))
                lore.add(passive.description)
            }
        }

        // If this item holds experience
        if (blueprint is ExperienceThrowable) {
            lore.add(ComponentUtils.EMPTY)
            lore.add(
                ComponentUtils.create("Stored Experience: ").append(
                    ComponentUtils.create(
                        MinecraftStringUtils.formatNumber(
                            blueprint.getExperience().toLong()
                        ) + "XP", NamedTextColor.GREEN
                    )
                )
            )
        }

        // Does this item have consumable properties? First, check for edibility since it is more specific.
        if (blueprint is IEdible) {
            lore.add(ComponentUtils.EMPTY)
            lore.addAll(IEdible.generateEdibilityComponent(itemStack, blueprint))
        } else if (blueprint is IConsumable) {
            lore.add(ComponentUtils.EMPTY)
            lore.addAll(IConsumable.generateConsumabilityComponent(itemStack, blueprint))
        }

        // Temp hack, get food and consumable properties for vanilla items.
        // Keeping this here until we get all vanilla items on the same page as custom ones.
        val foodData = itemStack.getData(DataComponentTypes.FOOD)
        val consumableData = itemStack.getData(DataComponentTypes.CONSUMABLE)
        if (!blueprint.isCustom && foodData != null && consumableData != null) {
            lore.add(ComponentUtils.EMPTY)
            lore.addAll(
                IEdible.generateEdibilityComponent(
                    itemStack,
                    IEdible.fromVanillaData(foodData, consumableData)
                )
            )
        } else if (!blueprint.isCustom && consumableData != null) {
            val consumableData = itemStack.getData(DataComponentTypes.CONSUMABLE)
            if (consumableData != null) {
                lore.add(ComponentUtils.EMPTY)
                lore.addAll(
                    IConsumable.generateConsumabilityComponent(
                        itemStack
                    ) { i: ItemStack? -> consumableData }
                )
            }
        }

        // Is this item compressed (i.e. it decompresses into a lower tier, meaning it is not the base of the chain)?
        val compressionId = getIdentifier(itemStack)
        val compressionBase = if (CompressionGraph.isCompressed(compressionId)) resolveIdentifier(CompressionGraph.baseOf(compressionId)) else null
        if (compressionBase != null) {
            val material: Component = getBlueprint(compressionBase).getGenericMaterial().component()
                .decoration(TextDecoration.BOLD, true)
            val compressedAmount = CompressionGraph.ratioToBase(compressionId)
            lore.add(ComponentUtils.EMPTY)
            lore.add(ComponentUtils.create("An ultra compressed"))
            lore.add(ComponentUtils.create("collection of ").append(material))
            lore.add(ComponentUtils.EMPTY)
            lore.add(
                ComponentUtils.create("(1x)  Uncompressed amount: ", NamedTextColor.DARK_GRAY).append(
                    ComponentUtils.create(
                        MinecraftStringUtils.formatNumber(compressedAmount.toLong()),
                        NamedTextColor.DARK_GRAY, TextDecoration.BOLD
                    )
                )
            )
            lore.add(
                ComponentUtils.create("(64x) Uncompressed amount: ", NamedTextColor.DARK_GRAY).append(
                    ComponentUtils.create(
                        MinecraftStringUtils.formatNumber(compressedAmount * 64L),
                        NamedTextColor.DARK_GRAY, TextDecoration.BOLD
                    )
                )
            )
        }

        // Casts abilities?
        if (blueprint is IAbilityCaster) {
            for (ability in blueprint.getAbilities(itemStack)) {
                lore.add(ComponentUtils.EMPTY)
                lore.add(
                    ComponentUtils.merge(
                        AbilityUtil.getAbilityComponent(ability.ability.friendlyName),
                        ComponentUtils.create(
                            String.format(" (%s)", ability.activation.displayName),
                            NamedTextColor.DARK_GRAY
                        )
                    )
                )
                lore.addAll(ability.ability.description)
                lore.add(
                    ComponentUtils.merge(
                        ComponentUtils.create("Usage Cost: ", NamedTextColor.GRAY),
                        ComponentUtils.create(
                            ability.cost.amount.toString() + ability.cost.resource.symbol,
                            ability.cost.resource.color
                        )
                    )
                )
                lore.add(
                    AbilityUtil.getCooldownComponent(
                        String.format(
                            "%.1fs",
                            blueprint.getCooldown(itemStack) / 20.0
                        )
                    )
                )
            }
        }

        // Is this item reforged?
        if (blueprint.isReforged(itemStack)) {
            lore.add(ComponentUtils.EMPTY)
            lore.addAll(blueprint.getReforgeComponent(itemStack))
        }

        // Footer description (if present)
        if (blueprint is IFooterDescribable) {
            lore.add(ComponentUtils.EMPTY)
            lore.addAll(blueprint.getFooter(itemStack))
        }

        //
        if (blueprint is ISkillRequirement) {
            lore.add(ComponentUtils.EMPTY)
            for (skill in blueprint.skillRequirements) {
                lore.add(ComponentUtils.merge(
                    ComponentUtils.create("• Requires ", NamedTextColor.RED),
                    ComponentUtils.create("${skill.key.displayName} ", skill.key.color),
                    ComponentUtils.create(skill.value.toString(), NamedTextColor.RED)
                ))
            }
        }

        // Durability if the item has it. Ignore charged item blueprints since that is handled in its own class.
        val durabilityComponent = itemStack.getData<@Positive Int?>(DataComponentTypes.MAX_DAMAGE)
        val durabilityUsed = itemStack.getData<@NonNegative Int?>(DataComponentTypes.DAMAGE)
        if (durabilityComponent != null && durabilityUsed != null && (blueprint !is ChargedItemBlueprint)) {
            lore.add(ComponentUtils.EMPTY)
            if (blueprint is WalletBlueprint) {
                lore.add(
                    ComponentUtils.create("Balance: ")
                        .append(ComponentUtils.create(EconomyService.formatMoney((blueprint.getBalance(itemStack)).toLong()), NamedTextColor.GOLD))
                        .append(ComponentUtils.create("/" + EconomyService.formatMoney((blueprint.maxCoins).toLong()), NamedTextColor.GOLD))
                )
            }
            else if (blueprint is IFueledEquipment || blueprint is FuelTankBlueprint) {
                // We need to spoof durability by subtracting 1, since we store as +1 to allow for durability bar rendering.
                lore.add(
                    ComponentUtils.create("Fuel: ")
                        .append(ComponentUtils.create(MinecraftStringUtils.formatNumber((durabilityComponent - durabilityUsed - 1).toLong()), NamedTextColor.RED))
                        .append(ComponentUtils.create("/" + MinecraftStringUtils.formatNumber((durabilityComponent - 1).toLong()), NamedTextColor.DARK_GRAY))
                )
            }
            else{
                lore.add(
                    ComponentUtils.create("Durability: ")
                        .append(ComponentUtils.create(MinecraftStringUtils.formatNumber((durabilityComponent - durabilityUsed).toLong()), NamedTextColor.RED))
                        .append(ComponentUtils.create("/" + MinecraftStringUtils.formatNumber(durabilityComponent.toLong()), NamedTextColor.DARK_GRAY))
                )
                if (durabilityComponent - durabilityUsed == 1) {
                    lore.add(ComponentUtils.EMPTY)
                    lore.add(ComponentUtils.merge(
                        ComponentUtils.create("This item is ", NamedTextColor.RED),
                        ComponentUtils.create("BROKEN", NamedTextColor.DARK_RED, TextDecoration.BOLD),
                        ComponentUtils.create(". Take it to an anvil to repair with:", NamedTextColor.RED),
                    ))

                    if (blueprint is IRepairable) {
                        val repMats = mutableListOf<Key>()
                        val blueprintRepairMaterials = blueprint.repairMaterial.toMutableList()
                        for (repairMaterial in blueprintRepairMaterials) {
                            val repairMatBp = blueprint(repairMaterial)
                            val modelDataId = repairMatBp.customModelDataIdentifier
                            repMats.add(Key.key("smprpg:item/repair_sprites/${modelDataId.substring(7)}"))
                        }
                        var i = 0
                        for (repMat in repMats) {
                            lore.add(
                                ComponentUtils.merge(
                                    ComponentUtils.atlasSprite(Key.key("minecraft:items"), repMat),
                                    ComponentUtils.create(" "),
                                    blueprintRepairMaterials[i].displayName()
                                )
                            )
                            i++
                        }
                    }
                    lore.add(COMMON_REPAIR_CORE_ATLAS_ICON
                        .append(UNCOMMON_REPAIR_CORE_ATLAS_ICON)
                        .append(RARE_REPAIR_CORE_ATLAS_ICON)
                        .append(EPIC_REPAIR_CORE_ATLAS_ICON)
                        .append(LEGENDARY_REPAIR_CORE_ATLAS_ICON)
                        .append(ComponentUtils.merge(
                            ComponentUtils.create(" ["),
                            ComponentUtils.create("Any Repair Core", NamedTextColor.GREEN)),
                            ComponentUtils.create("]")))
                }
            }
        }

        // Damage resistant?
        if (meta != null && meta.hasDamageResistant()) {
            val resistance = meta.damageResistant
            if (resistance != null) lore.add(
                ComponentUtils.create(
                    Symbols.FIRE + resistance.key.asMinimalString(),
                    NamedTextColor.GOLD
                )
            )
        }

        // Now, value and rarity
        lore.add(ComponentUtils.EMPTY)
        if (blueprint is ISellable) {
            val value = blueprint.getWorth(itemStack.asOne())
            if (value > 0) lore.add(
                ComponentUtils.create("Sell Value: ")
                    .append(ComponentUtils.create(EconomyService.formatMoney(value), NamedTextColor.GOLD))
            )
        }

        var itemCategory: String = blueprint.itemClassification.name.replace("_", " ")
        // Fishing rods have an extra prefix...
        if (blueprint is IFishingRod) itemCategory = IFishingRod.FishingFlag.prefix(blueprint.getFishingFlags())
            .uppercase(Locale.getDefault()) + " " + itemCategory

        if (!itemStack.persistentDataContainer.getOrDefault(Recombobulator.RECOMBOBULATOR_KEY, PersistentDataType.BOOLEAN, false)) {
            lore.add(
                blueprint.getRarity(itemStack)
                    .applyDecoration(ComponentUtils.create(blueprint.getRarity(itemStack).name + " " + itemCategory))
                    .decoration(TextDecoration.BOLD, true).color(blueprint.getRarity(itemStack).color)
            )
        }
        else {
            lore.add(
                ComponentUtils.merge(
                    ComponentUtils.create("a ", TextColor.color(blueprint.getRarity(itemStack).color), TextDecoration.OBFUSCATED, TextDecoration.BOLD),
                    blueprint.getRarity(itemStack)
                        .applyDecoration(ComponentUtils.create(blueprint.getRarity(itemStack).name + " " + itemCategory))
                        .decoration(TextDecoration.BOLD, true).color(blueprint.getRarity(itemStack).color),
                    ComponentUtils.create(" b", TextColor.color(blueprint.getRarity(itemStack).color), TextDecoration.OBFUSCATED, TextDecoration.BOLD),
                )
            )
        }
        lore.add(ComponentUtils.create(blueprint.customModelDataIdentifier, NamedTextColor.DARK_GRAY))
        return ComponentUtils.cleanItalics(lore)
    }

    /**
     * Given an item stack, ensure that this item stack is up to date. First makes sure that it contains its
     * initial needed item meta (lore, attributes, etc.) and also determines if updates need to be applied to it.
     *
     * @param itemStack The item to update.
     * @return The newly updated item. This isn't necessary to use, as it should reference the same item as passed in.
     */
    fun ensureItemStackUpdated(itemStack: ItemStack?): ItemStack? {

        if (itemStack == null)
            return null

        if (itemStack.type == Material.AIR)
            return itemStack

        if (shouldIgnoreMetaUpdate(itemStack))
            return itemStack

        // For now just force update all the time, maybe in the future we can have item versioning
        val blueprint = getBlueprint(itemStack)
        blueprint.updateItemData(itemStack)
        return itemStack
    }

    /**
     * Given a player and an item stack, ensure that the player meets the requirements for this item
     * Unlike the static meetsRequirements method, this method will clear out any attribute modifiers on the item.
     *
     * @param player A Player entity
     * @param itemStack The item to check skill requirements on
     * @return Whether the player meets requirements for the item.
     */
    fun updateItemSkillRequirements(player : Player, itemStack: ItemStack?) : Boolean {
        if (itemStack == null) return true  // I mean, I guess you can always use nothing?

        val leveledPlayer = SMPRPG.getService(EntityService::class.java).getPlayerInstance(player)
        val requirementsMet = meetsRequirements(itemStack, leveledPlayer)
        if (!requirementsMet) AttributeService.instance.clearAttributeModifiers(itemStack)

        return requirementsMet
    }

    private fun discoverRecipesForItem(player: Player, item: ItemStack) {
        val blueprint = getBlueprint(item)

        // If this blueprint is a compression member, find the chain root and discover all its linked recipes.
        val chainId = getIdentifier(item)
        if (CompressionGraph.inChain(chainId)) {
            val rootStack = resolveIdentifier(CompressionGraph.baseOf(chainId))
            when (val firstInChain = rootStack?.let { getBlueprint(it) }) {
                is VanillaItemBlueprint -> materialToRecipeUnlocks[firstInChain.material]?.let { player.discoverRecipes(it) }
                is CustomItemBlueprint -> customItemToRecipeUnlocks[firstInChain.customItemType]?.let { player.discoverRecipes(it) }
                else -> {}
            }
        }

        // If this is a vanilla item, see if recipes are discovered by the material
        if (blueprint is VanillaItemBlueprint) {
            val materialRecipe = materialToRecipeUnlocks[blueprint.material]
            if (materialRecipe != null)
                player.discoverRecipes(materialRecipe)
        }

        // If this is a custom item, see if recipes are discovered by the type
        if (blueprint is CustomItemBlueprint) {
            val materialRecipe = customItemToRecipeUnlocks[blueprint.customItemType]
            if (materialRecipe != null)
                player.discoverRecipes(materialRecipe)
        }
    }

    /**
     * When generating loot, update all the items
     */
    @EventHandler
    @Suppress("unused")
    private fun onGenerateLoot(event: LootGenerateEvent) {
        // Loop through all the loot and fix it

        val fixed: MutableList<ItemStack> = ArrayList<ItemStack>()
        for (item in event.loot) {
            val fixedItem = ensureItemStackUpdated(item)
            if (fixedItem == null)
                continue
            fixed.add(fixedItem)
        }
        event.setLoot(fixed)
    }

    /**
     * Makes sure that item meta is set correctly when being brought into this world
     */
    @EventHandler
    @Suppress("unused")
    private fun onMiscResult(event: PrepareResultEvent) {
        // If the item doesn't exist don't do anything

        if (event.result == null)
            return

        // If the item is just air don't do anything
        if (event.result!!.type == Material.AIR)
            return

        ensureItemStackUpdated(event.result)
    }

    @EventHandler
    @Suppress("unused")
    private fun onRequestVillagerTrades(event: PlayerInteractEntityEvent) {
        // Is the entity being interacted with a villager?

        if (event.rightClicked !is Villager)
            return

        val villager = event.rightClicked as Villager

        // Loop through all their trades and ensure their items are up to date.
        val recipes: MutableList<MerchantRecipe> = ArrayList<MerchantRecipe>()
        for (trade in villager.recipes) {
            // Construct a new recipe for this villager copying the attributes from the old one

            val newTradeResult = ItemUtil.checkVillagerItem(this, ensureItemStackUpdated(trade.result))
            val fixedRecipe = MerchantRecipe(newTradeResult, trade.maxUses)

            // Copy over attributes
            fixedRecipe.uses = trade.uses
            fixedRecipe.villagerExperience = trade.villagerExperience
            fixedRecipe.priceMultiplier = trade.priceMultiplier
            fixedRecipe.setExperienceReward(false)
            fixedRecipe.specialPrice = trade.specialPrice
            fixedRecipe.demand = trade.demand
            fixedRecipe.setIgnoreDiscounts(trade.shouldIgnoreDiscounts())

            // Fix the ingredients
            val fixedIngredients: MutableList<ItemStack> = ArrayList<ItemStack>()
            for (ingredient in trade.ingredients) fixedIngredients.add(
                ItemUtil.checkVillagerItem(
                    this,
                    ensureItemStackUpdated(ingredient)
                )
            )
            fixedRecipe.setIngredients(fixedIngredients)

            recipes.add(fixedRecipe)
        }
        villager.recipes = recipes
    }

    @EventHandler
    @Suppress("unused")
    private fun onCraftingResult(event: PrepareItemCraftEvent) {
        // If we aren't crafting a recipe don't do anything

        if (event.inventory.result == null) return

        // If the item is just air don't do anything
        if (event.inventory.result!!.type == Material.AIR) return

        ensureItemStackUpdated(event.inventory.result)
    }

    /**
     * Don't ever let custom items be smith-ed into something else in a smithing table.
     * ALSO
     * We need to handle the rare event that someone is upgrading their gear to netherite.
     * We do not want to keep the old diamond name. But we also don't want to override a custom name. BLEH
     */
    @EventHandler
    @Suppress("unused")
    private fun onSmithingPrepareResult(event: PrepareSmithingEvent) {
        val input = event.inventory.inputEquipment
        // Nothing in input means we don't care
        if (input == null || input.type == Material.AIR) return

        // Nothing in output means we don't care
        val output = event.result
        if (output == null || output.type == Material.AIR) return

        val inputBlueprint = getBlueprint(input)
        val outputBlueprint = getBlueprint(output)

        // Don't allow custom items to turn into vanilla ones. If we have a custom input and a vanilla output, something
        // is not right.
        if (inputBlueprint is CustomItemBlueprint && outputBlueprint is VanillaItemBlueprint) {
            event.result = null
            return
        }

        // Don't allow custom items to be an input
        if (inputBlueprint is CustomItemBlueprint) {
            event.result = null
            return
        }

        // If we have a dummy item as a result, set the result to nothing, this is so that custom smithing recipes can work properly
        if (outputBlueprint is CustomItemBlueprint && outputBlueprint.customItemType == CustomItemType.DUMMY_SMITHING_RESULT) {
            event.result = null
            return
        }

        ensureItemStackUpdated(output)
        event.result = output
    }

    /**
     * Called when a custom item entity spawns in the world. Add a title to it so people know it's custom
     */
    @EventHandler
    @Suppress("unused")
    private fun onCustomItemSpawn(event: ItemSpawnEvent) {
        val blueprint = getBlueprint(event.getEntity().itemStack)
        ensureItemStackUpdated(event.getEntity().itemStack)

        var quantity: Component = ComponentUtils.EMPTY
        if (event.getEntity().itemStack.amount > 1) quantity =
            ComponentUtils.create(String.format("%dx ", event.getEntity().itemStack.amount))

        event.getEntity().customName(quantity.append(blueprint.getNameComponent(event.getEntity().itemStack)))
        event.getEntity().isCustomNameVisible = true

        // Don't continue if the item is common.
        if (blueprint.getRarity(event.getEntity().itemStack) == ItemRarity.COMMON) event.getEntity().isCustomNameVisible =
            false
    }

    /**
     * When custom items merge update the name to reflect the new total
     */
    @EventHandler
    @Suppress("unused")
    private fun onCustomItemMerge(event: ItemMergeEvent) {
        val blueprint = getBlueprint(event.getEntity().itemStack)

        // Do not do this for common items.
        if (blueprint.getRarity(event.getEntity().itemStack) == ItemRarity.COMMON) {
            event.getEntity().isCustomNameVisible = false
            return
        }

        val newTotal = event.getEntity().itemStack.amount + event.target.itemStack.amount

        var quantity = ComponentUtils.EMPTY
        if (newTotal > 1) quantity = ComponentUtils.create(String.format("%dx ", newTotal))

        event.target.customName(quantity.append(blueprint.getNameComponent(event.getEntity().itemStack)))
        event.target.isCustomNameVisible = true
    }

    /**
     * Never ever allow custom items to translate into vanilla items unless we make a recipe ourselves.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    @Suppress("unused")
    fun onAttemptCraft(event: PrepareItemCraftEvent) {
        // No recipe :p

        if (event.recipe == null) return

        var craftingWithCustomItems = false
        for (input in event.inventory.matrix) {
            if (input == null) continue

            val ceItem = BukkitAdaptor.adapt(input)
            if (this.getItemInformation(input).isCustom && !ceItem.isCustomItem) {
                craftingWithCustomItems = true
                break
            }
        }

        // If we are purely doing a vanilla crafting interaction, ignore :3
        if (!craftingWithCustomItems) return

        if (event.recipe !is Keyed)
            return

        // If we are dealing with a recipe that is not in vanilla minecraft, ignore
        val recipe = event.recipe as Keyed
        if (recipe.key().namespace() != Key.MINECRAFT_NAMESPACE)
            return

        // So now we know we are using a vanilla recipe, and custom items are in the input.
        // Custom compression items reuse vanilla materials (e.g. an enchanted iron block is a IRON_BLOCK), so a
        // vanilla recipe greedily matches them and wins the auto-pick before our compression recipe can. Recover the
        // intended compression result so the grid auto-resolves; if there is no compression result, fall back to
        // refusing the craft so custom items can never yield vanilla output through a vanilla recipe.
        event.inventory.result = resolveCollidingCompressionResult(event.inventory.matrix)
    }

    /**
     * Given a crafting matrix that a vanilla recipe matched while it actually contained custom items, work out the
     * compression result the player intended. Returns null when the matrix is not a uniform compression grid.
     *
     * The matrix must hold a single custom [ICompressible] item in every occupied slot; the number of occupied slots
     * then selects the direction (the compressor input count compresses, a single item decompresses). The amounts are
     * read straight from the compression model, so this stays in sync with the recipes built in buildCompressionRecipe.
     */
    private fun resolveCollidingCompressionResult(matrix: Array<ItemStack?>): ItemStack? {
        val items = matrix.filterNotNull().filter { it.type != Material.AIR }
        if (items.isEmpty()) return null

        val ingredient = items.first()
        // Only custom items collide with vanilla recipes; vanilla compression resolves normally.
        val ingredientKey = getItemKey(ingredient) ?: return null
        val id = getIdentifier(ingredient)
        if (!CompressionGraph.inChain(id)) return null

        // Every occupied slot must hold the exact same custom item for a compression recipe to apply.
        if (items.any { getItemKey(it) != ingredientKey }) return null

        val occupiedSlots = items.size
        // A full chain's worth of this item compresses up into the next tier (always one output).
        CompressionGraph.compressStep(id)?.let { (higherId, inputAmount) ->
            if (occupiedSlots == inputAmount)
                return resolveIdentifier(higherId)?.apply { amount = 1 }
        }
        // A single item decompresses down into its lower tier (the decompression ratio worth of output).
        CompressionGraph.decompressStep(id)?.let { (lowerId, ratio) ->
            if (occupiedSlots == 1)
                return resolveIdentifier(lowerId)?.apply { amount = ratio }
        }
        return null
    }

    @EventHandler(priority = EventPriority.MONITOR)
    @Suppress("unused")
    fun onEnchant(e: EnchantItemEvent) {
        if (e.isCancelled) return

        object : BukkitRunnable() {
            override fun run() {
                ensureItemStackUpdated(e.item)
            }
        }.runTaskLater(SMPRPG.plugin, TickTime.INSTANTANEOUSLY)
    }

    /**
     * Schedules item update for next tick so we can catch things like interacting with buckets
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    @Suppress("unused")
    fun onInteract(event: PlayerInteractEvent) {
        object : BukkitRunnable() {
            override fun run() {
                ensureItemStackUpdated(event.player.inventory.itemInMainHand)
                ensureItemStackUpdated(event.player.inventory.itemInOffHand)

                updateItemSkillRequirements(event.player, event.player.inventory.itemInMainHand)
                updateItemSkillRequirements(event.player, event.player.inventory.itemInOffHand)
                updateItemSkillRequirements(event.player, event.player.inventory.helmet)
                updateItemSkillRequirements(event.player, event.player.inventory.chestplate)
                updateItemSkillRequirements(event.player, event.player.inventory.leggings)
                updateItemSkillRequirements(event.player, event.player.inventory.boots)
            }
        }.runTaskLater(SMPRPG.plugin, 0L)
    }

    /**
     * Mainly used for keeping durability in sync when breaking blocks
     */
    @EventHandler(priority = EventPriority.MONITOR)
    @Suppress("unused")
    fun onBreakBlock(event: BlockBreakEvent) {
        if (event.isCancelled) return

        object : BukkitRunnable() {
            override fun run() {
                ensureItemStackUpdated(event.player.inventory.itemInMainHand)
            }
        }.runTaskLater(SMPRPG.plugin, 0L)
    }

    @EventHandler
    @Suppress("unused")
    private fun onArmorTakeDamage(event: EntityDamageEvent) {

        val entity = event.entity

        if (entity !is LivingEntity)
            return

        if (entity.equipment == null)
            return

        val gear = arrayOf<ItemStack?>(
            entity.equipment!!.helmet, entity.equipment!!.chestplate,
            entity.equipment!!.leggings, entity.equipment!!.boots
        )

        object : BukkitRunnable() {
            override fun run() {
                for (item in gear) ensureItemStackUpdated(item)
            }
        }.runTaskLater(SMPRPG.plugin, 0L)
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    @Suppress("unused")
    private fun onClickItem(event: InventoryClickEvent) {
        if (event.getCurrentItem() == null || event.getCurrentItem()!!.type == Material.AIR) return

        if (event.clickedInventory == null)
            return

        if (event.clickedInventory!!.type == InventoryType.CHEST)
            return

        ensureItemStackUpdated(event.getCurrentItem())
        discoverRecipesForItem(event.whoClicked as Player, event.getCurrentItem()!!)
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    @Suppress("unused")
    private fun onPickupItem(event: PlayerAttemptPickupItemEvent) {
        if (!event.flyAtPlayer) return

        discoverRecipesForItem(event.player, event.item.itemStack)
    }

    @EventHandler
    @Suppress("unused")
    private fun onPlaceCustomItem(event: BlockPlaceEvent) {
        val item = event.getItemInHand()
        val blueprint = getBlueprint(item)

        // Hack for summoning crystals. Allow them to be placed!
        if (blueprint is CustomItemBlueprint && blueprint.customItemType == CustomItemType.SUMMONING_CRYSTAL) return

        if (blueprint is IDamageFromCrops) return

        // Craft Engine items are allowed to handle their own logic.
        if (blueprint is CraftEngineBlueprint) return

        // If this item is a custom item, don't allow it to be placed!!!
        if (blueprint.isCustom) event.isCancelled = true
    }

    companion object {

        // Integer to tag items with whenever we update them to prevent unnecessary work
        const val VERSION: Int = 1
        const val VERSION_NO_UPDATE: Int = -1
        val SELL_VALUE_KEY: NamespacedKey = NamespacedKey("smprpg", "sell-value")
        val FREEZER_FUEL_KEY: NamespacedKey = NamespacedKey("smprpg", "freeze-time")

        val COMMON_REPAIR_CORE_ATLAS_ICON = ComponentUtils.atlasSprite(Key.key("minecraft:items"), Key.key("smprpg:item/repair_sprites/common_repair_core"))
        val UNCOMMON_REPAIR_CORE_ATLAS_ICON = ComponentUtils.atlasSprite(Key.key("minecraft:items"), Key.key("smprpg:item/repair_sprites/uncommon_repair_core"))
        val RARE_REPAIR_CORE_ATLAS_ICON = ComponentUtils.atlasSprite(Key.key("minecraft:items"), Key.key("smprpg:item/repair_sprites/rare_repair_core"))
        val EPIC_REPAIR_CORE_ATLAS_ICON = ComponentUtils.atlasSprite(Key.key("minecraft:items"), Key.key("smprpg:item/repair_sprites/epic_repair_core"))
        val LEGENDARY_REPAIR_CORE_ATLAS_ICON = ComponentUtils.atlasSprite(Key.key("minecraft:items"), Key.key("smprpg:item/repair_sprites/legendary_repair_core"))

        val CRAFT_ENGINE_ID = NamespacedKey("craftengine", "id")

        // Shortcut methods to do very common operations much less verbosely. This instance should always be a singleton
        // so static method calls like this are designed to be safe.

        /**
         * Given a material wrapper, retrieve a default ItemStack of the item type.
         *
         * @param type The type of item you want
         * @return an ItemStack instance freshly generated of the type desired
         */
        @JvmStatic
        fun generate(type: MaterialWrapper): ItemStack {
            return if (type.isCustom)
                generate(type.custom)
            else
                generate(type.vanilla)
        }

        @JvmStatic
        fun generate(type: MaterialWrapper, quantity: Int): ItemStack {
            val item = generate(type)
            item.amount = quantity
            return item
        }

        // Shortcut methods to do very common operations much less verbosely. This instance should always be a singleton
        // so static method calls like this are designed to be safe.
        /**
         * Given a custom item type enum, retrieve a default ItemStack of the custom item type.
         *
         * @param type The type of item you want
         * @return an ItemStack instance freshly generated of the type desired
         */
        @JvmStatic
        fun generate(type: CustomItemType): ItemStack {
            try {
                return SMPRPG.getService(ItemService::class.java).getCustomItem(type)
            } catch (e: Exception) {
                SMPRPG.plugin.logger.severe { "Failed to generate vanilla item for $type" }
                e.printStackTrace()
                return ItemStack(Material.AIR)
            }
        }

        @JvmStatic
        fun generate(type: CustomItemType, quantity: Int): ItemStack {
            val item = generate(type)
            item.amount = quantity
            return item
        }

        /**
         * Given a Bukkit material, return a vanilla instanced ItemStack with properly updated metadata.
         *
         * @param material The Minecraft material to retrieve
         * @return An ItemStack instance freshly generated of the vanilla material desired
         */
        @JvmStatic
        fun generate(material: Material): ItemStack {
            try {
                return SMPRPG.getService(ItemService::class.java).getCustomItem(material)
            } catch (e: Exception) {
                SMPRPG.plugin.logger.severe { "Failed to generate vanilla item for $material" }
                e.printStackTrace()
                return ItemStack(Material.AIR)
            }
        }

        @JvmStatic
        fun generate(material: Material, quantity: Int): ItemStack {
            val item = generate(material)
            item.amount = quantity
            return item
        }

        /**
         * Given an item, retrieve its blueprint.
         * @param item The item to get a blueprint of.
         * @return The blueprint.
         */
        @JvmStatic
        fun blueprint(item: ItemStack): SMPItemBlueprint {
            return SMPRPG.getService(ItemService::class.java).getBlueprint(item)
        }

        /**
         * Given a vanilla material, retrieve its blueprint that it would map to if it was in item form.
         * @param item The item to get a blueprint of.
         * @return The blueprint.
         */
        @JvmStatic
        fun blueprint(item: Material): SMPItemBlueprint {
            return SMPRPG.getService(ItemService::class.java).getVanillaBlueprint(ItemStack.of(item))
        }

        /**
         * Given a vanilla material, retrieve its blueprint that it would map to if it was in item form.
         * @param item The item to get a blueprint of.
         * @return The blueprint.
         */
        @JvmStatic
        fun blueprint(item: CustomItemType): SMPItemBlueprint {
            return SMPRPG.getService(ItemService::class.java).getBlueprint(item)
        }

        /**
         * Given an item, return whether it is broken or not
         * @param item The item to check
         * @return Whether it is broken or not
         */
        @JvmStatic
        fun isBroken(item: ItemStack) : Boolean {
            if (blueprint(item) is IBreakableEquipment) {
                val damage = item.getData(DataComponentTypes.DAMAGE) as Int
                val maxDamage = item.getData(DataComponentTypes.MAX_DAMAGE) as Int
                return (damage >= (maxDamage - 1))
            }
            return false  // Can't be broken if it isn't breakable.
        }

        /**
         * Given an item, return whether the LeveledPlayer can equip/use it
         * @param item The item to check
         * @param player The LeveledPlayer instance to check against
         * @return Whether it can be used or not
         */
        @JvmStatic
        fun meetsRequirements(item: ItemStack, player: LeveledPlayer): Boolean {
            if (player.player.gameMode == GameMode.CREATIVE) return true
            val bp = blueprint(item)
            if (bp is ISkillRequirement) {
                for (requirement in bp.skillRequirements) {
                    for (skill in player.skills) {
                        if (skill.type == requirement.key) {
                            if (skill.level < requirement.value)
                                return false
                        }
                    }
                }
            }
            return true
        }
        @JvmStatic
        fun meetsRequirements(item: ItemStack, player: Player): Boolean {
            val leveledPlayer = SMPRPG.getService(EntityService::class.java).getPlayerInstance(player)
            return meetsRequirements(item, leveledPlayer)
        }

        /**
         * Given an item, retrieve a copy of it that is set to default behavior/data.
         * @param item The item to get a clean copy of.
         * @return The item.
         */
        @JvmStatic
        fun clean(item: ItemStack): ItemStack {
            return SMPRPG.getService(ItemService::class.java).getBlueprint(item).generate(item.amount)
        }

        /**
         * Given an item and an item type, change the underlying item class on the item without affecting any of its
         * stored data, then update the item. This should effectively allow seamless item upgrades.
         * Keep in mind, if you abuse this unexpected things will happen. Usage should only be limited to item upgrades
         * of the same type.
         * @param itemStack The item to transform.
         * @param newBlueprint The new blueprint to make the item conform to.
         * @return A new item stack copy with change applied.
         */
        @JvmStatic
        fun transmute(itemStack: ItemStack, newBlueprint: SMPItemBlueprint): ItemStack {
            // Clone the item.

            val copy = itemStack.clone()
            val service = SMPRPG.getService(ItemService::class.java)

            // Remove the item class key from this item completely so it loses its reference to what it was.
            copy.editPersistentDataContainer { pdc: PersistentDataContainer -> pdc.remove(service.itemTypeKey) }

            // Force the item to be updated against the new blueprints rules. If the blueprint is custom, the blueprint
            // will inject the new desired item class key for us, as well as update required item components.
            newBlueprint.updateItemData(/* itemStack = */ copy)
            return copy
        }

        @JvmStatic
        fun isOfSameType(o1: ItemStack, o2: ItemStack): Boolean {
            return blueprint(o1).isItemOfType(o2)
        }

        @JvmStatic
        fun isInternalIdMatch(o1: ItemStack, o2: ItemStack) : Boolean {
            // TODO: Super hacky, find out why repair items fail an "isSimilar" check, forcing an internal-id comparison
            return o1.persistentDataContainer.get(NamespacedKey("smprpg", "item-type"), PersistentDataType.STRING).equals(
                o2.persistentDataContainer.get(NamespacedKey("smprpg", "item-type"), PersistentDataType.STRING))
        }
    }
}


