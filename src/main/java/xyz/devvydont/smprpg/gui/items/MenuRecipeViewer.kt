package xyz.devvydont.smprpg.gui.items

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Keyed
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.*
import org.bukkit.inventory.RecipeChoice.ExactChoice
import org.bukkit.inventory.RecipeChoice.MaterialChoice
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.scheduler.BukkitTask
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.broadcastToOperatorsCausedBy
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.gui.InterfaceUtil.getNamedItem
import xyz.devvydont.smprpg.gui.InterfaceUtil.getNamedItemWithDescription
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.gui.base.MenuButtonClickHandler
import xyz.devvydont.smprpg.items.interfaces.ICraftable
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.services.ItemService.Companion.blueprint
import xyz.devvydont.smprpg.services.ItemService.Companion.clean
import xyz.devvydont.smprpg.services.ItemService.Companion.generate
import xyz.devvydont.smprpg.services.RecipeService.Companion.getRecipesFor
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.time.TickTime
import java.util.List
import java.util.function.Consumer

class MenuRecipeViewer(
    player: Player,
    parentMenu: MenuBase?,
    private val recipes: MutableList<Recipe>,
    result: ItemStack
) : MenuBase(player, ROWS, parentMenu) {
    // The index of the recipe we want to show if there is more than one recipe.
    private var currentRecipe = 0

    private val result: ItemStack =
        clean(result) // This may seem silly, but it's necessary to remove the "craftable" lore.

    // Integer that will allow us to continuously flip through different options a recipe provides.
    private var recipeChoiceIndex = 0

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        super.handleInventoryOpened(event)
        event.titleOverride(ComponentUtils.merge(ComponentUtils.create("Recipes for: "), result.displayName()))
        this.render()
        Bukkit.getScheduler().runTaskTimer(plugin, Consumer runTaskTimer@{ task: BukkitTask ->

            // If nobody is viewing us, we can stop the task.
            if (this.inventory.viewers.isEmpty()) {
                task.cancel()
                return@runTaskTimer
            }

            render()
            recipeChoiceIndex++
        }, TickTime.HALF_SECOND, TickTime.HALF_SECOND)
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        super.handleInventoryClicked(event)
        event.isCancelled = true
        this.playInvalidAnimation()
    }

    private fun getItemFromRecipeChoice(choice: RecipeChoice?): ItemStack {
        // Yes, this is possible... LMAO
        if (choice == null) return ItemStack.of(Material.AIR)

        var item: ItemStack? = null
        if (choice is ExactChoice)
            item = clean(choice.choices[recipeChoiceIndex % choice.choices.size])

        if (choice is MaterialChoice)
            item = generate(choice.choices[recipeChoiceIndex % choice.choices.size])

        // This will only occur if Spigot/Paper ever add a new child of RecipeChoice in a future update.
        if (item == null) {
            broadcastToOperatorsCausedBy(
                this.player,
                ComponentUtils.create("Unknown recipe choice candidate: " + choice.javaClass.getSimpleName())
            )
            throw IllegalStateException("Unknown choice type: " + choice.javaClass.getSimpleName())
        }

        blueprint(item).updateItemData(item)

        // If this item is deemed craftable by us, inject lore explaining that they can click it to go deeper.
        if (!getRecipesFor(item).isEmpty()) {
            val lore = ArrayList<Component>(
                listOf<TextComponent>(
                    ComponentUtils.EMPTY,
                    ComponentUtils.create("Click to view recipe!", NamedTextColor.YELLOW),
                    ComponentUtils.EMPTY
                )
            )
            if (item.lore() != null) lore.addAll(item.lore()!!)
            item.lore(lore)
        }

        return item
    }

    /**
     * When an ingredient is clicked, if the blueprint of that ingredient is craftable itself as well then open
     * another layer of this menu with its recipe.
     *
     * @param itemStack The ItemStack clicked as a crafting component
     */
    private fun handleIngredientClick(itemStack: ItemStack) {
        // Retrieve the blueprint of this item. If it is craftable, enter another recipe layer

        val recipesFor = getRecipesFor(blueprint(itemStack).generate())
        if (recipesFor.isEmpty() || itemStack.type == Material.AIR) {
            this.playInvalidAnimation()
            return
        }

        this.openSubMenu(MenuRecipeViewer(player, this, recipesFor, itemStack))
    }

    /**
     * Renders the recipe completely
     */
    fun renderRecipe() {
        if (recipes.isEmpty()) {
            broadcastToOperatorsCausedBy(
                this.player,
                ComponentUtils.create(
                    "Achieved impossible recipe viewer state (no recipes to show): ",
                    NamedTextColor.RED
                ).append(result.displayName())
            )
            return
        }

        if (currentRecipe >= recipes.size)
            currentRecipe = 0
        if (currentRecipe < 0)
            currentRecipe = recipes.size - 1
        
        when (val recipe = recipes[currentRecipe]) {
            is CookingRecipe<*> -> renderCookingRecipe(recipe)
            is ShapelessRecipe -> renderShapelessRecipe(recipe)
            is ShapedRecipe -> renderShapedRecipe(recipe)
            is TransmuteRecipe -> renderTransmuteRecipe(recipe)
            is SmithingTransformRecipe -> renderSmithingTransformRecipe(recipe)
            is StonecuttingRecipe -> renderStonecuttingRecipe(recipe)
            is ComplexRecipe -> renderComplexRecipe(recipe)
            else -> broadcastToOperatorsCausedBy(
                this.player,
                ComponentUtils.create("Unknown recipe handler: " + recipe.javaClass.getSimpleName())
            )
        }
    }

    private fun renderComplexRecipe(complex: ComplexRecipe) {
        setSlot(
            CORNER + 10, getNamedItemWithDescription(
                Material.BARRIER,
                ComponentUtils.create("Complex Recipe", NamedTextColor.RED),
                ComponentUtils.EMPTY,
                ComponentUtils.create("This recipe is considered 'complex',"),
                ComponentUtils.create("which means that we can't"),
                ComponentUtils.create("determine the process to craft the"),
                ComponentUtils.create("result. There's not much we can provide"),
                ComponentUtils.create("you in this interface about this recipe!"),
                ComponentUtils.EMPTY,
                ComponentUtils.merge(
                    ComponentUtils.create("Recipe Key: "),
                    ComponentUtils.create(complex.key.asString(), NamedTextColor.RED)
                )
            )
        )
    }

    private fun renderStonecuttingRecipe(stonecutting: StonecuttingRecipe) {
        val input = getItemFromRecipeChoice(stonecutting.inputChoice)
        setButton(
            CORNER + 10,
            input
        ) { event: InventoryClickEvent -> handleIngredientClick(input) }
        setSlot(
            CORNER + 18 + 1,
            getNamedItem(Material.STONECUTTER, ComponentUtils.create("Stonecutter Recipe", NamedTextColor.GOLD))
        )
    }

    private fun renderSmithingTransformRecipe(smithing: SmithingRecipe) {
        if (smithing is SmithingTransformRecipe) {
            var input = getItemFromRecipeChoice(smithing.template)
            if (input.type == Material.AIR) input =
                getNamedItem(Material.BARRIER, ComponentUtils.create("No template needed!", NamedTextColor.GREEN))
            val finalInput = input
            setButton(
                CORNER + 9,
                input
            ) { event: InventoryClickEvent -> handleIngredientClick(finalInput) }
        }

        var base = getItemFromRecipeChoice(smithing.base)
        var addition = getItemFromRecipeChoice(smithing.addition)

        if (addition.type == Material.AIR) addition =
            getNamedItem(Material.BARRIER, ComponentUtils.create("No additional item needed!", NamedTextColor.GREEN))

        if (base.type == Material.AIR) base =
            getNamedItem(Material.BARRIER, ComponentUtils.create("No base item needed!", NamedTextColor.GREEN))

        val finalBase = base
        val finalAddition = addition
        setButton(
            CORNER + 10,
            base
        ) { event: InventoryClickEvent -> handleIngredientClick(finalBase) }
        setButton(
            CORNER + 11,
            addition
        ) { event: InventoryClickEvent -> handleIngredientClick(finalAddition) }

        setSlot(
            CORNER + 19,
            getNamedItem(Material.SMITHING_TABLE, ComponentUtils.create("Smithing Recipe", NamedTextColor.GOLD))
        )
    }

    private fun renderShapelessRecipe(shapeless: ShapelessRecipe) {
        var x = 0
        var y = 0

        // Loop through all the choices. These are essentially just the ingredients.
        for (choice in shapeless.getChoiceList()) {
            val item = getItemFromRecipeChoice(choice)
            this.setButton(
                y * 9 + x + CORNER,
                item
            ) { event: InventoryClickEvent -> handleIngredientClick(item) }

            x += 1
            // If we are out of bounds, go to the next row.
            if (x >= 3) {
                x = 0
                y += 1
            }
        }
        this.setSlot(
            CORNER + 27 + 1,
            getNamedItem(
                Material.CRAFTING_TABLE,
                ComponentUtils.create("Shapeless Crafting Recipe", NamedTextColor.GOLD)
            )
        )
    }

    private fun renderShapedRecipe(shaped: ShapedRecipe) {
        var x = 0
        var y = 0
        for (row in shaped.shape) {
            for (ingredient in row.toCharArray()) {
                val choice = shaped.getChoiceMap()[ingredient]
                val item = getItemFromRecipeChoice(choice)
                this.setButton(
                    y * 9 + x + CORNER,
                    item
                ) { event: InventoryClickEvent -> handleIngredientClick(item) }
                x += 1
            }
            y += 1
            x = 0
        }
        this.setSlot(
            CORNER + 27 + 1,
            getNamedItem(Material.CRAFTING_TABLE, ComponentUtils.create("Shaped Crafting Recipe", NamedTextColor.GOLD))
        )
    }

    /**
     * Renders a transmute recipe on the interface.
     * Transmute recipes are pretty simple, it is just two items that turn an item into another, similar to shapeless.
     * @param transmute The transmute recipe.
     */
    private fun renderTransmuteRecipe(transmute: TransmuteRecipe) {
        val input = getItemFromRecipeChoice(transmute.input)
        val transmuter = getItemFromRecipeChoice(transmute.material)
        this.setButton(
            CORNER + 9 + 1,
            input
        ) { event: InventoryClickEvent -> handleIngredientClick(input) }
        this.setButton(
            CORNER + 9 + 2,
            transmuter
        ) { event: InventoryClickEvent -> handleIngredientClick(transmuter) }
        this.setSlot(
            CORNER + 27 + 1,
            getNamedItem(
                Material.CRAFTING_TABLE,
                ComponentUtils.create("Transmute Crafting Recipe", NamedTextColor.GOLD)
            )
        )
    }

    private fun renderCookingRecipe(cooking: CookingRecipe<*>) {
        val top: Int = CORNER + 1
        val middle = top + 9
        val bottom = middle + 9

        val smelter = when (cooking) {
            is FurnaceRecipe -> getNamedItem(Material.FURNACE, ComponentUtils.create("Furnace Recipe", NamedTextColor.GOLD))
            is BlastingRecipe -> getNamedItem(Material.BLAST_FURNACE, ComponentUtils.create("Blasting Recipe", NamedTextColor.GOLD))
            is SmokingRecipe -> getNamedItem(Material.SMOKER, ComponentUtils.create("Smoker Recipe", NamedTextColor.GOLD))
            is CampfireRecipe -> getNamedItem(Material.CAMPFIRE, ComponentUtils.create("Campfire Recipe", NamedTextColor.GOLD))
            else -> getNamedItem(
                Material.BARRIER,
                ComponentUtils.create("Unknown Smelter: " + cooking.javaClass.getSimpleName(), NamedTextColor.RED)
            )
        }

        smelter.lore(
            ComponentUtils.cleanItalics(
                listOf<Component>(
                    ComponentUtils.EMPTY,
                    ComponentUtils.merge(
                        ComponentUtils.create("Cook in a "),
                        Component.translatable(smelter.translationKey()).color(NamedTextColor.YELLOW),
                        ComponentUtils.create(" block")
                    ),
                    ComponentUtils.merge(
                        ComponentUtils.create("for "),
                        ComponentUtils.create((cooking.cookingTime / 20).toString() + "s", NamedTextColor.GREEN)
                    )
                )
            )
        )

        val ingredient = getItemFromRecipeChoice(cooking.inputChoice)
        this.setButton(
            top,
            ingredient
        ) { event: InventoryClickEvent -> handleIngredientClick(ingredient) }
        this.setSlot(middle, smelter)
        this.setSlot(bottom, getNamedItem(Material.COAL, ComponentUtils.EMPTY))
    }

    /**
     * Generates an item stack that shows what "requirements" an item has in order to craft. This is subject to change,
     * but for now it simply displays what item "discovers" the recipe upon picking up. A common example is how the
     * recipe for a copper chestplate is discovered when we pick up a copper ingot.
     *
     * @param requirements A collection of item stacks that is "required" to discover this recipe.
     * @return an item stack to be used as a display in the interface
     */
    fun getRequirements(requirements: MutableCollection<ItemStack>): ItemStack {
        val paper = createNamedItem(Material.PAPER, ComponentUtils.create("Crafting Requirements", NamedTextColor.RED))
        paper.editMeta(Consumer { meta: ItemMeta ->
            val lore: MutableList<Component?> = ArrayList()
            lore.add(ComponentUtils.EMPTY)
            lore.add(ComponentUtils.create("The following items must be"))
            lore.add(ComponentUtils.create("discovered to unlock this recipe"))
            lore.add(ComponentUtils.EMPTY)
            for (item in requirements) lore.add(ComponentUtils.create("- ").append(item.displayName()))
            meta.lore(ComponentUtils.cleanItalics(lore))
            meta.setEnchantmentGlintOverride(true)
        })
        SMPRPG.getService(ItemService::class.java).setIgnoreMetaUpdate(paper)
        return paper
    }

    private fun changePage(delta: Int) {
        currentRecipe += delta

        if (delta < 0) this.sounds.playPagePrevious()
        else this.sounds.playPageNext()

        render()
    }

    fun render() {
        this.clear()
        this.setBorderFull()

        this.renderRecipe()

        // Misc buttons
        this.setSlot(RESULT, result)

        val blueprint = blueprint(result)
        if (blueprint is ICraftable) this.setSlot(REQUIREMENTS, getRequirements(blueprint.unlockedBy()))

        if (recipes.size > 1) {
            this.setButton(
                37,
                getNamedItem(
                    Material.ARROW,
                    ComponentUtils.create(
                        "Previous Page " + (currentRecipe + 1) + "/" + recipes.size,
                        NamedTextColor.GOLD
                    )
                )
            ) { event: InventoryClickEvent -> changePage(-1) }
            this.setButton(
                39,
                getNamedItem(
                    Material.ARROW,
                    ComponentUtils.create(
                        "Next Page " + (currentRecipe + 1) + "/" + recipes.size,
                        NamedTextColor.GOLD
                    )
                )
            ) { event: InventoryClickEvent -> changePage(1) }
        }

        if (this.player.isOp) {
            var key = "?"
            val recipe = recipes[currentRecipe]
            if (recipe is Keyed)
                key = (recipe as Keyed).key.asString()
            this.setSlot(
                0, getNamedItemWithDescription(
                    Material.OAK_SIGN,
                    ComponentUtils.create("Info", NamedTextColor.RED),
                    ComponentUtils.create("Key: $key", NamedTextColor.YELLOW)
                )
            )
        }

        // Utility buttons
        this.setButton(
            (ROWS - 1) * 9 + 4,
            BUTTON_BACK
        ) { e: InventoryClickEvent -> this.openParentMenu() }
    }

    companion object {
        const val ROWS: Int = 6

        // Hard set positions
        const val CORNER: Int = 10
        const val RESULT: Int = 23
        const val REQUIREMENTS: Int = 25
    }
}
