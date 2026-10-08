package xyz.devvydont.smprpg.entity.farming

import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.entity.CustomEntityType
import xyz.devvydont.smprpg.entity.MobType
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.services.ItemService.Companion.generate
import xyz.devvydont.smprpg.util.items.ChancedItemDrop
import xyz.devvydont.smprpg.util.items.LootDrop
import xyz.devvydont.smprpg.util.items.QuantityLootDrop
import java.util.List

class Ogreling
/**
 * An unsafe constructor to use to allow dynamic creation of custom entities.
 * This is specifically used as a casting hack for the CustomEntityType enum in order to dynamically create
 * entities.
 *
 * @param entity     The entity that should map the T type parameter.
 * @param entityType The entity type.
 */
    (entity: LivingEntity, entityType: CustomEntityType) : CropCritter<LivingEntity>(entity, entityType) {
    override fun setup() {
        mobTypes.add(MobType.CROP_CRITTER)
        mobTypes.add(MobType.FAE)
        mobTypes.add(MobType.HUMANOID)

        super.setup()
    }

    override fun updateAttributes() {
        super.updateAttributes()
        updateBaseAttribute(AttributeWrapper.SCALE, 0.5)
    }

    override fun getItemDrops(): MutableCollection<LootDrop> {
        return mutableListOf(
            QuantityLootDrop(generate(CustomItemType.PREMIUM_ONION), 8, 16, this),
            ChancedItemDrop(generate(CustomItemType.ENCHANTED_ONION), 4, this),
            ChancedItemDrop(generate(CustomItemType.ONION_SKIN), 10, this),
            ChancedItemDrop(generate(CustomItemType.SUPER_SOIL), 10, this)
        )
    }
}
