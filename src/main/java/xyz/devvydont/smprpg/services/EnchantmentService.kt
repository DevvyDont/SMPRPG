package xyz.devvydont.smprpg.services

import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.TypedKey
import io.papermc.paper.registry.keys.EnchantmentKeys
import org.bukkit.GameMode
import org.bukkit.Registry
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentOffer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.enchantment.PrepareItemEnchantEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.EnchantingInventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ItemType
import org.bukkit.inventory.meta.ItemMeta
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.enchantments.CustomEnchantment
import xyz.devvydont.smprpg.enchantments.calculator.EnchantmentCalculator
import xyz.devvydont.smprpg.enchantments.calculator.EnchantmentCalculator.EnchantmentSlot
import xyz.devvydont.smprpg.enchantments.definitions.*
import xyz.devvydont.smprpg.enchantments.definitions.vanilla.UnknownEnchantment
import xyz.devvydont.smprpg.enchantments.definitions.vanilla.overrides.*
import xyz.devvydont.smprpg.enchantments.definitions.vanilla.unchanged.*
import java.util.*

class EnchantmentService : IService, Listener {

    val enchantments: MutableMap<Enchantment, CustomEnchantment> = HashMap<Enchantment, CustomEnchantment>()

    @Throws(RuntimeException::class)
    override fun setup() {
        for (enchantment in CUSTOM_ENCHANTMENTS) {
            enchantments.put(getEnchantment(enchantment), enchantment)

            // If an enchantment wants to listen to events, register it.
            if (enchantment is Listener)
                plugin.server.pluginManager.registerEvents(enchantment, plugin)
        }
    }

    override fun cleanup() {
    }

