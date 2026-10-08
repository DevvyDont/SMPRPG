# Recipe System — Developer / Implementation Plan

> Audience: plugin developers. For server-admin instructions (how to write/edit recipe files), see
> [`recipes.md`](recipes.md).

## Why this exists

Recipes today are **bound to item blueprints in code**. A blueprint opts into crafting by implementing
`ICraftable` / `ICompressible` (both extend `IRecipeProvider`), and `ItemService.registerProvidedRecipes()`
walks every blueprint, asks for its `UnlockableRecipe`s, and pushes Bukkit `ShapedRecipe`/`ExactChoice`
recipes into the server. The custom stations (cooking pot, cutting board, freezer) sidestep Bukkit but are
**hardcoded as Kotlin enums** (`CookingPotRecipes`, `CuttingBoardRecipes`, `FreezerRecipes`), matched with
`ItemStack.isSimilar` (exact-NBT, no count predicates, no namespace resolution).

Problems this causes:

- A recipe can only exist if a Java/Kotlin class exists for it — no recipes without code.
- Every recipe change requires a recompile + server restart.
- Three parallel, incompatible recipe models (Bukkit `ICraftable`, compression chains, per-station enums).
- No way to express "needs 4 of X" per slot, and ingredients can't be written as `minecraft:gold_ingot`
  or `smprpg:steel_ingot`.

**Goal:** one custom, data-driven recipe engine. Recipes live in editable YAML, reload without a restart,
cover both vanilla-style and station recipes (freezer / enchanting / etc.), use custom ingredient predicates
(namespaced item types + stack-count requirements), and are completely independent of Bukkit's recipe API.

**Locked design decisions:**

| Decision | Choice |
|---|---|
| On-disk format | **YAML** (one file per station type) |
| Crafting table | **Custom GUI station** (`MenuBase`), enabling per-slot count predicates |
| Migration | **Incremental** — engine runs alongside the old system, migrate station-by-station, delete old code last |
| Discovery | **Custom recipe browser only** (`MenuRecipeViewer`); no vanilla recipe book |

## Architecture — core model

New package `xyz.devvydont.smprpg.recipe.core` (Kotlin). **Nothing here touches Bukkit's `Recipe` /
`Keyed`.**

- **`ItemIdentifier`** — value type wrapping a namespaced key string (`smprpg:steel_ingot`,
  `minecraft:gold_ingot`). Parses `namespace:path`. Resolves to an `ItemStack` and tests membership.
  Generalizes the existing `MaterialWrapper` (`util/crafting/MaterialWrapper.java`), which already models
  "vanilla `Material` OR `CustomItemType`" — reuse its `.get(itemService)` / `.key()` logic.
