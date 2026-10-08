package xyz.devvydont.smprpg.items.blueprints.sets.mithril

import io.papermc.paper.registry.keys.SoundEventKeys
import org.bukkit.Particle
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.ability.Ability
import xyz.devvydont.smprpg.ability.AbilityActivationMethod
import xyz.devvydont.smprpg.ability.AbilityCost
import xyz.devvydont.smprpg.ability.AbilityCost.Companion.of
import xyz.devvydont.smprpg.ability.handlers.SpreadShotAbilityHandler
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
import xyz.devvydont.smprpg.skills.SkillType
import xyz.devvydont.smprpg.util.items.ToolStats

class MithrilStaff(itemService: ItemService, type: CustomItemType) : CustomAttributeItem(itemService, type),
    IBreakableEquipment, ICantCrit, IMageBeam, IRepairable, ISkillRequirement, IAbilityCaster {

    override val itemClassification: ItemClassification get() = ItemClassification.STAFF
    override val repairMaterial: MutableCollection<ItemStack> get() = mutableListOf(itemService.getCustomItem(CustomItemType.MITHRIL_INGOT))
    override val skillRequirements: MutableMap<SkillType, Int> get() = mutableMapOf(
        Pair(SkillType.COMBAT, ToolStats.MITHRIL.skillReqLevel),
        Pair(SkillType.MAGIC, ToolStats.MITHRIL.skillReqLevel),
    )

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.STRENGTH, 25.0),
            AdditiveAttributeEntry(AttributeWrapper.INTELLIGENCE, 60.0),
            AdditiveAttributeEntry(AttributeWrapper.ARCANE_RATING, 20.0),
            MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, -.5)
        )
    }

    override fun getPowerRating(): Int { return ToolStats.MITHRIL.power }

    override fun getActiveSlot(): EquipmentSlotGroup { return EquipmentSlotGroup.MAINHAND }

    override fun getMaxDurability(): Int { return ToolStats.MITHRIL.durability }

    override val manaCost: Int get() = 20
    override val hitParticle: Particle get() = Particle.END_ROD
    override val missParticle: Particle get() = Particle.ENCHANTED_HIT
    override val particleRange: Int get() = 11
    override val particleDensity: Int get() = particleRange * 2

    override fun updateItemData(itemStack: ItemStack) {
        super.updateItemData(itemStack)
        IMageBeam.updateStaffComponents(itemStack, particleRange, 0.2f, SoundEventKeys.ENTITY_BLAZE_SHOOT, SoundEventKeys.ENTITY_EXPERIENCE_ORB_PICKUP)
    }

    override fun getAbilities(item: ItemStack): Collection<AbilityEntry> {
        return mutableListOf(
            AbilityEntry(
                Ability.SPREAD_SHOT,
                AbilityActivationMethod.RIGHT_CLICK,
                of(AbilityCost.Resource.MANA, 100)
            )
        )
    }

    override fun getCooldown(item: ItemStack): Long { return SpreadShotAbilityHandler.COOLDOWN }
}
