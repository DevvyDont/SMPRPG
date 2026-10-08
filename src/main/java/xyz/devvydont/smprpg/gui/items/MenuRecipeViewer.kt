package xyz.devvydont.smprpg.gui.items

import io.papermc.paper.datacomponent.DataComponentTypes
import net.kyori.adventure.key.Key
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
import xyz.devvydont.smprpg.gui.InterfaceUtil.getNamedItem
import xyz.devvydont.smprpg.gui.InterfaceUtil.getNamedItemWithDescription
import xyz.devvydont.smprpg.gui.base.IRecipeDependentMenu
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.recipe.core.RecipeStationType
import xyz.devvydont.smprpg.services.RecipeService
import xyz.devvydont.smprpg.recipe.cookingpot.CookingPotRecipe
import xyz.devvydont.smprpg.recipe.crafting.ShapedDisplayRecipe
import xyz.devvydont.smprpg.recipe.crafting.ShapelessDisplayRecipe
import xyz.devvydont.smprpg.recipe.cuttingboard.CuttingBoardRecipe
import xyz.devvydont.smprpg.recipe.cuttingboard.CuttingBoardToolTags
import xyz.devvydont.smprpg.recipe.freezer.FreezerRecipe
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.services.RecipeService.Companion.getRecipesFor
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.Symbols
import xyz.devvydont.smprpg.util.time.TickTime
import java.util.function.Consumer