    private val enchantmentRegistry: Registry<Enchantment>
        get() = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)

    /**
     * Given a vanilla enchantment key, return the vanilla representation of the enchantment.
     * @param key The enchantment key.
     * @return A vanilla enchantment instance.
     * @throws NoSuchElementException When the element is not present.
     */
    fun getEnchantment(key: TypedKey<Enchantment>): Enchantment {
        return this.enchantmentRegistry.getOrThrow(key)
    }

    /**
     * Given a custom enchant wrapper, return the vanilla representation of the enchantment.
     * @param enchantment The custom enchantment that is meant to be vanilla.
     * @return A vanilla enchantment instance.
     */
    fun getEnchantment(enchantment: CustomEnchantment): Enchantment {
        return getEnchantment(TypedKey.create(RegistryKey.ENCHANTMENT, enchantment.getKey()))
    }

    /**
     * Given a vanilla enchantment, return the custom wrapped version of the enchantment.
     * @param enchantment The vanilla enchantment instance.
     * @return The custom wrapper over it.
     */
    fun getEnchantment(enchantment: Enchantment): CustomEnchantment? {
        if (!enchantments.containsKey(enchantment)) return UnknownEnchantment(
            TypedKey.create(
                RegistryKey.ENCHANTMENT,
                enchantment.key
            )
        )
        return enchantments[enchantment]
    }

    val customEnchantments: MutableCollection<CustomEnchantment>
        /**
         * Returns all registered custom enchantments.
         * @return All the enchantments registered.
         */
        get() = enchantments.values

    /**
     * Given an item, return a collection of custom enchantments stored on the item.
     * @param meta The ItemMeta to query.
     * @return A sorted collection of enchantments.
     */
    fun getCustomEnchantments(meta: ItemMeta): MutableCollection<CustomEnchantment> {
        return getCustomEnchantments(meta.enchants)
    }

    val orderedCustomEnchantments: MutableCollection<CustomEnchantment>
        /**
         * Returns all enchantments that are registered on the server in the order they were registered.
         * This is mainly only used for displaying using the "default" sorting mode in the enchanting GUI.
         * If you don't really care about the order, call [EnchantmentService.getCustomEnchantments] instead.
         * @return A sorted collection of enchantments.
         */
        get() {
            val registered = ArrayList<CustomEnchantment>()
            for (enchantment in CUSTOM_ENCHANTMENTS)
                if (enchantments.containsKey(enchantment.enchantment))
                    registered.add(enchantment)
            return registered
        }

    /**
     * Returns a collection of enchantments sorted by the order that they are defined above
     * @param enchants The enchants to sort.
     * @return A sorted collection enchantments.
     */
    fun getCustomEnchantments(enchants: MutableMap<Enchantment, Int>): MutableCollection<CustomEnchantment> {
        val enchantList: MutableList<CustomEnchantment> = ArrayList<CustomEnchantment>()
        for (enchantment in CUSTOM_ENCHANTMENTS) {
            val queried = enchants[enchantment.enchantment]
            if (queried != null)
                enchantList.add(enchantment.build(queried))
        }
        return enchantList
    }

    /**
     * Returns a new enchanting calculator you can use to apply enchants randomly to items exactly how enchanting
     * tables do it.
     */
    fun getCalculator(item: ItemStack, bookshelfBonus: Int, magicLevel: Int): EnchantmentCalculator {
        return EnchantmentCalculator(item, bookshelfBonus, magicLevel)
    }

    /**
     * When opening an enchanting GUI, the lapis lazuli slot should always be filled.
     */
    @EventHandler
    @Suppress("unused")
    private fun onOpenEnchantInterface(event: InventoryOpenEvent) {

        if (event.inventory !is EnchantingInventory)
            return

        val enchanting = event.inventory as EnchantingInventory
        enchanting.secondary = ItemType.LAPIS_LAZULI.createItemStack(64)
    }

    /**
     * When closing an enchanting GUI, get rid of the lapis lazuli so we don't duplicate it
     */
    @EventHandler
    @Suppress("unused")
    private fun onCloseEnchantInterface(event: InventoryCloseEvent) {

        if (event.inventory !is EnchantingInventory)
            return

        val enchanting = event.inventory as EnchantingInventory
        enchanting.secondary = null
    }

    /**
     * When clicking the lapis in the enchant GUI, don't allow any interactions with it
     */
    @EventHandler
    @Suppress("unused")
    private fun onClickLapisEnchantInterface(event: InventoryClickEvent) {

        if (event.clickedInventory == null)
            return

        if (event.clickedInventory!!.type != InventoryType.ENCHANTING)
            return

        val lapisLazuliSlot = 1
        if (event.slot != lapisLazuliSlot)
            return

        event.isCancelled = true
    }

    @EventHandler
    @Suppress("unused")
    private fun onEnchantPrepare(event: PrepareItemEnchantEvent) {

        val player = SMPRPG.getService(EntityService::class.java).getPlayerInstance(event.enchanter)
        val calculator = getCalculator(event.item, event.enchantmentBonus, player.magicSkill.level)

        val offers = calculator.calculate()
        calculatorCache.put(event.enchanter.uniqueId, offers)

        // Loop through the entries and update the preview that shows when players hover over the options.
        for (entry in offers.entries)
            event.offers[entry.key!!.ordinal] =
                if (!entry.value!!.isEmpty())
                    entry.value.first()
                else
                    null
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    @Suppress("unused")
    private fun onEnchant(event: EnchantItemEvent) {

        val allOffers: MutableMap<EnchantmentSlot, MutableList<EnchantmentOffer>>? = calculatorCache[event.enchanter.uniqueId]
        if (allOffers == null)
            return

        val clickedOffers: MutableList<EnchantmentOffer>? = allOffers[EnchantmentSlot.fromButton(event.whichButton())]
        if (clickedOffers == null)
            return

        event.enchantsToAdd.clear()
        for (offer in clickedOffers)
            event.enchantsToAdd.put(offer.enchantment, offer.enchantmentLevel)

        // Hack to make it "seem" like we are taking all the experience
        var newLevel = event.enchanter.level - event.expLevelCost

        // Creative mode players can enchant freely.
        if (event.enchanter.gameMode == GameMode.CREATIVE)
            newLevel = event.enchanter.level

        val scheduler = plugin.server.scheduler
        val finalNewLevel = newLevel
        scheduler.runTaskLater(plugin, Runnable runTaskLater@{
            val view = event.enchanter.openInventory
            if (view.topInventory !is EnchantingInventory)
                return@runTaskLater
            val inv = view.topInventory as EnchantingInventory
            inv.secondary = ItemType.LAPIS_LAZULI.createItemStack(64)
            val result: ItemStack? = inv.item
            SMPRPG.getService(ItemService::class.java).ensureItemStackUpdated(result)
            inv.item = result
            event.enchanter.level = finalNewLevel
        }, 0L)
    }

    companion object {

        // Vanilla overrides
        @JvmField
        val AQUA_AFFINITY: CustomEnchantment = AquaAffinityEnchantment(EnchantmentKeys.AQUA_AFFINITY)

        @JvmField
        val BANE_OF_ARTHROPODS: CustomEnchantment = BaneOfArthropodsEnchantment(EnchantmentKeys.BANE_OF_ARTHROPODS)

        @JvmField
        val BLAST_PROTECTION: CustomEnchantment = BlastProtectionEnchantment(EnchantmentKeys.BLAST_PROTECTION)

        @JvmField
        val BREACH: CustomEnchantment = BreachEnchantment(EnchantmentKeys.BREACH)

        @JvmField
        val CHANNELING: CustomEnchantment = ChannelingEnchantment(EnchantmentKeys.CHANNELING)

        @JvmField
        val BINDING_CURSE: CustomEnchantment = BindingCurseEnchantment(EnchantmentKeys.BINDING_CURSE)

        @JvmField
        val VANISHING_CURSE: CustomEnchantment = VanishingCurseEnchantment(EnchantmentKeys.VANISHING_CURSE)

        @JvmField
        val DENSITY: CustomEnchantment = DensityEnchantment(EnchantmentKeys.DENSITY)

        @JvmField
        val DEPTH_STRIDER: CustomEnchantment = DepthStriderEnchantment(EnchantmentKeys.DEPTH_STRIDER)

        @JvmField
        val EFFICIENCY: CustomEnchantment = EfficiencyEnchantment(EnchantmentKeys.EFFICIENCY)

        @JvmField
        val FEATHER_FALLING: CustomEnchantment = FeatherFallingEnchantment(EnchantmentKeys.FEATHER_FALLING)

        @JvmField
        val FIRE_ASPECT: CustomEnchantment = FireAspectEnchantment(EnchantmentKeys.FIRE_ASPECT)

        @JvmField
        val FIRE_PROTECTION: CustomEnchantment = FireProtectionEnchantment(EnchantmentKeys.FIRE_PROTECTION)

        @JvmField
        val FLAME: CustomEnchantment = FlameEnchantment(EnchantmentKeys.FLAME)

        @JvmField
        val FORTUNE: CustomEnchantment = FortuneEnchantment(EnchantmentKeys.FORTUNE)

        @JvmField
        val FROST_WALKER: CustomEnchantment = FrostWalkerEnchantment(EnchantmentKeys.FROST_WALKER)

        @JvmField
        val IMPALING: CustomEnchantment = ImpalingEnchantment(EnchantmentKeys.IMPALING)

        @JvmField
        val INFINITY: CustomEnchantment = InfinityEnchantment(EnchantmentKeys.INFINITY)

        @JvmField
        val KNOCKBACK: CustomEnchantment = KnockbackEnchantment(EnchantmentKeys.KNOCKBACK)

        @JvmField
        val LOOTING: CustomEnchantment = LootingEnchantment(EnchantmentKeys.LOOTING)

        @JvmField
        val LOYALTY: CustomEnchantment = LoyaltyEnchantment(EnchantmentKeys.LOYALTY)

        @JvmField
        val LUCK_OF_THE_SEA: CustomEnchantment = LuckOfTheSeaEnchantment(EnchantmentKeys.LUCK_OF_THE_SEA)

        @JvmField
        val LURE: CustomEnchantment = LureEnchantment(EnchantmentKeys.LURE)

        @JvmField
        val MENDING: CustomEnchantment = MendingEnchantment(EnchantmentKeys.MENDING)

        @JvmField
        val MULTISHOT: CustomEnchantment = MultishotEnchantment(EnchantmentKeys.MULTISHOT)

        @JvmField
        val PIERCING: CustomEnchantment = PiercingEnchantment(EnchantmentKeys.PIERCING)

        @JvmField
        val POWER: CustomEnchantment = PowerEnchantment(EnchantmentKeys.POWER)

        @JvmField
        val PROJECTILE_PROTECTION: CustomEnchantment = ProjectileProtectionEnchantment(EnchantmentKeys.PROJECTILE_PROTECTION)

        @JvmField
        val PROTECTION: CustomEnchantment = ProtectionEnchantment(EnchantmentKeys.PROTECTION)

        @JvmField
        val PUNCH: CustomEnchantment = PunchEnchantment(EnchantmentKeys.PUNCH)

        @JvmField
        val QUICK_CHARGE: CustomEnchantment = QuickChargeEnchantment(EnchantmentKeys.QUICK_CHARGE)

        @JvmField
        val RESPIRATION: CustomEnchantment = RespirationEnchantment(EnchantmentKeys.RESPIRATION)

        @JvmField
        val RIPTIDE: CustomEnchantment = RiptideEnchantment(EnchantmentKeys.RIPTIDE)

        @JvmField
        val SHARPNESS: CustomEnchantment = SharpnessEnchantment(EnchantmentKeys.SHARPNESS)

        @JvmField
        val SILK_TOUCH: CustomEnchantment = SilkTouchEnchantment(EnchantmentKeys.SILK_TOUCH)

        @JvmField
        val SMITE: CustomEnchantment = SmiteEnchantment(EnchantmentKeys.SMITE)

        @JvmField
        val SOUL_SPEED: CustomEnchantment = SoulSpeedEnchantment(EnchantmentKeys.SOUL_SPEED)

        @JvmField
        val SWEEPING_EDGE: CustomEnchantment = SweepingEdgeEnchantment(EnchantmentKeys.SWEEPING_EDGE)

        @JvmField
        val SWIFT_SNEAK: CustomEnchantment = SwiftSneakEnchantment(EnchantmentKeys.SWIFT_SNEAK)

        @JvmField
        val THORNS: CustomEnchantment = ThornsEnchantment(EnchantmentKeys.THORNS)

        @JvmField
        val UNBREAKING: CustomEnchantment = UnbreakingEnchantment(EnchantmentKeys.UNBREAKING)

        @JvmField
        val WIND_BURST: CustomEnchantment = WindBurstEnchantment(EnchantmentKeys.WIND_BURST)

        // Custom enchantments
        @JvmField
        val KEEPING_BLESSING: CustomEnchantment = KeepingBlessing("keeping")

        @JvmField
        val TELEKINESIS_BLESSING: CustomEnchantment = TelekinesisBlessing("telekinesis")

        @JvmField
        val MERCY_BLESSING: CustomEnchantment = MercyBlessing("mercy")

        @JvmField
        val VOIDSTRIDING_BLESSING: CustomEnchantment = VoidstridingBlessing("voidstriding")

        @JvmField
        val FORTUITY: CustomEnchantment = FortuityEnchantment("fortuity")

        @JvmField
        val HEARTY: CustomEnchantment = HeartyEnchantment("hearty")

        @JvmField
        val SPEEDSTER: CustomEnchantment = SpeedsterEnchantment("speedster")

        @JvmField
        val LEECH: CustomEnchantment = LeechEnchantment("leech")

        @JvmField
        val TRACING: CustomEnchantment = BossTracingEnchantment("tracing")

        @JvmField
        val BLESSED: CustomEnchantment = BlessedEnchantment("blessed")

        @JvmField
        val STABILIZED: CustomEnchantment = StabilizedEnchantment("stabilized")

        @JvmField
        val PROFICIENT: CustomEnchantment = ProficientEnchantment("proficient")

        @JvmField
        val CLIMBING: CustomEnchantment = ClimbingEnchantment("climbing")

        @JvmField
        val SERRATED: CustomEnchantment = SerratedEnchantment("serrated")

        @JvmField
        val OPPORTUNIST: CustomEnchantment = OpportunistEnchantment("opportunist")

        @JvmField
        val SNIPE: CustomEnchantment = SnipeEnchantment("snipe")

        @JvmField
        val VELOCITY: CustomEnchantment = VelocityEnchantment("velocity")

        @JvmField
        val FIRST_STRIKE: CustomEnchantment = FirstStrikeEnchantment("first_strike")

        @JvmField
        val DOUBLE_TAP: CustomEnchantment = DoubleTapEnchantment("double_tap")

        @JvmField
        val EXECUTE: CustomEnchantment = ExecuteEnchantment("execute")

        @JvmField
        val SYPHON: CustomEnchantment = SyphonEnchantment("syphon")

        @JvmField
        val CALAMITY: CustomEnchantment = CalamityEnchantment("calamity")

        @JvmField
        val WISDOM: CustomEnchantment = WisdomEnchantment("wisdom")

        @JvmField
        val VITALITY: CustomEnchantment = VitalityEnchantment("vitality")

        @JvmField
        val VIGOROUS: CustomEnchantment = VigorousEnchantment("vigorous")

        @JvmField
        val INSIGHT: CustomEnchantment = InsightEnchantment("insight")

        @JvmField
        val ABYSSAL_INSTINCT: CustomEnchantment = AbyssalInstinctEnchantment("abyssal_instinct")

        @JvmField
        val TREASURE_HUNTER: CustomEnchantment = TreasureHunterEnchantment("treasure_hunter")

        @JvmField
        val FELLING: CustomEnchantment = FellingEnchantment("felling")

        @JvmField
        val CHOPPING: CustomEnchantment = ChoppingEnchantment("chopping")

        @JvmField
        val HARVESTING: CustomEnchantment = HarvestingEnchantment("harvesting")

        @JvmField
        val REPLENISHING: CustomEnchantment = ReplenishingBlessing("replenishing")


        /**
         * Enchantments to register on the server. The order you define them here will affect the order that they
         * are displayed on items, and the default sorting mode in the /enchantments interface.
         */
        @JvmField
        val CUSTOM_ENCHANTMENTS: Array<CustomEnchantment> = arrayOf<CustomEnchantment>( // Blessings
            KEEPING_BLESSING,
            TELEKINESIS_BLESSING,
            MERCY_BLESSING,
            VOIDSTRIDING_BLESSING,
            REPLENISHING,  // Curses

            BINDING_CURSE,
            VANISHING_CURSE,  // Important enchantments (display first)

            SHARPNESS,
            POWER,
            SMITE,
            BANE_OF_ARTHROPODS,

            BLESSED,
            STABILIZED,

            SERRATED,
            OPPORTUNIST,
            EFFICIENCY,
            FIRST_STRIKE,
            DOUBLE_TAP,
            EXECUTE,

            INFINITY,

            PROTECTION,
            BLAST_PROTECTION,
            FIRE_PROTECTION,
            PROJECTILE_PROTECTION,
            HEARTY,
            INSIGHT,
            VITALITY,
            CALAMITY,
            WISDOM,

            AQUA_AFFINITY,
            BREACH,
            CHANNELING,
            DENSITY,
            DEPTH_STRIDER,
            FEATHER_FALLING,
            FIRE_ASPECT,
            FLAME,
            FORTUNE,
            HARVESTING,
            FELLING,
            CHOPPING,
            FROST_WALKER,
            IMPALING,
            KNOCKBACK,
            LOOTING,
            LOYALTY,
            LUCK_OF_THE_SEA,
            LURE,
            ABYSSAL_INSTINCT,
            TREASURE_HUNTER,
            MULTISHOT,
            PIERCING,
            PUNCH,
            QUICK_CHARGE,
            RESPIRATION,
            RIPTIDE,
            SILK_TOUCH,
            SOUL_SPEED,
            SWEEPING_EDGE,
            SWIFT_SNEAK,
            THORNS,
            UNBREAKING,
            WIND_BURST,
            MENDING,

            CLIMBING,
            VIGOROUS,
            FORTUITY,
            LEECH,
            SYPHON,
            PROFICIENT,
            SPEEDSTER,
            TRACING,
            SNIPE,
            VELOCITY
        )

        // Caches calculator queries so that the EnchantItemEvent can use results from PrepareItemEnchantEvent
        var calculatorCache: MutableMap<UUID, MutableMap<EnchantmentSlot, MutableList<EnchantmentOffer>>> =
            HashMap<UUID, MutableMap<EnchantmentSlot, MutableList<EnchantmentOffer>>>()
    }
}
