package xyz.devvydont.smprpg.gui.player

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.attribute.AttributeCategory
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.entity.player.LeveledPlayer
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.services.AttributeService.Companion.instance
import xyz.devvydont.smprpg.services.ChatService
import xyz.devvydont.smprpg.services.EntityDamageCalculatorService.Companion.calculateEffectiveHealth
import xyz.devvydont.smprpg.services.EntityDamageCalculatorService.Companion.calculateResistancePercentage
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.MinecraftStringUtils
import xyz.devvydont.smprpg.util.formatting.Symbols
import xyz.devvydont.smprpg.util.tasks.PlaytimeTracker
import java.lang.Double
import java.util.function.Consumer
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String

class InterfaceStats : MenuBase {
    private val target: LivingEntity

    constructor(player: Player, target: LivingEntity, parentMenu: MenuBase?) : super(player, ROWS, parentMenu) {
        this.target = target
    }

    constructor(player: Player, target: LivingEntity) : super(player, ROWS) {
        this.target = target
    }

    fun render() {
        this.clear()
        this.setBorderFull()

        // Work through each slot and render based on index.
        for (slot in 0..<this.inventorySize) {
            // Ignore the slot if there's nothing to render there.
            if (!this.shouldBePopulated(slot)) {
                continue
            }

            // Render equipment if it's present.
            val display = this.getEquipment(slot)
            if (display != null && display.type != Material.AIR) {
                this.setSlot(slot, display)
                continue
            }

            val emptyItem = createNamedItem(Material.CLAY_BALL, this.getMissingComponent(slot))
            this.setSlot(slot, emptyItem)
        }

        if (this.target is Player) {
            this.setButton(SLOT_INVENTORY, this.inventoryButton) { e: InventoryClickEvent ->
                this.playSound(Sound.BLOCK_CHEST_OPEN)
                MenuInventoryPeek(this.player, this.target, this).openMenu()
            }
        }

        this.setButton(
            SLOT_STATS,
            this.stats
        ) { e: InventoryClickEvent ->
            this.openSubMenu(
                SubmenuStatOverview(
                    this.player,
                    this.target,
                    this
                )
            )
        }

        if (this.target is Player)
            this.setSlot(SLOT_STATS - 1, this.info)

        this.setBackButton()
    }

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        // Prepare the inventory

        val name = if (this.target is Player) SMPRPG.getService(ChatService::class.java)
            .getPlayerDisplay(target) else target.name()

        event.titleOverride(
            ComponentUtils.merge(
                ComponentUtils.create("Statistics Viewer (", NamedTextColor.BLACK),
                name,
                ComponentUtils.create(")", NamedTextColor.BLACK)
            )
        )

