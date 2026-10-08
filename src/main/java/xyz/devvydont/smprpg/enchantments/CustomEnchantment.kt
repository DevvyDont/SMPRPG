package xyz.devvydont.smprpg.enchantments

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.plugin.bootstrap.BootstrapContext
import io.papermc.paper.plugin.lifecycle.event.handler.LifecycleEventHandler
import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.TypedKey
import io.papermc.paper.registry.data.EnchantmentRegistryEntry
import io.papermc.paper.registry.data.EnchantmentRegistryEntry.EnchantmentCost
import io.papermc.paper.registry.event.RegistryComposeEvent
import io.papermc.paper.registry.event.RegistryEvents
import io.papermc.paper.registry.set.RegistryKeySet
import io.papermc.paper.registry.set.RegistrySet
import io.papermc.paper.registry.tag.TagKey
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Color
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ItemType
import org.bukkit.inventory.meta.ItemMeta
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.enchantments.recipe.EnchantmentRecipe
import xyz.devvydont.smprpg.entity.player.LeveledPlayer
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.services.EnchantmentService
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.services.RecipeService
import java.util.*
import java.util.function.Consumer
import kotlin.math.min

enum class ScrollColor(val color : Color) {
    STANDARD(Color.fromRGB(207, 197, 162)),
    ARTIFICE(Color.fromRGB(255, 195, 121)),
    ARTIFICE_PURPLE(Color.fromRGB(195, 0, 211)),
    BLESSING(Color.fromRGB(191, 255, 255))
}

abstract class CustomEnchantment(val id: String) : Cloneable {
    var isBootstrapped: Boolean = false
        private set

    open var key: Key = Key.key("smprpg", id)
    open var typedKey: TypedKey<Enchantment> = TypedKey.create(RegistryKey.ENCHANTMENT, this.key)

    open var level: Int = UNAPPLIED

    fun build(level: Int): CustomEnchantment {
        val copy: CustomEnchantment

        try {
            copy = clone() as CustomEnchantment
        } catch (e: CloneNotSupportedException) {
            throw IllegalStateException("Enchantment $id cannot be cloned", e)
        }

        copy.level = level
        return copy
    }

    fun bootstrapCompleted() {
        this.isBootstrapped = true
    }

    open fun bootstrap(context: BootstrapContext) {
        check(!this.isBootstrapped) { "Enchantment " + javaClass.getName() + " is already bootstrapped!" }

        context.getLifecycleManager().registerEventHandler(
            RegistryEvents.ENCHANTMENT.compose()
                .newHandler(LifecycleEventHandler { event: RegistryComposeEvent<Enchantment, EnchantmentRegistryEntry.Builder> ->
                    event.registry().register(
                        this.typedKey,
                        Consumer { b: EnchantmentRegistryEntry.Builder ->
                            b.description(this.displayName)
                                .primaryItems(getSupportedItems(event))
                                .supportedItems(getSupportedItems(event))
                                .anvilCost(0)
                                .maxLevel(this.maxLevel)
                                .weight(this.weight)
                                .minimumCost(EnchantmentCost.of(1, 1))
                                .maximumCost(EnchantmentCost.of(1, 1))
                                .activeSlots(this.equipmentSlotGroup!!)
                                .exclusiveWith(this.conflictingEnchantments)
                        }
                    )
                })
        )

        bootstrapCompleted()
    }

    /**
     * The "Vanilla" enchantment representation. This is what is registered to minecraft via data-driven enchantments.
     */
    val enchantment: Enchantment get() = SMPRPG.getService(EnchantmentService::class.java).getEnchantment(this.typedKey)

    open val conflictingEnchantments: RegistryKeySet<Enchantment>
        /**
         * A set of enchantments that this enchantment conflicts with.
         * If there are none, this enchantment has no conflicts
         */
        get() = RegistrySet.keySet(RegistryKey.ENCHANTMENT)

    /**
     * The name that the enchantment shows in item lore, without the numerals.
     */
    abstract val displayName: Component

    /**
     * A short description of the enchantment
     */
    abstract val description: Component

    /**
     * Optional
     * A longer description of the enchantment. If present, will override any instances of "description" in lore/ui.
     */
    open val longDescription: MutableCollection<Component?> get() = mutableListOf()

    /**
     * The TagKey for what item enchantability categories this enchantment can apply to (Ex: ItemTypeTagKeys.ENCHANTABLE_SHARP_WEAPON)"
     */
    abstract val itemTypeTag: TagKey<ItemType>

    /**
     * The color that this enchantment shows up as in item lore.
     */
    open val enchantColor: TextColor get() = NamedTextColor.LIGHT_PURPLE

