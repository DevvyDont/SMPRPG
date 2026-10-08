package xyz.devvydont.smprpg.items.blueprints.resources.slayer.drops

import io.papermc.paper.datacomponent.item.Consumable
import io.papermc.paper.registry.keys.SoundEventKeys
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.attribute.AttributeModifier
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint
import xyz.devvydont.smprpg.items.interfaces.IHeaderDescribable
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden
import xyz.devvydont.smprpg.items.interfaces.ISellable
import xyz.devvydont.smprpg.items.interfaces.ITrackedConsumable
import xyz.devvydont.smprpg.services.AttributeService.Companion.instance
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import java.util.function.Consumer

class UndigestedBrains(itemService: ItemService, type: CustomItemType) : CustomItemBlueprint(itemService, type),
    IHeaderDescribable, Listener, ITrackedConsumable, ISellable, IModelOverridden {
    override fun getHeader(meta: ItemStack?): MutableList<Component?> {
        return listOf(
            ComponentUtils.merge(
                ComponentUtils.create("Consume to be "),
                ComponentUtils.create("plagued", NamedTextColor.DARK_GRAY),
                ComponentUtils.create(" by the memories of an")
            ),
            ComponentUtils.create("adventurer unfortunate enough to perish to the "),
            ComponentUtils.merge(
                ComponentUtils.create("Shambling Abomination", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD),
                ComponentUtils.create(".")
            ),
            ComponentUtils.EMPTY,
            ComponentUtils.create("(And also gain +10 permanent intelligence up to 5 times.)", NamedTextColor.AQUA)
        ).toMutableList()
    }

    override val itemClassification: ItemClassification get() = ItemClassification.CONSUMABLE

    override fun getConsumableComponent(item: ItemStack?): Consumable {
        return Consumable.consumable()
            .consumeSeconds(5f)
            .sound(SoundEventKeys.BLOCK_HONEY_BLOCK_BREAK)
            .build()
    }

    override fun updateItemData(itemStack: ItemStack) {
        super.updateItemData(itemStack)
        itemStack.editMeta(Consumer { meta: ItemMeta -> meta.setMaxStackSize(1) })
    }

    override fun getWorth(item: ItemStack): Int {
        return 100000 * item.amount
    }

    override fun getConumableTrackerKey(): NamespacedKey {
        return TRACKER_KEY
    }

    override fun getMaxUses(): Int {
        return 5
    }

    @EventHandler
    fun onConsumeBrain(event: PlayerItemConsumeEvent) {
        val consumedItem = event.item
        if (!isItemOfType(consumedItem)) return

        val player = event.getPlayer()
        val playerPdc = player.persistentDataContainer
        var numTimesEaten = playerPdc.getOrDefault<Int, Int>(conumableTrackerKey, PersistentDataType.INTEGER!!, 0)
        if (numTimesEaten < maxUses) {
            val intComp = ComponentUtils.create("You have gained +10 intelligence!", NamedTextColor.AQUA)
            player.sendMessage(intComp)

            // TODO: Make fun flavor text for these
            //switch (numTimesEaten) {
            //    case 0:
            //    {

            //    }
            //}
            player.playSound(player, Sound.ENTITY_VILLAGER_TRADE, 1f, 2f)
            player.playSound(player, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1f, 2f)
            numTimesEaten++
            playerPdc.set(conumableTrackerKey, PersistentDataType.INTEGER, numTimesEaten)
            val intInst = instance.getOrCreateAttribute(player, AttributeWrapper.INTELLIGENCE)
            intInst.addModifier(
                AttributeModifier(
                    BRAIN_ATTRIBUTE_BONUS,
                    INTELLIGENCE_BONUS * numTimesEaten,
                    AttributeModifier.Operation.ADD_NUMBER
                )
            )
            intInst.save(player, AttributeWrapper.INTELLIGENCE)
        } else {
            player.sendMessage(ComponentUtils.error("You have already eaten $maxUses lumps of undigested brains!"))
            player.playSound(player, Sound.ENTITY_ZOMBIE_AMBIENT, 1f, 0.5f)
            event.isCancelled = true
        }
    }

    override fun getDisplayKey(): Key? { return IModelOverridden.ofItemTypeInDirectory(customItemType, "consumables") }

    companion object {
        val TRACKER_KEY: NamespacedKey = NamespacedKey(plugin, "consumable_undigested_brain")
        val BRAIN_ATTRIBUTE_BONUS: NamespacedKey = NamespacedKey(plugin, "undigested_brains_bonus")
        const val INTELLIGENCE_BONUS: Double = 10.0
    }
}