        // Render the UI
        this.render()
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        event.isCancelled = true
        this.playInvalidAnimation()
    }

    private val head: ItemStack
        get() {
            val item: ItemStack
            if (this.target is Player) {
                item = ItemStack(Material.PLAYER_HEAD)
                val meta = item.itemMeta as SkullMeta
                meta.playerProfile = this.target.playerProfile
                item.setItemMeta(meta)
            } else {
                item = ItemStack(Material.SKELETON_SKULL)
            }
            return item
        }

    val stats: ItemStack
        get() {
            val skull = this.head
            val meta = skull.itemMeta
            val entityService: EntityService = SMPRPG.getService(EntityService::class.java)
            val entity = entityService.getEntityInstance(this.target)

            meta.displayName(
                this.target.name().color(NamedTextColor.AQUA).append(ComponentUtils.create("'s stats"))
                    .decoration(TextDecoration.ITALIC, false)
            )
            val lore: MutableList<Component?> =
                ArrayList<Component?>()
            lore.add(ComponentUtils.EMPTY)

            // Add the power rating
            lore.add(
                ComponentUtils.create("Power Rating: ", NamedTextColor.GOLD)
                    .append(ComponentUtils.create(Symbols.POWER + entity.level, NamedTextColor.YELLOW))
            )
            lore.add(ComponentUtils.EMPTY)

            val hpAttr =
                instance.getAttribute<LivingEntity>(this.target, AttributeWrapper.HEALTH)
            val hp = hpAttr?.getValue() ?: 0.0
            val def = entity.defense
            val ehp = calculateEffectiveHealth(hp, def.toDouble())

            for (wrapper in AttributeWrapper.entries) {
                // We can skip attributes we don't have

                val attribute =
                    instance.getAttribute<LivingEntity>(this.target, wrapper)
                if (attribute == null) continue

                // Skip an attribute if it isn't whitelisted to show up
                if (!ATTRIBUTES_TO_SHOW.contains(wrapper)) continue

                val attributeValue = attribute.getValue()

                var numberColor = NamedTextColor.DARK_GRAY
                val baseAttributeValue = attribute.baseValue
                if (attributeValue > baseAttributeValue) numberColor = NamedTextColor.GREEN
                if (attributeValue < baseAttributeValue) numberColor = NamedTextColor.RED

                var attributeNameColor = NamedTextColor.GOLD
                if (wrapper.Category == AttributeCategory.SPECIAL) attributeNameColor = NamedTextColor.LIGHT_PURPLE

                val deltaDiff = (baseAttributeValue - attributeValue) / baseAttributeValue * 100 * -1
                var deltaPercentComponent: Component = ComponentUtils.create(
                    String.format(
                        " (%s%.2f%%)",
                        if (deltaDiff > 0) "+" else "",
                        deltaDiff
                    ), if (deltaDiff > 0) NamedTextColor.AQUA else NamedTextColor.DARK_RED
                )
                if (deltaDiff == 0.0 || Double.isNaN(deltaDiff) || Double.isInfinite(deltaDiff)) deltaPercentComponent =
                    ComponentUtils.EMPTY

                lore.add(
                    ComponentUtils.create(wrapper.DisplayName + ": ", attributeNameColor)
                        .append(ComponentUtils.create(String.format("%.2f", attributeValue), numberColor))
                        .append(deltaPercentComponent)
                )

                // Append Defense/EHP if def stat
                if (wrapper == AttributeWrapper.DEFENSE) {
                    lore.add(
                        ComponentUtils.merge(
                            ComponentUtils.create("- Effective Health: ", NamedTextColor.YELLOW),
                            ComponentUtils.create(String.format("%d ", ehp.toInt()), NamedTextColor.GREEN),
                            ComponentUtils.create(
                                String.format(
                                    "EHP=%dHP/%.2fDEF%%",
                                    hp.toInt(),
                                    calculateResistancePercentage(def.toDouble()) * 100
                                ), NamedTextColor.DARK_GRAY
                            )
                        )
                    )
                }
            }

            lore.add(ComponentUtils.EMPTY)
            lore.add(ComponentUtils.create("Click to get a more detailed breakdown!", NamedTextColor.YELLOW))
            meta.lore(ComponentUtils.cleanItalics(lore))
            skull.setItemMeta(meta)
            return skull
        }

    private fun getSkillDisplay(player: LeveledPlayer): MutableList<Component?> {
        val display: MutableList<Component?> = ArrayList<Component?>()
        for (skill in player.skills) display.add(
            ComponentUtils.merge(
                ComponentUtils.create(skill.type.displayName + " " + skill.level, NamedTextColor.GOLD),
                ComponentUtils.create(" - "),
                ComponentUtils.create(
                    MinecraftStringUtils.formatNumber(skill.experienceProgress.toLong()),
                    NamedTextColor.GREEN
                ),
                ComponentUtils.create("/"),
                ComponentUtils.create(
                    MinecraftStringUtils.formatNumber(skill.nextExperienceThreshold.toLong()),
                    NamedTextColor.GRAY
                ),
                ComponentUtils.create(" ("),
                ComponentUtils.create(
                    MinecraftStringUtils.formatNumber(skill.experience.toLong()) + "XP",
                    NamedTextColor.DARK_GRAY
                ),
                ComponentUtils.create(")")
            )
        )
        display.add(ComponentUtils.EMPTY)
        display.add(
            ComponentUtils.create("Skill Average: ", NamedTextColor.GOLD).append(
                ComponentUtils.create(
                    String.format("%.2f", player.getAverageSkillLevel()),
                    NamedTextColor.GREEN
                )
            )
        )
        return display
    }

    private fun formatTime(minutes: Long, includeDays: Boolean): String {
        val hours = minutes / 60
        val onlyMinutes = minutes % 60
        val days = hours / 24

        // If multiple days are present, don't include minutes.
        if (includeDays && days > 0) return String.format("%dd%dh", days, hours % 24)

        // If we only care about hours, return 12h34m.
        if (hours > 0) return String.format("%dh%dm", hours, onlyMinutes)

        // Otherwise, just minutes.
        return String.format("%d minutes", onlyMinutes)
    }

    private val info: ItemStack
        get() {

            if (this.target !is Player) {
                throw IllegalStateException("Target must be a Player. You cannot call this method on non-players.")
            }

            val paper = ItemStack(Material.PAPER)
            val player = SMPRPG.getService(EntityService::class.java).getPlayerInstance(this.target)
            val timePlayed = formatTime(PlaytimeTracker.getPlaytime(player.player).toLong(), false)
            // Calculate how old this player is. Take the ms difference and convert it to minutes.
            val ageMs = System.currentTimeMillis() - PlaytimeTracker.getFirstSeen(player.player)
            val age = formatTime(ageMs / 1000 / 60, true)

            paper.editMeta(Consumer { meta: ItemMeta? ->
                meta!!.displayName(
                    this.target.name().color(NamedTextColor.AQUA).append(ComponentUtils.create("'s Information"))
                        .decoration(TextDecoration.ITALIC, false)
                )
                meta.lore(
                    ComponentUtils.cleanItalics(
                        listOf(
                            ComponentUtils.EMPTY,
                            ComponentUtils.merge(
                                ComponentUtils.create("Profile type: "),
                                ComponentUtils.create(player.difficulty.Display, player.difficulty.Color)
                            ),
                            ComponentUtils.EMPTY,
                            ComponentUtils.merge(
                                ComponentUtils.create("Playtime: "),
                                ComponentUtils.create(timePlayed, NamedTextColor.GREEN)
                            ),
                            ComponentUtils.merge(
                                ComponentUtils.create("First Seen: "),
                                ComponentUtils.create("$age ago", NamedTextColor.GREEN)
                            )
                        )
                    )
                )
            })

            return paper
        }

    private val skills: ItemStack
        get() {
            val paper = ItemStack(Material.IRON_PICKAXE)
            val meta = paper.itemMeta

            meta.displayName(
                this.target.name().color(NamedTextColor.AQUA).append(ComponentUtils.create("'s Skills"))
                    .decoration(TextDecoration.ITALIC, false)
            )
            val lore: MutableList<Component?> =
                ArrayList<Component?>()
            lore.add(ComponentUtils.EMPTY)

            val entityService: EntityService = SMPRPG.getService(EntityService::class.java)

            if (this.target is Player)
                lore.addAll(getSkillDisplay(entityService.getPlayerInstance(this.target)))

            else lore.add(ComponentUtils.create("Only players have skills!", NamedTextColor.RED))
            meta.lore(ComponentUtils.cleanItalics(lore))
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
            paper.setItemMeta(meta)
            return paper
        }

    private val inventoryButton: ItemStack
        get() {
            val chest = ItemStack(Material.CHEST)
            val meta = chest.itemMeta

            meta.displayName(
                this.target.name().color(NamedTextColor.AQUA).append(ComponentUtils.create("'s Full Inventory"))
                    .decoration(TextDecoration.ITALIC, false)
            )
            val lore =
                listOf(
                    ComponentUtils.EMPTY,
                    ComponentUtils.merge(
                        ComponentUtils.create("Click to view their "),
                        ComponentUtils.create("inventory", NamedTextColor.GOLD),
                        ComponentUtils.create(" and "),
                        ComponentUtils.create("ender chest", NamedTextColor.LIGHT_PURPLE),
                        ComponentUtils.create("!")
                    )
                )
            meta.lore(ComponentUtils.cleanItalics(lore))
            chest.setItemMeta(meta)
            return chest
        }

    private fun getEquipment(slot: Int): ItemStack? {

        val equipment = this.target.equipment
        if (equipment == null)
            return null

        return when (slot) {
            SLOT_HELMET -> equipment.helmet
            SLOT_CHESTPLATE -> equipment.chestplate
            SLOT_LEGGINGS -> equipment.leggings
            SLOT_BOOTS -> equipment.boots
            SLOT_MAIN_HAND -> equipment.itemInMainHand
            SLOT_OFF_HAND -> equipment.itemInOffHand
            SLOT_STATS -> this.stats
            SLOT_MISC_INFO -> this.skills
            else -> null
        }
    }

    private fun getMissingComponent(slot: Int): Component {
        val name = this.target.name()
        return when (slot) {
            SLOT_HELMET -> name.append(ComponentUtils.create(" is not wearing a helmet", NamedTextColor.RED))
            SLOT_CHESTPLATE -> name.append(ComponentUtils.create(" is not wearing a chestplate", NamedTextColor.RED))
            SLOT_LEGGINGS -> name.append(ComponentUtils.create(" is not wearing leggings", NamedTextColor.RED))
            SLOT_BOOTS -> name.append(ComponentUtils.create(" is not wearing boots", NamedTextColor.RED))
            SLOT_MAIN_HAND -> name.append(ComponentUtils.create(" is not holding anything", NamedTextColor.RED))
            SLOT_OFF_HAND -> name.append(
                ComponentUtils.create(
                    " is not holding anything in their off hand",
                    NamedTextColor.RED
                )
            )

            SLOT_STATS -> name.append(ComponentUtils.create(" does not have stats", NamedTextColor.RED))
            SLOT_MISC_INFO -> name.append(ComponentUtils.create(" does not have information", NamedTextColor.RED))
            else -> ComponentUtils.EMPTY
        }
    }

    private fun shouldBePopulated(slot: Int): Boolean {
        return this.getMissingComponent(slot) != ComponentUtils.EMPTY
    }

    companion object {
        const val ROWS: Int = 6

        private const val SLOT_HELMET = 15
        private const val SLOT_CHESTPLATE = 24
        private const val SLOT_LEGGINGS = 33
        private const val SLOT_BOOTS = 42
        private const val SLOT_MAIN_HAND = 25
        private const val SLOT_OFF_HAND = 23
        private const val SLOT_STATS = 20
        private const val SLOT_INVENTORY = 11
        private const val SLOT_MISC_INFO = 29

        private val ATTRIBUTES_TO_SHOW: Set<AttributeWrapper> = setOf(
            AttributeWrapper.HEALTH,
            AttributeWrapper.DEFENSE,
            AttributeWrapper.STRENGTH,
            AttributeWrapper.ATTACK_SPEED,
            AttributeWrapper.INTELLIGENCE,
            AttributeWrapper.MOVEMENT_SPEED,
            AttributeWrapper.LUCK,
            AttributeWrapper.PROFICIENCY
        )
    }
}
