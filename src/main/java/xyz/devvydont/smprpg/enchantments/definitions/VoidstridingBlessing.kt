package xyz.devvydont.smprpg.enchantments.definitions

import io.papermc.paper.event.entity.EntityMoveEvent
import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import io.papermc.paper.registry.set.RegistryKeySet
import io.papermc.paper.registry.set.RegistrySet
import io.papermc.paper.registry.tag.TagKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerLoadEvent
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.enchantments.CustomEnchantment
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity
import xyz.devvydont.smprpg.enchantments.ScrollColor
import xyz.devvydont.smprpg.services.EnchantmentService
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.services.ItemService.Companion.blueprint
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.time.TickTime

class VoidstridingBlessing(id: String) : CustomEnchantment(id), Listener {
    override val displayName: Component get() = ComponentUtils.create("Blessing of Voidstriding", NamedTextColor.YELLOW)
    override val description: Component get() = ComponentUtils.merge(ComponentUtils.create("Instead of falling through the void, you will glide."))
    override val enchantColor: TextColor get()   = NamedTextColor.YELLOW
    override val scrollColor: Color get()        = ScrollColor.BLESSING.color
    override val scrollBindingColor: Color get() = Color.fromRGB(161, 255, 106)

    override val maxLevel: Int get() = 1
    override val weight: Int get() = EnchantmentRarity.BLESSING.weight
    override val isBlessing: Boolean get() = true
    override val itemTypeTag: TagKey<ItemType> get() = ItemTypeTagKeys.ENCHANTABLE_FOOT_ARMOR
    override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.FEET
    override val skillRequirement: Int get() = 50

    override val conflictingEnchantments: RegistryKeySet<Enchantment>
        get() = RegistrySet.keySet(
            RegistryKey.ENCHANTMENT,
            EnchantmentService.KEEPING_BLESSING.typedKey,
            EnchantmentService.MERCY_BLESSING.typedKey,
            EnchantmentService.TELEKINESIS_BLESSING.typedKey,
            EnchantmentService.REPLENISHING.typedKey,
            EnchantmentService.IGNORANCE_BLESSING.typedKey
        )

    private fun damageBoots(player: Player) {
        val boots = player.equipment.boots
        if (boots != null) {
            if (playerHasAttributeActive(player)) {
                boots.damage(1, player)
                blueprint(boots).updateItemData(boots)
            }
        }
    }

    private fun playerHasAttributeActive(player: Player): Boolean {
        val attributeInstance = player.getAttribute(Attribute.GRAVITY)
        if (attributeInstance == null) return false

        return attributeInstance.getModifier(Companion.key) != null
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onTouchVoid(event: EntityMoveEvent) {
        if (!event.hasChangedBlock()) return

        // Check that the item is enchanted.
        val entity = event.entity
        val equipment = entity.equipment
        if (equipment == null) return

        if (entity is Player) {
            val leveledPlayer = SMPRPG.getService(EntityService::class.java).getPlayerInstance(entity)
            if (!isEnchantmentActive(equipment.boots, leveledPlayer)) return
        }

        val boots = equipment.boots
        val attribute = event.getEntity().getAttribute(Attribute.GRAVITY)
        if (boots == null) {
            // Player has no boots, can't have enchanted boots.
            attribute?.removeModifier(Companion.key)
            return
        }

        val enchLevel = boots.getEnchantmentLevel(enchantment)
        if (enchLevel == 0) {
            attribute?.removeModifier(Companion.key)
            return
        }

        val destLoc = event.to
        val minHeight = event.entity.world.minHeight

        if ((destLoc.y <= minHeight) && (event.entity.velocity.y < 0)) {
            event.entity.velocity = event.entity.velocity.setY(0)
            if (attribute != null) {
                attribute.removeModifier(Companion.key)
                attribute.addTransientModifier(
                    AttributeModifier(
                        Companion.key,
                        -1.0,
                        AttributeModifier.Operation.MULTIPLY_SCALAR_1
                    )
                )
            }
        } else if (destLoc.y > minHeight) {
            attribute?.removeModifier(Companion.key)
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onServerLaunch(event: ServerLoadEvent?) {
        Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            for (player in Bukkit.getOnlinePlayers()) {
                damageBoots(player)
            }
        }, TickTime.INSTANTANEOUSLY, TickTime.seconds(1))
    }

    companion object {
        private val key = NamespacedKey("smprpg", "voidstriding_mult")
    }
}
