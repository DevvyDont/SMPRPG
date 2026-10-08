package xyz.devvydont.smprpg.items.blueprints.consumables

import io.papermc.paper.datacomponent.item.Consumable
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint
import xyz.devvydont.smprpg.items.interfaces.IHeaderDescribable
import xyz.devvydont.smprpg.items.interfaces.ITrackedConsumable
import xyz.devvydont.smprpg.services.AttributeService.Companion.instance
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import kotlin.math.min

/**
 * A consumable that permanently increases the player's maximum health, inspired by Terraria's
 * heart crystals. A single blueprint backs every [HeartTier]; the tier (and therefore the health
 * granted) is resolved from the item type this instance is registered to.
 *
 * Maximum health is a vanilla attribute, and Minecraft does not reliably persist runtime attribute
 * modifiers across relogs. To stay correct, the granted health is not trusted to persist on its
 * own: the number of times a player has consumed this tier is stored on the player and the health
 * modifier is recomputed and re-applied whenever the player joins. This mirrors how
 * [xyz.devvydont.smprpg.services.SkillService] re-applies its skill attribute rewards on join.
 *
 * Because each registered item type produces its own blueprint instance (each a [Listener]), a
 * player joining re-applies every tier independently, and all tiers stack additively.
 */
class HeartConsumableBlueprint(itemService: ItemService, type: CustomItemType) :
    CustomItemBlueprint(itemService, type), ITrackedConsumable, IHeaderDescribable, Listener {

    private val tier: HeartTier = HeartTier.fromItemType(type)

    override val itemClassification: ItemClassification get() = ItemClassification.CONSUMABLE

    override fun getMaxUses(): Int = MAX_CONSUMPTIONS

    override fun getConumableTrackerKey(): NamespacedKey =
        NamespacedKey(plugin, TRACKER_KEY_PREFIX + customItemType.key)

    /**
     * The key of the attribute modifier this tier applies to the player's maximum health.
     * Each tier uses a distinct key so tiers stack additively rather than overwriting each other.
     */
    private val modifierKey: NamespacedKey
        get() = NamespacedKey(plugin, MODIFIER_KEY_PREFIX + customItemType.key)

    override fun getConsumableComponent(item: ItemStack?): Consumable {
        return Consumable.consumable()
            .consumeSeconds(CONSUME_SECONDS)
            .build()
    }

    override fun getHeader(meta: ItemStack?): MutableList<Component?> {
        return mutableListOf(
            ComponentUtils.create("Consume to permanently gain"),
            ComponentUtils.merge(
                ComponentUtils.create("+${tier.healthPerConsumption} ", NamedTextColor.RED),
                ComponentUtils.create("❤ Maximum Health", NamedTextColor.RED, TextDecoration.BOLD)
            ),
            ComponentUtils.merge(
                ComponentUtils.create("up to "),
                ComponentUtils.create("$MAX_CONSUMPTIONS", NamedTextColor.RED),
                ComponentUtils.create(" times.")
            ),
            ComponentUtils.EMPTY,
            ComponentUtils.merge(
                ComponentUtils.create("Pulled from the heart of the "),
                ComponentUtils.create(tier.origin, tier.originColor),
                ComponentUtils.create(".")
            )
        )
    }

    private fun timesConsumed(player: Player): Int =
        player.persistentDataContainer.getOrDefault<Int, Int>(conumableTrackerKey, PersistentDataType.INTEGER!!, 0)

    /**
     * Recomputes this tier's maximum-health modifier from the player's stored consumption count and
     * applies it. Idempotent: any existing modifier under this tier's key is removed first, so
     * repeated calls (such as on every join) never stack the bonus more than once.
     */
    private fun reapplyHealthBonus(player: Player) {
        val consumed = min(timesConsumed(player), maxUses)
        val healthInstance = instance.getOrCreateAttribute(player, AttributeWrapper.HEALTH)
        healthInstance.removeModifier(modifierKey)
        if (consumed > 0) {
            healthInstance.addModifier(
                AttributeModifier(
                    modifierKey,
                    tier.healthPerConsumption.toDouble() * consumed,
                    AttributeModifier.Operation.ADD_NUMBER,
                    EquipmentSlotGroup.ANY
                )
            )
        }
        healthInstance.save(player, AttributeWrapper.HEALTH)
    }

    @EventHandler
    @Suppress("unused")
    fun onConsume(event: PlayerItemConsumeEvent) {
        if (!isItemOfType(event.item)) return

        val player = event.player
        val consumed = timesConsumed(player)

        if (consumed >= maxUses) {
            event.isCancelled = true
            player.sendMessage(ComponentUtils.error("This heart's power has already taken hold; your body cannot accept another."))
            player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1f, 1f)
            return
        }

        val newCount = consumed + 1
        player.persistentDataContainer.set(conumableTrackerKey, PersistentDataType.INTEGER, newCount)
        reapplyHealthBonus(player)

        player.sendMessage(
            ComponentUtils.merge(
                ComponentUtils.create("You feel your vitality surge! ", NamedTextColor.RED),
                ComponentUtils.create("+${tier.healthPerConsumption} ❤ Maximum Health ", NamedTextColor.GREEN),
                ComponentUtils.create("($newCount/$MAX_CONSUMPTIONS)", NamedTextColor.DARK_GRAY)
            )
        )
        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.5f)
        player.playSound(player, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1f, 1f)
    }

    @EventHandler(priority = EventPriority.HIGH)
    @Suppress("unused")
    fun onJoin(event: PlayerJoinEvent) {
        reapplyHealthBonus(event.player)
    }

    companion object {
        const val MAX_CONSUMPTIONS: Int = 7
        const val CONSUME_SECONDS: Float = 2.0f
        const val TRACKER_KEY_PREFIX: String = "heart_consumed_"
        const val MODIFIER_KEY_PREFIX: String = "heart_bonus_"
    }
}
