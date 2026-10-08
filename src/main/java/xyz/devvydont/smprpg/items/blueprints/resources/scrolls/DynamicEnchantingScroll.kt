package xyz.devvydont.smprpg.items.blueprints.resources.scrolls

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.CustomModelData
import io.papermc.paper.datacomponent.item.ItemEnchantments
import io.papermc.paper.datacomponent.item.TooltipDisplay
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.enchantments.CustomEnchantment
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity
import xyz.devvydont.smprpg.enchantments.EnchantmentTargetDisplay
import xyz.devvydont.smprpg.gui.enchantments.EnchantmentSubMenu
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.ItemRarity
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint
import xyz.devvydont.smprpg.items.interfaces.IFooterDescribable
import xyz.devvydont.smprpg.items.interfaces.IHeaderDescribable
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden
import xyz.devvydont.smprpg.items.interfaces.ISellable
import xyz.devvydont.smprpg.services.EnchantmentService
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.services.ItemService.Companion.blueprint
import xyz.devvydont.smprpg.services.ItemService.Companion.generate
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import java.util.function.Consumer

class DynamicEnchantingScroll(itemService: ItemService, type: CustomItemType) :
    CustomItemBlueprint(itemService, type), IHeaderDescribable, IFooterDescribable, ISellable, IModelOverridden, Listener {
    /**
     * Determine what type of item this is.
     */
    override val itemClassification: ItemClassification get() = ItemClassification.ITEM

    /**
     * Given this item stack, how much should it be able to sell for?
     * Keep in mind that the size of the stack needs to considered as well!
     *
     * @param item The item that can be sold.
     * @return The worth of the item.
     */
    override fun getWorth(item: ItemStack): Int {
        return 1000 * item.getAmount()
    }

    override fun updateItemData(itemStack: ItemStack) {
        // Fallback to Smite if enchantment isn't generated
        if (itemStack.getData(DataComponentTypes.STORED_ENCHANTMENTS) == null) itemStack.setData(
            DataComponentTypes.STORED_ENCHANTMENTS,
            ItemEnchantments.itemEnchantments().add(Enchantment.SMITE, 255).build()
        )

        itemStack.setData(
            DataComponentTypes.TOOLTIP_DISPLAY,
            TooltipDisplay.tooltipDisplay().addHiddenComponents(DataComponentTypes.STORED_ENCHANTMENTS).build()
        )

        val enchToUse = itemStack.getData(DataComponentTypes.STORED_ENCHANTMENTS)!!.enchantments().keys.first()
        val customEnch : CustomEnchantment = SMPRPG.getService(EnchantmentService::class.java).getEnchantment(enchToUse)!!
        super.updateItemData(itemStack)
        itemStack.setData(
            DataComponentTypes.CUSTOM_MODEL_DATA,
            CustomModelData.customModelData().addColors(listOf(customEnch.scrollColor, customEnch.scrollBindingColor)))
        itemStack.editMeta(Consumer { meta: ItemMeta? -> meta!!.setMaxStackSize(8) })
    }

    override fun getHeader(itemStack: ItemStack): MutableList<Component?> {
        val enchant = itemStack.getData(DataComponentTypes.STORED_ENCHANTMENTS)!!
            .enchantments().keys.toTypedArray()[0]
        var color = enchant.description().color()
        if (color != null) {
            if (getRarity(itemStack) == ItemRarity.ARTIFICE) {
                val textComp = SMPRPG.getService(EnchantmentService::class.java).getEnchantment(enchant)!!.displayName
                return mutableListOf(
                    ComponentUtils.gradient(
                        PlainTextComponentSerializer.plainText().serialize(textComp),
                        NamedTextColor.DARK_PURPLE,
                        TextColor.color(255, 0, 0)
                    ).decorate(TextDecoration.ITALIC)
                )
            }
            color = if (color == NamedTextColor.WHITE) NamedTextColor.GRAY else enchant.description().color()
        } else {
            color = NamedTextColor.DARK_RED
        }
        return mutableListOf(
            enchant.displayName(0).color(color)
        )
    }

    override fun getRarity(item: ItemStack): ItemRarity {
        val enchant = item.getData(DataComponentTypes.STORED_ENCHANTMENTS)!!.enchantments().keys.toTypedArray()[0] as Enchantment?
        if (enchant != null) {
            val weight = enchant.getWeight()
            if (weight == EnchantmentRarity.COMMON.getWeight()) return ItemRarity.RARE
            else if (weight == EnchantmentRarity.UNCOMMON.getWeight() || weight == EnchantmentRarity.CURSE.getWeight()) return ItemRarity.EPIC
            else if (weight == EnchantmentRarity.RARE.getWeight()) return ItemRarity.LEGENDARY
            else if (weight == EnchantmentRarity.ARTIFICE.getWeight()) return ItemRarity.ARTIFICE
            else if (weight == EnchantmentRarity.BLESSING.getWeight()) return ItemRarity.MYTHIC
            else if (weight == EnchantmentRarity.ARTIFACT.getWeight()) return ItemRarity.SPECIAL
        }
        return defaultRarity
    }

    override fun getDisplayKey(): Key? {
        return IModelOverridden.ofItemType(customItemType)
    }

    override fun getFooter(itemStack: ItemStack): List<Component> {
        val retComps = mutableListOf<Component>()
        val enchant = itemStack.getData(DataComponentTypes.STORED_ENCHANTMENTS)!!.enchantments().keys.toTypedArray()[0]
        val customEnch = SMPRPG.getService(EnchantmentService::class.java).getEnchantment(enchant)
        customEnch!!.level = 1
        val comp = EnchantmentTargetDisplay.getApplicableItemsComponent(customEnch.itemTypeTag)
        val applicationComp = ComponentUtils.merge(
            ComponentUtils.create("Applies to: ", NamedTextColor.GOLD),
            comp
        )
        val footer = mutableListOf(
            customEnch.description,
            ComponentUtils.create("(Values for level 1 enchantment shown.)", NamedTextColor.DARK_GRAY),
            ComponentUtils.EMPTY,
            applicationComp
        )

        // If this enchantment has application recipes defined, hint that the reagents can be viewed.
        if (hasApplicationRecipes(customEnch)) {
            footer.add(ComponentUtils.EMPTY)
            footer.add(ComponentUtils.create("Right click while holding to view", NamedTextColor.YELLOW))
            footer.add(ComponentUtils.create("required reagents to apply!", NamedTextColor.YELLOW))
        }

        return footer
    }

    /**
     * Determines whether the enchantment this scroll holds has any application recipe defined across its levels.
     * Some enchantments have no recipes, in which case there are no reagents to display.
     *
     * @param enchant The enchantment to check.
     * @return True if at least one level has a recipe defined, otherwise false.
     */
    private fun hasApplicationRecipes(enchant: CustomEnchantment): Boolean {
        for (level in 1..enchant.maxLevel)
            if (enchant.getRecipe(level) != null) return true
        return false
    }

    /**
     * Opens the recipe viewer for the scroll's enchantment when it is right clicked while held, provided that the
     * enchantment actually has application recipes defined.
     */
    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        if (!event.action.isRightClick) return

        val item = event.item ?: return
        if (!isItemOfType(item)) return

        val stored = item.getData(DataComponentTypes.STORED_ENCHANTMENTS) ?: return
        val enchant = stored.enchantments().keys.firstOrNull() ?: return
        val customEnch = SMPRPG.getService(EnchantmentService::class.java).getEnchantment(enchant) ?: return

        if (!hasApplicationRecipes(customEnch)) return

        event.isCancelled = true
        event.player.playSound(event.player.location, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f)
        EnchantmentSubMenu(event.player, null, customEnch).openMenu()
    }

    companion object {
        @JvmStatic
        fun getScrollWithEnchantment(enchant: CustomEnchantment): ItemStack {
            val scroll = generate(CustomItemType.ENCHANTING_SCROLL)
            scroll.setData(
                DataComponentTypes.STORED_ENCHANTMENTS,
                ItemEnchantments.itemEnchantments().add(enchant.enchantment, 255).build()
            )
            blueprint(scroll).updateItemData(scroll)
            return scroll
        }
    }
}
