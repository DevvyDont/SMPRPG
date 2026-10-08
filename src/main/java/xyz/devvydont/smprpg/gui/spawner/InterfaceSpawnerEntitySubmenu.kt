package xyz.devvydont.smprpg.gui.spawner

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.entity.CustomEntityType
import xyz.devvydont.smprpg.entity.spawning.EntitySpawner
import xyz.devvydont.smprpg.entity.spawning.SpawnableEntity
import xyz.devvydont.smprpg.gui.InterfaceUtil.getNamedItem
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.MinecraftStringUtils
import xyz.devvydont.smprpg.util.formatting.Symbols
import kotlin.math.max
import kotlin.math.min

/**
 * Paginated editor for the pool of entities a spawner can spawn. Each entity is shown as a button whose weight is
 * adjusted by clicking. Both our custom entities and vanilla entities can be added, and the displayed list can be
 * narrowed with the source ([SourceFilter]) and "enabled only" filters at the bottom of the menu.
 */
class InterfaceSpawnerEntitySubmenu(owner: Player, mainMenu: InterfaceSpawnerMainMenu) : MenuBase(owner, ROWS, mainMenu) {
    private val spawner: EntitySpawner = mainMenu.spawner

    private var page = 0
    private var sourceFilter = SourceFilter.ALL
    private var enabledOnly = false

    init {
        render()
    }

    /**
     * The list of entities to display given the current filter state.
     */
    private fun visibleEntities(): List<SpawnableEntity> {
        val base = when (sourceFilter) {
            SourceFilter.ALL -> CUSTOM_CANDIDATES + VANILLA_CANDIDATES
            SourceFilter.CUSTOM -> CUSTOM_CANDIDATES
            SourceFilter.VANILLA -> VANILLA_CANDIDATES
        }
        if (!enabledOnly) return base
        return base.filter { spawner.options.getWeight(it) > 0 }
    }

    fun render() {
        this.clear()
        this.setBorderEdge()

        val entities = visibleEntities()
        val pageCount = max(1, (entities.size + PAGE_SIZE - 1) / PAGE_SIZE)
        page = page.coerceIn(0, pageCount - 1)

        // Place the entities for the current page into the content area.
        val offset = page * PAGE_SIZE
        for ((index, slot) in CONTENT_SLOTS.withIndex()) {
            val entityIndex = offset + index
            if (entityIndex >= entities.size) break

            val entity = entities[entityIndex]
            setButton(slot, createEntityButton(entity)) { e: InventoryClickEvent -> adjustWeight(entity, e) }
        }

        renderControls(pageCount)
    }

    /**
     * Lays out the bottom-row controls: pagination arrows, both filter toggles, and the back button.
     */
    private fun renderControls(pageCount: Int) {
        this.setBackButton(SLOT_BACK)

        val previous = BUTTON_PAGE_PREVIOUS.clone()
        previous.editMeta { it.itemName(ComponentUtils.create("Previous Page (${page + 1}/$pageCount)", NamedTextColor.GOLD)) }
        setButton(SLOT_PREVIOUS_PAGE, previous) { _: InventoryClickEvent ->
            page = (page - 1 + pageCount) % pageCount
            render()
            this.sounds.playPagePrevious()
        }

        val next = BUTTON_PAGE_NEXT.clone()
        next.editMeta { it.itemName(ComponentUtils.create("Next Page (${page + 1}/$pageCount)", NamedTextColor.GOLD)) }
        setButton(SLOT_NEXT_PAGE, next) { _: InventoryClickEvent ->
            page = (page + 1) % pageCount
            render()
            this.sounds.playPageNext()
        }

        setButton(SLOT_SOURCE_FILTER, createSourceFilterButton()) { _: InventoryClickEvent ->
            sourceFilter = sourceFilter.next()
            page = 0
            render()
            this.playSound(Sound.UI_BUTTON_CLICK)
        }

        setButton(SLOT_ENABLED_FILTER, createEnabledFilterButton()) { _: InventoryClickEvent ->
            enabledOnly = !enabledOnly
            page = 0
            render()
            this.playSound(Sound.UI_BUTTON_CLICK)
        }
    }

