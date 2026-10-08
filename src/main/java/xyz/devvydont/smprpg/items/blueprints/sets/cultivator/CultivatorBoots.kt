package xyz.devvydont.smprpg.items.blueprints.sets.cultivator

import net.kyori.adventure.key.Key
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.interfaces.IEquippableAssetOverride
import xyz.devvydont.smprpg.services.ItemService

class CultivatorBoots(itemService: ItemService, type: CustomItemType) : CultivatorArmorSet(itemService, type), IEquippableAssetOverride {

    override val itemClassification: ItemClassification get() = ItemClassification.BOOTS

    override fun getMaxDurability(): Int { return 256 }

    override fun getAssetId(): Key { return assetKey }
}
