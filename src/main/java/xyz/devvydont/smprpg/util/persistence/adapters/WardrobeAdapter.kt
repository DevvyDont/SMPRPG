package xyz.devvydont.smprpg.util.persistence.adapters

import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.entity.player.PlayerWardrobe
import xyz.devvydont.smprpg.util.persistence.PDCAdapters

val CAPACITY_KEY = NamespacedKey("smprpg", "wardrobe_capacity")
val CURRENTLY_EQUIPPED_KEY = NamespacedKey("smprpg", "equipped")

class WardrobeAdapter : PersistentDataType<PersistentDataContainer, PlayerWardrobe> {

    override fun getPrimitiveType(): Class<PersistentDataContainer> {
        return PersistentDataContainer::class.java
    }

    override fun getComplexType(): Class<PlayerWardrobe> {
        return PlayerWardrobe::class.java
    }

    override fun toPrimitive(
        complex: PlayerWardrobe,
        context: PersistentDataAdapterContext
    ): PersistentDataContainer {
        val container = context.newPersistentDataContainer()
        container.set(CAPACITY_KEY, PersistentDataType.INTEGER, complex.maxCapacity)
        container.set(CURRENTLY_EQUIPPED_KEY, PersistentDataType.INTEGER, complex.currentlyEquipped)
        for ((index, set) in complex.all())
            container.set(NamespacedKey("smprpg", "$index"), PDCAdapters.EQUIPMENT_SET_ADAPTER, set)
        return container
    }

    override fun fromPrimitive(
        primitive: PersistentDataContainer,
        context: PersistentDataAdapterContext
    ): PlayerWardrobe {

        val wardrobe = PlayerWardrobe()
        wardrobe.maxCapacity = primitive.getOrDefault(CAPACITY_KEY, PersistentDataType.INTEGER, 3)
        wardrobe.currentlyEquipped = primitive.getOrDefault(CURRENTLY_EQUIPPED_KEY, PersistentDataType.INTEGER, 0)

        // Loop through all the keys in the PDC. It should map to primitive equipment set (ItemStack bytes).
        for (key in primitive.keys) {

            if (key == CAPACITY_KEY || key == CURRENTLY_EQUIPPED_KEY)
                continue

            val index = key.value().toIntOrNull()
            if (index == null) {
                SMPRPG.plugin.logger.warning("Invalid Wardrobe adapter key: $key")
                continue
            }

            val equipment = primitive.get(key, PDCAdapters.EQUIPMENT_SET_ADAPTER)
            if (equipment == null) {
                SMPRPG.plugin.logger.warning("Wardrobe adapter key does not resolve to equipment set: $key")
                continue
            }
            wardrobe.set(index, equipment)
        }
        return wardrobe
    }
}