    /**
     * scrollColor: The color of the scroll parchment on the DynamicEnchantingScroll item
     * scrollBindingColor: The color of the binding on the DynamicEnchantingScroll item
     */
    open val scrollColor : Color get() = ScrollColor.STANDARD.color
    open val scrollBindingColor : Color get() = Color.fromRGB(128, 128, 128)

    /**
     * The maximum level this enchantment can go to.
     */
    abstract val maxLevel: Int

    /**
     * The "weight" of the enchantment. This normally would be its roll chance in vanilla, but it has been
     * repurposed to be associated with enchantment rarity.
     *
     * When setting this variable, it should be done via EnchantmentRarity.*raritu*.weight (Ex: EnchantmentRarity.COMMON.weight)
     */
    abstract val weight: Int

    /**
     * The slot that the enchanted item must be present in for the enchantment to function.
     */
    abstract val equipmentSlotGroup: EquipmentSlotGroup?

    /**
     * Boolean flag for whether this enchantment is a blessing or not.
     */
    open val isBlessing: Boolean get() = false

    /**
     * Magic Skill level requirement for the first tier of this enchantment.
     */
    abstract val skillRequirement: Int

    /**
     * DEPRECATED: Still technically can be used, but is part of old enchanting tech
     * The skill level required to stop rolling this enchantment. This is mainly used so that players can stop
     * rolling curse enchantments on gear at a certain level
     */
    open val skillRequirementToAvoid: Int get() = Int.Companion.MAX_VALUE

    fun getSupportedItems(event: RegistryComposeEvent<Enchantment, EnchantmentRegistryEntry.Builder>): RegistryKeySet<ItemType> {
        return event.getOrCreateTag(this.itemTypeTag)
    }

    open fun getRecipeKey(level: Int): NamespacedKey {
        return NamespacedKey(
            plugin,
            this.key.value().lowercase(Locale.getDefault()) + String.format("%s-recipe", level)
        )
    }

    /**
     * The reagents + magic power to apply this enchantment at the given level. Read from the data-driven
     * recipe registry (the recipes enchanting folder); returns null when no recipe is defined (e.g. above max level).
     * Ingredients are rebuilt with an inflated max stack size so large reagent counts display in the table GUI.
     */
    open fun getRecipe(level: Int): EnchantmentRecipe? {
        val core = SMPRPG.getService(RecipeService::class.java).getRegistry().enchantingRecipe(id, level) ?: return null
        val itemService = SMPRPG.getService(ItemService::class.java)
        val ingredients = core.ingredients.mapNotNull { ingredient ->
            itemService.resolveIdentifier(ingredient.identifier.asString())?.apply {
                setAmount(ingredient.amount)
                editMeta(Consumer { meta: ItemMeta? -> meta!!.setMaxStackSize(99) })
            }
        }
        return EnchantmentRecipe(getRecipeKey(level), core.power, *ingredients.toTypedArray())
    }

    /**
     * Checks the requirement of the enchantment for a specific level of the enchantment.
     * @param level
     * @return
     */
    fun getSkillRequirementForLevel(level: Int): Int {
        val percentage = (level - 1).toDouble() / this.maxLevel
        val enchantLevelRequirement = (percentage * (100 - this.skillRequirement) + this.skillRequirement).toInt()
        return min(100, enchantLevelRequirement)
    }

    fun getMagicExperience(level: Int): Int {
        // 100-1000 XP for level bonus
        val levelBonus = (this.level / this.maxLevel.toDouble() * 900 + 100).toInt()
        // Rarity multiplier, rarer enchants provide better bonuses
        // can get 100-1000 xp for this as well
        val rarityBonus = (1.0 / (this.weight + 1.0)).toInt() * 900 + 100

        return levelBonus + rarityBonus
    }

    open val magicExperience: Int get() = getMagicExperience(0)

    override fun toString(): String {
        return this.key.toString() + " " + this.level
    }

    open fun isEnchantmentActive(itemStack : ItemStack, player : LeveledPlayer) : Boolean {
        val itemBp = ItemService.blueprint(itemStack)

        // If item is breakable, and is broken, return false.
        if (itemBp is IBreakableEquipment) {
            if (itemStack.getData(DataComponentTypes.DAMAGE) as Int >= (itemStack.getData(DataComponentTypes.MAX_DAMAGE) as Int - 1))
                return false
        }

        // If we do not meet the skill requirement for this item, return false.
        return ItemService.meetsRequirements(itemStack, player)
    }

    companion object {
        val UNAPPLIED: Int = -1

        // Placeholder value, VERY near white by default but used to flag in lore generation
        @JvmStatic
        var ARTIFICE_COLOR: TextColor = TextColor.color(16711422)
    }
}
