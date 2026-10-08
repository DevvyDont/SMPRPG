package xyz.devvydont.smprpg.entity.creatures

import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.Spider
import org.bukkit.persistence.PersistentDataType
import xyz.devvydont.smprpg.entity.CustomEntityType
import xyz.devvydont.smprpg.entity.MobType
import xyz.devvydont.smprpg.entity.base.CustomEntityInstance
import xyz.devvydont.smprpg.entity.slayer.illager.IllagerWarlockParent
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.services.ItemService.Companion.generate
import xyz.devvydont.smprpg.util.items.ChancedItemDrop
import xyz.devvydont.smprpg.util.items.LootDrop
import xyz.devvydont.smprpg.util.persistence.KeyStore
import java.util.List

class MansionSpider : CustomEntityInstance<Spider?> {
    constructor(entity: Entity?, entityType: CustomEntityType?) : super(entity, entityType)

    constructor(entity: Spider?, entityType: CustomEntityType?) : super(entity, entityType)

    override fun getItemDrops(): Collection<LootDrop>? {
        return listOf(
            ChancedItemDrop(generate(CustomItemType.COTTON_CANDY), 2, this),
            ChancedItemDrop(generate(Material.STRING), 2, this),
            ChancedItemDrop(generate(Material.SPIDER_EYE), 2, this),
            ChancedItemDrop(generate(CustomItemType.PREMIUM_STRING), 110, this),
            ChancedItemDrop(generate(CustomItemType.PREMIUM_SPIDER_EYE), 70, this),
            ChancedItemDrop(generate(CustomItemType.ENCHANTED_STRING), 1100, this),
            ChancedItemDrop(generate(CustomItemType.ENCHANTED_SPIDER_EYE), 700, this)
        )
    }

    override fun setup() {
        mobTypes.add(MobType.ARTHROPOD)

        super.setup()

        _entity!!.persistentDataContainer.set(
            KeyStore.SLAYER_SPAWN_TYPE,
            PersistentDataType.STRING,
            IllagerWarlockParent.SPAWN_MOB_FLAG
        )
    }
}
