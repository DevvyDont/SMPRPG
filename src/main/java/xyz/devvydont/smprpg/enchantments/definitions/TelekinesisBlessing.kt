package xyz.devvydont.smprpg.enchantments.definitions

import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import io.papermc.paper.registry.set.RegistryKeySet
import io.papermc.paper.registry.set.RegistrySet
import io.papermc.paper.registry.tag.TagKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ItemType
import org.bukkit.inventory.meta.ItemMeta
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.enchantments.CustomEnchantment
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity
import xyz.devvydont.smprpg.enchantments.ScrollColor
import xyz.devvydont.smprpg.services.ActionBarService
import xyz.devvydont.smprpg.services.DropsService
import xyz.devvydont.smprpg.services.DropsService.DropFlag
import xyz.devvydont.smprpg.services.EnchantmentService
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import java.util.function.Consumer

class TelekinesisBlessing(id: String) : CustomEnchantment(id), Listener {
    override val displayName: Component get() = ComponentUtils.create("Blessing of Telekinesis", NamedTextColor.YELLOW)
    override val description: Component get() = ComponentUtils.merge(
            ComponentUtils.create("Loot is "),
            ComponentUtils.create("magically transported", NamedTextColor.DARK_PURPLE),
            ComponentUtils.create(" straight to your inventory")
        )
    override val enchantColor: TextColor get()   = NamedTextColor.YELLOW
    override val scrollColor: Color get()        = ScrollColor.BLESSING.color
    override val scrollBindingColor: Color get() = Color.fromRGB(195, 143, 255)

    override val itemTypeTag: TagKey<ItemType> get()           = ItemTypeTagKeys.ENCHANTABLE_HEAD_ARMOR
    override val maxLevel: Int get()                           = 1
    override val weight: Int get()                             = EnchantmentRarity.BLESSING.weight
    override val isBlessing: Boolean get()                     = true
    override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.HEAD
    override val skillRequirement: Int get()                   = 20

    override val conflictingEnchantments: RegistryKeySet<Enchantment>
        get() = RegistrySet.keySet(
            RegistryKey.ENCHANTMENT,
            EnchantmentService.KEEPING_BLESSING.typedKey,
            EnchantmentService.MERCY_BLESSING.typedKey,
            EnchantmentService.VOIDSTRIDING_BLESSING.typedKey,
            EnchantmentService.REPLENISHING.typedKey,
            EnchantmentService.IGNORANCE_BLESSING.typedKey
        )

    /*
    * There may be multiple instances where we want to attempt to perform this enchants ability on an item, so pull
    * out the behavior into a method. BlockDropItemEvent happens after ItemSpawnEvent, so we can delay the
    * telekinetic check until the next tick to try and capture it
    *
    * @param item the item to teleport into an owner's inventory if present
    * @return true if successful false otherwise
    */
    private fun performTelekinesis(item: Item): Boolean {
        // Does this item have an owner?

        val ownerID = SMPRPG.getService(DropsService::class.java).getOwner(item)
        if (ownerID == null) return false

        // Is the owner of the item online?
        val owner = Bukkit.getPlayer(ownerID)
        if (owner == null) return false

        // Is the enchantment active?
        val leveledPlayer = SMPRPG.getService(EntityService::class.java).getPlayerInstance(owner)
        if (!isEnchantmentActive(owner.equipment.itemInMainHand, leveledPlayer)) return false

        // Is this item marked with the "loot" tag?
        val flag = SMPRPG.getService(DropsService::class.java).getFlag(item)
        if (flag != DropFlag.LOOT) return false

        // Do we have telekinesis?
        val helmet = owner.inventory.helmet
        if (helmet == null) return false

        if (!helmet.containsEnchantment(enchantment)) return false

        // Do we have an empty spot in our inventory?
        if (owner.inventory.firstEmpty() == -1) {
            SMPRPG.getService(ActionBarService::class.java)
                .addActionBarComponent(
                    owner,
                    ActionBarService.ActionBarSource.MISC,
                    ComponentUtils.create("FULL INVENTORY!", NamedTextColor.RED),
                    2
                )
            owner.playSound(owner.location, Sound.BLOCK_CHEST_OPEN, .25f, 2f)
            return false
        }

        // We have telekinesis and this drop belongs to us. Attempt to add it
        val drop = item.itemStack
        val overflow: MutableMap<Int?, ItemStack?> = owner.inventory.addItem(drop)
        owner.world.playSound(owner.location, Sound.ENTITY_ITEM_PICKUP, .25f, 1.75f)

        // If the overflow map is empty, then we successfully transported items!
        if (overflow.isEmpty()) return true

        // We have overflow items, go ahead and spawn the items back into the world but with an alternative tag so that
        // we ignore this item when it drops.
        for (entry in overflow.entries) {
            entry.value!!.editMeta(Consumer { meta: ItemMeta? ->
                SMPRPG.getService(DropsService::class.java).setFlag(meta!!, DropFlag.TELEKINESIS_FAIL)
            })
            owner.world.dropItemNaturally(owner.eyeLocation, entry.value!!)
        }

        return true
    }

