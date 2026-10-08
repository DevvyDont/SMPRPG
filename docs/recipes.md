# Recipes — Server Admin Guide

How to add, edit, and reload custom recipes on an SMPRPG server. No coding or server restart required.

> Developers: see [`recipes-dev.md`](recipes-dev.md) for the engine internals.

## Where recipes live

Recipe files are plain YAML under `plugins/SMPRPG/recipes/`. **There is one recipe per file**, and the
**file name (without `.yml`) is the recipe id**. To add a recipe, drop in a new `.yml` file; to remove one,
delete its file. The plugin loads every `.yml` it finds in that folder.

```
plugins/SMPRPG/recipes/
├── steel_ingot.yml
├── iron_from_raw.yml
├── beef_stew.yml
├── ice_from_water.yml
└── ...
```

You may organize recipes into **subfolders** (e.g. `recipes/weapons/`, `recipes/food/`) — the loader scans
recursively. The recipe id still comes from the file name, so keep file names unique (a duplicate id is
skipped with a warning). A handful of example recipes are seeded on first run; after that the folder is
yours — your edits and deletions are preserved across restarts.

## Referring to items

Every ingredient and result is written as a **namespaced item key**:

- `minecraft:<material>` — any vanilla item, e.g. `minecraft:gold_ingot`, `minecraft:water_bucket`.
- `smprpg:<custom_item>` — any custom item, e.g. `smprpg:steel_ingot`, `smprpg:beef_stew`. The custom name
  is the lowercase form of the item's internal type.

An item reference can be written two ways:

```yaml
# Long form — lets you set an amount (and, for results, a chance)
{ item: "smprpg:iron_ingot", amount: 4 }

# Shorthand — a bare string means amount 1
"minecraft:blaze_powder"
```

**Matching is by item *type*, not exact data.** An enchanted or reforged custom item still counts as its
base type, so ingredients keep working on modified items.

## Reloading after an edit

After changing any recipe file, run in-game or from console:

```
/smprpg recipes reload
```

This reloads **all** recipe files live — no restart. If a recipe is malformed (bad item key, missing
field), it is skipped and a warning is logged; the rest still load. Check the console after a reload if a
recipe doesn't appear.

## Exporting code-defined recipes

Some recipes are (or were) generated from code rather than YAML. To turn them into editable YAML files,
run once:

```
/smprpg recipes export
```

This writes:
- `plugins/SMPRPG/recipes/compression/` — one file per chain/family (e.g. `iron_compression.yml`).
  Each declares the compress direction; the decompress recipe is derived automatically.
- `plugins/SMPRPG/recipes/crafting_table/` — one file per custom crafting recipe (e.g. `adamantium_sword.yml`).
- `plugins/SMPRPG/recipes/enchanting/` — one file per enchantment, all levels (e.g. `abyssal_instinct.yml`).

After exporting, run `/smprpg recipes reload` (or restart) to load them.

## Recipe types

Each file holds one recipe written at the top level (no wrapping id key — the id is the file name). Every
recipe needs a `type:` field that tells the loader which station and shape it is. The examples below show
the full contents of one file.

### `shaped` — crafting table, fixed pattern

`steel_ingot.yml`:

```yaml
type: shaped
pattern:                 # 1–3 rows, 1–3 chars each; a space = empty slot
  - "III"
  - "IBI"
  - "III"
ingredients:
  I: { item: "smprpg:iron_ingot", amount: 4 }   # 4 required in each 'I' slot
  B: "minecraft:blaze_powder"
result: { item: "smprpg:steel_ingot", amount: 1 }
unlocked_by: ["minecraft:iron_ingot"]            # optional; reveals it in the recipe browser
```

Each letter in `pattern` maps to one entry under `ingredients`. Per-slot `amount` lets a single slot
require a stack (e.g. 4 iron in every `I`).

### `shapeless` — crafting table, any arrangement

`mixed_dough.yml`:

```yaml
type: shapeless
ingredients:
  - { item: "minecraft:wheat", amount: 3 }
  - "minecraft:water_bucket"
result: "smprpg:dough"
```

### `smelting` — furnace

`steel_from_ore.yml`:

```yaml
type: smelting
input: "smprpg:raw_steel"
time: 200                # ticks to cook
result: "smprpg:steel_ingot"
```

### `cooking_pot`

`beef_stew.yml`:

