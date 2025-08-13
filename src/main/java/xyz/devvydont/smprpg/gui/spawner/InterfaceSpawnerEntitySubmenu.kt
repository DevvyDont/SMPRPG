package xyz.devvydont.smprpg.gui.spawner

import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.meta.ItemMeta
import xyz.devvydont.smprpg.entity.CustomEntityType
import xyz.devvydont.smprpg.entity.spawning.EntitySpawner
import xyz.devvydont.smprpg.gui.InterfaceUtil.getNamedItem
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.MinecraftStringUtils
import xyz.devvydont.smprpg.util.formatting.Symbols
import java.util.function.Consumer
import kotlin.math.max
import kotlin.math.min

class InterfaceSpawnerEntitySubmenu(owner: Player, mainMenu: InterfaceSpawnerMainMenu) : MenuBase(owner, 6, mainMenu) {
    private val spawner: EntitySpawner = mainMenu.spawner

    init {
        render()
    }

    fun render() {
        this.setBorderFull()
        this.setBackButton(49)
        val limit = 44
        var position = 0
        for (type in CustomEntityType.entries) {
            if (!type.canBeSpawnerSpawned()) continue

            if (position > limit) {
                this.sendMessageToPlayer(ComponentUtils.error("Could not display all entities. Yell at dev to stop being lazy and paginate this GUI!"))
                break
            }

            val display = getNamedItem(
                type.displayMaterial,
                ComponentUtils.create("Set Weight: ", NamedTextColor.GOLD)
                    .append(ComponentUtils.create(type.name, NamedTextColor.RED))
            )
            display.editMeta(Consumer { meta: ItemMeta ->
                meta.lore(
                    listOf(
                        ComponentUtils.EMPTY,
                        ComponentUtils.create("Current Weight:").append(
                            ComponentUtils.create(
                                " " + spawner.options.getWeight(type),
                                NamedTextColor.GREEN
                            )
                        ),
                        ComponentUtils.EMPTY,
                        ComponentUtils.create("Default Statistics:", NamedTextColor.GOLD),
                        ComponentUtils.powerLevel(type.level)
                            .append(ComponentUtils.create(" " + type.name, NamedTextColor.RED)),
                        ComponentUtils.create("Base Health: ").append(
                            ComponentUtils.create(
                                MinecraftStringUtils.formatNumber(type.hp.toLong()),
                                NamedTextColor.GREEN
                            )
                        ).append(ComponentUtils.create(Symbols.HEART, NamedTextColor.RED)),
                        ComponentUtils.create("Base Damage: ").append(
                            ComponentUtils.create(
                                MinecraftStringUtils.formatNumber(type.damage.toLong()),
                                NamedTextColor.RED
                            )
                        ).append(ComponentUtils.create(Symbols.SKULL, NamedTextColor.DARK_GRAY)),
                        ComponentUtils.create("Base Entity: ")
                            .append(ComponentUtils.create(type.type.name, NamedTextColor.GOLD)),
                        ComponentUtils.EMPTY,
                        ComponentUtils.create("Left click to increase, Right click to decrease"),
                        ComponentUtils.EMPTY,
                        ComponentUtils.create("Setting a weight of 0 will remove this entity from this spawner"),
                        ComponentUtils.create("Higher weights means this mob is more likely to spawn"),
                        ComponentUtils.create("If only one entity is enabled in a spawner, weight only needs to be 1")
                    )
                )
                meta.lore(ComponentUtils.cleanItalics(meta.lore()))
                meta.setEnchantmentGlintOverride(spawner.options.getWeight(type) > 0)
            })

            setButton(position, display) { e: InventoryClickEvent ->
                val clickType = e.click
                var delta = if (clickType.isShiftClick) 5 else 1
                if (clickType.isRightClick) delta *= -1

                val level = spawner.options.getWeight(type)
                var newLevel = level + delta
                newLevel = min(max(0, newLevel), 100)
                if (newLevel == 0) spawner.options.removeEntity(type)
                else spawner.options.setWeight(type, newLevel)
                spawner.saveOptions()
                render()
                this.playSound(Sound.UI_BUTTON_CLICK)
            }

            position++
        }
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        event.isCancelled = true
        this.playInvalidAnimation()
    }

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        event.titleOverride(ComponentUtils.create("Spawner Editor - Entities", NamedTextColor.RED))
    }
}
