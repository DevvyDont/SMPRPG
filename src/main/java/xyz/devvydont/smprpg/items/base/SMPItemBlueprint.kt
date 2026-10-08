package xyz.devvydont.smprpg.items.base

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.CustomModelData
import io.papermc.paper.datacomponent.item.Enchantable
import io.papermc.paper.datacomponent.item.Equippable
import io.papermc.paper.datacomponent.item.UseCooldown
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ArmorMeta
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.trim.ArmorTrim
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.enchantments.CustomEnchantment
import xyz.devvydont.smprpg.enchantments.definitions.vanilla.overrides.UnbreakingEnchantment
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.ItemRarity
import xyz.devvydont.smprpg.items.interfaces.*
import xyz.devvydont.smprpg.reforge.ReforgeBase
import xyz.devvydont.smprpg.reforge.ReforgeType
import xyz.devvydont.smprpg.services.AttributeService
import xyz.devvydont.smprpg.services.EnchantmentService
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.services.ItemService.Companion.blueprint
import xyz.devvydont.smprpg.util.attributes.AttributeUtil
import xyz.devvydont.smprpg.util.crafting.MaterialWrapper
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.Symbols
import xyz.devvydont.smprpg.util.items.FoodUtil
import java.util.function.Consumer
import kotlin.math.roundToInt

/**
 * The base class for every single item in the game. All items and blueprints will inherit from this
 * Can be broken into two major chunks:
 * - VanillaItem
 * - CustomItem
 */
