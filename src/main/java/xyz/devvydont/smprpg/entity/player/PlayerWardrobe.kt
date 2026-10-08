package xyz.devvydont.smprpg.entity.player

import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.EntityEquipment
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataHolder
import xyz.devvydont.smprpg.util.persistence.KeyStore
import xyz.devvydont.smprpg.util.persistence.PDCAdapters

/**
 * Represents a collection of player equipment sets. These sets can be swapped to and from, and even modified
 * if the player has the space for it.
 */
class PlayerWardrobe {

    /**
     * The actual stored sets in this wardrobe. The length of this list is not guaranteed to be equal to the capacity,
     * as the capacity is just a player limit. This list is an actual representation of what is already stored.
     */
    private val _sets = mutableMapOf<Int, EquipmentSet>()

    /**
     * The maximum allowed sets this wardrobe can store. This can be increased by various means for additional storage.
     */
    var maxCapacity: Int = 3
    var currentlyEquipped: Int = 0

    /**
     * Queries for the set that is at the specified index. If you pass in an index that the player does not have unlocked
     * (or out of bounds) you will be given a null result.
     */
    fun query(index: Int): EquipmentSet? {
        return _sets[index]
    }

    fun set(index: Int, set: EquipmentSet) {
        if (index < 0 || index >= maxCapacity)
            return
        _sets[index] = set
    }

    fun equip(entity: LivingEntity, index: Int): Boolean {

        // Do nothing if they haven't unlocked the slot yet.
        if (index >= maxCapacity)
            return false

        // Do nothing if the clicked index is already equipped. This will just cause problems.
        if (index == this.currentlyEquipped)
            return false

        // All we need to do, is swap the equipment the player is wearing, and the equipment in the desired slot.
        var newSet = this.query(index)
        if (newSet == null){
            val clean = EquipmentSet()
            this.set(index, clean)
            newSet = clean
        }

        this.set(this.currentlyEquipped, EquipmentSet(entity.equipment))
        newSet.equip(entity)
        this.currentlyEquipped = index
        entity.world.playSound(entity.location, Sound.ITEM_ARMOR_EQUIP_GENERIC, 1f, 1.5f)
        return true
    }

    /**
     * Returns a copy of all the sets present in this wardrobe. Any modifications made to the result will
     * affect the player's wardrobe.
     */
    fun all(): Map<Int, EquipmentSet> {
        return _sets.toMap()
    }

    /**
     * Saves this wardrobe configuration to a valid PDC holder.
     * When making modifications, it is important to make sure that changes are saved.
     */
    fun save(target: PersistentDataHolder) {
        target.persistentDataContainer.set(KeyStore.PLAYER_WARDROBE, PDCAdapters.WARDROBE_ADAPTER, this)
    }

}

/**
 * An equipment set is defined as a full set of armor. We don't want to deal with the headache of nullability here
 * due to how annoying serialization can be, so we define an "empty" slot as being an item stack of air.
 */
class EquipmentSet {

    constructor(helmet: ItemStack, chestplate: ItemStack, leggings: ItemStack, boots: ItemStack) {
        this.helmet = helmet
        this.chestplate = chestplate
        this.leggings = leggings
        this.boots = boots
    }

    constructor() {
        this.helmet = AIR
        this.chestplate = AIR
        this.leggings = AIR
        this.boots = AIR
    }

    /**
     * This constructor will clone an entity's equipment, but default to air if the entire equipment resolves to null.
     */
    constructor(equipment: EntityEquipment?) : this() {
        if (equipment == null)
            return

        this.helmet = equipment.helmet ?: AIR
        this.chestplate = equipment.chestplate ?: AIR
        this.leggings = equipment.leggings ?: AIR
        this.boots = equipment.boots ?: AIR
    }

    var helmet: ItemStack = AIR
    var chestplate: ItemStack = AIR
    var leggings: ItemStack = AIR
    var boots: ItemStack = AIR

    companion object {
        val AIR: ItemStack = ItemStack(Material.AIR)
    }

    /**
     * Equips the items present in this set to an entity, if they can wear armor.
     */
    fun equip(living: LivingEntity) {
        val equipment = living.equipment
        if (equipment == null)
            return
        equipment.setHelmet(helmet)
        equipment.setChestplate(chestplate)
        equipment.setLeggings(leggings)
        equipment.setBoots(boots)
    }

}