- **`Ingredient`** — `{ identifier: ItemIdentifier, amount: Int }` plus `matches(ItemStack): Boolean` and
  `display(): Component`. Matching delegates to the namespace resolver below (**NOT** `isSimilar`, so
  reforged / enchanted custom items still match by type). Leave room for future predicate subtypes
  (tag-based, e.g. the cutting board's `KNIVES` tool tag — see `CuttingBoardToolTags`).
- **`RecipeOutput`** — `{ item: ItemIdentifier, amount: Int, chance: Double = 1.0 }` (chance supports the
  cutting board's probabilistic multi-output).
- **`CustomRecipe`** (sealed interface) — `key: NamespacedKey`, `station: RecipeStationType`,
  `ingredients: List<Ingredient>`, `outputs: List<RecipeOutput>`, `unlockedBy: List<ItemIdentifier>`, and
  station-specific fields. Concrete types: `ShapedRecipe` (3×3 grid + per-slot counts), `ShapelessRecipe`,
  `SmeltingRecipe` (cook time), `CookingPotRecipe`, `CuttingBoardRecipe`, `FreezerRecipe`,
  `CompressionRecipe`, `EnchantingRecipe`.
- **`RecipeStationType`** enum — `CRAFTING_TABLE, FURNACE, COOKING_POT, CUTTING_BOARD, FREEZER, COMPRESSOR,
  ENCHANTING`. Drives which file / loader / GUI a recipe belongs to.

### Namespace resolution (extend `ItemService`, `services/ItemService.kt`)

The lookup logic already exists; these methods just unify it behind namespaced strings:

- `resolveIdentifier(key: String): ItemStack?` — `smprpg:x` → `getCustomItem(x)` (≈ line 846);
  `minecraft:x` → `Material.matchMaterial` → `getCustomItem(material)`.
- `getIdentifier(item: ItemStack): String` — `getItemKey(item)` non-null → `smprpg:<key>`, else
  `minecraft:<material>`.
- `matchesIdentifier(item: ItemStack, key: String): Boolean` — type-level compare (reuse `getBlueprint` /
  `isOfSameType`), used by `Ingredient.matches`.

## Data format & file layout (YAML)

**One recipe per file.** The loader recursively scans `plugins/SMPRPG/recipes/` for every `.yml`/`.yaml`
file; each file is a single recipe written at the top level (no wrapping id key), and the file name (minus
extension, lowercased) is the recipe id / `NamespacedKey` path. Subfolders are allowed purely for
organization. A small set of example files ships in `src/main/resources/recipes/` and is seeded to the data
folder on first run only (when the folder doesn't yet exist), so admin edits and deletions survive restarts.
Full schema lives in [`recipes.md`](recipes.md); a representative file:

```yaml
# recipes/steel_ingot.yml   (id = "steel_ingot")
type: shaped
pattern: ["III", "IBI", "III"]
ingredients:
  I: { item: "smprpg:iron_ingot", amount: 4 }   # per-slot count predicate
  B: "minecraft:blaze_powder"                    # shorthand: bare string = amount 1
result: { item: "smprpg:steel_ingot", amount: 1 }
unlocked_by: ["minecraft:iron_ingot"]
```

Compression chains (`ICompressible`) become one `compression` file per step; the reverse decompression
recipe is generated by the station driver — keeps the 9↔1 chains terse.

## Services & lifecycle

- **`RecipeRegistry`** (new, `recipe.core`) — in-memory store. Indexes recipes by `station` and by result
  identifier (for the browser's `getRecipesFor`). Pure data, no Bukkit listeners.
- **`RecipeLoader`** (new) — recursively walks the `recipes/` folder, loads each file via Bukkit
  `YamlConfiguration` (id = file name), deserializes by the `type:` discriminator into `CustomRecipe`s,
  validates every `ItemIdentifier` resolves and the id is unique (log + skip bad/duplicate files, **never
  crash startup**), populates a fresh `RecipeRegistry`.
- **Repurpose `RecipeService`** (`services/RecipeService.kt`) as owner: on `setup()` it builds the registry
  via `RecipeLoader`; expose `reload()` that rebuilds atomically (build new registry, swap reference),
  following the `DimensionPortalLockingListener.reload()` precedent.
- **Reload command** — add `/smprpg recipes reload` in the Brigadier command tree
  (`SMPRPGBootstrapper.kt`, COMMANDS lifecycle). No restart needed.
- `RecipeService.getRecipesFor(item)` (the browser's entry point, currently mixing Bukkit + enums) is
  reimplemented to query `RecipeRegistry` only.

## Station drivers (each reads the registry instead of hardcode)

- **Crafting table (custom GUI):** new `MenuCraftingTable : MenuBase` (`gui/`), a 3×3 input matrix +
  result preview, intercepting vanilla table opens the way `AnvilMenuListener` intercepts anvils. Matching
  = registry shaped/shapeless match with per-slot `Ingredient.amount` checks. Replaces the
  `PrepareItemCraftEvent` flow and the `CraftingTransmuteUpgradeFix` NBT workaround (transmute / upgrade
  becomes a first-class recipe behavior in the engine).
- **Furnace / smelting:** replace `RecipeService.registerFurnaceRecipes()` (scans `ISmeltable` blueprints)
  with `SmeltingRecipe`s from `recipes/furnace.yml`, driven by a custom furnace controller (same
  block-entity pattern as the cooking pot).
- **Cooking pot / cutting board / freezer:** the block-entity controllers already centralize matching in
  `getFirstRecipeMatch` / enum scans (`CookingPotBlockEntityController.tick` →
  `CookingPotRecipe.getFirstRecipeMatch`). Repoint to `RecipeRegistry.byStation(COOKING_POT)` etc., and
  swap `isSimilar` checks for `Ingredient.matches`. Controllers otherwise unchanged.
- **Compression & enchanting:** compression chains move from `ICompressible` into `compression` recipes;
  the (new) enchanting station reads `ENCHANTING` recipes.

## Migration status

Done so far (each station: data ported to YAML, driver repointed to the registry, legacy code deleted, build green):

- **Freezer, cutting board, cooking pot** — block-entity controllers read `RecipeRegistry.byStation(...)` and
  match via `Ingredient.matchesType`; the `*Recipes` enums are deleted. The recipe browser shows them via
  lightweight display adapters built from the registry. (Cooking pot matching also became count-aware: a
  recipe needs the exact ingredient set in at least the listed amounts.)
- **Furnace** — hybrid: `SmeltingRecipe` (now carrying `experience` + `cook` type) loaded from
  `recipes/furnace/`, and `RecipeService.registerFurnaceRecipes()` builds Bukkit cooking recipes from the
  registry (removed+re-added on reload for live updates). `ISmeltable` deleted.
- **Compression** — fully data-driven. The ~241 chain edges live in `recipes/compression/*.yml` (generated
  via `/smprpg recipes export`, then committed). `RecipeService.registerCompressionRecipes()` builds both
  Bukkit shaped recipes per edge; `CompressionGraph` (registry-backed) powers item worth
  (`calculateCompressedWorth`), compressed-item lore, recipe discovery, the bazaar flow, and the
  vanilla-grid collision fix. **`ICompressible` and `VanillaCompressibleBlueprint`'s chain logic are
  deleted** (~100 blueprints stripped); `getWorth` delegates to `CompressionGraph` via an
  `SMPItemBlueprint` extension.

- **Crafting table** — the ~405 `ICraftable` recipes were exported to `recipes/crafting_table/*.yml` and now
  register as Bukkit shaped/shapeless recipes from the registry (`RecipeService.registerCraftingRecipes`), so
  the 2x2 grid, recipe book, browser, and `CraftingTransmuteUpgradeFix` keep working. A custom
  `MenuCraftingTable` GUI (via `CraftingTableMenuListener`) replaces the workbench: `CraftingRecipeMatcher`
  matches registry recipes count-aware first, then falls back to vanilla. **`ICraftable`, `IRecipeProvider`,
  `registerProvidedRecipes`, and `util/crafting/builders/` are deleted** (~330 blueprints stripped); the
  sell-price nerf that keyed off `is ICraftable` now uses `RecipeService.isCraftable` (registry-backed).

**`ICraftable` / `ICompressible` / `ISmeltable` / `IRecipeProvider` are all gone — the recipe system is fully
data-driven.** Remaining polish on the custom crafting menu (needs server testing): shift-click craft-all,
porting transmute/upgrade into the menu matcher, and vanilla ingredient remainders. An **enchanting** station
remains as future work.

## Migration plan (incremental — old and new coexist until the end)

1. **Engine, no behavior change.** Land the core model, `ItemService` namespace methods,
   `RecipeRegistry` + `RecipeLoader`, and the reload command. Registry loads but nothing consumes it yet.
2. **Migrate one station at a time**, lowest-risk first: freezer → cutting board → cooking pot → furnace →
   compression → crafting table. For each: author the YAML (a one-off exporter that dumps the current enum
   to YAML keeps fidelity), repoint the driver, delete the enum.
3. **Crafting table.** Migrate `ICraftable` blueprints' grid recipes into `crafting_table.yml`; build
   `MenuCraftingTable`.
4. **Discovery.** Wire `MenuRecipeViewer` to the registry; drop vanilla recipe-book unlock registration in
   `ItemService`.
5. **Delete legacy.** Remove `ICraftable`, `ICompressible`, `IRecipeProvider`, the `instanceof` checks,
   `registerProvided*` in `ItemService`, the recipe `builders/`, and the per-station enums.

## Critical files

- **Extend:** `services/ItemService.kt` (namespace resolvers; later remove `registerProvidedRecipes`),
  `services/RecipeService.kt` (own registry + reload), `SMPRPGBootstrapper.kt` (reload command).
- **New:** `recipe/core/` (`ItemIdentifier`, `Ingredient`, `RecipeOutput`, `CustomRecipe` + subtypes,
  `RecipeStationType`, `RecipeRegistry`, `RecipeLoader`), `gui/MenuCraftingTable.kt`, custom furnace
  controller, `src/main/resources/recipes/*.yml`.
- **Repoint:** block-entity controllers in `block/entity/` (cooking pot / cutting board / freezer),
  `gui/items/MenuRecipeViewer.kt`.
- **Reuse:** `util/crafting/MaterialWrapper.java`, `gui/base/MenuBase.kt`,
  `util/listeners/ToggleableListener.java`, `listeners/crafting/AnvilMenuListener.kt` (interception
  precedent).
- **Delete (last):** `items/interfaces/ICraftable.java`, `ICompressible.kt`, `IRecipeProvider.kt`,
  `util/crafting/builders/`, `recipe/**/<Station>Recipes.kt` enums.

## Verification

- **Build:** `./gradlew build` after each migration step.
- **Reload, no restart:** edit a recipe in `plugins/SMPRPG/recipes/crafting_table.yml`, run
  `/smprpg recipes reload`, confirm the change takes effect live.
- **Predicates:** craft a recipe needing `amount: 4` of one ingredient in a single grid slot; confirm it
  fails with 3 and succeeds with 4.
- **Namespace mix:** a recipe combining `minecraft:` and `smprpg:` ingredients resolves and crafts.
- **Type-level matching:** an enchanted / reforged custom item still satisfies its ingredient slot.
- **Per station:** smelt, cooking pot, cutting board (probabilistic outputs), freezer, compression (9↔1),
  and the new crafting-table GUI each produce correct results from YAML.
- **Discovery:** `MenuRecipeViewer` lists the migrated recipes and their unlocks.
- **Bad data:** a malformed entry logs a warning and is skipped without aborting startup.