```yaml
type: cooking_pot
ingredients:
  - "minecraft:carrot"
  - "minecraft:potato"
  - "smprpg:onion"
  - "minecraft:beef"
time: 200
result: "smprpg:beef_stew"
plating: "minecraft:bowl"     # optional vessel consumed with the craft
skill_xp:                     # optional skill reward
  farming: 50
```

### `cutting_board`

Supports multiple outputs, each with an independent `chance` (1.0 = always). `tool` restricts which tool
tag may process it.

`beef_to_ground_beef.yml`:

```yaml
type: cutting_board
input: "minecraft:beef"
tool: "knives"                # knives | axes | shovels
result:
  - { item: "smprpg:ground_beef", amount: 2, chance: 1.0 }
  - { item: "minecraft:bone_meal", amount: 1, chance: 0.25 }
```

### `freezer`

`ice_from_water.yml`:

```yaml
type: freezer
input: "minecraft:water_bucket"
time: 200
result: "minecraft:ice"
```

### `compression` — two-way stacking chains

One file defines an **entire chain**, **one file per family** — the file name (minus any `_compression`
suffix) is the family name, like the legacy resource families (`iron_compression.yml` → family `iron`).
`tiers:` is an ordered list: the first tier is the base item, and each later tier's `amount` is how many of
the *previous* tier compress into one of it. Each adjacent pair becomes one N→1 compress recipe, and the
reverse decompression is generated automatically.

`iron_compression.yml`:

```yaml
type: compression
tiers:
  - minecraft:iron_ingot                        # base
  - { item: minecraft:iron_block, amount: 9 }   # 9 ingots -> 1 block
  - { item: smprpg:enchanted_iron, amount: 9 }  # 9 blocks -> 1 enchanted iron
  # ...continue the chain as far as it goes
```

### `enchanting`

The reagents to apply a custom enchantment at the enchanting table. No item is produced — the enchantment
is imbued onto the item being enchanted. **One file per enchantment**, with a `levels:` map keyed by level
number; each level has its own `power` (magic level required) and `ingredients`.

`abyssal_instinct.yml`:

```yaml
type: enchanting
enchantment: abyssal_instinct   # the custom enchantment id
levels:
  1:
    power: 5
    ingredients:
      - { item: "smprpg:common_fish_essence", amount: 16 }
      - { item: "minecraft:ink_sac", amount: 32 }
      - { item: "minecraft:lapis_lazuli", amount: 8 }
  2:
    power: 24
    ingredients:
      - { item: "smprpg:uncommon_fish_essence", amount: 16 }
      - { item: "minecraft:ink_sac", amount: 64 }
      - { item: "minecraft:lapis_lazuli", amount: 16 }
```

## Field reference

| Field | Applies to | Meaning |
|---|---|---|
| `type` | all | Recipe/station type (required). |
| `pattern` | shaped | 1–3 strings of 1–3 chars; space = empty. |
| `ingredients` | shaped / shapeless / cooking_pot / enchanting | Item inputs (map of letters for shaped, list otherwise). |
| `input` | smelting / cutting_board / freezer | The single input item. |
| `tiers` | compression | Ordered chain: base item first, each later tier's `amount` = how many of the previous tier compress into one. |
| `enchantment` | enchanting | The custom enchantment id this file's recipes imbue. |
| `levels` | enchanting | Map of level number → `{ power, ingredients }`. |
| `power` | enchanting | Magic level required to apply that enchantment level (per-level). |
| `result` | all | Output item, or list of outputs (cutting_board). |
| `amount` | any item ref | Stack count required (ingredient) or produced (result). Default 1. |
| `chance` | cutting_board result | Probability 0.0–1.0 an output is produced. Default 1.0. |
| `time` | smelting / cooking_pot / freezer | Processing time in ticks. |
| `tool` | cutting_board | Required tool tag (`knives` / `axes` / `shovels`). |
| `plating` | cooking_pot | Optional vessel item consumed with the craft. |
| `skill_xp` | cooking_pot | Optional `skill: amount` rewards on craft. |
| `unlocked_by` | all | Optional items that reveal this recipe in the browser. |

## Quick start: add a new recipe

1. Create a new file `plugins/SMPRPG/recipes/<your_recipe_id>.yml` (optionally inside a subfolder).
2. Write the recipe body at the top level, starting with `type:` and the fields above.
3. Save, then run `/smprpg recipes reload`.
4. Confirm it works in-game (and check console for any warning if it didn't load).