    /**
     * Applies a weight change to the clicked entity. Left click increases, right click decreases, shift adjusts in
     * larger steps. Dropping to a weight of 0 removes the entity from the spawner entirely.
     */
    private fun adjustWeight(entity: SpawnableEntity, event: InventoryClickEvent) {
        var delta = if (event.click.isShiftClick) WEIGHT_STEP_SHIFT else 1
        if (event.click.isRightClick) delta *= -1

        val newWeight = min(max(0, spawner.options.getWeight(entity) + delta), MAX_WEIGHT)
        if (newWeight == 0) spawner.options.removeEntity(entity)
        else spawner.options.setWeight(entity, newWeight)

        spawner.saveOptions()
        render()
        this.playSound(Sound.UI_BUTTON_CLICK)
    }

    private fun createEntityButton(entity: SpawnableEntity): ItemStack {
        val weight = spawner.options.getWeight(entity)
        val display = getNamedItem(
            entity.displayMaterial,
            ComponentUtils.create("Set Weight: ", NamedTextColor.GOLD)
                .append(ComponentUtils.create(entity.displayName(), NamedTextColor.RED))
        )

        val lore = ArrayList<Component>()
        lore.add(ComponentUtils.EMPTY)
        lore.add(
            ComponentUtils.create("Current Weight:")
                .append(ComponentUtils.create(" $weight", NamedTextColor.GREEN))
        )
        lore.add(ComponentUtils.EMPTY)
        lore.addAll(statisticsLore(entity))
        lore.add(ComponentUtils.EMPTY)
        lore.add(ComponentUtils.create("Left click to increase, Right click to decrease"))
        lore.add(ComponentUtils.create("(Shift click to adjust by $WEIGHT_STEP_SHIFT)"))
        lore.add(ComponentUtils.EMPTY)
        lore.add(ComponentUtils.create("Setting a weight of 0 will remove this entity from this spawner"))
        lore.add(ComponentUtils.create("Higher weights means this mob is more likely to spawn"))
        lore.add(ComponentUtils.create("If only one entity is enabled in a spawner, weight only needs to be 1"))

        display.editMeta { meta ->
            meta.lore(ComponentUtils.cleanItalics(lore))
            meta.setEnchantmentGlintOverride(weight > 0)
        }
        return display
    }

    /**
     * Builds the stat block shown on an entity button. Custom entities expose concrete base statistics, while
     * vanilla entities are scaled purely off of the spawner's configured level.
     */
    private fun statisticsLore(entity: SpawnableEntity): List<Component> {
        val custom = entity.custom()
        if (custom == null) {
            return listOf(
                ComponentUtils.create("Vanilla Entity", NamedTextColor.AQUA),
                ComponentUtils.create("Base Entity: ")
                    .append(ComponentUtils.create(entity.vanillaType().name, NamedTextColor.GOLD)),
                ComponentUtils.create("Statistics scale to this spawner's level", NamedTextColor.GRAY)
            )
        }

        return listOf(
            ComponentUtils.create("Default Statistics:", NamedTextColor.GOLD),
            ComponentUtils.powerLevel(custom.level)
                .append(ComponentUtils.create(" " + custom.name, NamedTextColor.RED)),
            ComponentUtils.create("Base Health: ")
                .append(ComponentUtils.create(MinecraftStringUtils.formatNumber(custom.hp.toLong()), NamedTextColor.GREEN))
                .append(ComponentUtils.create(Symbols.HEART, NamedTextColor.RED)),
            ComponentUtils.create("Base Damage: ")
                .append(ComponentUtils.create(MinecraftStringUtils.formatNumber(custom.damage.toLong()), NamedTextColor.RED))
                .append(ComponentUtils.create(Symbols.SKULL, NamedTextColor.DARK_GRAY)),
            ComponentUtils.create("Base Entity: ")
                .append(ComponentUtils.create(custom.type.name, NamedTextColor.GOLD))
        )
    }

