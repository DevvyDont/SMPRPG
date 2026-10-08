package xyz.devvydont.smprpg.enchantments.definitions

import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import io.papermc.paper.registry.set.RegistryKeySet
import io.papermc.paper.registry.set.RegistrySet
import io.papermc.paper.registry.tag.TagKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.block.data.Ageable
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.enchantments.CustomEnchantment
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity
import xyz.devvydont.smprpg.enchantments.ScrollColor
import xyz.devvydont.smprpg.services.EnchantmentService
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.skills.listeners.FarmingExperienceListener
import xyz.devvydont.smprpg.util.craftengine.CraftEngineHelpers
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.time.TickTime
import java.util.*
import kotlin.math.min

class ReplenishingBlessing(id: String) : CustomEnchantment(id), Listener {
    private val random = Random()

    override val displayName: Component get()    = ComponentUtils.create("Blessing of Replenishing", NamedTextColor.YELLOW)
    override val description: Component get()    = ComponentUtils.merge(ComponentUtils.create("Crops are automatically replanted on harvest"))
    override val enchantColor: TextColor get()   = NamedTextColor.YELLOW
    override val scrollColor: Color get()        = ScrollColor.BLESSING.color
    override val scrollBindingColor: Color get() = Color.fromRGB(161, 255, 106)

    override val itemTypeTag: TagKey<ItemType> get()           = ItemTypeTagKeys.HOES
    override val maxLevel: Int get()                           = 1
    override val weight: Int get()                             = EnchantmentRarity.BLESSING.weight
    override val isBlessing: Boolean get()                     = true
    override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.MAINHAND
    override val skillRequirement: Int get()                   = 30

    override val conflictingEnchantments: RegistryKeySet<Enchantment>
        get() = RegistrySet.keySet(
            RegistryKey.ENCHANTMENT,
            EnchantmentService.KEEPING_BLESSING.typedKey,
            EnchantmentService.MERCY_BLESSING.typedKey,
            EnchantmentService.TELEKINESIS_BLESSING.typedKey,
            EnchantmentService.VOIDSTRIDING_BLESSING.typedKey
        )

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onBreakBlock(event: BlockBreakEvent) {
        // Calculate our odds of replanting the crop
        // If we are successful, replace the block at the location
        // with the stage 0 crop.

        // Check that the item in hand is enchanted.

        val enchLevel = event.player.inventory.itemInMainHand.getEnchantmentLevel(enchantment)
        if (enchLevel <= 0) return

        val leveledPlayer = SMPRPG.getService(EntityService::class.java).getPlayerInstance(event.player)
        if (!isEnchantmentActive(event.player.equipment.itemInMainHand, leveledPlayer)) return

        val passThreshold = getReplantChance(enchLevel)
        val block = event.block
        val data = block.blockData
        val blockMat = data.placementMaterial

        if (data is Ageable) {
            val ageable = data
            if (ageable.age != ageable.maximumAge) {
                when (blockMat) {
                    Material.BAMBOO, Material.SUGAR_CANE -> {}
                    else -> event.isCancelled = true
                }
                return
            }

            if (random.nextInt(100) <= passThreshold) {
                ageable.age = 0
                Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                    block.type = block.type
                    block.blockData = ageable
                }, TickTime.INSTANTANEOUSLY)
            }
        }

        if (CraftEngineBlocks.isCustomBlock(block)) {
            val customBlockState = CraftEngineBlocks.getCustomBlockState(block)!!
            val blockKey = CraftEngineHelpers.getBlockKey(block)!!
            val age = customBlockState.customBlockState().getProperty<Int>("age") ?: return
            val maxAge = FarmingExperienceListener.getCustomCropMaxAge(blockKey)
            if (age == maxAge) {
                val properties = customBlockState.nbtToSave.deepClone()  // Copy our NBT
                properties.putInt("age", 0)  // Then override our age to 0
                Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                    CraftEngineBlocks.place(block.location, blockKey, properties, false)
                }, TickTime.INSTANTANEOUSLY)
            }
            else event.isCancelled = true
        }
    }

    companion object {
        fun getReplantChance(level: Int): Int {
            return min(100, level * 100)
        }
    }
}
