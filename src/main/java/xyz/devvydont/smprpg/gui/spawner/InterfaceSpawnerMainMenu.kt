package xyz.devvydont.smprpg.gui.spawner

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import xyz.devvydont.smprpg.entity.spawning.EntitySpawner
import xyz.devvydont.smprpg.gui.InterfaceUtil.getNamedItem
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.gui.base.MenuButtonClickHandler
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import java.util.function.Consumer
import kotlin.math.max
import kotlin.math.min

class InterfaceSpawnerMainMenu(owner: Player, val spawner: EntitySpawner) : MenuBase(owner, 6) {
    init {
        render()
    }

    private fun canUse(): Boolean {
        return this.player.isOp || this.player.permissionValue("smprpg.items.spawneditor.modify")
            .toBooleanOrElse(false)
    }

    fun render() {
        this.setBorderFull()
        this.setBackButton(49)

        // Create the button to delete this spawner.
        this.setButton(43, createDeleteButtonItem(), (MenuButtonClickHandler { event: InventoryClickEvent ->
            if (!canUse()) return@MenuButtonClickHandler
            this.spawner.entity.remove()
            this.playSound(Sound.BLOCK_ANVIL_BREAK, 1f, 1.5f)
            player.closeInventory()
            player.sendMessage(ComponentUtils.success("Successfully deleted spawner!"))
        }))

        // Create the button to open the submenu to add/remove entities to this spawner.
        this.setButton(31, createEntriesButtonItem(), (MenuButtonClickHandler { event: InventoryClickEvent ->
            if (!canUse()) return@MenuButtonClickHandler
            this.playSound(Sound.BLOCK_ENCHANTMENT_TABLE_USE)
            this.openSubMenu(InterfaceSpawnerEntitySubmenu(this.player, this))
        }))

        // Create the button to tweak the spawn limits of this spawner.
        this.setButton(15, createLimitButtonItem(), (MenuButtonClickHandler { event: InventoryClickEvent ->
            if (!canUse()) return@MenuButtonClickHandler
            var delta = if (event.click.isShiftClick) 5 else 1
            if (event.click.isRightClick) delta *= -1

            val level = this.spawner.options.getLimit()
            var newLevel = level + delta
            newLevel = min(max(1, newLevel), 75)
            this.spawner.options.setLimit(newLevel)
            this.spawner.saveOptions()
            this.playSound(Sound.UI_BUTTON_CLICK)
            this.render()
        }))

        this.setButton(11, createLevelButtonItem(), (MenuButtonClickHandler { event: InventoryClickEvent ->
            if (!canUse()) return@MenuButtonClickHandler
            var delta = if (event.click.isShiftClick) 5 else 1
            if (event.click.isRightClick) delta *= -1

            val level = this.spawner.options.getLevel()
            var newLevel = level + delta
            newLevel = min(max(1, newLevel), 99)
            this.spawner.options.setLevel(newLevel)
            this.spawner.saveOptions()
            this.playSound(Sound.UI_BUTTON_CLICK)
            this.render()
        }))

        this.setButton(13, createRangeButtonItem(), (MenuButtonClickHandler { event: InventoryClickEvent ->
            if (!canUse()) return@MenuButtonClickHandler
            var delta = if (event.click.isShiftClick) 5 else 1
            if (event.click.isRightClick) delta *= -1

            val level = this.spawner.options.getRadius()
            var newLevel = level + delta
            newLevel = min(max(1, newLevel), 30)
            this.spawner.options.setRadius(newLevel)
            this.spawner.saveOptions()
            this.playSound(Sound.UI_BUTTON_CLICK)
            this.render()
        }))
    }

    private fun createDeleteButtonItem(): ItemStack {
        val delete = getNamedItem(Material.BEDROCK, ComponentUtils.create("Delete Spawner", NamedTextColor.RED))
        val lore: MutableList<Component?> = ArrayList()
        lore.add(ComponentUtils.EMPTY)
        lore.add(ComponentUtils.create("Click to remove this spawner from the world!", NamedTextColor.RED))
        delete.editMeta(Consumer { meta: ItemMeta? ->
            meta!!.lore(lore)
            meta.lore(ComponentUtils.cleanItalics(meta.lore()))
        })

        return delete
    }

