package xyz.devvydont.smprpg.items.blueprints.resources

import org.bukkit.Material
import xyz.devvydont.smprpg.items.base.VanillaItemBlueprint
import xyz.devvydont.smprpg.services.ItemService

/**
 * A VanillaItemBlueprint registered for every vanilla Material that appears as a member of a compression
 * family. The compression chain data it used to carry now lives in the data-driven recipe registry
 * (the recipes compression folder); this class remains only so those material registrations keep resolving.
 */
class VanillaCompressibleBlueprint(itemService: ItemService, material: Material) :
    VanillaItemBlueprint(itemService, material)
