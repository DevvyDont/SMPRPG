package xyz.devvydont.smprpg.market.auction

import org.bukkit.Material
import xyz.devvydont.smprpg.items.ItemClassification

enum class AuctionCategory(val displayName: String, val icon: Material) {
    WEAPONS("Weapons", Material.DIAMOND_SWORD),
    ARMOR("Armor", Material.DIAMOND_CHESTPLATE),
    TOOLS("Tools", Material.DIAMOND_PICKAXE),
    BOWS("Bows", Material.BOW),
    ACCESSORIES("Accessories", Material.TOTEM_OF_UNDYING),
    CONSUMABLES("Consumables", Material.GOLDEN_APPLE),
    MATERIALS("Materials", Material.RAW_IRON),
    BLOCKS("Blocks", Material.STONE),
    MISC("Misc", Material.PAPER);

    companion object {

        fun fromClassification(classification: ItemClassification): AuctionCategory {
            return when (classification) {
                ItemClassification.WEAPON,
                ItemClassification.SWORD,
                ItemClassification.SPEAR,
                ItemClassification.STAFF,
                ItemClassification.TRIDENT,
                ItemClassification.MACE,
                ItemClassification.KNIFE,
                ItemClassification.AXE -> WEAPONS

                ItemClassification.BOW,
                ItemClassification.SHORTBOW,
                ItemClassification.CROSSBOW -> BOWS

                ItemClassification.HELMET,
                ItemClassification.CHESTPLATE,
                ItemClassification.LEGGINGS,
                ItemClassification.BOOTS -> ARMOR

                ItemClassification.PICKAXE,
                ItemClassification.DRILL,
                ItemClassification.SHOVEL,
                ItemClassification.HOE,
                ItemClassification.HATCHET,
                ItemClassification.SHEARS,
                ItemClassification.TOOL,
                ItemClassification.ROD -> TOOLS

                ItemClassification.CHARM,
                ItemClassification.TOME,
                ItemClassification.EQUIPMENT -> ACCESSORIES

                ItemClassification.CONSUMABLE -> CONSUMABLES
                ItemClassification.MATERIAL,
                ItemClassification.REFORGE_STONE,
                ItemClassification.AUGMENT_STONE,
                ItemClassification.SPELL-> MATERIALS

                ItemClassification.BLOCK,
                ItemClassification.STORAGE -> BLOCKS

                ItemClassification.ITEM -> MISC
            }
        }
    }
}