    private fun createRangeButtonItem(): ItemStack {
        val display = getNamedItem(Material.SPYGLASS, ComponentUtils.create("Set Spawn Radius", NamedTextColor.GOLD))
        display.editMeta(Consumer { meta: ItemMeta? ->
            meta!!.lore(
                listOf(
                    ComponentUtils.EMPTY,
                    ComponentUtils.create("Current Radius:").append(
                        ComponentUtils.create(
                            " " + this.spawner.options.getRadius() + " blocks",
                            NamedTextColor.GREEN
                        )
                    ),
                    ComponentUtils.EMPTY,
                    ComponentUtils.create("Left click to increase, Right click to decrease"),
                    ComponentUtils.EMPTY,
                    ComponentUtils.create("Range is how far away (in blocks) a mob can spawn from the location"),
                    ComponentUtils.create("of this spawner. Y position is ignored as a Y value is calculated dynamically")
                )
            )
            meta.lore(ComponentUtils.cleanItalics(meta.lore()))
        })
        return display
    }

    private fun createLevelButtonItem(): ItemStack {
        val display = getNamedItem(Material.EXPERIENCE_BOTTLE, ComponentUtils.create("Set Level", NamedTextColor.GOLD))
        display.editMeta(Consumer { meta: ItemMeta? ->
            meta!!.lore(
                listOf(
                    ComponentUtils.EMPTY,
                    ComponentUtils.create("Current Level:").append(
                        ComponentUtils.create(
                            " " + this.spawner.options.getLevel(),
                            NamedTextColor.GREEN
                        )
                    ),
                    ComponentUtils.EMPTY,
                    ComponentUtils.create("Left click to increase, Right click to decrease"),
                    ComponentUtils.EMPTY,
                    ComponentUtils.create("The level to attempt to spawn mobs at. Mobs spawned at a different"),
                    ComponentUtils.create("level than their base level may have unexpected statistics however")
                )
            )
            meta.lore(ComponentUtils.cleanItalics(meta.lore()))
        })
        return display
    }

    private fun createEntriesButtonItem(): ItemStack {
        val display =
            getNamedItem(Material.SKELETON_SKULL, ComponentUtils.create("Edit Entity Choices", NamedTextColor.GOLD))
        val lore: MutableList<Component?> = ArrayList()
        lore.add(ComponentUtils.EMPTY)
        lore.add(
            ComponentUtils.create(
                "Currently spawning " + this.spawner.options.getEntries().size + " entities"
            )
        )
        for (entry in this.spawner.options.getEntries()) lore.add(
            ComponentUtils.create("- Entity: ").append(ComponentUtils.create(entry.type.Name, NamedTextColor.RED))
                .append(
                    ComponentUtils.create(" Weight: ")
                        .append(ComponentUtils.create("" + entry.weight, NamedTextColor.GREEN))
                )
        )
        lore.add(ComponentUtils.EMPTY)
        lore.add(ComponentUtils.create("Click to edit!", NamedTextColor.YELLOW))
        display.editMeta(Consumer { meta: ItemMeta? ->
            meta!!.lore(lore)
            meta.lore(ComponentUtils.cleanItalics(meta.lore()))
        })
        return display
    }

    private fun createLimitButtonItem(): ItemStack {
        val display = getNamedItem(Material.LEVER, ComponentUtils.create("Set Spawn Limit", NamedTextColor.GOLD))
        display.editMeta(Consumer { meta: ItemMeta? ->
            meta!!.lore(
                listOf(
                    ComponentUtils.EMPTY,
                    ComponentUtils.create("Current Limit:").append(
                        ComponentUtils.create(
                            " " + this.spawner.options.getLimit() + " entities",
                            NamedTextColor.GREEN
                        )
                    ),
                    ComponentUtils.EMPTY,
                    ComponentUtils.create("Left click to increase, Right click to decrease"),
                    ComponentUtils.EMPTY,
                    ComponentUtils.create("Spawner limit is how many entities at a time this specific"),
                    ComponentUtils.create("spawner can add entities to the world. Spawning cycles are"),
                    ComponentUtils.create(
                        "skipped when " + this.spawner.options
                            .getLimit() + " entities spawned from this spawner are alive"
                    )
                )
            )
            meta.lore(ComponentUtils.cleanItalics(meta.lore()))
        })
        return display
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        event.isCancelled = true
        this.playInvalidAnimation()
    }

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        event.titleOverride(ComponentUtils.create("Spawner Editor", NamedTextColor.RED))
    }
}