    /*
     * There may be multiple instances where we want to attempt to perform this enchants ability on an item, so pull
     * out the behavior into a method. BlockDropItemEvent happens after ItemSpawnEvent, so we can delay the
     * telekinetic check until the next tick to try and capture it
     *
     * @param item the item to teleport into an owner's inventory if present
     * @return true if successful false otherwise
     */
    private fun performTelekinesis(item: ItemStack): Boolean {
        // Does this item have an owner?

        val ownerID = SMPRPG.getService(DropsService::class.java).getOwner(item)
        if (ownerID == null) return false

        // Is the owner of the item online?
        val owner = Bukkit.getPlayer(ownerID)
        if (owner == null) return false

        // Is this item marked with the "loot" tag?
        val flag = SMPRPG.getService(DropsService::class.java).getFlag(item)
        if (flag != DropFlag.LOOT) return false

        // Do we have telekinesis?
        val helmet = owner.inventory.helmet
        if (helmet == null) return false

        if (!helmet.containsEnchantment(enchantment)) return false

        // Do we have an empty spot in our inventory?
        if (owner.inventory.firstEmpty() == -1) {
            SMPRPG.getService(ActionBarService::class.java)
                .addActionBarComponent(
                    owner,
                    ActionBarService.ActionBarSource.MISC,
                    ComponentUtils.create("FULL INVENTORY!", NamedTextColor.RED),
                    2
                )
            owner.playSound(owner.location, Sound.BLOCK_CHEST_OPEN, .25f, 2f)
            return false
        }

        // We have telekinesis and this drop belongs to us. Attempt to add it
        val drop = item.clone()
        SMPRPG.getService(DropsService::class.java).removeAllTags(drop)
        val overflow: MutableMap<Int?, ItemStack?> = owner.inventory.addItem(drop)
        owner.world.playSound(owner.location, Sound.ENTITY_ITEM_PICKUP, .25f, 1.75f)

        // If the overflow map is empty, then we successfully transported items!
        if (overflow.isEmpty()) return true

        // We have overflow items, go ahead and spawn the items back into the world but with an alternative tag so that
        // we ignore this item when it drops.
        for (entry in overflow.entries) {
            entry.value!!.editMeta(Consumer { meta: ItemMeta? ->
                SMPRPG.getService(DropsService::class.java)
                    .setFlag(meta!!, DropFlag.TELEKINESIS_FAIL)
            })
            owner.world.dropItemNaturally(owner.eyeLocation, entry.value!!)
        }

        return true
    }

    /*
     * When an item is spawned into the world, attempt to perform telekinesis on it on the next available tick.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onItemSpawnWhileWearing(event: EntityDeathEvent) {
        // Run through every drop this entity is going to drop. If we successfully transport the item directly to
        // a player inventory, then remove it from the drops.

        for (item in event.drops.stream().toList()) {
            // Attempt telekinesis, if we were successful then remove the drop.

            val success = performTelekinesis(item)
            if (success) event.drops.remove(item)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onBlockDropItemWhileWearing(event: BlockDropItemEvent) {
        // Run through every drop this block is going to drop. If we successfully transport the item directly to
        // a player inventory, then remove it from the drops.

        for (item in event.items.stream().toList()) {
            // Attempt telekinesis, if we were successful then remove the drop.

            var success = performTelekinesis(item.itemStack)
            // Failed on the item stack itself. Maybe try the normal item?
            if (!success) success = performTelekinesis(item)

            if (success) {
                event.items.remove(item)
                item.remove()
            }
        }
    }
}
