package xyz.devvydont.smprpg.entity.creatures

import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.persistence.PersistentDataType
import xyz.devvydont.smprpg.entity.CustomEntityType
import xyz.devvydont.smprpg.entity.MobType
import xyz.devvydont.smprpg.entity.base.CustomEntityInstance
import xyz.devvydont.smprpg.entity.slayer.illager.IllagerWarlockParent
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.items.ChancedItemDrop
import xyz.devvydont.smprpg.util.items.LootDrop
import xyz.devvydont.smprpg.util.persistence.KeyStore
import java.util.List

class WoodlandExile<T : LivingEntity?> : CustomEntityInstance<T?> {
    constructor(entity: Entity?, entityType: CustomEntityType?) : super(entity, entityType)

    constructor(entity: T?, entityType: CustomEntityType?) : super(entity, entityType)

    override fun setup() {
        mobTypes.add(MobType.HUMANOID)
        mobTypes.add(MobType.ILLAGER)

        super.setup()
        removeEquipment()
        setNoDropEquipment()

        if (_entity!!.equipment != null) if (getEntityType() == CustomEntityType.WOODLAND_BERSERKER)
            _entity.equipment!!.setItemInMainHand(getAttributelessItem(Material.IRON_AXE))
        else
            _entity.equipment!!.setItemInMainHand(getAttributelessItem(Material.CROSSBOW))

        _entity.persistentDataContainer.set(
            KeyStore.SLAYER_SPAWN_TYPE,
            PersistentDataType.STRING,
            IllagerWarlockParent.Companion.SPAWN_MOB_FLAG
        )
    }

    override fun getItemDrops(): Collection<LootDrop>? {
        return listOf(
            ChancedItemDrop(ItemService.Companion.generate(CustomItemType.POTATO_CHIP), 2, this),
            ChancedItemDrop(ItemService.Companion.generate(Material.EMERALD), 5, this),
            ChancedItemDrop(ItemService.Companion.generate(Material.EMERALD_BLOCK), 90, this),
            ChancedItemDrop(ItemService.Companion.generate(CustomItemType.ENCHANTED_EMERALD), 1250, this),
            ChancedItemDrop(ItemService.Companion.generate(CustomItemType.ENCHANTED_EMERALD_BLOCK), 80000, this),
            ChancedItemDrop(ItemService.Companion.generate(CustomItemType.STRENGTH_CHARM), 750, this),
            ChancedItemDrop(ItemService.Companion.generate(CustomItemType.LUCKY_CHARM), 750, this),
            ChancedItemDrop(ItemService.Companion.generate(CustomItemType.SPEED_CHARM), 850, this),
            ChancedItemDrop(ItemService.Companion.generate(CustomItemType.EXILED_CROSSBOW), 700, this),
            ChancedItemDrop(ItemService.Companion.generate(CustomItemType.EXILED_AXE), 700, this)
        )
    }
}