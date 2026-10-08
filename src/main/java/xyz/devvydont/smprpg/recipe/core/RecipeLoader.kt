package xyz.devvydont.smprpg.recipe.core

import org.bukkit.NamespacedKey
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.skills.SkillType
import java.io.File

/**
 * Builds a fresh [RecipeRegistry] by recursively scanning the plugin's `recipes/` folder for YAML files.
 *
 * Most files hold exactly one recipe; the file name (without extension) is the recipe id, and the file's
 * `type:` field selects the concrete [CustomRecipe]. (Enchanting files are the exception: one file per
 * enchantment defines a recipe for each of its `levels:`.) Subfolders are allowed purely for organization.
 * Malformed files (bad shape, an item key that does not resolve, or a duplicate id) are logged and skipped —
 * a single bad file never aborts loading.
 */
object RecipeLoader {

    private const val FOLDER = "recipes"
    private val YAML_EXTENSIONS = setOf("yml", "yaml")

    fun load(): RecipeRegistry {
        val plugin = SMPRPG.plugin
        val itemService = SMPRPG.getService(ItemService::class.java)
        val registry = RecipeRegistry()
        val files = recipeFiles()

        if (files.isEmpty()) {
            plugin.logger.info("No recipe files found under $FOLDER/; loaded 0 custom recipes.")
            return registry
        }

        val failures = mutableListOf<String>()
        for (file in files)
            failures += loadFileInto(file, registry, itemService)

        val suffix = if (failures.isNotEmpty()) " (${failures.size} failed — see warnings above)." else "."
        plugin.logger.info("Loaded ${registry.size} custom recipes from $FOLDER/$suffix")
        return registry
    }

    /** Every YAML recipe file under the data folder's `recipes/` directory, sorted for deterministic loading. */
    fun recipeFiles(): List<File> {
        val dir = File(SMPRPG.plugin.dataFolder, FOLDER)
        if (!dir.isDirectory) return emptyList()
        return dir.walkTopDown()
            .filter { it.isFile && it.extension.lowercase() in YAML_EXTENSIONS }
            .sortedBy { it.path }
            .toList()
    }

    /**
     * Parse a single recipe file and register its recipe(s) into [registry]. Malformed entries (bad shape, an
     * unresolved item, or a duplicate id) are logged and skipped without aborting — the same per-file resilience
     * [load] has, exposed so the batched reload can drive one file per work unit.
     *
     * @return a one-line reason for each recipe in this file that failed to load (empty if all registered);
     *   every reason is also logged as a console warning.
     */
    fun loadFileInto(file: File, registry: RecipeRegistry, itemService: ItemService): List<String> {
        val id = file.nameWithoutExtension.lowercase()
        val yaml = YamlConfiguration.loadConfiguration(file)
        val failures = mutableListOf<String>()
        try {
            val recipes = parse(id, yaml)
            if (recipes.isEmpty())
                return listOf(fail(file, null, "could not parse (check 'type' and required fields)"))
            for (recipe in recipes) {
                if (registry.byKey(recipe.key.asString()) != null) {
                    failures += fail(file, recipe.key.value(), "duplicate recipe id")
                    continue
                }
                val unresolved = firstUnresolved(recipe, itemService)
                if (unresolved != null) {
                    failures += fail(file, recipe.key.value(), "item '$unresolved' does not resolve")
                    continue
                }
                registry.register(recipe)
            }
        } catch (e: Exception) {
            failures += fail(file, null, e.message ?: "unexpected error")
        }
        return failures
    }

    /** Build a one-line failure reason, log it as a console warning, and return it for surfacing to the admin. */
    private fun fail(file: File, recipeId: String?, reason: String): String {
        val where = if (recipeId != null) "$recipeId (${file.name})" else file.name
        val message = "$where — $reason"
        SMPRPG.plugin.logger.warning("Skipping recipe $message")
        return message
    }

