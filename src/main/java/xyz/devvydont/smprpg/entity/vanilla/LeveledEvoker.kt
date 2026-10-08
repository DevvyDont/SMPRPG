package xyz.devvydont.smprpg.entity.vanilla

import org.bukkit.entity.Evoker
import org.bukkit.persistence.PersistentDataType
import xyz.devvydont.smprpg.entity.MobType
import xyz.devvydont.smprpg.entity.base.VanillaEntity
import xyz.devvydont.smprpg.entity.slayer.illager.IllagerWarlockParent
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.blueprints.resources.scrolls.DynamicEnchantingScroll.Companion.getScrollWithEnchantment
import xyz.devvydont.smprpg.services.EnchantmentService
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.items.ChancedItemDrop
import xyz.devvydont.smprpg.util.items.LootDrop
import xyz.devvydont.smprpg.util.persistence.KeyStore

class LeveledEvoker(entity: Evoker) : VanillaEntity<Evoker>(entity) {

    override fun getItemDrops(): Collection<LootDrop> {
        return listOf(
            ChancedItemDrop(ItemService.generate(CustomItemType.BOOK_OF_SHADOWS), 50, this)
        )
    }

    override fun setup() {
        mobTypes.add(MobType.HUMANOID)
        mobTypes.add(MobType.ILLAGER)

        super.setup()

        _entity!!.persistentDataContainer.set(
            KeyStore.SLAYER_SPAWN_TYPE,
            PersistentDataType.STRING,
            IllagerWarlockParent.SPAWN_MOB_FLAG
        )
    }
}
