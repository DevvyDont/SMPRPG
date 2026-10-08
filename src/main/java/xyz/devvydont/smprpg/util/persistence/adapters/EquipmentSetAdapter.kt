package xyz.devvydont.smprpg.util.persistence.adapters

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import xyz.devvydont.smprpg.entity.player.EquipmentSet

val INVALID_KEY = NamespacedKey("smprpg", "air")
val HELMET_KEY = NamespacedKey("smprpg", "helmet")
val CHESTPLATE_KEY = NamespacedKey("smprpg", "chestplate")
val LEGGINGS_KEY = NamespacedKey("smprpg", "leggings")
val BOOTS_KEY = NamespacedKey("smprpg", "boots")

class EquipmentSetAdapter : PersistentDataType<PersistentDataContainer, EquipmentSet> {

    override fun getPrimitiveType(): Class<PersistentDataContainer> {
        return PersistentDataContainer::class.java
    }

    override fun getComplexType(): Class<EquipmentSet> {
        return EquipmentSet::class.java
    }

    override fun toPrimitive(
        complex: EquipmentSet,
        context: PersistentDataAdapterContext
    ): PersistentDataContainer {
        val container = context.newPersistentDataContainer()
        container.set(HELMET_KEY, PersistentDataType.BYTE_ARRAY, serializeItem(complex.helmet))
        container.set(CHESTPLATE_KEY, PersistentDataType.BYTE_ARRAY, serializeItem(complex.chestplate))
        container.set(LEGGINGS_KEY, PersistentDataType.BYTE_ARRAY, serializeItem(complex.leggings))
        container.set(BOOTS_KEY, PersistentDataType.BYTE_ARRAY, serializeItem(complex.boots))
        return container
    }

    override fun fromPrimitive(
        primitive: PersistentDataContainer,
        context: PersistentDataAdapterContext
    ): EquipmentSet {
        return EquipmentSet(
            resolveItemFromBytes(primitive.get(HELMET_KEY, PersistentDataType.BYTE_ARRAY)),
            resolveItemFromBytes(primitive.get(CHESTPLATE_KEY, PersistentDataType.BYTE_ARRAY)),
            resolveItemFromBytes(primitive.get(LEGGINGS_KEY, PersistentDataType.BYTE_ARRAY)),
            resolveItemFromBytes(primitive.get(BOOTS_KEY, PersistentDataType.BYTE_ARRAY))
        )
    }

    /**
     * We cannot serialize air, but we need to store "empty" items. To do this, we are going to tag an item
     * with something so we can judge after deserialization if it should be converted to air.
     */
    private fun getEmptyItem(): ItemStack {
        val item = ItemStack.of(Material.BARRIER)
        item.editPersistentDataContainer { pdc -> pdc.set(INVALID_KEY, PersistentDataType.BOOLEAN, true) }
        return item
    }

    private fun isEmptyItem(item: ItemStack): Boolean {
        return item.persistentDataContainer.has(INVALID_KEY)
    }

    /**
     * Resolves an item from raw bytes. If this fails, we fall back to AIR.
     * This may be considered dangerous, as if the serialization process changes then we could just lose the item.
     */
    private fun resolveItemFromBytes(data: ByteArray?): ItemStack {
        if (data == null)
            return ItemStack(Material.AIR)

        val item = ItemStack.deserializeBytes(data)
        if (isEmptyItem(item))
            return ItemStack(Material.AIR)

        return item
    }

    /**
     * Serialize an item. This mainly serves as a purpose of injection to make air serialize as our "invalid" item
     * instead of attempting to serialize air as that is not supported.
     */
    private fun serializeItem(item: ItemStack): ByteArray {
        if (item.type == Material.AIR)
            return getEmptyItem().serializeAsBytes()
        return item.serializeAsBytes()
    }
}