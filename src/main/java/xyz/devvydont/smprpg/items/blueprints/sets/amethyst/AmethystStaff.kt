package xyz.devvydont.smprpg.items.blueprints.sets.amethyst

import io.papermc.paper.registry.keys.SoundEventKeys
import org.bukkit.Particle
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.ability.Ability
import xyz.devvydont.smprpg.ability.AbilityActivationMethod
import xyz.devvydont.smprpg.ability.AbilityCost
import xyz.devvydont.smprpg.ability.AbilityCost.Companion.of
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry
import xyz.devvydont.smprpg.items.base.CustomAttributeItem
import xyz.devvydont.smprpg.items.interfaces.*
import xyz.devvydont.smprpg.items.interfaces.IAbilityCaster.AbilityEntry
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.items.ToolStats
import xyz.devvydont.smprpg.util.time.TickTime

class AmethystStaff(itemService: ItemService, type: CustomItemType) : CustomAttributeItem(itemService, type),
    IBreakableEquipment, ICantCrit, IMageBeam, IAbilityCaster, IRepairable {

    override val itemClassification: ItemClassification get() = ItemClassification.STAFF
    override val repairMaterial: MutableCollection<ItemStack> get() = mutableListOf(itemService.getCustomItem(CustomItemType.ENCHANTED_AMETHYST))

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.STRENGTH, 15.0),
            AdditiveAttributeEntry(AttributeWrapper.INTELLIGENCE, 25.0),
            AdditiveAttributeEntry(AttributeWrapper.ARCANE_RATING, 15.0),
            MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, -.5)
        )
    }

    override fun getPowerRating(): Int { return 12 }

    override fun getActiveSlot(): EquipmentSlotGroup { return EquipmentSlotGroup.MAINHAND }

    override fun getMaxDurability(): Int { return ToolStats.IRON.durability }

    override val manaCost: Int get() = 10
    override val hitParticle: Particle get() = Particle.END_ROD
    override val missParticle: Particle get() = Particle.CRIT
    override val particleRange: Int get() = 10
    override val particleDensity: Int get() = particleRange * 2

    override fun getAbilities(item: ItemStack): Collection<AbilityEntry> {
        return mutableListOf(
            AbilityEntry(
                Ability.SHARD_STRIKE,
                AbilityActivationMethod.RIGHT_CLICK,
                of(AbilityCost.Resource.MANA, 50)
            )
        )
    }

    override fun getCooldown(item: ItemStack): Long { return TickTime.seconds(1) }

    override fun updateItemData(itemStack: ItemStack) {
        super.updateItemData(itemStack)
        IMageBeam.updateStaffComponents(itemStack, particleRange, 0.15f, SoundEventKeys.BLOCK_AMETHYST_BLOCK_RESONATE, SoundEventKeys.BLOCK_TRIAL_SPAWNER_EJECT_ITEM)
    }
}
