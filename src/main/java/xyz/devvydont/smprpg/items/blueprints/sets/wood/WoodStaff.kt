package xyz.devvydont.smprpg.items.blueprints.sets.wood

import io.papermc.paper.registry.keys.SoundEventKeys
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry
import xyz.devvydont.smprpg.items.base.CustomAttributeItem
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.items.interfaces.ICantCrit
import xyz.devvydont.smprpg.items.interfaces.IMageBeam
import xyz.devvydont.smprpg.items.interfaces.IRepairable
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.items.ToolStats

class WoodStaff(itemService: ItemService, type: CustomItemType) : CustomAttributeItem(itemService, type),
    IBreakableEquipment, ICantCrit, IMageBeam, IRepairable {

    override val itemClassification: ItemClassification get() = ItemClassification.STAFF
    override val repairMaterial : MutableCollection<ItemStack> = mutableListOf(itemService.getCustomItem(Material.OAK_PLANKS), itemService.getCustomItem(Material.BIRCH_PLANKS), itemService.getCustomItem(Material.SPRUCE_PLANKS),
        itemService.getCustomItem(Material.JUNGLE_PLANKS), itemService.getCustomItem(Material.DARK_OAK_PLANKS), itemService.getCustomItem(Material.ACACIA_PLANKS),
        itemService.getCustomItem(Material.CRIMSON_PLANKS), itemService.getCustomItem(Material.WARPED_PLANKS), itemService.getCustomItem(Material.MANGROVE_PLANKS),
        itemService.getCustomItem(Material.CHERRY_PLANKS), itemService.getCustomItem(Material.PALE_OAK_PLANKS), itemService.getCustomItem(Material.BAMBOO_PLANKS))

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.STRENGTH, 7.0),
            AdditiveAttributeEntry(AttributeWrapper.INTELLIGENCE, 10.0),
            AdditiveAttributeEntry(AttributeWrapper.ARCANE_RATING, 10.0),
            MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, -.5)
        )
    }

    override fun getPowerRating(): Int { return ToolStats.WOOD.power }

    override fun getActiveSlot(): EquipmentSlotGroup { return EquipmentSlotGroup.MAINHAND }

    override fun getMaxDurability(): Int { return ToolStats.WOOD.durability }

    override val manaCost: Int get() = 5
    override val hitParticle: Particle get() = Particle.ENCHANTED_HIT
    override val missParticle: Particle get() = Particle.CRIT
    override val particleRange: Int get() = 8
    override val particleDensity: Int get() = particleRange * 2

    override fun updateItemData(itemStack: ItemStack) {
        super.updateItemData(itemStack)
        IMageBeam.updateStaffComponents(itemStack, particleRange, 0.1f, SoundEventKeys.ENTITY_PLAYER_ATTACK_SWEEP, SoundEventKeys.BLOCK_TRIAL_SPAWNER_EJECT_ITEM)
    }

}