    /** Returns the first ingredient/output identifier that fails to resolve, or null if all are valid. */
    private fun firstUnresolved(recipe: CustomRecipe, itemService: ItemService): String? {
        for (ingredient in recipe.ingredients)
            if (itemService.resolveIdentifier(ingredient.identifier.asString()) == null)
                return ingredient.identifier.asString()
        for (output in recipe.outputs)
            if (itemService.resolveIdentifier(output.identifier.asString()) == null)
                return output.identifier.asString()
        return null
    }

    private fun parse(id: String, section: ConfigurationSection): List<CustomRecipe> {
        val key = NamespacedKey("smprpg", id)
        val type = section.getString("type")?.lowercase() ?: return emptyList()
        return when (type) {
            "shaped" -> listOfNotNull(parseShaped(key, section))
            "shapeless" -> listOfNotNull(parseShapeless(key, section))
            "smelting" -> listOfNotNull(parseSmelting(key, section))
            "cooking_pot" -> listOfNotNull(parseCookingPot(key, section))
            "cutting_board" -> listOfNotNull(parseCuttingBoard(key, section))
            "freezer" -> listOfNotNull(parseFreezer(key, section))
            "compression" -> parseCompression(id, section)
            "enchanting" -> parseEnchanting(section)
            else -> emptyList()
        }
    }

    private fun parseShaped(key: NamespacedKey, section: ConfigurationSection): CustomRecipe? {
        val pattern = section.getStringList("pattern")
        if (pattern.isEmpty()) return null
        val mapSection = section.getConfigurationSection("ingredients") ?: return null
        val keyMap = HashMap<Char, Ingredient>()
        for (charKey in mapSection.getKeys(false)) {
            if (charKey.isEmpty()) continue
            val ingredient = Ingredient.deserialize(normalize(mapSection.get(charKey))) ?: return null
            keyMap[charKey[0]] = ingredient
        }
        val result = RecipeOutput.deserialize(normalize(section.get("result"))) ?: return null
        val upgradeChar = parseShapedUpgrade(section, pattern, keyMap)
        return ShapedRecipe(
            key, pattern, keyMap, result, unlockedBy(section), upgradeChar,
            parseRewards(section), parseRequirements(section)
        )
    }

    /**
     * Resolve and validate the optional `upgrade:` field of a shaped recipe: the named character must be a
     * defined ingredient, occupy exactly one grid slot, and require amount 1 (you can only transfer data from
     * a single item). Throws if the designation is invalid so the file is skipped with a clear message.
     */
    private fun parseShapedUpgrade(
        section: ConfigurationSection,
        pattern: List<String>,
        keyMap: Map<Char, Ingredient>,
    ): Char? {
        val raw = section.getString("upgrade")?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        require(raw.length == 1) { "upgrade must be a single ingredient character, got '$raw'." }
        val upgradeChar = raw[0]
        val ingredient = keyMap[upgradeChar]
            ?: throw IllegalArgumentException("upgrade '$upgradeChar' is not a defined ingredient.")
        val occurrences = pattern.sumOf { row -> row.count { it == upgradeChar } }
        require(occurrences == 1) { "upgrade '$upgradeChar' must occupy exactly one slot, found $occurrences." }
        require(ingredient.amount == 1) { "upgrade '$upgradeChar' must require amount 1, got ${ingredient.amount}." }
        return upgradeChar
    }

    private fun parseShapeless(key: NamespacedKey, section: ConfigurationSection): CustomRecipe? {
        val ingredients = ingredientList(section, "ingredients") ?: return null
        val result = RecipeOutput.deserialize(normalize(section.get("result"))) ?: return null
        val upgradeIngredient = parseShapelessUpgrade(section, ingredients)
        return ShapelessRecipe(
            key, ingredients, result, unlockedBy(section), upgradeIngredient,
            parseRewards(section), parseRequirements(section)
        )
    }

    /**
     * Resolve and validate the optional `upgrade:` field of a shapeless recipe: the named item must be a
     * single ingredient of amount 1. Throws on an invalid designation so the file is skipped with a message.
     */
    private fun parseShapelessUpgrade(
        section: ConfigurationSection,
        ingredients: List<Ingredient>,
    ): ItemIdentifier? {
        val raw = section.getString("upgrade")?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        val identifier = ItemIdentifier.parse(raw)
        val matches = ingredients.filter { it.identifier == identifier }
        require(matches.size == 1) { "upgrade '$raw' must match exactly one ingredient, found ${matches.size}." }
        require(matches.first().amount == 1) { "upgrade '$raw' must require amount 1, got ${matches.first().amount}." }
        return identifier
    }