    private fun createSourceFilterButton(): ItemStack {
        val display = getNamedItem(
            sourceFilter.icon,
            ComponentUtils.create("Filter: ", NamedTextColor.GOLD)
                .append(ComponentUtils.create(sourceFilter.displayName, NamedTextColor.YELLOW))
        )
        val lore = ArrayList<Component>()
        lore.add(ComponentUtils.EMPTY)
        for (option in SourceFilter.entries) {
            val color = if (option == sourceFilter) NamedTextColor.GREEN else NamedTextColor.DARK_GRAY
            val prefix = if (option == sourceFilter) "▶ " else "  "
            lore.add(ComponentUtils.create(prefix + option.displayName, color))
        }
        lore.add(ComponentUtils.EMPTY)
        lore.add(ComponentUtils.create("Click to cycle which entities are shown", NamedTextColor.YELLOW))
        display.editMeta { it.lore(ComponentUtils.cleanItalics(lore)) }
        return display
    }

    private fun createEnabledFilterButton(): ItemStack {
        val state = if (enabledOnly) "On" else "Off"
        val display = getNamedItem(
            Material.COMPARATOR,
            ComponentUtils.create("Show Enabled Only: ", NamedTextColor.GOLD)
                .append(ComponentUtils.create(state, if (enabledOnly) NamedTextColor.GREEN else NamedTextColor.RED))
        )
        display.editMeta { meta ->
            meta.lore(
                ComponentUtils.cleanItalics(
                    listOf(
                        ComponentUtils.EMPTY,
                        ComponentUtils.create("When on, only entities this spawner is", NamedTextColor.GRAY),
                        ComponentUtils.create("currently spawning are displayed", NamedTextColor.GRAY),
                        ComponentUtils.EMPTY,
                        ComponentUtils.create("Click to toggle", NamedTextColor.YELLOW)
                    )
                )
            )
            meta.setEnchantmentGlintOverride(enabledOnly)
        }
        return display
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        event.isCancelled = true
    }

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        event.titleOverride(ComponentUtils.create("Spawner Editor - Entities", NamedTextColor.RED))
    }

    /**
     * The set of entity "sources" that can be displayed in the editor.
     */
    private enum class SourceFilter(val displayName: String, val icon: Material) {
        ALL("All Entities", Material.NETHER_STAR),
        CUSTOM("Custom Only", Material.SKELETON_SKULL),
        VANILLA("Vanilla Only", Material.EGG);

        fun next(): SourceFilter = entries[(ordinal + 1) % entries.size]
    }

    companion object {
        private const val ROWS = 6
        private const val MAX_WEIGHT = 100
        private const val WEIGHT_STEP_SHIFT = 5

        // The bottom-row control slots.
        private const val SLOT_PREVIOUS_PAGE = 45
        private const val SLOT_SOURCE_FILTER = 47
        private const val SLOT_BACK = 49
        private const val SLOT_ENABLED_FILTER = 51
        private const val SLOT_NEXT_PAGE = 53

        // The inner slots (everything inside the edge border) used to display entities.
        private val CONTENT_SLOTS: List<Int> = buildList {
            for (row in 1..ROWS - 2)
                for (col in 1..7)
                    add(row * 9 + col)
        }
        private val PAGE_SIZE = CONTENT_SLOTS.size

        // The full pool of entities a spawner is allowed to spawn, computed once and reused across menus.
        private val CUSTOM_CANDIDATES: List<SpawnableEntity> =
            CustomEntityType.entries
                .filter { it.canBeSpawnerSpawned() }
                .map { SpawnableEntity.of(it) }

        private val VANILLA_CANDIDATES: List<SpawnableEntity> =
            EntityType.entries
                .filter { isSpawnableVanilla(it) }
                .sortedBy { it.name }
                .map { SpawnableEntity.of(it) }

        /**
         * Determines whether a vanilla entity type is a living entity that can be spawned from a spawner.
         */
        private fun isSpawnableVanilla(type: EntityType): Boolean {
            if (type == EntityType.PLAYER || type == EntityType.UNKNOWN) return false
            if (!type.isSpawnable) return false
            val entityClass = type.entityClass ?: return false
            return LivingEntity::class.java.isAssignableFrom(entityClass)
        }
    }
}
