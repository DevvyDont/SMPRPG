package xyz.devvydont.smprpg.items.blueprints.sets.inferno

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.UseCooldown
import io.papermc.paper.registry.keys.SoundEventKeys
import net.kyori.adventure.key.Key
import org.bukkit.NamespacedKey
import org.bukkit.Particle
import org.bukkit.event.Listener
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG
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
import xyz.devvydont.smprpg.skills.SkillType
import xyz.devvydont.smprpg.util.time.TickTime

class InfernoStaff(itemService: ItemService, type: CustomItemType) : CustomAttributeItem(itemService, type),
    IBreakableEquipment, ICantCrit, IMageBeam, IAbilityCaster, IModelOverridden, ISkillRequirement,
    Listener {

    override val itemClassification: ItemClassification get() = ItemClassification.STAFF
    override val skillRequirements: MutableMap<SkillType, Int> get() = mutableMapOf(
        Pair(SkillType.COMBAT, 35),
        Pair(SkillType.MAGIC, 35),
    )

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.STRENGTH, 125.0),
            AdditiveAttributeEntry(AttributeWrapper.INTELLIGENCE, 200.0),
            AdditiveAttributeEntry(AttributeWrapper.ARCANE_RATING, 30.0),
            MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, -.5)
        )
    }

    override fun getPowerRating(): Int { return InfernoArmorSet.POWER }

    override fun getActiveSlot(): EquipmentSlotGroup { return EquipmentSlotGroup.MAINHAND }

    override fun getMaxDurability(): Int { return 50000 }

    override val manaCost : Int get() = 35
    override val hitParticle: Particle get() = Particle.FLAME
    override val missParticle: Particle get() = Particle.SMOKE
    override val particleRange: Int get() = 13
    override val particleDensity: Int get() = particleRange * 2

    override fun updateItemData(itemStack: ItemStack) {
        super.updateItemData(itemStack)
        IMageBeam.Companion.updateStaffComponents(itemStack, particleRange, 0.2f, SoundEventKeys.ENTITY_BLAZE_SHOOT, SoundEventKeys.ENTITY_EXPERIENCE_ORB_PICKUP)
        itemStack.setData(DataComponentTypes.USE_COOLDOWN, UseCooldown.useCooldown(0.0001f).cooldownGroup(COOLDOWN_GROUP))
    }

    override fun getAbilities(item: ItemStack): MutableCollection<AbilityEntry> {
        return mutableListOf(
            AbilityEntry(
                Ability.HOT_SHOT,
                AbilityActivationMethod.RIGHT_CLICK,
                of(AbilityCost.Resource.MANA, 150)
            )
        )
    }

    override fun getCooldown(item: ItemStack): Long { return TickTime.seconds(1) }

    override fun getDisplayKey(): Key {
        return IModelOverridden.ofItemType(type)
    }

    companion object {
        val COOLDOWN_GROUP = NamespacedKey(SMPRPG.Companion.plugin, "inferno_staff_ability")
    }
}