    private fun parseSmelting(key: NamespacedKey, section: ConfigurationSection): CustomRecipe? {
        val input = Ingredient.deserialize(normalize(section.get("input"))) ?: return null
        val result = RecipeOutput.deserialize(normalize(section.get("result"))) ?: return null
        val experience = (section.get("experience") as? Number)?.toFloat() ?: 0f
        val cook = SmeltingCookType.fromId(section.getString("cook"))
        return SmeltingRecipe(
            key, input, section.getInt("time", 200), result, experience, cook, unlockedBy(section),
            parseRewards(section), parseRequirements(section)
        )
    }

    private fun parseCookingPot(key: NamespacedKey, section: ConfigurationSection): CustomRecipe? {
        val ingredients = ingredientList(section, "ingredients") ?: return null
        val result = RecipeOutput.deserialize(normalize(section.get("result"))) ?: return null
        val plating = section.getString("plating")?.let { ItemIdentifier.parse(it) }
        return CookingPotRecipe(
            key, ingredients, section.getInt("time", 200), result, plating, unlockedBy(section),
            parseRewards(section), parseRequirements(section)
        )
    }

    private fun parseCuttingBoard(key: NamespacedKey, section: ConfigurationSection): CustomRecipe? {
        val input = Ingredient.deserialize(normalize(section.get("input"))) ?: return null
        val rawResults = section.getList("result") ?: return null
        val results = rawResults.mapNotNull { RecipeOutput.deserialize(normalize(it)) }
        if (results.isEmpty()) return null
        return CuttingBoardRecipe(
            key, input, results, section.getString("tool"), unlockedBy(section),
            parseRewards(section), parseRequirements(section)
        )
    }

    private fun parseFreezer(key: NamespacedKey, section: ConfigurationSection): CustomRecipe? {
        val input = Ingredient.deserialize(normalize(section.get("input"))) ?: return null
        val result = RecipeOutput.deserialize(normalize(section.get("result"))) ?: return null
        return FreezerRecipe(
            key, input, section.getInt("time", 200), result, unlockedBy(section),
            parseRewards(section), parseRequirements(section)
        )
    }

    /**
     * One compression file defines a whole chain via an ordered `tiers:` list: the first tier is the base, and
     * each later tier's `amount` is how many of the *previous* tier compress into one of it. Every adjacent pair
     * becomes one N->1 [CompressionRecipe] edge (keyed `smprpg:<lower>_to_<higher>`); the reverse decompression
     * is generated by the station driver. The chain's [family] is the file name minus any `_compression` suffix.
     */
    private fun parseCompression(id: String, section: ConfigurationSection): List<CompressionRecipe> {
        val rawTiers = section.getList("tiers") ?: return emptyList()
        val tiers = rawTiers.mapNotNull { Ingredient.deserialize(normalize(it)) }
        if (tiers.size < 2) return emptyList()
        val family = id.removeSuffix("_compression")
        val recipes = ArrayList<CompressionRecipe>()
        for (i in 0 until tiers.size - 1) {
            val lower = tiers[i].identifier
            val higher = tiers[i + 1].identifier
            val perHigher = tiers[i + 1].amount   // how many of the lower tier compress into one higher
            val key = NamespacedKey("smprpg", "${lower.path}_to_${higher.path}")
            recipes.add(CompressionRecipe(key, Ingredient(lower, perHigher), RecipeOutput(higher), family))
        }
        return recipes
    }