class MenuRecipeViewer(
    player: Player,
    parentMenu: MenuBase?,
    private val recipes: MutableList<Recipe>,
    result: ItemStack
) : MenuBase(player, ROWS, parentMenu), IRecipeDependentMenu {
    // The index of the recipe we want to show if there is more than one recipe.
    private var currentRecipe = 0

    private val result: ItemStack =
        ItemService.clean(result) // This may seem silly, but it's necessary to remove the "craftable" lore.

    // Integer that will allow us to continuously flip through different options a recipe provides.
    private var recipeChoiceIndex = 0

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        super.handleInventoryOpened(event)
        event.titleOverride(ComponentUtils.merge(ComponentUtils.create("Recipes for: "), ItemService.blueprint(result).getNameComponent(result)))
        this.render(event)
        Bukkit.getScheduler().runTaskTimer(SMPRPG.plugin, Consumer runTaskTimer@{ task: BukkitTask ->

            // If nobody is viewing us, we can stop the task.
            if (this.inventory.viewers.isEmpty()) {
                task.cancel()
                return@runTaskTimer
            }

            render(event)
            recipeChoiceIndex++
        }, TickTime.HALF_SECOND, TickTime.HALF_SECOND)
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        super.handleInventoryClicked(event)
        event.isCancelled = true
        //this.playInvalidAnimation();
    }

    private fun getItemFromRecipeChoice(choice: RecipeChoice?): ItemStack {
        // Yes, this is possible... LMAO
        if (choice == null) return ItemStack.of(Material.AIR)

        var item: ItemStack? = null
        if (choice is ExactChoice) item =
            ItemService.clean(choice.choices[recipeChoiceIndex % choice.choices.size])

        if (choice is MaterialChoice) item =
            ItemService.generate(choice.choices[recipeChoiceIndex % choice.choices.size])

        // This will only occur if Spigot/Paper ever add a new child of RecipeChoice in a future update.
        if (item == null) {
            broadcastToOperatorsCausedBy(
                this.player,
                ComponentUtils.create("Unknown recipe choice candidate: " + choice.javaClass.getSimpleName())
            )
            throw IllegalStateException("Unknown choice type: " + choice.javaClass.getSimpleName())
        }

        ItemService.blueprint(item).updateItemData(item)

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

        val recipesFor = getRecipesFor(ItemService.blueprint(itemStack).generate())
        if (recipesFor.isEmpty() || itemStack.type == Material.AIR) {
            //this.playInvalidAnimation();
            return
        }

        this.openSubMenu(MenuRecipeViewer(player, this, recipesFor, itemStack))
    }

    /**
     * Renders the recipe completely
     */
    fun renderRecipe(event: InventoryOpenEvent) {
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

        if (currentRecipe >= recipes.size) currentRecipe = 0
        if (currentRecipe < 0) currentRecipe = recipes.size - 1

        val currentRecipeObj = recipes[currentRecipe]
        // CuttingBoardRecipe.getResult() throws by design — amount stays as-is for multi-output recipes.
        if (currentRecipeObj !is CuttingBoardRecipe)
            result.amount = currentRecipeObj.result.amount

        when (val recipe = currentRecipeObj) {
            is CookingPotRecipe -> renderCookingPotRecipe(recipe, event)
            is CuttingBoardRecipe -> renderCuttingBoardRecipe(recipe)
            is FreezerRecipe -> renderFreezerRecipe(recipe, event)
            is ShapedDisplayRecipe -> renderShapedDisplayRecipe(recipe, event)
            is ShapelessDisplayRecipe -> renderShapelessDisplayRecipe(recipe, event)
            is CookingRecipe<*> -> renderCookingRecipe(recipe, event)
            is ShapelessRecipe -> renderShapelessRecipe(recipe, event)
            is ShapedRecipe -> renderShapedRecipe(recipe, event)
            is TransmuteRecipe -> renderTransmuteRecipe(recipe)
            is SmithingTransformRecipe -> renderSmithingTransformRecipe(recipe, event)
            is StonecuttingRecipe -> renderStonecuttingRecipe(recipe, event)
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

    private fun prepareIngredientDisplay(item: ItemStack): ItemStack {
        val display = item.clone()
        ItemService.blueprint(display).updateItemData(display)
        if (getRecipesFor(display).isNotEmpty()) {
            val lore = mutableListOf<Component>(ComponentUtils.EMPTY, ComponentUtils.create("Click to view recipe!", NamedTextColor.YELLOW), ComponentUtils.EMPTY)
            if (display.lore() != null) lore.addAll(display.lore()!!)
            display.lore(lore)
        }
        return display
    }

    private fun renderCookingPotRecipe(recipe: CookingPotRecipe, event: InventoryOpenEvent) {
        event.titleOverride(
            ComponentUtils.merge(
                ComponentUtils.create(Symbols.OFFSET_NEG_1 + Symbols.COOKING_POT_RECIPE_MENU, NamedTextColor.WHITE),
                ComponentUtils.create(
                    Symbols.OFFSET_NEG_128 + Symbols.OFFSET_NEG_32 + Symbols.OFFSET_NEG_2 + "Recipes for: ",
                    NamedTextColor.BLACK
                ),
                ItemService.blueprint(result).getNameComponent(result)
            )
        )

        var x = 0
        var y = 0
        for (ingredient in recipe.inputs) {
            setButton(y * 9 + x + CORNER, prepareIngredientDisplay(ingredient)) { _: InventoryClickEvent -> handleIngredientClick(ingredient) }
            x++
            if (x >= 3) { x = 0; y++ }
        }
        if (recipe.platingItem != null) {
            val plating = recipe.platingItem.clone()
            ItemService.blueprint(plating).updateItemData(plating)
            val lore = mutableListOf<Component>(ComponentUtils.EMPTY, ComponentUtils.create("Place in the plating slot", NamedTextColor.YELLOW))
            if (plating.lore() != null) lore.addAll(plating.lore()!!)
            plating.lore(ComponentUtils.cleanItalics(lore))
            setSlot(CORNER + 32, plating)
        }
        setSlot(CORNER + 37, getNamedItemWithDescription(
            CustomItemType.COOKING_POT,
            ComponentUtils.create("Cooking Pot Recipe", NamedTextColor.GOLD),
            ComponentUtils.EMPTY,
            ComponentUtils.merge(ComponentUtils.create("for "), ComponentUtils.create("${recipe.cookTime / 20}s", NamedTextColor.GREEN))
        ))
    }

    private fun renderCuttingBoardRecipe(recipe: CuttingBoardRecipe) {
        setButton(CORNER + 1, prepareIngredientDisplay(recipe.input)) { _: InventoryClickEvent -> handleIngredientClick(recipe.input) }

        val toolMaterial = when (recipe.processToolTag) {
            CuttingBoardToolTags.KNIVES -> Material.IRON_SWORD
            CuttingBoardToolTags.AXES -> Material.IRON_AXE
            CuttingBoardToolTags.SHOVELS -> Material.IRON_SHOVEL
            else -> Material.IRON_SWORD
        }
        val toolLabel = when (recipe.processToolTag) {
            CuttingBoardToolTags.KNIVES -> "Knife"
            CuttingBoardToolTags.AXES -> "Axe"
            CuttingBoardToolTags.SHOVELS -> "Shovel"
            else -> "Tool"
        }
        setSlot(CORNER + 3, getNamedItemWithDescription(
            toolMaterial,
            ComponentUtils.create("Required Tool", NamedTextColor.GOLD),
            ComponentUtils.EMPTY,
            ComponentUtils.create("Use a $toolLabel")
        ))

        var outputSlot = CORNER + 18
        for ((output, chance) in recipe.recipeResult) {
            if (output.type == Material.BARRIER) continue
            val outDisplay = output.clone()
            ItemService.blueprint(outDisplay).updateItemData(outDisplay)
            val chanceStr = if (chance >= 1.0) "Guaranteed" else "${"%.0f".format(chance * 100)}% chance"
            val chanceColor = if (chance >= 1.0) NamedTextColor.GREEN else NamedTextColor.YELLOW
            val lore = mutableListOf<Component>(ComponentUtils.EMPTY, ComponentUtils.create(chanceStr, chanceColor))
            if (outDisplay.lore() != null) lore.addAll(outDisplay.lore()!!)
            outDisplay.lore(ComponentUtils.cleanItalics(lore))
            setSlot(outputSlot, outDisplay)
            outputSlot++
        }

        setSlot(CORNER + 37, getNamedItem(CustomItemType.CUTTING_BOARD, ComponentUtils.create("Cutting Board Recipe", NamedTextColor.GOLD)))
    }

    private fun renderFreezerRecipe(recipe: FreezerRecipe, event: InventoryOpenEvent) {
        val top = CORNER + 1
        val middle = top + 9
        val bottom = middle + 9

        event.titleOverride(
            ComponentUtils.merge(
                ComponentUtils.create(Symbols.OFFSET_NEG_1 + Symbols.FURNACE_RECIPE_MENU, NamedTextColor.WHITE),
                ComponentUtils.create(
                    Symbols.OFFSET_NEG_128 + Symbols.OFFSET_NEG_32 + Symbols.OFFSET_NEG_2 + "Recipes for: ",
                    NamedTextColor.BLACK
                ),
                ItemService.blueprint(result).getNameComponent(result)
            )
        )

        val flame = BORDER_NORMAL.clone()
        flame.setData(DataComponentTypes.ITEM_MODEL, Key.key("smprpg:ui/freezer_burn_recipe"))

        setButton(top, prepareIngredientDisplay(recipe.input)) { _: InventoryClickEvent -> handleIngredientClick(recipe.input) }
        setSlot(middle, flame)
        setSlot(bottom, getNamedItem(CustomItemType.ICESTONE, ComponentUtils.EMPTY))
        setSlot(CORNER + 37, getNamedItemWithDescription(
            CustomItemType.FREEZER,
            ComponentUtils.create("Freezer Recipe", NamedTextColor.GOLD),
            ComponentUtils.EMPTY,
            ComponentUtils.merge(ComponentUtils.create("for "), ComponentUtils.create("${recipe.freezeTime / 20}s", NamedTextColor.GREEN))
        ))
    }

    private fun renderStonecuttingRecipe(stonecutting: StonecuttingRecipe, event: InventoryOpenEvent) {
        event.titleOverride(
            ComponentUtils.merge(
                ComponentUtils.create(Symbols.OFFSET_NEG_1 + Symbols.STONECUTTER_RECIPE_MENU, NamedTextColor.WHITE),
                ComponentUtils.create(
                    Symbols.OFFSET_NEG_128 + Symbols.OFFSET_NEG_32 + Symbols.OFFSET_NEG_2 + "Recipes for: ",
                    NamedTextColor.BLACK
                ),
                ItemService.blueprint(result).getNameComponent(result)
            )
        )

        val input = getItemFromRecipeChoice(stonecutting.inputChoice)
        setButton(
            CORNER + 10,
            input
        ) { _: InventoryClickEvent -> handleIngredientClick(input) }
        setSlot(
            CORNER + 36 + 1,
            getNamedItem(Material.STONECUTTER, ComponentUtils.create("Stonecutter Recipe", NamedTextColor.GOLD))
        )
    }

    private fun renderSmithingTransformRecipe(smithing: SmithingRecipe, event: InventoryOpenEvent) {
        event.titleOverride(
            ComponentUtils.merge(
                ComponentUtils.create(Symbols.OFFSET_NEG_1 + Symbols.SMITHING_RECIPE_MENU, NamedTextColor.WHITE),
                ComponentUtils.create(
                    Symbols.OFFSET_NEG_128 + Symbols.OFFSET_NEG_32 + Symbols.OFFSET_NEG_2 + "Recipes for: ",
                    NamedTextColor.BLACK
                ),
                ItemService.blueprint(result).getNameComponent(result)
            )
        )

        if (smithing is SmithingTransformRecipe) {
            var input = getItemFromRecipeChoice(smithing.template)
            if (input.type == Material.AIR) input =
                getNamedItem(Material.BARRIER, ComponentUtils.create("No template needed!", NamedTextColor.GREEN))
            val finalInput = input
            setButton(
                CORNER + 9,
                input
            ) { _: InventoryClickEvent -> handleIngredientClick(finalInput) }
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
        ) { _: InventoryClickEvent -> handleIngredientClick(finalBase) }
        setButton(
            CORNER + 11,
            addition
        ) { _: InventoryClickEvent -> handleIngredientClick(finalAddition) }

        setSlot(
            CORNER + 37,
            getNamedItem(Material.SMITHING_TABLE, ComponentUtils.create("Smithing Recipe", NamedTextColor.GOLD))
        )
    }

    private fun renderShapelessRecipe(shapeless: ShapelessRecipe, event: InventoryOpenEvent) {
        var x = 0
        var y = 0
        event.titleOverride(
            ComponentUtils.merge(
                ComponentUtils.create(Symbols.OFFSET_NEG_1 + Symbols.SHAPELESS_RECIPE_MENU, NamedTextColor.WHITE),
                ComponentUtils.create(
                    Symbols.OFFSET_NEG_128 + Symbols.OFFSET_NEG_32 + Symbols.OFFSET_NEG_2 + "Recipes for: ",
                    NamedTextColor.BLACK
                ),
                ItemService.blueprint(result).getNameComponent(result)
            )
        )

        // Loop through all the choices. These are essentially just the ingredients.
        for (choice in shapeless.choiceList) {
            val item = getItemFromRecipeChoice(choice)
            this.setButton(
                y * 9 + x + CORNER,
                item
            ) { _: InventoryClickEvent -> handleIngredientClick(item) }

            x += 1
            // If we are out of bounds, go to the next row.
            if (x >= 3) {
                x = 0
                y += 1
            }
        }
        this.setSlot(
            CORNER + 36 + 1,
            getNamedItem(
                Material.CRAFTING_TABLE,
                ComponentUtils.create("Shapeless Crafting Recipe", NamedTextColor.GOLD)
            )
        )
    }

    private fun renderShapedRecipe(shaped: ShapedRecipe, event: InventoryOpenEvent) {
        var x = 0
        var y = 0
        event.titleOverride(
            ComponentUtils.merge(
                ComponentUtils.create(Symbols.OFFSET_NEG_1 + Symbols.SHAPED_RECIPE_MENU, NamedTextColor.WHITE),
                ComponentUtils.create(
                    Symbols.OFFSET_NEG_128 + Symbols.OFFSET_NEG_32 + Symbols.OFFSET_NEG_2 + "Recipes for: ",
                    NamedTextColor.BLACK
                ),
                ItemService.blueprint(result).getNameComponent(result)
            )
        )
        for (row in shaped.shape) {
            for (ingredient in row.toCharArray()) {
                val choice = shaped.choiceMap[ingredient]
                val item = getItemFromRecipeChoice(choice)
                this.setButton(
                    y * 9 + x + CORNER,
                    item
                ) { _: InventoryClickEvent -> handleIngredientClick(item) }
                x += 1
            }
            y += 1
            x = 0
        }
        this.setSlot(
            CORNER + 36 + 1,
            getNamedItem(Material.CRAFTING_TABLE, ComponentUtils.create("Shaped Crafting Recipe", NamedTextColor.GOLD))
        )
    }

    /**
     * Renders a registry-sourced shaped recipe. Unlike the Bukkit shaped renderer, each ingredient display
     * carries its required per-slot count (so count>1 recipes read correctly), and the upgrade slot is marked.
     */
    private fun renderShapedDisplayRecipe(recipe: ShapedDisplayRecipe, event: InventoryOpenEvent) {
        event.titleOverride(
            ComponentUtils.merge(
                ComponentUtils.create(Symbols.OFFSET_NEG_1 + Symbols.SHAPED_RECIPE_MENU, NamedTextColor.WHITE),
                ComponentUtils.create(
                    Symbols.OFFSET_NEG_128 + Symbols.OFFSET_NEG_32 + Symbols.OFFSET_NEG_2 + "Recipes for: ",
                    NamedTextColor.BLACK
                ),
                ItemService.blueprint(result).getNameComponent(result)
            )
        )
        var x = 0
        var y = 0
        for (row in recipe.pattern) {
            for (ch in row.toCharArray()) {
                val ingredient = recipe.ingredients[ch]
                if (ingredient != null) {
                    val display = prepareIngredientDisplay(ingredient)
                    if (ch == recipe.upgradeChar) markUpgradeBase(display)
                    setButton(y * 9 + x + CORNER, display) { _: InventoryClickEvent -> handleIngredientClick(ingredient) }
                }
                x += 1
            }
            x = 0
            y += 1
        }
        setSlot(CORNER + 36 + 1, getNamedItem(Material.CRAFTING_TABLE, ComponentUtils.create("Shaped Crafting Recipe", NamedTextColor.GOLD)))
    }

    /** Renders a registry-sourced shapeless recipe, each ingredient display carrying its required count. */
    private fun renderShapelessDisplayRecipe(recipe: ShapelessDisplayRecipe, event: InventoryOpenEvent) {
        event.titleOverride(
            ComponentUtils.merge(
                ComponentUtils.create(Symbols.OFFSET_NEG_1 + Symbols.SHAPELESS_RECIPE_MENU, NamedTextColor.WHITE),
                ComponentUtils.create(
                    Symbols.OFFSET_NEG_128 + Symbols.OFFSET_NEG_32 + Symbols.OFFSET_NEG_2 + "Recipes for: ",
                    NamedTextColor.BLACK
                ),
                ItemService.blueprint(result).getNameComponent(result)
            )
        )
        var x = 0
        var y = 0
        for (ingredient in recipe.ingredients) {
            val display = prepareIngredientDisplay(ingredient)
            setButton(y * 9 + x + CORNER, display) { _: InventoryClickEvent -> handleIngredientClick(ingredient) }
            x += 1
            if (x >= 3) { x = 0; y += 1 }
        }
        setSlot(CORNER + 36 + 1, getNamedItem(Material.CRAFTING_TABLE, ComponentUtils.create("Shapeless Crafting Recipe", NamedTextColor.GOLD)))
    }

    /** Append an "upgrade base" note to an ingredient's lore (the slot whose data carries over on craft). */
    private fun markUpgradeBase(item: ItemStack) {
        val lore = item.lore()?.toMutableList() ?: mutableListOf()
        lore.add(ComponentUtils.create("Upgrade base — keeps its enchants & reforges", NamedTextColor.AQUA))
        item.lore(lore)
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
        ) { _: InventoryClickEvent -> handleIngredientClick(input) }
        this.setButton(
            CORNER + 9 + 2,
            transmuter
        ) { _: InventoryClickEvent -> handleIngredientClick(transmuter) }
        this.setSlot(
            CORNER + 27 + 1,
            getNamedItem(
                Material.CRAFTING_TABLE,
                ComponentUtils.create("Transmute Crafting Recipe", NamedTextColor.GOLD)
            )
        )
    }

    private fun renderCookingRecipe(cooking: CookingRecipe<*>, event: InventoryOpenEvent) {
        val top: Int = CORNER + 1
        val middle = top + 9
        val bottom = middle + 9
        val smelterSlot = middle + 27

        event.titleOverride(
            ComponentUtils.merge(
                ComponentUtils.create(Symbols.OFFSET_NEG_1 + Symbols.FURNACE_RECIPE_MENU, NamedTextColor.WHITE),
                ComponentUtils.create(
                    Symbols.OFFSET_NEG_128 + Symbols.OFFSET_NEG_32 + Symbols.OFFSET_NEG_2 + "Recipes for: ",
                    NamedTextColor.BLACK
                ),
                ItemService.blueprint(result).getNameComponent(result)
            )
        )

        val smelter = when (cooking) {
            is FurnaceRecipe -> getNamedItem(
                Material.FURNACE,
                ComponentUtils.create("Furnace Recipe", NamedTextColor.GOLD)
            )

            is BlastingRecipe -> getNamedItem(
                Material.BLAST_FURNACE,
                ComponentUtils.create("Blasting Recipe", NamedTextColor.GOLD)
            )

            is SmokingRecipe -> getNamedItem(
                Material.SMOKER,
                ComponentUtils.create("Smoker Recipe", NamedTextColor.GOLD)
            )

            is CampfireRecipe -> getNamedItem(
                Material.CAMPFIRE,
                ComponentUtils.create("Campfire Recipe", NamedTextColor.GOLD)
            )

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

        val fire = BORDER_NORMAL.clone()
        fire.setData(DataComponentTypes.ITEM_MODEL, Key.key("smprpg:ui/furnace_burn"))

        val ingredient = getItemFromRecipeChoice(cooking.inputChoice)
        this.setButton(
            top,
            ingredient
        ) { _: InventoryClickEvent -> handleIngredientClick(ingredient) }
        this.setSlot(smelterSlot, smelter)
        this.setSlot(middle, fire)
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
        paper.editMeta(Consumer { meta: ItemMeta? ->
            val lore: MutableList<Component?> = ArrayList<Component?>()
            lore.add(ComponentUtils.EMPTY)
            lore.add(ComponentUtils.create("The following items must be"))
            lore.add(ComponentUtils.create("discovered to unlock this recipe"))
            lore.add(ComponentUtils.EMPTY)
            for (item in requirements) lore.add(ComponentUtils.create("- ").append(item.displayName()))
            meta!!.lore(ComponentUtils.cleanItalics(lore))
            meta.setEnchantmentGlintOverride(true)
        })
        SMPRPG.getService(ItemService::class.java).setIgnoreMetaUpdate(paper)
        return paper
    }

    private fun changePage(delta: Int, event: InventoryOpenEvent) {
        currentRecipe += delta

        if (delta < 0) this.sounds.playPagePrevious()
        else this.sounds.playPageNext()

        render(event)
    }

    fun render(event: InventoryOpenEvent) {
        this.clear()
        this.setBorderFull()

        this.renderRecipe(event)

        // Misc buttons
        this.setSlot(RESULT, result)

        // Show the unlock requirements for this item's data-driven crafting recipe, if it has one.
        val itemService = SMPRPG.getService(ItemService::class.java)
        val craftingRecipe = SMPRPG.getService(RecipeService::class.java).getRegistry()
            .byResult(itemService.getIdentifier(result))
            .firstOrNull { it.station == RecipeStationType.CRAFTING_TABLE }
        if (craftingRecipe != null && craftingRecipe.unlockedBy.isNotEmpty()) {
            val requirements = craftingRecipe.unlockedBy.mapNotNull { itemService.resolveIdentifier(it.asString()) }.toMutableList()
            this.setSlot(REQUIREMENTS, getRequirements(requirements))
        }

        if (recipes.size > 1) {
            this.setButton(
                46,
                getNamedItem(
                    Material.ARROW,
                    ComponentUtils.create(
                        "Previous Page " + (currentRecipe + 1) + "/" + recipes.size,
                        NamedTextColor.GOLD
                    )
                )
            ) { _: InventoryClickEvent -> changePage(-1, event) }
            this.setButton(
                48,
                getNamedItem(
                    Material.ARROW,
                    ComponentUtils.create(
                        "Next Page " + (currentRecipe + 1) + "/" + recipes.size,
                        NamedTextColor.GOLD
                    )
                )
            ) { _: InventoryClickEvent -> changePage(1, event) }
        }

        if (this.player.isOp) {
            var key = "?"
            val keyedRecipe = recipes[currentRecipe]
            if (keyedRecipe is Keyed)
                key = keyedRecipe.key.asString()
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
        ) { _: InventoryClickEvent -> this.openParentMenu() }
    }

    companion object {
        const val ROWS: Int = 6

        // Hard set positions
        const val CORNER: Int = 10
        const val RESULT: Int = 24
        const val REQUIREMENTS: Int = 26
    }
}