abstract class SMPItemBlueprint(
    /**
     * Set a reference to the Item Service for ease of access
     */
    // TODO: Get rid of this after kotlin migration?
    @JvmField val itemService: ItemService
) {


    /**
     * Determine what type of item this is.
     */
    abstract val itemClassification: ItemClassification

    /**
     * Extract rarity from the item. We do this to check if rarity has been upgraded. If we don't find anything special,
     * we use getDefaultRarity().
     */
    abstract fun getRarity(item: ItemStack): ItemRarity

    /**
     * The rarity that this item will start at. Does not guarantee that the rarity will always be this, however.
     * (Rarity can be changed via upgrades on an item stack, this is simply for generating new items.)
     */
    abstract val defaultRarity: ItemRarity

    /**
     * Determines the string that this item will be identified by when using custom model data.
     * @return A string to be used for resource pack creation.
     */
    abstract val customModelDataIdentifier: String

    /**
     * Shortcut method to retrieving a material wrapper to get access to things like unique keys from blueprints.
     */
    fun getGenericMaterial(): MaterialWrapper {
        if (this is CustomItemBlueprint)
            return MaterialWrapper(this.customItemType)
        if (this is VanillaItemBlueprint)
            return MaterialWrapper(this.material)
        throw IllegalStateException("${this::class.simpleName} could not be determined as vanilla or custom. Check class structure!")
    }


    /**
     * Given item meta, determine how this item's display name should look
     */
    fun getNameComponent(item: ItemStack): Component {
        return getRarity(item).applyDecoration(ComponentUtils.create(getReforgePrefix(item) + getItemName(item)))
    }

    /**
     * Retrieves the currently applied to reforge singleton instance contained on this reforge.
     * If this item doesn't have a reforge, null is returned
     *
     * @param item ItemMeta contained on an item stack
     * @return a Reforge singleton if the ItemMeta has a reforge type stored in it
     */
    fun getReforge(item: ItemStack): ReforgeBase? {
        return itemService.getReforge(item)
    }

    /**
     * Retrieves the currently applied to reforge type contained on this reforge.
     * If this item doesn't have a reforge, null is returned
     *
     * @param item ItemMeta contained on an item stack
     * @return a ReforgeType enum if the ItemMeta has a reforge type stored in it
     */
    fun getReforgeType(item: ItemStack?): ReforgeType? {
        val reforge = getReforge(item!!)
        if (reforge == null) return null
        return reforge.type
    }

    fun isReforged(item: ItemStack?): Boolean {
        return getReforge(item!!) != null
    }

    /**
     * Gets the name of this item
     *
     * @param item The item.
     * @return The item name.
     */
    abstract fun getItemName(item: ItemStack?): String

    /**
     * Gets the prefix to inject before the name of the item. Returns an empty string if no reforge is applied.
     *
     * @param item The item.
     * @return The reforge prefix.
     */
    fun getReforgePrefix(item: ItemStack): String {
        val reforge = getReforge(item)
        if (reforge == null) return ""

        return reforge.type.display() + " "
    }

    /**
     * Set the fake glow status of this item. If an item is enchantable, you should make this false.
     */
    open fun wantFakeEnchantGlow(): Boolean {
        return false
    }

    /**
     * Determines if item given is an item belonging to this blueprint
     */
    abstract fun isItemOfType(itemStack: ItemStack): Boolean

    /**
     * Determines if this item is custom. When this is false, we have a purely vanilla item.
     */
    abstract val isCustom: Boolean

    /**
     * Generate an ItemStack of this blueprint.
     */
    abstract fun generate(): ItemStack


    fun generate(amount: Int): ItemStack {
        val stack = generate()
        stack.amount = amount
        return stack
    }

    /**
     * The number of allowed enchantments on an item depends on the rarity of it.
     * Common = 1
     * Uncommon = 3
     * Rare = 5
     * Epic = 7
     * Legendary = 9
     * Mythic = 11
     * Divine = 13
     * Transcendent = 15
     * Special = 17
     *
     * @return How many enchants this item can have.
     */
    open fun getMaxAllowedEnchantments(item: ItemStack): Int {
        val blueprint = blueprint(item)
        return when (blueprint.getRarity(item)) {
            ItemRarity.COMMON -> 1
            ItemRarity.UNCOMMON -> 3
            ItemRarity.RARE -> 5
            ItemRarity.EPIC -> 7
            ItemRarity.LEGENDARY -> 9
            ItemRarity.ARTIFICE, ItemRarity.MYTHIC -> 11
            ItemRarity.DIVINE -> 13
            ItemRarity.TRANSCENDENT -> 15
            ItemRarity.SPECIAL -> 17
            else -> 0
        }
    }

    /**
     * Given item meta for this specific item type, return what to display for enchants
     */
    open fun getEnchantsComponent(item: ItemStack): MutableList<Component?> {
        val meta = item.itemMeta
        val lines: MutableList<Component?> = ArrayList()
        lines.add(ComponentUtils.EMPTY)
        for (enchantment in SMPRPG.getService(EnchantmentService::class.java)
            .getCustomEnchantments(meta)) {
            val color = enchantment.enchantColor
            var name = if (color === CustomEnchantment.ARTIFICE_COLOR) {
                ComponentUtils.gradient(
                    PlainTextComponentSerializer.plainText()
                        .serialize(enchantment.enchantment.displayName(enchantment.level)),
                    NamedTextColor.DARK_PURPLE,
                    TextColor.color(255, 0, 0)
                ).decorate(TextDecoration.ITALIC)
            } else {
                enchantment.enchantment.displayName(enchantment.level).color(enchantment.enchantColor)
            }
            name = ComponentUtils.create(Symbols.SPARKLES + " ", NamedTextColor.LIGHT_PURPLE).append(name)
            lines.add(name)
            if (meta.enchants.size <= 9) {
                if (!enchantment.longDescription.isEmpty()) {
                    lines.addAll(enchantment.longDescription)
                } else {
                    lines.add(enchantment.description)
                }
            }
        }
        lines.add(
            ComponentUtils.create(
                "Enchantments: " + meta.enchants.size + "/" + getMaxAllowedEnchantments(
                    item
                ), NamedTextColor.DARK_GRAY
            )
        )
        return lines
    }

    fun getReforgeComponent(item: ItemStack): MutableList<Component?> {
        val reforge = getReforge(item)
        if (reforge == null) return mutableListOf()

        val lines: MutableList<Component?> = ArrayList()
        lines.add(ComponentUtils.create(reforge.type.display() + " Reforge", NamedTextColor.BLUE))
        lines.addAll(reforge.description)
        return lines
    }

    /**
     * Called to set various components and attributes of this item, can be overidden for extra functionality
     */
    open fun updateItemData(meta: ItemMeta) {
        // Add fake glow (if wanted)

        if (wantFakeEnchantGlow()) meta.setEnchantmentGlintOverride(true)

        // Apply a color! (if we want them and can actually apply them...)
        if (this is IDyeable && meta is LeatherArmorMeta) {
            val color = (this as IDyeable).getColor()
            meta.setColor(color)
            meta.addItemFlags(ItemFlag.HIDE_DYE)
        }

        // Apply armor trims! (if we want them and can actually apply them...)
        if (this is ITrimmable && meta is ArmorMeta) {
            val material = (this as ITrimmable).getTrimMaterial()
            val pattern = (this as ITrimmable).getTrimPattern()
            meta.trim = ArmorTrim(material, pattern)
            meta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM)
        }

        // Set this item to be vulnerable to damage if it is custom no matter what.
        if (this.isCustom) meta.damageResistant = null

        // Never allow vanilla attribute data to show.
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
    }

    fun updateVanillaFoodComponent(item: ItemStack) {
        if (item.type.asItemType() == null) return

        if (!item.type.asItemType()!!.isEdible) return

        val food = FoodUtil.getVanillaFoodComponent(item.type)
        item.editMeta(Consumer { meta: ItemMeta? -> meta!!.setFood(food) })
    }

    /**
     * Called to retrieve item meta off of an item stack, apply new updated item meta to it, and apply it
     * Ideally, we should only be working with the new component API over item meta.
     */
    open fun updateItemData(itemStack: ItemStack) {

        // Air is considered the "null" item. Don't do anything if we don't have an item.
        if (itemStack.type == Material.AIR)
            return

        // If this is a vanilla item, we need to hack the vanilla food component back on the item.
        if (!this.isCustom)
            updateVanillaFoodComponent(itemStack)

        // If it's a custom edible item, do it the preferred way.
        if (this is IEdible) {
            val food = FoodUtil.getVanillaFoodComponent(Material.APPLE)
            val edible = this as IEdible
            food.nutrition = edible.getNutrition(itemStack)
            food.saturation = edible.getSaturation(itemStack)
            food.setCanAlwaysEat(edible.canAlwaysEat(itemStack))
            itemStack.editMeta(Consumer { meta: ItemMeta? -> meta!!.setFood(food) })
        }

        // If this is equipment with durability, apply a durability tag on it. Anything without this durability tag is considered unbreakable.
        when (this) {
            is IBreakableEquipment -> {
                val dmg = itemStack.getData(DataComponentTypes.DAMAGE)
                val breakable = this as IBreakableEquipment
                var maxDurability = breakable.maxDurability.toDouble()
                val unbreakingLevel = itemStack.getEnchantmentLevel(EnchantmentService.UNBREAKING.enchantment)
                if (unbreakingLevel > 0) {
                    maxDurability *= (1 + (UnbreakingEnchantment.getDurabilityIncrease(unbreakingLevel) / 100.0))
                }
                itemStack.setData(DataComponentTypes.MAX_DAMAGE, maxDurability.roundToInt())
                itemStack.setData(DataComponentTypes.DAMAGE, dmg ?: 0)
                itemStack.unsetData(DataComponentTypes.UNBREAKABLE)
            }

            is ChargedItemBlueprint, is IFueledEquipment, is ICosmeticDurability -> {
                // Don't do anything here. We need the damage data to persist.
            }

            else -> {
                itemStack.unsetData(DataComponentTypes.MAX_DAMAGE)
                itemStack.unsetData(DataComponentTypes.DAMAGE)
                itemStack.editMeta(Consumer { meta: ItemMeta? -> meta!!.isUnbreakable = true })
            }
        }

        itemStack.addItemFlags(ItemFlag.HIDE_UNBREAKABLE)

        // This is a hack to allow any item as far as vanilla is concerned to be enchanted.
        // Our plugin decides what enchants get rolled and what items can be enchanted in the first place.
        if (this.itemClassification.isEnchantable) itemStack.setData(
            DataComponentTypes.ENCHANTABLE,
            Enchantable.enchantable(10)
        )
        else itemStack.unsetData(DataComponentTypes.ENCHANTABLE)

        // Disable vanilla repairable components, we handle this in the plugin
        itemStack.unsetData(DataComponentTypes.REPAIRABLE)

        // Apply custom model data.
        itemStack.setData(
            DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData()
                .addString(this.customModelDataIdentifier)
                .build()
        )

        // Apply a model override if desired.
        if (this is IModelOverridden)  {
            val overridden = this as IModelOverridden
            itemStack.setData<Key?>(DataComponentTypes.ITEM_MODEL, overridden.getDisplayKey())
        }

        // Apply custom texture data if present. This also will remove the ability to equip it.
        if (this is ICustomTextured) {
            val textured = this as ICustomTextured
            ICustomTextured.update(itemStack, textured)
            // It also turns out that for some reason setting the name doesn't work for skull items. Lovely.
            itemStack.setData<Component?>(
                DataComponentTypes.CUSTOM_NAME,
                getNameComponent(itemStack).decoration(TextDecoration.ITALIC, false)
            )
        }

        // If this is a faked armor piece, override the equipment data.
        if (this is IEquippableOverride) {
            val override = this as IEquippableOverride
            itemStack.setData<Equippable?>(DataComponentTypes.EQUIPPABLE, override.getEquipmentOverride())
            itemStack.editMeta(Consumer { meta: ItemMeta? -> meta!!.setMaxStackSize(1) }) // Also don't allow it to stack.
        }

        // Now that our item has equippable properties (if it's meant to), we can inject an asset ID onto the item
        // so a resource pack can override it (if it has a key defined).
        if (this is IEquippableAssetOverride) {
            val equippable = itemStack.getData(DataComponentTypes.EQUIPPABLE)
            val override = this as IEquippableAssetOverride
            if (equippable != null) itemStack.setData(
                DataComponentTypes.EQUIPPABLE, Equippable.equippable(equippable.slot())
                    .damageOnHurt(equippable.damageOnHurt())
                    .dispensable(equippable.dispensable())
                    .swappable(equippable.swappable())
                    .equipSound(equippable.equipSound())
                    .allowedEntities(equippable.allowedEntities())
                    .assetId(override.getAssetId())
                    .shearSound(equippable.shearSound())
                    .cameraOverlay(equippable.cameraOverlay())
                    .equipOnInteract(equippable.equipOnInteract())
                    .canBeSheared(equippable.canBeSheared())
                    .build()
            )
        }

        // If this is an ability caster, apply the cooldown group data to the item stack.
        if (this is IAbilityCaster) {
            val caster = this as IAbilityCaster
            val cooldown = caster.getCooldown(itemStack).toFloat()
            val cooldownGroup = caster.getCooldownGroup(itemStack)
            if (cooldown > 0.0f) {
                itemStack.setData(
                    DataComponentTypes.USE_COOLDOWN,
                    UseCooldown.useCooldown(cooldown)
                        .cooldownGroup(cooldownGroup).build()
                )
            }
        }

        // If this is a consumable, apply the consumable data to the item stack.
        if (this is IConsumable) {
            val consumable = this as IConsumable
            itemStack.setData(
                DataComponentTypes.CONSUMABLE,
                consumable.getConsumableComponent(itemStack)
            )
        }

        // Set our tooltip style according to rarity
        itemStack.setData(DataComponentTypes.TOOLTIP_STYLE, Key.key("smprpg:${getRarity(itemStack).name.lowercase()}_item"))

        // If this item contains attributes, apply them.
        var addAttributes = true
        if (this is IBreakableEquipment) {
            // Only apply our modifiers if the item is not broken.
            if (itemStack.getData(DataComponentTypes.DAMAGE) as Int >= (itemStack.getData(DataComponentTypes.MAX_DAMAGE) as Int - 1))
                addAttributes = false
            }

        if (addAttributes)
            AttributeUtil.applyModifiers(this, itemStack)
        else
            AttributeService.instance.clearAttributeModifiers(itemStack)

        // Update meta properties. Eventually, I think we should remove this. We can edit meta using this method.
        val meta = itemStack.itemMeta
        updateItemData(meta)
        itemStack.setItemMeta(meta)

        // Set name of item
        itemStack.setData(DataComponentTypes.ITEM_NAME, getNameComponent(itemStack))

        // Finally, after applying all updates re-render the lore of the item.
        itemStack.lore(itemService.renderItemStackLore(itemStack))
    }

    val isVanilla: Boolean
        /**
         * Check if this blueprint is a vanilla wrapper. This is the same as checking if this is an instance of
         * [VanillaItemBlueprint].
         * @return true if this is a vanilla item wrapper.
         */
        get() = !this.isCustom
}