    /**
     * One enchanting file defines a recipe for each level. The `enchantment` id keys the lookup; each entry
     * under `levels:` (a level number -> `{ power, ingredients }`) becomes its own [EnchantingRecipe], keyed
     * `smprpg:<enchantment>_<level>`. A bad level is skipped without dropping the rest.
     */
    private fun parseEnchanting(section: ConfigurationSection): List<EnchantingRecipe> {
        val enchantment = section.getString("enchantment")?.lowercase() ?: return emptyList()
        val levels = section.getConfigurationSection("levels") ?: return emptyList()
        val recipes = ArrayList<EnchantingRecipe>()
        for (levelKey in levels.getKeys(false)) {
            val level = levelKey.toIntOrNull()?.takeIf { it > 0 } ?: continue
            val levelSection = levels.getConfigurationSection(levelKey) ?: continue
            val power = levelSection.getInt("power", 0)
            val ingredients = ingredientList(levelSection, "ingredients") ?: continue
            val key = NamespacedKey("smprpg", "${enchantment}_$level")
            recipes.add(EnchantingRecipe(key, enchantment, level, power, ingredients))
        }
        return recipes
    }

    /** Read a list of ingredients (each entry a string or a `{ item, amount }` map). Null if missing/empty. */
    private fun ingredientList(section: ConfigurationSection, path: String): List<Ingredient>? {
        val raw = section.getList(path) ?: return null
        val ingredients = raw.mapNotNull { Ingredient.deserialize(normalize(it)) }
        return ingredients.ifEmpty { null }
    }

    private fun unlockedBy(section: ConfigurationSection): List<ItemIdentifier> =
        section.getStringList("unlocked_by").map { ItemIdentifier.parse(it) }

    /**
     * Parse the optional `rewards:` block (`coins:` and a `skill_xp:` map). A legacy top-level `skill_xp:`
     * block (used by cooking pot recipes) is folded in too, so existing recipes keep working; an explicit
     * `rewards.skill_xp` entry overrides the legacy one for the same skill.
     */
    private fun parseRewards(section: ConfigurationSection): RecipeRewards {
        val rewardsSection = section.getConfigurationSection("rewards")
        val coins = (rewardsSection?.get("coins") as? Number)?.toLong() ?: 0L
        val skillXp = LinkedHashMap<SkillType, Int>()
        readSkillXp(section.getConfigurationSection("skill_xp"), skillXp)          // legacy top-level
        readSkillXp(rewardsSection?.getConfigurationSection("skill_xp"), skillXp)  // new rewards.skill_xp
        return RecipeRewards(coins, skillXp)
    }

    /** Parse the optional `requirements:` block (currently a `skills:` map of `skill -> minimum level`). */
    private fun parseRequirements(section: ConfigurationSection): RecipeRequirements {
        val skills = section.getConfigurationSection("requirements")?.getConfigurationSection("skills")
            ?: return RecipeRequirements()
        val levels = LinkedHashMap<SkillType, Int>()
        for (skill in skills.getKeys(false)) {
            val type = skillTypeOf(skill) ?: continue
            levels[type] = skills.getInt(skill)
        }
        return RecipeRequirements(levels)
    }

    /** Read a `skill -> amount` config section into [into], skipping (and warning on) unknown skill names. */
    private fun readSkillXp(section: ConfigurationSection?, into: MutableMap<SkillType, Int>) {
        if (section == null) return
        for (skill in section.getKeys(false)) {
            val type = skillTypeOf(skill) ?: continue
            into[type] = section.getInt(skill)
        }
    }

    /** Resolve a skill name to its [SkillType], logging and returning null for an unknown name. */
    private fun skillTypeOf(name: String): SkillType? {
        val type = runCatching { SkillType.valueOf(name.uppercase()) }.getOrNull()
        if (type == null)
            SMPRPG.plugin.logger.warning("Unknown skill '$name' in a recipe rewards/requirements block; ignoring it.")
        return type
    }

    /**
     * Bukkit deserializes nested YAML maps into [ConfigurationSection]s but leaves list-element maps as
     * plain [Map]s. Normalize both into a [Map] so [Ingredient]/[RecipeOutput] deserializers see one shape.
     */
    private fun normalize(value: Any?): Any? = when (value) {
        is ConfigurationSection -> value.getValues(false)
        else -> value
    }
}
