package xyz.devvydont.smprpg.enchantments.definitions

import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import io.papermc.paper.registry.set.RegistryKeySet
import io.papermc.paper.registry.set.RegistrySet
import io.papermc.paper.registry.tag.TagKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Color
import org.bukkit.NamespacedKey
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.enchantments.CustomEnchantment
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity
import xyz.devvydont.smprpg.enchantments.ScrollColor
import xyz.devvydont.smprpg.services.AttributeService.Companion.instance
import xyz.devvydont.smprpg.services.EnchantmentService
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class MinersFervorArtificeEnchantment(id: String) : CustomEnchantment(id), Listener {
    override val displayName: Component get() = ComponentUtils.create("Miner's Fervor")
    override val description: Component get() = ComponentUtils.create("Harvest speed scales with your position relative to sea level.")
    override val longDescription: MutableCollection<Component?> get() = mutableListOf(
        ComponentUtils.merge(
            ComponentUtils.create("Increases harvest speed by "),
            ComponentUtils.create(
                "+" + getPercentageIncrease(level) + "%",
                NamedTextColor.GREEN
            ),
            ComponentUtils.create(" per block "),
            ComponentUtils.create("below", NamedTextColor.DARK_GRAY),
            ComponentUtils.create(" sea level", NamedTextColor.AQUA),
            ComponentUtils.create(",")
        ),
        ComponentUtils.merge(
            ComponentUtils.create("but "),
            ComponentUtils.create("decreases", NamedTextColor.RED),
            ComponentUtils.create(" harvest speed by "),
            ComponentUtils.create(
                "-" + (getPercentageIncrease(level) * 2).toInt() + "%",
                NamedTextColor.RED
            ),
            ComponentUtils.create(" per block "),
            ComponentUtils.create("above", NamedTextColor.YELLOW),
            ComponentUtils.create(" sea level", NamedTextColor.AQUA)
        )
    )
    override val enchantColor: TextColor get()   = ARTIFICE_COLOR
    override val scrollColor: Color get()        = ScrollColor.ARTIFICE.color
    override val scrollBindingColor: Color get() = Color.fromRGB(255, 149, 0)

    override val itemTypeTag: TagKey<ItemType> get()           = ItemTypeTagKeys.ENCHANTABLE_MINING
    override val maxLevel: Int get()                           = 3
    override val weight: Int get()                             = EnchantmentRarity.ARTIFICE.weight
    override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.MAINHAND
    override val skillRequirement: Int get()                   = 30

    override val conflictingEnchantments: RegistryKeySet<Enchantment>
        get() = RegistrySet.keySet(
            RegistryKey.ENCHANTMENT,
            EnchantmentService.VIGOROUS.typedKey
        )

    override val magicExperience: Int get() = level * 200 * (1 + (level * 3 / maxLevel))

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerElevationChange(event: PlayerMoveEvent) {
        val fromPos = event.from
        val toPos = event.to

        // Optimize this at least a little bit by ignoring any non-integer changes in elevation.
        if (fromPos.blockY == toPos.blockY) return

        val player = event.player
        val mainhandItem = player.inventory.itemInMainHand

        val leveledPlayer = SMPRPG.getService(EntityService::class.java).getPlayerInstance(player)
        if (!isEnchantmentActive(mainhandItem, leveledPlayer)) return

        if (!mainhandItem.containsEnchantment(this.enchantment)) return

        val seaLevel = player.world.seaLevel
        val playerY = toPos.blockY
        val blocksFromSeaLevel = seaLevel - playerY
        var speedMod: Double = blocksFromSeaLevel * (getPercentageIncrease(mainhandItem.getEnchantmentLevel(this.enchantment)) / 100.0)
        if (playerY > seaLevel) {
            speedMod *= 2.0
        }

        val miningSpeedAttr = instance.getOrCreateAttribute(player, AttributeWrapper.MINING_SPEED)

        miningSpeedAttr.addModifier(
            AttributeModifier(
                MODIFIER_KEY,
                speedMod,
                AttributeModifier.Operation.MULTIPLY_SCALAR_1
            )
        )
        miningSpeedAttr.save(player, AttributeWrapper.MINING_SPEED)
    }

    companion object {
        @JvmField
        val MODIFIER_KEY: NamespacedKey = NamespacedKey("smprpg", "fervor_boost")
        fun getPercentageIncrease(level: Int): Double { return level * 0.5 }
    }
}
